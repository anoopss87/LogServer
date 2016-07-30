package quantil.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class Query
{
	public static String path;
	
	public static boolean validateInput(String inputLine) throws NumberFormatException, Exception
	{
		String[] params = inputLine.split("\\s+");
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
		return true;
	}
	
	/* read the binary log file of the queried machine into an in memory data structure*/
	public static HashMap<String, Integer> readLog(String date, String ipAddress) throws Exception
	{
		HashMap<String, Integer> ht = new HashMap<String, Integer>();
		try
		{
			RandomAccessFile file = new RandomAccessFile(path + File.separator + Util.generateFName(ipAddress, date), "r");
			
			int max_entries = Util.MIN_IN_AN_HOUR * Util.HOURS_IN_A_DAY * Util.getNoOfCores();
					
			for(int i=1;i<=max_entries;++i)
			{			
				long uTime = file.readLong();
				int core = file.read();
				int usage = file.read();
				String key = String.valueOf(uTime) + "_" + String.valueOf(core);				
				ht.put(key, usage);			
			}
			file.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Invalid query : Check ip address/time interval");
			//System.out.println("Exception thrown  :" + e);
		}
		return ht;
		
	}
	
	/* process the query */
	public static void handleQuery(String ip, String core, String d1, String t1, String d2, String t2) throws Exception
	{
		String startDate = d1 + " " + t1;
		String endDate = d2 + " " + t2;
		
		/* get the Unix time for the queried interval */
		long startTime = Util.getUnixTimeMS(startDate, t1)/1000;
		long endTime = Util.getUnixTimeMS(endDate, t2)/1000;		
		
		/* read the log for the specified interval */
		HashMap<String, Integer> ht = readLog(d1, ip);		
		
		if(!ht.isEmpty())
		{
			System.out.format("CPU%s usage on %s:\n", core, ip);
			while(startTime < endTime)
			{
				System.out.print("(");
				System.out.print(Util.getFormattedDate(startTime * 1000));
				System.out.print(", ");
				System.out.print(ht.get(startTime + "_" + core) + "%");
				System.out.print(")");
				startTime += Util.SEC_IN_A_MIN;
				if(startTime < endTime)
					System.out.print(", ");
			}
			System.out.println();
		}		
	}
	
	public static void main(String[] args) throws NumberFormatException, Exception
	{
		/* path where the logs have to be read */
		if(args.length == 0)
			System.out.println("No arguments found : command requires data_path as an argument");
		
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
			if(!validateInput(inputLine))
			{
				System.out.println("Try again!!!!!!");				
			}					
			
			else if(params[0].trim().toLowerCase().equals("query"))
			{
				long startTime = System.currentTimeMillis();
				handleQuery(params[1], params[2], params[3], params[4], params[5], params[6]);
				long endTime = System.currentTimeMillis();
				System.out.format("Elapsed time is %d milli seconds\n", (endTime - startTime));
			}
			else
				System.out.println("Invalid query : Try again!!!!!!");
		}
	}
}
