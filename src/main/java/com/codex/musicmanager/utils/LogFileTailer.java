package com.codex.musicmanager.utils;

import java.io.*;

import com.codex.musicmanager.Application;
import com.codex.musicmanager.constants.YTDLConstants;
import com.vaadin.flow.component.textfield.TextArea;


public class LogFileTailer {
	

	public void runTailer(TextArea logTextArea) {
		Thread t = new Thread() {
			boolean running = true;
	        @Override
	        public void run() {
	
	            BufferedReader reader = null;
	            try {
	                reader = new BufferedReader(new FileReader(YTDLConstants.YTDL_TEMP_FOLDER+YTDLConstants.LOG_FILE));
	                String line;
	                while (running) {
	                    line = reader.readLine();
	                    if (line == null) {
	                        // wait until there is more lines in the file
	                        Thread.sleep(500);
	                    } else {
	                        // append to the log Label
	                        synchronized (Application.class) {
	                        	logTextArea.setValue(line);
	                        }
	                    }
	                }
	            } catch (IOException e) {
	                // TODO: handle me
	                e.printStackTrace();
	            } catch (InterruptedException e) {
	                // TODO: handle me
	                e.printStackTrace();
	            } finally {
	                running = false;
	                if (reader != null) {
	                    try {
	                        reader.close();
	                    } catch (IOException ignore) {
	                    }
	                }
	            }
	
	        }
	    };
	    t.start();
	}
}
