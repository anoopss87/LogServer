package quantil.tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Utilities used by Generator and Query
* 
* @author  Anoop S Somashekar
* @version 1.0
* @since   2016-08-01
*/
public class Util
{
	private static final int num_of_servers = 10;
	private static final int num_of_cores = 4;
	public final static int SEC_IN_A_MIN = 60;	
	public final static int HOURS_IN_A_DAY = 24;
	public final static int MIN_IN_AN_HOUR = 60;
	public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";		
				
	/**
	   * This method generates file name using IP address and date separated by "_".
	   * @param ipAddr IP address
	   * @param date Date for which the logs needs to be generated
	   * @return generated file name	  
	   */
	public static String generateFName(String ipAddr, String date)
	{
		String fName = ipAddr.replace('.', '-') + "_" + date;
		return fName;
	}
	
	/**
	   * This method gets the formatted date "yyyy-MM-dd HH:mm".
	   * @param unixTimeMS Unix time in milliseconds	  
	   * @return formatted date	  
	   */
	public static String getFormattedDate(long unixTimeMS)
	{
		Date date = new Date(unixTimeMS);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	/**
	   * This method gets Unix time in milliseconds
	   * @param dateString date in YYYY-MM-DD format
	   * @param time time in HH:MM format	  
	   * @exception ParseException on invalid date format error
	   * @return Unix time	  
	   */
	public static long getUnixTimeMS(String dateString, String time) throws ParseException
	{
		long uTime = 0;
		try
		{
			DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
			Date date = formatter.parse(dateString + " " + time);
			uTime =  date.getTime();
		}
		catch(ParseException e)
		{
			System.out.println("Couldn't parse date string : " + e);
		}
		return uTime;
	}
	
	/**
	   * This method gets number of servers which will be used by Generator and Query	   
	   * @return number of servers
	   * @exception	Exception on invalid property file  
	   */
	public static int getNoOfServers() throws Exception
	{
		try
		{
			String val =  ReadPropFile.getInstance().getNoOfServers();
			if(val != null)
				return Integer.parseInt(val);
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
		}		
		return num_of_servers;
	}
	
	/**
	   * This method gets number of cores which will be used by Generator and Query	    
	   * @return number of cores
	   * @exception	Exception on invalid property file  
	   */
	public static int getNoOfCores() throws Exception
	{
		try
		{
			String val =  ReadPropFile.getInstance().getNoOfCores();
			if(val != null)
				return Integer.parseInt(val);
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
		}		
		return num_of_cores;
	}	 
}
