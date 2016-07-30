package quantil.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.HashMap;

public class Query
{
	public static String path;
	
	public static boolean validateInput(String inputLine)
	{
		String[] params = inputLine.split("\\s+");
		if(params.length != 7)
		{
			System.out.println("Incorrect query format : QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2");
			return false;
		}
		if(!params[3].equals(params[5]))
		{
			System.out.println("Incorrect date - YYYY-MM-DD should be same: QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2");
			return false;
		}
		if(Integer.parseInt(params[2]) > Util.NUM_OF_CORES-1 || Integer.parseInt(params[2]) < 0)
		{
			System.out.println("Incorrect core number - YYYY-MM-DD should be same: QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2");
			return false;
		}
		return true;
	}
	
	/* read the binary log file of the queried machine into an in memory data structure*/
	public static HashMap<String, Integer> readLog(String date, String ipAddress) throws IOException
	{
		HashMap<String, Integer> ht = new HashMap<String, Integer>();
		try
		{
			RandomAccessFile file = new RandomAccessFile(path + File.separator + Util.generateFName(ipAddress, date), "r");
					
			for(int i=1;i<=Util.NUM_OF_ENTRIES_PER_FILE;++i)
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
			System.out.println("Invalid time interval/format");
			System.out.println("Exception thrown  :" + e);
		}
		return ht;
		
	}
	
	/* process the query */
	public static void handleQuery(String ip, String core, String d1, String t1, String d2, String t2) throws ParseException, IOException
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
				System.out.println(Util.getFormattedDate(startTime * 1000));
				System.out.println(", ");
				System.out.print(ht.get(startTime + "_" + core + "%"));
				System.out.println(")");
				startTime += Util.SEC_IN_A_MIN;
				if(startTime < endTime)
					System.out.print(", ");
			}
			System.out.println();
		}		
	}
	
	public static void main(String[] args) throws IOException, ParseException
	{
		/* path where the logs have to be read */
		path = args[0];
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true)
		{
			System.out.print(">");
			String inputLine = bufferReader.readLine();
			String[] params = inputLine.split("\\s+");
			if(!validateInput(inputLine))
			{
				System.out.println("Invalid query : Try again");				
			}
			
			else if (inputLine.trim().toLowerCase().equals("exit"))
			{
				System.out.println("Exiting!!!!!");
				break;
			}			
			
			else if(params[0].trim().toLowerCase().equals("query"))
			{
				long startTime = System.currentTimeMillis();
				handleQuery(params[1], params[2], params[3], params[4], params[5], params[6]);
				long endTime = System.currentTimeMillis();
				System.out.format("Elapsed time is %d milli seconds\n", (endTime - startTime));
			}
			else
				System.out.println("Invalid query : Try again");
		}
	}
}
