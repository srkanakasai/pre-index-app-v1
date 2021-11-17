package com.smarsh.preindex.common;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationArguments;

import com.smarsh.preindex.bo.BatchInput;

public class BatchUtil {

	private static final String INPUT_DIR = "input_dir";
	private static final String BATCH_TXT = "batch.txt";

	public static List<BatchInput> extractBatchInput(ApplicationArguments args) throws URISyntaxException
	{
		String batchInputFileDir = getInputDirPath(args);
		String batchInputFile = batchInputFileDir+File.separator+BATCH_TXT;
		File batchFile = new File(batchInputFile);
		List<String> fileInputLines = UTIL.strReader(batchFile);
		List<BatchInput> inputs = fileInputLines.stream().skip(1).map(line -> {
			String[] data = line .split("\\|");
			Date[] outliers = null;
			if(data.length>3) {
				String[] dates = data[3].split(",");
				outliers = Arrays.stream(dates)
								.map(Constants::parse)
								.toArray(Date[]::new);
			}
			BatchInput batchInput = new BatchInput(data[0], data[1], data[2], outliers);
			return batchInput;
		}).collect(Collectors.toList());
		return inputs;
	}

	public static String getInputDirPath(ApplicationArguments args) {
		String batchInputFileDir = args.getOptionValues(INPUT_DIR).get(0);
		return batchInputFileDir;
	}

	public static void main(String[] args) {
		String line = "BNY|APAC|eggfkfjhkj|2012-01-01,2021-12-31";
		String[] data = line .split("\\|");
		Date[] outliers = null;
		if(data.length>3) {
			String[] dates = data[3].split(",");
			outliers = Arrays.stream(dates)
					.map(Constants::parse)
					.toArray(Date[]::new);
		}
		BatchInput batchInput = new BatchInput(data[0], data[1], data[2], outliers);
		System.out.println(batchInput);

	}
}
