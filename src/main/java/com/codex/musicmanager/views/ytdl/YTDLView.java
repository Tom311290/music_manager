package com.codex.musicmanager.views.ytdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.codex.musicmanager.constants.YTDLConstants;
import com.codex.musicmanager.utils.ExternalProcessStarter;
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
	private final static Logger logger = Logger.getLogger(YTDLView.class.getName());
	Process process = null;

	Properties prop = null;
	String ytdlExeLocationProperty = "ytdlExeLocationProperty";
	String lastDownloadedURLProperty = "lastDownloadedURLProperty";
	String lastDownloadToLocationProperty = "lastDownloadToLocationProperty";
	String songNameFormatProperty = "songNameFormatProperty";
	
	private Button startButton;
	
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
    
    //Get ExecutorService from Executors utility class, thread pool size is 10
    ExecutorService executorService;
    private void initData() {
    	//executorService = Executors.newFixedThreadPool(5);
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
				logger.log(Level.WARNING, "No init file! Creating one: " + initFile.getAbsolutePath());
	
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
			logger.log(Level.SEVERE, "Unable to load last settings!", e);
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
        
        getContent().add(consoleLogTextArea);
    }
    
    private void createSongNameFormatLayout() {
        TextField songNameFormatTextField = new TextField();
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
    	 TextField downloadFromURLTextField = new TextField();
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

        TextField downloadDestinationTextField = new TextField();
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
        
        TextField ytdlExeLocationTextField = new TextField();
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
			logger.log(Level.WARNING, "Youtube-dl location not set! Unable to update application!");
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
	
}
