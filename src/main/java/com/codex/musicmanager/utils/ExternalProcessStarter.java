package com.codex.musicmanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import java.util.logging.*;


/**
 * Starts external process in new thread, and reads it's output to logger
 * @author Tomislav
 *
 */
public class ExternalProcessStarter implements Callable<Object> {
	
	private ProcessBuilder processBuilder;
	private Process process;
	private Logger logger;
	private String startProcessMessage = "";
	private String endProcessMessage = "";
	
	@Override
	public Object call() {
		try {
			
			process = processBuilder.start();
			logger.info(startProcessMessage);
			
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	
			String s = null;
			while ((s = lineReader.readLine()) != null) {
				logger.info(s);
			}
	
			while ((s = errorReader.readLine()) != null) {
				logger.info(s);
			}
			return true;
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			return false;
			
		} finally {
			process.destroy();
			logger.info(endProcessMessage);
		}
	}
	
	public ProcessBuilder getProcessBuilder() {
		return processBuilder;
	}

	public void setProcessBuilder(ProcessBuilder processBuilder) {
		this.processBuilder = processBuilder;
	}

	public Logger getLog() {
		return logger;
	}

	public void setLogger(Logger log) {
		this.logger = log;
	}
	
	public void stop() {
		process.destroy();
	}

	public String getStartProcessMessage() {
		return startProcessMessage;
	}

	public void setStartProcessMessage(String startProcessMessage) {
		this.startProcessMessage = startProcessMessage;
	}

	public String getEndProcessMessage() {
		return endProcessMessage;
	}

	public void setEndProcessMessage(String endProcessMessage) {
		this.endProcessMessage = endProcessMessage;
	}
}
