package com.smarsh.preindex.ingestion;

import static com.smarsh.preindex.common.Constants.dateFormat;
import static com.smarsh.preindex.common.Constants.mega;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.bo.HistogramData;
import com.smarsh.preindex.bo.Pair;
import com.smarsh.preindex.common.IndexType;
import com.smarsh.preindex.common.Region;
import com.smarsh.preindex.common.UTIL;
import com.smarsh.preindex.config.ApplicationContextProvider;
import com.smarsh.preindex.config.ElasticSearchConfig;
import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.IndexCreationException;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.model.IndexMetaData;

@Service
public class IndexMetaGeneratorService {
	
	@Autowired
	private PreIndexMetaConfigs metaConfigs;
	
	@Autowired
	private ElasticSearchConfig esConfigs;
	
	@Autowired
	private IndexingService preIndexService;

	private static final int SEQ_NO_1000 = 1000;
	private static Logger logger = Logger.getLogger(IndexMetaGeneratorService.class);
	private static Long MAX_SIZE_PER_INDEX = 200000000l;

	private int replicaCount;
	private boolean activeFl;
	private boolean indexFull;
	private String cluster;
	private String indexType;
	private String siteId;
	private String mappingSchemaVersion;
	
	@PostConstruct
	public void init() {
		replicaCount = esConfigs.getArchiveNumberOfReplicas();
		activeFl = true;
		indexFull = false;
		cluster = "data";
		indexType = IndexType.ARCHIVE.name();
		siteId = esConfigs.getSideId();
		mappingSchemaVersion = esConfigs.getIndexManagerArchiveSchemaVersion();
	}

