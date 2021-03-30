package org.rscangel.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config
{
	/**
	 * Called to load config settings from the given file
	 */
	public static void initConfig( String file ) throws IOException
	{
		START_TIME = System.currentTimeMillis();

		Properties props = new Properties();
		props.load( new FileInputStream( file ) );

		//SERVER_IP = props.getProperty( "server" );
		//SERVER_PORT = Integer.parseInt( props.getProperty( "port" ) );
		CLIENT_DIR = props.getProperty( "client_dir" );
		String hys  = props.getProperty("height_y_shift");
		if(isInt(hys))
			HEIGHT_Y_SHIFT = getIntFromString(hys);
		else
			HEIGHT_Y_SHIFT = 944;
		//MEDIA_DIR = props.getProperty( "media_dir" );

		props.clear();
	}
	
	private static boolean isInt(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	private static int getIntFromString(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			return -1;
		}
	}

	//public static String SERVER_IP;
	public static String CLIENT_DIR;
	//public static String MEDIA_DIR;
	//public static int SERVER_PORT;
	public static long START_TIME;
	public static int HEIGHT_Y_SHIFT;
}
