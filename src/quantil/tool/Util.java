package quantil.tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util
{
	private static final int num_of_servers = 10;
	private static final int num_of_cores = 4;
	public final static int SEC_IN_A_MIN = 60;	
	public final static int HOURS_IN_A_DAY = 24;
	public final static int MIN_IN_AN_HOUR = 60;
	public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";		
				
	/* generate fileName given IP address and date */
	public static String generateFName(String ipAddr, String date)
	{
		String fName = ipAddr.replace('.', '-') + "_" + date;
		return fName;
	}
	
	/* convert Unix time into date format yyyy-MM-dd HH:mm*/
	public static String getFormattedDate(long unixTimeMS)
	{
		Date date = new Date(unixTimeMS);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	/*convert date format yyyy-MM-dd HH:mm into Unix time  */
	public static long getUnixTimeMS(String dateString, String time) throws ParseException
	{
		DateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
		Date date = formatter.parse(dateString + " " + time);
		return date.getTime();
	}
	
	public static int getNoOfServers() throws Exception
	{
		String val =  ReadPropFile.getInstance().getNoOfServers();
		if(val != null)
			return Integer.parseInt(val);
		else
			return num_of_servers;
	}
	
	public static int getNoOfCores() throws Exception
	{
		String val =  ReadPropFile.getInstance().getNoOfCores();
		if(val != null)
			return Integer.parseInt(val);
		else
			return num_of_cores;
	}	 
}
