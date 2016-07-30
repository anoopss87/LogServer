package quantil.tool;

import java.io.InputStream;
import java.util.Properties;

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
	
	public static ReadPropFile getInstance() throws Exception
	{		
		if(instance == null)
			instance = new ReadPropFile();
		return instance;		
	}
	
	public String getNoOfServers()
	{
		return  prop.getProperty("num_of_servers");		
	}
	
	public String getNoOfCores()
	{
		return prop.getProperty("num_of_cores");		
	}
}