	public void generatePreIndexes(List<Region> regions) {
		logger.info("***generatePreIndexes STARTS****");
		StringBuilder summary = new StringBuilder("\n**\tIndex Summary\t**\n");
		try {

			MAX_SIZE_PER_INDEX = UTIL.getMaxSizePerIndexInGB(
					this.metaConfigs.getShards(),
					this.metaConfigs.getShardsize(),
					this.metaConfigs.getFillThreshold())*mega;

			for(Region region : regions) {
				handleRegion(summary, region);
			}
			
			summary.append("**\tEnd Of Summary\t**");
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getCause().getClass().getSimpleName(), e);
		}
		finally {
			logger.info(summary.toString());
			logger.info("***generatePreIndexes END****");
		}
	}

	private void handleRegion(StringBuilder summary, Region region) {
		try {
			Stream<String> lines = UTIL.readFile(region.getFileName(),this);

			ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax = this.groupByOutliersAndSize(
					lines, 
					region);

			List<IndexMetaData> indexes = generateIndexSnapshot(groupByIndexSumMax, region);
			
			indexes.forEach(index -> {
				try {
					this.preIndexService.index(index, false);
				} catch (IndexCreationException | MetaDataCreationException e) {
					//DO Nothing
					//Already handled in the index service
				}
			});
			
			summary.append(String.format("\tRegion : %s, Indexes Required : %d\n", region.name(), groupByIndexSumMax.size()));
		} catch (IOException ioe) {
			logger.info("Exception when processing "+region.getFileName(), ioe);
			summary.append(String.format("\tRegion : %s, Index creation Exception", region.name()));
		}
	}

	private List<IndexMetaData> generateIndexSnapshot(ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax, Region region) {

		List<IndexMetaData> indexes = new ArrayList<>();
		String outputFileName = region+"_indexes.txt";
		File newFile = new File(outputFileName);
		if(newFile.exists())
			newFile.delete();

		try {
			if(newFile.createNewFile()) {
				FileWriter fw = new FileWriter(outputFileName);

				logger.debug("Total No. of Indexes for: ******* "+region+" : "+groupByIndexSumMax.size()+" *******");
				fw.write("Total No. of Indexes for: ******* "+region+" : "+groupByIndexSumMax.size()+" *******\n");

				for(int i = 0; i<groupByIndexSumMax.size(); i++) {
					List<HistogramData> subSet = groupByIndexSumMax.get(i).getRight();
					HistogramData startRange = subSet.get(0);
					HistogramData endRange = subSet.get(subSet.size()-1);
					Integer indexMemSize = groupByIndexSumMax.get(i).getLeft().intValue();
					Date startDate = startRange.getDate();
					String indexName = String.format("%s_%s_data_%s_%d_archive.av5", 
												this.metaConfigs.getTenant(), 
												region.name(), 
												UTIL.getDateForIndex(startDate), SEQ_NO_1000)
											.toLowerCase();

					logger.debug(String.format("IndexID:%s, StartDate:%s, EndDate:%s, MemoryConsumption:%d(KB)|%d(GB), ",
							indexName, startRange.getDateInString(), endRange.getDateInString(), indexMemSize.intValue(), indexMemSize.intValue()/mega));
					fw.write(String.format("[%s -to- %s]\t:\tIndexID:%s, MemoryConsumption:%d(KB)|%d(GB)\n",
							startRange.getDateInString(), endRange.getDateInString(), indexName, indexMemSize.intValue(), indexMemSize.intValue()/mega));

					//FIXME
					IndexMetaData indexMetaData = prepareIndexMetadata(
							SEQ_NO_1000, this.activeFl, this.indexFull, this.siteId, this.cluster, this.indexType, this.mappingSchemaVersion, 
							i, i, replicaCount, this.metaConfigs.getShards(), indexName);
					indexes.add(indexMetaData);
				}

				fw.write("************** END ***********************\n");
				logger.debug("************** END ***********************");

				fw.close();
			}
		} catch (IOException e) {
			logger.error("Exception in writing the details to output file", e);
		}
		return indexes;
	}

	public IndexMetaData prepareIndexMetadata(int seqNo, boolean activeFl, boolean indexFull, String siteId,
			String cluster, String indexType, String mappingSchemaVersion, long startTime, long toTime,
			int replicaCount, int shardCount, String indexName) {
		IndexMetaData indexMetaData = new IndexMetaData();
		indexMetaData.setActiveFl(activeFl);
		indexMetaData.setCluster(cluster);
		indexMetaData.setCreateDateTime(System.currentTimeMillis());
		indexMetaData.setFromDate(startTime);
		indexMetaData.setToDate(toTime);
		indexMetaData.setMaxDate(UTIL.getEndOfDay(startTime));
		indexMetaData.setIndexAppType(indexType);
		indexMetaData.setIndexFull(indexFull);
		indexMetaData.setIndexName(indexName);
		indexMetaData.setIndexVersion(mappingSchemaVersion);
		indexMetaData.setReplicaCount(replicaCount);
		indexMetaData.setShardCount(shardCount);
		indexMetaData.setSequenceNumber(seqNo);
		indexMetaData.setSiteId(siteId);

		return indexMetaData;
	}

	private ArrayList<Pair<Double, List<HistogramData>>> groupByOutliersAndSize(Stream<String> lines, Region region) {

		ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax = lines
				.skip(1)
				.map(line -> {
					String[] data = line.split(",");
					Date date = null;
					try {
						date = dateFormat.get().parse(data[0]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					BigDecimal sizeInKB = new BigDecimal(data[2]);
					Integer hour = Integer.parseInt(data[1]);
					BigDecimal indexSizeV2 = sizeInKB.multiply(this.metaConfigs.getIndexToDocMem());
					HistogramData histogramData = new HistogramData(region, date, sizeInKB, hour, indexSizeV2.doubleValue());
					return histogramData;
				})
				.sorted((h1,h2)->Long.valueOf(h1.getDate().getTime()).compareTo(h2.getDate().getTime()))
				.filter(histoGram -> histoGram.getDate().after(metaConfigs.getStartDate()))
				.collect(ArrayList<Pair<Double, List<HistogramData>>>::new, Accumulator::indexAggregator, (x, y) -> {});

		return groupByIndexSumMax;
	}

	private static class Accumulator {
		public static void indexAggregator(List<Pair<Double, List<HistogramData>>> lPair, HistogramData histo) {
			Pair<Double, List<HistogramData>> lastPair = lPair.isEmpty() ? null : lPair.get(lPair.size() - 1);
			Double indexSize = histo.getIndexSize();
			Region region = histo.getRegion();
			
			Boolean useOutliers = ApplicationContextProvider.isOutlierEnabled();

			if(!useOutliers) {
				if( Objects.isNull(lastPair) || lastPair.left + indexSize > MAX_SIZE_PER_INDEX) {
					lPair.add(
							new Pair<Double, List<HistogramData>>(indexSize,
									Arrays.asList(histo)));
				} else {
					List<HistogramData> newList = new ArrayList<>();
					newList.addAll(lastPair.getRight());
					newList.add(histo);
					lastPair.setLeft(lastPair.getLeft() + indexSize);
					lastPair.setRight(newList);
				}
			} else {
				if( Objects.isNull(lastPair)) {
					lPair.add(
							new Pair<Double, List<HistogramData>>(indexSize,
									Arrays.asList(histo)));
				} else if (histo.getDate().before(region.getLowerBound())) {
					if(lastPair.left + indexSize > MAX_SIZE_PER_INDEX) {
						lPair.add(
								new Pair<Double, List<HistogramData>>(
										indexSize,
										Arrays.asList(histo)));
					} else {
						List<HistogramData> newList = new ArrayList<>();
						newList.addAll(lastPair.getRight());
						newList.add(histo);
						lastPair.setLeft(lastPair.getLeft() + indexSize);
						lastPair.setRight(newList);
					}
				} else if(histo.getDate().after(region.getUpperBound())) {
					HistogramData lastDataMarkedAsComplete = lastPair.right.get(lastPair.right.size()-1);
					if( (lastDataMarkedAsComplete.getDate().before(region.getUpperBound()))
							|| (lastPair.left + indexSize > MAX_SIZE_PER_INDEX)) {
						lPair.add(
								new Pair<Double, List<HistogramData>>(indexSize,
										Arrays.asList(histo)));
					} else {
						List<HistogramData> newList = new ArrayList<>();
						newList.addAll(lastPair.getRight());
						newList.add(histo);
						lastPair.setLeft(lastPair.getLeft() + indexSize);
						lastPair.setRight(newList);
					}
				}
				else {
					HistogramData lastDataMarkedAsComplete = lastPair.right.get(lastPair.right.size()-1);
					if( (lastDataMarkedAsComplete.getDate().before(region.getLowerBound()))
							|| (lastPair.left + indexSize > MAX_SIZE_PER_INDEX) // Size check
							|| (UTIL.compareYears(lastDataMarkedAsComplete.getDate(), histo.getDate())>0)) { // YEAR wise partition
						lPair.add(new Pair<Double, List<HistogramData>>(
								indexSize,
								Arrays.asList(histo)));
					} else {
						List<HistogramData> newList = new ArrayList<>();
						newList.addAll(lastPair.getRight());
						newList.add(histo);
						lastPair.setLeft(lastPair.getLeft() + indexSize);
						lastPair.setRight(newList);
					}
				}
			}
		}
	}
}
