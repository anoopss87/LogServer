package quantil.tool;

import java.io.InputStream;
import java.util.Properties;

/**
* ReadPropFile reads configuration property file and gets the 
* specified parameters to be used by Generator and Query.  
* @author  Anoop S Somashekar
* @version 1.0
* @since   2016-08-01
*/
public class ReadPropFile
{
	protected Properties prop = null;
	protected InputStream input = ReadPropFile.class.getClassLoader().getResourceAsStream("conf.properties");
	private static ReadPropFile instance = null;	
	
	protected ReadPropFile() throws Exception	
	{
		prop = new Properties();
		
		if(input != null)
			prop.load(input);		
	}
	
	/**
	   * This method returns an instance of ReadPropFile 	
	   * @exception Exception If the configuration properties file read error
	   * @return Singleton object of ReadPropFile 	  
	 */
	public static ReadPropFile getInstance() throws Exception
	{
		try
		{
			if(instance == null)
				instance = new ReadPropFile();
		}
		catch(Exception e)
		{
			System.out.println("Unable to read configuration properties file : e");
		}
		return instance;		
	}
	
	/**
	   * This method reads num_of_servers from the configuration properties file  	 
	   * @return Number of servers 	  
	 */
	public String getNoOfServers()
	{
		return  prop.getProperty("num_of_servers");		
	}
	
	/**
	   * This method reads num_of_cores from the configuration properties file	   	  
	   * @return Number of cores 	  
	 */
	public String getNoOfCores()
	{
		return prop.getProperty("num_of_cores");		
	}
}

