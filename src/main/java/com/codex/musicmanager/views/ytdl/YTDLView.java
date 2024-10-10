package com.codex.musicmanager.views.ytdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codex.musicmanager.constants.YTDLConstants;
import com.codex.musicmanager.utils.ExternalProcessStarter;
import com.codex.musicmanager.utils.LogFileTailer;
import com.codex.musicmanager.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("YTDL")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class YTDLView extends Composite<VerticalLayout> {
	private static final long serialVersionUID = -5744399912356346832L;
	
	private final static Logger logger = LogManager.getLogger(YTDLView.class.getName());
	Process process = null;

	Properties prop = null;
	String ytdlExeLocationProperty = "ytdlExeLocationProperty";
	String lastDownloadedURLProperty = "lastDownloadedURLProperty";
	String lastDownloadToLocationProperty = "lastDownloadToLocationProperty";
	String songNameFormatProperty = "songNameFormatProperty";
	
	private Button startButton;
   
	//Get ExecutorService from Executors utility class, thread pool size is 10
    ExecutorService executorService;
	
    private TextField ytdlExeLocationTextField;
	private TextField songNameFormatTextField;
	private TextField downloadFromURLTextField;
	private TextField downloadDestinationTextField;
	
    public YTDLView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setHeightFull();
        
        initData();
        
        createYtdlExeLocationLayout();
        createDownloadFromURLLayout();
        createDestinationLocationLayout();
        createSongNameFormatLayout();
        createStartButtonLayout();
        createConsoleLogTextArea();
    }
    

    private void initData() {
		executorService = Executors.newFixedThreadPool(4,new ThreadFactory() {
											            public Thread newThread(Runnable r) {
											                Thread t = Executors.defaultThreadFactory().newThread(r);
											                t.setDaemon(true);
											                return t;
											            }
											        });
									        
			
		logger.info("========================================================================================================");
		logger.info("Starting application...");
	
		try {
			prop = new Properties();
			File initFile = null;
			InputStream input = null;
	
			//logScreen.appendText("Initialization of application started... \n");
			logger.info("Initialization of application started...");
	
			try {
				initFile = new File(YTDLConstants.YTDL_TEMP_FOLDER, YTDLConstants.INIT_FILE);
				input = new FileInputStream(initFile);
	
			}catch (FileNotFoundException e){
				//logScreen.appendText("No init file! Creating one: " + initFile.getAbsolutePath()+"\n");
				logger.warn("No init file! Creating one: " + initFile.getAbsolutePath());
	
				initFile.getParentFile().mkdirs();
				initFile.createNewFile();
				input = new FileInputStream(initFile);
			}
			
			logger.info("Loading app properties from: " + initFile.getAbsolutePath());
			prop.load(input);
			logger.info("Loading of app properties finished!");
			logger.info("Initialization of application finished!");

			updateYTDL();
		}catch (IOException e) {
			//logScreen.appendText(YTDLConstants.FATAL_ERROR_MESSAGE);
			logger.error("Unable to load last settings!", e);
			e.printStackTrace();
		}
		
	}

	private void createProgressBarLayout() {     
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(0.5);
        
        getContent().add(progressBar);
    }
    
    private void createStartButtonLayout() {
    	startButton = new Button();
        startButton.setText("Start");
        startButton.setWidth("100%");
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        startButton.addClickListener(event -> startDownload());
        
        getContent().add(startButton);
    }
    
    private void createConsoleLogTextArea() {
        TextArea consoleLogTextArea = new TextArea();
        consoleLogTextArea.getStyle().setPadding("0");
        consoleLogTextArea.setLabel("Log");
        consoleLogTextArea.setWidth("100%");
        consoleLogTextArea.getStyle().set("flex-grow", "1");
        consoleLogTextArea.setReadOnly(true);
        consoleLogTextArea.setHeightFull();
        
        (new LogFileTailer()).runTailer(consoleLogTextArea);
        
        getContent().add(consoleLogTextArea);
    }
    
    private void createSongNameFormatLayout() {
        songNameFormatTextField = new TextField();
        songNameFormatTextField.getStyle().setPadding("0");
        songNameFormatTextField.setLabel("Download song name format");
        songNameFormatTextField.setWidth("100%");
        
        String songNameFormatValue = getPropertyIfExists(songNameFormatProperty);
        songNameFormatTextField.setValue(getPropertyIfExists(songNameFormatProperty));//%(title)s.%(ext)s
		if(songNameFormatValue == null || songNameFormatValue.isEmpty()) {
			songNameFormatTextField.setValue("%(title)s.%(ext)s");
		}else {
			songNameFormatTextField.setValue(songNameFormatValue);
		}
		
        getContent().add(songNameFormatTextField);

    }
    
    private void createDownloadFromURLLayout() {
    	 downloadFromURLTextField = new TextField();
    	 downloadFromURLTextField.getStyle().setPadding("0");
         downloadFromURLTextField.setLabel("Download from URL");
         downloadFromURLTextField.setWidth("100%");
         downloadFromURLTextField.setValue(getPropertyIfExists(lastDownloadedURLProperty)); 
         getContent().add(downloadFromURLTextField);
    }

	private void createDestinationLocationLayout() {
		HorizontalLayout downloadDestinationLayout = new HorizontalLayout();
        downloadDestinationLayout.setWidthFull();
        downloadDestinationLayout.addClassName(Gap.MEDIUM);
        downloadDestinationLayout.setWidth("100%");
        downloadDestinationLayout.setHeight("min-content");

        downloadDestinationTextField = new TextField();
		downloadDestinationTextField.getStyle().setPadding("0");
        downloadDestinationTextField.setLabel("Dowload destination");
        downloadDestinationTextField.setWidth("100%");
        downloadDestinationTextField.setValue(getPropertyIfExists(lastDownloadToLocationProperty));
        downloadDestinationLayout.setAlignSelf(FlexComponent.Alignment.CENTER, downloadDestinationTextField);
        downloadDestinationLayout.add(downloadDestinationTextField);

        Button downloadDestinationSelectBtn = createSelectButton();
        downloadDestinationLayout.setAlignSelf(FlexComponent.Alignment.END, downloadDestinationSelectBtn);
        downloadDestinationLayout.add(downloadDestinationSelectBtn);

        getContent().setFlexGrow(1.0, downloadDestinationLayout);
        getContent().add(downloadDestinationLayout);
		
	}

	private void createYtdlExeLocationLayout() {
		HorizontalLayout ytdlExeLocationLayout = new HorizontalLayout();
        ytdlExeLocationLayout.setWidthFull();
        ytdlExeLocationLayout.addClassName(Gap.MEDIUM);
        ytdlExeLocationLayout.setWidth("100%");
        ytdlExeLocationLayout.setHeight("min-content");
        
        ytdlExeLocationTextField = new TextField();
        ytdlExeLocationTextField.getStyle().setPadding("0");
        ytdlExeLocationTextField.setLabel("YouTubeDL.exe location");
        ytdlExeLocationTextField.setWidth("100%");
        ytdlExeLocationTextField.setValue(getPropertyIfExists(ytdlExeLocationProperty));
        
        ytdlExeLocationLayout.add(ytdlExeLocationTextField);
        ytdlExeLocationLayout.setAlignSelf(FlexComponent.Alignment.CENTER, ytdlExeLocationTextField);
        
        Button ytdlExeLocationSelectBtn = createSelectButton();
        ytdlExeLocationLayout.setAlignSelf(FlexComponent.Alignment.END, ytdlExeLocationSelectBtn);
        ytdlExeLocationLayout.add(ytdlExeLocationSelectBtn);
      
        getContent().setFlexGrow(1.0, ytdlExeLocationLayout);
        getContent().add(ytdlExeLocationLayout);
        
	}
	
	private Button createSelectButton() {
		Button button = new Button();
		button.setText("Select");
		addButtonStyle(button);
		
		return button;
	}

	private void addButtonStyle(Button button) {
		button.setText("Select");
		button.setWidth("min-content");
		button.getStyle().setMargin("0");
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	}
	
	private String getPropertyIfExists(String propertyName) {
		String propertyValue = prop.getProperty(propertyName);
		if(propertyValue != null && !propertyValue.equals("")) {
			return propertyValue;
		}
		return "";
	}
	
	public void updateYTDL() {
		logger.info("Checking for youtube-dl updates! Please wait...");
		String ytdlExeLocation = getPropertyIfExists(ytdlExeLocationProperty);
		if(ytdlExeLocation != null && !ytdlExeLocation.isEmpty()){
			ProcessBuilder processBuilder = new ProcessBuilder(ytdlExeLocation, "-U", "--verbose");
			startProcess(processBuilder, "Update started...", "Update finished!");
		}else {
			logger.warn("Youtube-dl location not set! Unable to update application!");
		}

	}
	
	private void startProcess(ProcessBuilder processBuilder, String startProcMessage, String endProcMessage) {
		ExternalProcessStarter extProcStarter = new ExternalProcessStarter();
		extProcStarter.setLogger(logger);
		extProcStarter.setProcessBuilder(processBuilder);
		extProcStarter.setStartProcessMessage(startProcMessage);
		extProcStarter.setEndProcessMessage(endProcMessage);
		executorService.submit(extProcStarter);
	}
	
	public void startDownload() {
		try {
			String message = "";

			if(!message.equals("")){
				logger.info("---------------------------------------------------------------------");
				logger.info("Please take care of errors:" + message);
				logger.info("---------------------------------------------------------------------");
				return;
			}

			logger.info("Download started! Please wait...");
			String downloadFilePatt = "\"" +downloadDestinationTextField.getValue() + "\\" + songNameFormatTextField.getValue() + "\"";

			logger.info("Download to:" + downloadFilePatt);
			ProcessBuilder processBuilder = new ProcessBuilder(ytdlExeLocationTextField.getValue(), 
					"--extract-audio", 
					"--audio-format mp3",
					"--audio-quality 0",
					"-o", 
					downloadFilePatt, 
					downloadFromURLTextField.getValue());
			
			startProcess(processBuilder, "Download started...", "Download finished!");

			logger.info("Saving last setup...");
			prop.setProperty(ytdlExeLocationProperty, ytdlExeLocationTextField.getValue());
			prop.setProperty(lastDownloadedURLProperty, downloadFromURLTextField.getValue());
			prop.setProperty(lastDownloadToLocationProperty, downloadDestinationTextField.getValue());
			prop.setProperty(songNameFormatProperty, songNameFormatTextField.getValue());
			prop.store(new FileOutputStream(YTDLConstants.YTDL_TEMP_FOLDER + YTDLConstants.INIT_FILE), null);

			logger.info("Saving finished!");

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
