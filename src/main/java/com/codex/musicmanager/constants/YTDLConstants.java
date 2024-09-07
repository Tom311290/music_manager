package com.codex.musicmanager.constants;

public class YTDLConstants {

	//static final String  YTDL_TEMP_FOLDER = "C:/Users/" + System.getProperty("user.name") + "/YTDLTemp/";
	public static final String  YTDL_TEMP_FOLDER = "C:/Users/Public/YTDLTemp/";
	public static final String INIT_FILE = "initialize.txt";
	public static final String LOG_FILE = "log.txt";
	public static final String LOG_MESSAGE_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} |%level| %C{1}: %m%n";
	
	public static final String FATAL_ERROR_MESSAGE = "Something went worng! Please check log file " + LOG_FILE + " in " + YTDL_TEMP_FOLDER + "\n";

}
