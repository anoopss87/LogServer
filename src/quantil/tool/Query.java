package quantil.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
* The Query returns the various CPU usages of a
* particular machine which is uniquely identified 
* by IP address for the specified time interval 
*
* @author  Anoop S Somashekar
* @version 1.0
* @since   2016-08-01
*/
public class Query
{
	private static String path;
	
	 /**
	   * This method validates the input for any invalid input format
	   * @param inputLine Query string entered by the user
	   * @return boolean It returns true if the input is valid otherwise false	 
	   * @exception On input error 
	   */
	private boolean validateInput(String inputLine) throws NumberFormatException, Exception
	{
		String[] params = inputLine.split("\\s+");
		try
		{
			if(params.length < 7)
			{
				System.out.println("Incorrect query format : Format is QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2");
				return false;
			}
			else if(!params[3].equals(params[5]))
			{
				System.out.println("Incorrect date - YYYY-MM-DD should be same: Format is QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2");
				return false;
			}
			else if(Integer.parseInt(params[2]) > Util.getNoOfCores()-1 || Integer.parseInt(params[2]) < 0)
			{
				System.out.println("Incorrect CPU ID...");
				return false;
			}
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			parser.setLenient(false);
			Date startTime = parser.parse(params[3] + " " + params[4]);
			Date endTime = parser.parse(params[5] + " " + params[6]);
			if(endTime.before(startTime))
			{
				System.out.println("Invalid time interval : End time is before start time");
				return false;
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("Cannot convert CPU Id in string to integer : " + e);
			return false;
		}		
		catch(ParseException e)
		{
			System.out.println("Invalid time stamp : " + e);
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
			return false;
		}
		return true;
	}
	
	/**
	   * This is the method which reads log files of the specified 
	   * IP address and date into a hash map where where unixTime 
	   * and CPU ID is the key and CPU usage is the value.
	   * @param date Day specified in the query
	   * @param ipAddress IP address of the machine specified in the query
	   * @return HashMap<String, Integer> It returns the hash map built by reading log file.
	   * @exception FileNotFoundException On invalid ip address/time stamp	  
	   */
	private HashMap<String, Integer> readLog(String date, String ipAddress) throws Exception
	{
		HashMap<String, Integer> ht = new HashMap<String, Integer>();
		try
		{
			/* open the binary file in read mode */
			RandomAccessFile file = new RandomAccessFile(path + File.separator + Util.generateFName(ipAddress, date), "r");
			
			int max_entries = Util.MIN_IN_AN_HOUR * Util.HOURS_IN_A_DAY * Util.getNoOfCores();
					
			for(int i=1;i<=max_entries;++i)
			{			
				long uTime = file.readLong();
				int core = file.read();
				int usage = file.read();
				
				/* key is unixTime_cpuId and value is CPU usage */
				String key = String.valueOf(uTime) + "_" + String.valueOf(core);				
				ht.put(key, usage);			
			}
			file.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Invalid query : Check ip address/time interval " + e);			
		}
		return ht;		
	}
	
	/**
	   * This is the main method which handles query requests. 
	   * @param ip IP Address.
	   * @param core CPU ID
	   * @param d1 Start date in YYYY-MM-DD
	   * @param t1 Start time stamp HH:MM
	   * @param d2 End date in YYYY-MM-DD
	   * @param t1 End time stamp HH:MM
	   * @return Nothing.
	   * @exception Exception On input error.	  
	   */
	private void handleQuery(String ip, String core, String d1, String t1, String d2, String t2) throws Exception
	{
		String startDate = d1 + " " + t1;
		String endDate = d2 + " " + t2;
		
		try
		{
			/* get the Unix time for the queried interval */
			long startTime = Util.getUnixTimeMS(startDate, t1)/1000;
			long endTime = Util.getUnixTimeMS(endDate, t2)/1000;		
			
			/* read the log for the specified interval */
			HashMap<String, Integer> ht = readLog(d1, ip);		
			
			if(!ht.isEmpty() && startTime > 0 && endTime > 0)
			{
				System.out.format("CPU%s usage on %s:\n", core, ip);
				while(startTime < endTime)
				{
					System.out.print("(");
					System.out.print(Util.getFormattedDate(startTime * 1000));
					System.out.print(", ");
					System.out.print(ht.get(startTime + "_" + core) + "%");
					System.out.print(")");
					
					/* increment 60 seconds i.e. 1 minute */
					startTime += Util.SEC_IN_A_MIN;
					
					if(startTime < endTime)
						System.out.print(", ");
				}
				System.out.println();
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
		}
	}
	
	/**
	   * This is the main method which makes use of handleQuery method.
	   * @param args Data path where the logs files are stored.	   
	   * @exception Exception On input error.	 
	   */
	public static void main(String[] args) throws NumberFormatException, Exception
	{
		/* path where the logs have to be read */
		if(args.length == 0)
		{
			System.out.println("No arguments found : command requires data_path as an argument");
			System.exit(0);
		}
		
		try
		{
			Query query = new Query();
			path = args[0];
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
			
			while(true)
			{
				System.out.print(">");
				String inputLine = bufferReader.readLine();
				
				if (inputLine.trim().toLowerCase().equals("exit"))
				{
					System.out.println("Exiting!!!!!");
					break;
				}
				String[] params = inputLine.split("\\s+");
				if(!query.validateInput(inputLine))
				{
					System.out.println("Try again!!!!!!");				
				}					
				
				else if(params[0].trim().toLowerCase().equals("query"))
				{
					long startTime = System.currentTimeMillis();
					query.handleQuery(params[1], params[2], params[3], params[4], params[5], params[6]);
					long endTime = System.currentTimeMillis();
					System.out.format("Elapsed time is %d milli seconds\n", (endTime - startTime));
				}
				else
					System.out.println("Invalid query : Try again!!!!!!");
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
		}
	}
}
