package quantil.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import quantil.tool.Util;

/**
* Generates log files
* The Generator generates log files for multiple servers
* with one entry for every minute with the following fields:
* unixTime,cpuId, cpuUsage. It generates one file per machine
* for a single day i.e. from 00:00 to 23:59
* @author  Anoop S Somashekar
* @version 1.0
* @since   2016-08-01
*/
public class Generator
{	
	private static int SUBNET_MAX = 255;
	private static String DEFAULT_DATE = "2016-01-01";	
	private static String dir;
	
	 /**
	   * This method generates contiguous IP addresses starting from 192.168.1.1 
	   * @param size Number of IP addresses	  
	   * @return List of generated IP addresses.	  
	   */
	private ArrayList<String> createIPAddrList(int size)
	{
		ArrayList<String> ipList = new ArrayList<String>();
		boolean done = false;
		for(int i=1;i<=SUBNET_MAX;++i)
		{
			for(int j=1;j<=SUBNET_MAX;++j)
			{
				String ipAddr = "192.168."+String.valueOf(i)+"."+String.valueOf(j);
				ipList.add(ipAddr);
				if(ipList.size() == size)
				{
					done = true;
					break;
				}
			}
			if (done)
				break;
		}
		return ipList;
	}	
	
	/**
	   * This is the main method which generates log files. It generates
	   * unique file name and saves the log data in that file.
	   * @param date Day for which the log has to be generated
	   * @param ipAddress IP address of the machine for which the log has to be generated
	   * @return Nothing.	  
	   */
	private void generateLog(String date, String ipAddress) throws Exception
	{		
		try
		{
			/* create binary file for each ipAddress i.e. log file per machine */
			RandomAccessFile file = new RandomAccessFile(dir + "\\" + Util.generateFName(ipAddress, date), "rw");
			
			//clean if the file already exists
			file.setLength(0);
			
			/* convert date to Unix time */
			long unixTime = Util.getUnixTimeMS(date, "00:00") / 1000;
			Random rand = new Random();
			
			/* generate logs for one day with one entry per minute */
			for(int hour=1;hour<=Util.HOURS_IN_A_DAY;++hour)
			{
				for(int min=1;min<=Util.MIN_IN_AN_HOUR;++min)
				{				
					for(int core=0;core<Util.getNoOfCores();++core)
					{
						short val = (short) rand.nextInt(100);					
						file.writeLong(unixTime);					
						file.write(core);
						file.write(val);
					}
					unixTime += Util.SEC_IN_A_MIN;
				}
			}
			file.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Log file couldn't be created : " + e);			
		}
		catch(ParseException e)
		{
			System.out.println("Invalid date format : YYYY-MM-DD " + e);			
		}
	}
	
	/**
	   * This is the main method which makes use of generateLog method.
	   * @param args Data path where the log files needs to stored and optional date.
	   * If date is not specified as an argument then it uses the default date which is "2016-01-01"	   
	   * @exception Exception On input error.	 
	   */
	public static void main(String[] args) throws Exception
	{
		if(args.length == 0)
			System.out.println("No arguments found : command requires data_path as an argument and an optional date argument");
		
		Generator logGen = new Generator();		
				
		long startTime = System.currentTimeMillis();
		/* data path or log path where the logs will be created*/
		dir = args[0];		
		
		try
		{
			/* if the directory doesn't exist then create a new directory */
			File file = new File(dir);
			if (!file.exists())
			{
				if(!file.mkdir())
				{
					System.out.println("Data path directory creation failed.");
					System.exit(1);
				}
			}
		
			/* date for which the logs has to be created */
			String date = DEFAULT_DATE;
			if (args.length >= 2)
			{
				date = args[1];
			}
		
			ArrayList<String> ipList = logGen.createIPAddrList(Util.getNoOfServers());			
		
			System.out.print("Generating logs...");
			int counter = 0;
			for(int i=0;i<Util.getNoOfServers();++i)
			{			
				logGen.generateLog(date, ipList.get(i));
				counter++;
				if(counter == 20)
				{
					System.out.print(".");
					counter = 0;
				}
			}
			System.out.println();
			long endTime = System.currentTimeMillis();
			System.out.println("Log generation completed!!!!!!!!");
			System.out.format("Elapsed time is %d milli seconds\n", (endTime - startTime));
		}
		catch(Exception e)
		{
			System.out.println("Exception thrown  :" + e);
		}
	}
}
