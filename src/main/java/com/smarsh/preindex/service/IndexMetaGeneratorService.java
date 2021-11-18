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

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.bo.BatchInput;
import com.smarsh.preindex.bo.HistogramData;
import com.smarsh.preindex.bo.IndexMetaDataBO;
import com.smarsh.preindex.bo.Outliers;
import com.smarsh.preindex.bo.Pair;
import com.smarsh.preindex.common.BatchUtil;
import com.smarsh.preindex.common.UTIL;
import com.smarsh.preindex.config.ApplicationContextProvider;
import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.IndexCreationException;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.transformer.IndexMetaDataTransformer;

@Service
public class IndexMetaGeneratorService implements ApplicationRunner {
	
	private static final int SEQ_NO_1000 = 1000;
	private static Logger logger = Logger.getLogger(IndexMetaGeneratorService.class);
	private static Long MAX_SIZE_PER_INDEX = 200000000l; //DEFAULT
	private static final String INDEX_NAME_PATTERN = "%s_data_%s_%d_archive.av5";

	@Autowired
	private PreIndexMetaConfigs metaConfigs;

	@Autowired
	private IndexingService preIndexService;
	
	@Autowired
	private IndexMetaDataTransformer metaDataTransformer;
	
	@PostConstruct
	private void init() {
		MAX_SIZE_PER_INDEX = UTIL.getMaxSizePerIndexInGB(
				this.metaConfigs.getShards(),
				this.metaConfigs.getShardsize(),
				this.metaConfigs.getFillThreshold())*mega;
	}
	
	//generatePreIndexes(Arrays.asList(Region.values()));
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		List<BatchInput> batchInputs = BatchUtil.extractBatchInput(args);
		
		String batchInputFileDir = BatchUtil.getInputDirPath(args);
		
		processBatchInput(batchInputFileDir, batchInputs);
		
	}
	
	private void processBatchInput(String batchInputFileDir, List<BatchInput> batchInputs) {
		logger.info("***generatePreIndexes STARTS with TESTMODE="+this.metaConfigs.getIsTestMode()+"****");
		StringBuilder summary = new StringBuilder("\n**\tIndex Summary\t**\n");
		try {

			for(BatchInput input : batchInputs) {
				processBatchInput(summary, batchInputFileDir, input);
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

	public void processBatchInput(StringBuilder summary, String batchInputFileDir, BatchInput batchInput) throws IOException {
		
		String region = batchInput.getRegion();
		try {
			Stream<String> histoFileStream = UTIL.readFile(batchInputFileDir, batchInput.getFileName());

			ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax = this.groupByOutliersAndSize(
					histoFileStream, 
					batchInput);

			List<IndexMetaDataBO> indexes = generateIndexSnapshot(groupByIndexSumMax, batchInput, batchInputFileDir);

			indexes
				.stream()
				.map(metaDataTransformer)
				.forEach(index -> {
					try {
						if(!this.metaConfigs.getIsTestMode())
							this.preIndexService.index(index, false);
					} catch (IndexCreationException | MetaDataCreationException e) {
						summary.append(String.format("\tRegion : %s, Index : %s creation Exception", region , index));
						logger.error(String.format("\tRegion : %s, Index : %s creation Exception", region, index), e);
					}
				});

			summary.append(String.format("\tRegion : %s, Indexes Required : %d\n", region, groupByIndexSumMax.size()));
		} catch (IOException ioe) {
			logger.info("Exception when processing "+batchInput.getFileName(), ioe);
			summary.append(String.format("\tRegion : %s, Index creation Exception", region));
		}
	}

	private List<IndexMetaDataBO> generateIndexSnapshot(ArrayList<Pair<Double, List<HistogramData>>> groupByIndexSumMax, BatchInput batchInput, String batchInputFileDir) {

		List<IndexMetaDataBO> indexes = new ArrayList<>();
		String region = batchInput.getRegion();
		String outputFileName = region+"_indexes.txt";
		File newFile = new File(batchInputFileDir+File.separator+outputFileName);
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

	private ArrayList<Pair<Double, List<HistogramData>>> groupByOutliersAndSize(Stream<String> lines, BatchInput batchInput) {

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
					HistogramData histogramData = new HistogramData(batchInput.getRegion(), date, sizeInKB, hour, indexSizeV2.doubleValue(), batchInput.getOutliers());
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
			Outliers outliers = histo.getOutliers();

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
				} else if (histo.getDate().before(outliers.getLowerBound())) {
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
				} else if(histo.getDate().after(outliers.getUpperBound())) {
					HistogramData lastDataMarkedAsComplete = lastPair.right.get(lastPair.right.size()-1);
					if( (lastDataMarkedAsComplete.getDate().before(outliers.getUpperBound()))
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
					if( (lastDataMarkedAsComplete.getDate().before(outliers.getLowerBound()))
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
