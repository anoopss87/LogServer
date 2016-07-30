package quantil.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import quantil.tool.Util;

public class Generator
{
	/* create IP address for the log files */
	private static int SUBNET_MAX = 255;
	private static String dir;
	
	/* generate contiguous IP addresses which starts with 192.168 */
	public static void createIPAddrList(int size, ArrayList<String> ipList)
	{
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
	}	
	
	public static void generateLog(String date, String ipAddress) throws IOException, ParseException
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
					for(int core=0;core<Util.NUM_OF_CORES;++core)
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
			System.out.println("Log file couldn't be created");
			//System.out.println("Exception thrown  :" + e);
		}
		catch(ParseException e)
		{
			System.out.println("Invalid date format : YYYY-MM-DD");
			//System.out.println("Exception thrown  :" + e);
		}
	}
	public static void main(String[] args) throws IOException, ParseException
	{
		long startTime = System.currentTimeMillis();
		/* data path or log path where the logs will be created*/
		dir = args[0];
		
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
		String day = args[1];
		ArrayList<String> ipList = new ArrayList<String>();		
		
		createIPAddrList(Util.NUM_OF_SERVERS, ipList);
		
		System.out.print("Generating logs");
		int counter = 0;
		for(int i=0;i<Util.NUM_OF_SERVERS;++i)
		{			
			generateLog(day, ipList.get(i));
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
}
