package quantil.tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util
{
	public static int NUM_OF_SERVERS = 1000;
	public static int SEC_IN_A_MIN = 60;
	public static int NUM_OF_CORES = 2;
	public static int HOURS_IN_A_DAY = 24;
	public static int MIN_IN_AN_HOUR = 60;
	public static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";
	public static int NUM_OF_ENTRIES_PER_FILE = MIN_IN_AN_HOUR * HOURS_IN_A_DAY * NUM_OF_CORES;
	
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
}
