package com.smarsh.preindex.service;

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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.bo.HistogramData;
import com.smarsh.preindex.bo.IndexMetaDataBO;
import com.smarsh.preindex.bo.Pair;
import com.smarsh.preindex.common.Region;
import com.smarsh.preindex.common.UTIL;
import com.smarsh.preindex.config.ApplicationContextProvider;
import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.IndexCreationException;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.transformer.IndexMetaDataTransformer;

@Service
public class IndexMetaGeneratorService implements ApplicationRunner{

	private static final String INDEX_NAME_PATTERN = "%s_data_%s_%d_archive.av5";

	@Autowired
	private PreIndexMetaConfigs metaConfigs;

	@Autowired
	private IndexingService preIndexService;
	
	@Autowired
	private IndexMetaDataTransformer metaDataTransformer;

	private static final int SEQ_NO_1000 = 1000;
	private static Logger logger = Logger.getLogger(IndexMetaGeneratorService.class);
	private static Long MAX_SIZE_PER_INDEX = 200000000l; //DEFAULT
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		generatePreIndexes(Arrays.asList(Region.values()));
	}

	public void generatePreIndexes(List<Region> regions) {
		logger.info("***generatePreIndexes STARTS with TESTMODE="+this.metaConfigs.getIsTestMode()+"****");
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

	private void handleRegion(final StringBuilder summary, final Region region) {
		try {
			Stream<String> lines = UTIL.readFile(region.getFileName(), this);

			ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax = this.groupByOutliersAndSize(
					lines, 
					region);

			List<IndexMetaDataBO> indexes = generateIndexSnapshot(groupByIndexSumMax, region);

			indexes
				.stream()
				.map(metaDataTransformer)
				.forEach(index -> {
					try {
						if(!this.metaConfigs.getIsTestMode())
							this.preIndexService.index(index, false);
					} catch (IndexCreationException | MetaDataCreationException e) {
						summary.append(String.format("\tRegion : %s, Index : %s creation Exception", region.name(), index));
						logger.error(String.format("\tRegion : %s, Index : %s creation Exception", region.name(), index), e);
					}
				});
			

			summary.append(String.format("\tRegion : %s, Indexes Required : %d\n", region.name(), groupByIndexSumMax.size()));
		} catch (IOException ioe) {
			logger.info("Exception when processing "+region.getFileName(), ioe);
			summary.append(String.format("\tRegion : %s, Index creation Exception", region.name()));
		}
	}

	private List<IndexMetaDataBO> generateIndexSnapshot(ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax, Region region) {

		List<IndexMetaDataBO> indexes = new ArrayList<>();
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
					String indexName = String.format(INDEX_NAME_PATTERN, 
							this.metaConfigs.getTenant(), 
							UTIL.getDateForIndex(startDate), 
							SEQ_NO_1000)
							.toLowerCase();

					logger.debug(String.format("IndexID:%s, StartDate:%s, EndDate:%s, MemoryConsumption:%d(KB)|%d(GB), ",
							indexName, startRange.getDateInString(), endRange.getDateInString(), indexMemSize.intValue(), indexMemSize.intValue()/mega));
					fw.write(String.format("[%s -to- %s]\t:\tIndexID:%s, MemoryConsumption:%d(KB)|%d(GB)\n",
							startRange.getDateInString(), endRange.getDateInString(), indexName, indexMemSize.intValue(), indexMemSize.intValue()/mega));

					IndexMetaDataBO metaDataBO = IndexMetaDataBO.builder()
														.setIndexName(indexName)
														.setFromDate(startDate)
														.setSequenceNumber(SEQ_NO_1000)
														.setShardCount(this.metaConfigs.getShards())
														.setToDate(endRange.getDate())
														.build();
					
					indexes.add(metaDataBO);
				}

				fw.write("************** "+region+" END ***********************\n");
				logger.debug("************** "+region+" END ***********************\n");

				fw.close();
			}
		} catch (IOException e) {
			logger.error("Exception in writing the details to output file", e);
		}
		return indexes;
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
