package com.smarsh.preindex.common;

import static com.smarsh.preindex.common.Constants.ROUND_OFF_FACTOR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class UTIL {
	
	private static final Logger LOG = Logger.getLogger(UTIL.class);

	public static String getDateForIndex(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int monthDate = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);

		String monthStr = month<10?"0"+month:""+month;
		String dateStr = monthDate<10?"0"+monthDate:""+monthDate;

		return String.format("%d%s%s", year,monthStr,dateStr);
	}
	
	/**
	 * If D2 year is greater than D1 then return 1
	 * if D2 year is less than D1 then return -1
	 * if Both the years are same return 0;
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int compareYears(Date d1, Date d2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(d1);
		int year1 = calendar1.get(Calendar.YEAR);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(d2);
		int year2 = calendar2.get(Calendar.YEAR);
		
		return (year2>year1)?1:(year1>year2)?-1:0;
	}

	public static long getMaxSizePerIndexInGB(int maxNumOfShardsPerIndex, int shardSizeInGB, int fillPercentage) {
		int maxIndexMemoryAvailable = maxNumOfShardsPerIndex*shardSizeInGB;
		int maxAllowedSizePerIndex = ((maxIndexMemoryAvailable*fillPercentage)/100);
		return (ROUND_OFF_FACTOR*(Math.round((double)maxAllowedSizePerIndex/ROUND_OFF_FACTOR)));
	}
	
	public static Stream<String> readFile(String dirPath, String fileName) throws IOException{
		InputStream inputStream = Files.newInputStream(Paths.get(dirPath+File.separator+fileName));
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		Stream<String> lines = bufferedReader.lines();
		return lines;
	}

	public static Long getEndOfDay(long startTime) {
		DateTime time = new DateTime(startTime, DateTimeZone.UTC);
		return time.withTime(0,0,0,0).plusDays(1).getMillis()-1;
	}
	
	public static Long getEndOfDay(Date date) {
		DateTime time = new DateTime(date, DateTimeZone.UTC);
		return time.withTime(0,0,0,0).plusDays(1).getMillis()-1;
	}
	
    public static String loadAsString(final String path) {
    	
    	ClassPathResource classPathResource = new ClassPathResource(path);
    	try {
    	    byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
    	    return new String(binaryData, StandardCharsets.UTF_8);
    	} catch (IOException e) {
    		LOG.error(e.getMessage(), e);
            return null;
    	}
    }
    
    public static void main(String[] args) throws URISyntaxException {
    	File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").toURI());
    	System.out.println(jarDir.getPath());
    	
    	File jar = new File(UTIL.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	System.out.println(jar.getPath());
	}
    
    public static List<String> strReader (File in) throws URISyntaxException
    {
        List<String> totLines = new ArrayList<String>();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(in));
            String line;
            while ((line = br.readLine()) != null)
            {
                totLines.add(line);
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totLines;
    }
    
    public static Date getGMTDate(long timeInMillis) {
		Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCalendar.setTimeInMillis(timeInMillis);
		return gmtCalendar.getTime();
	}
}
