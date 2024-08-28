package com.codex.musicmanager.views.ytdl;

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

import ch.qos.logback.core.Layout;

@PageTitle("YTDL")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class YTDLView extends Composite<VerticalLayout> {

    public YTDLView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setHeightFull();
        
        createYtdlExeLocationLayout();
        createDownloadFromURLLayout();
        createDestinationLocationLayout();
        createSongTextFormatLayout();
        createStartButtonLayout();
        createConsoleLogTextArea();
    }
    
    private void createProgressBarLayout() {     
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(0.5);
        
        getContent().add(progressBar);
    }
    
    private void createStartButtonLayout() {
    	Button startButton = new Button();
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
    
    private void createSongTextFormatLayout() {
        TextField songNameFormatTextField = new TextField();
        songNameFormatTextField.getStyle().setPadding("0");
        songNameFormatTextField.setLabel("Download song name format");
        songNameFormatTextField.setWidth("100%");
        
        getContent().add(songNameFormatTextField);

    }
    
    private void createDownloadFromURLLayout() {
    	 TextField downloadFromURLTextField = new TextField();
    	 downloadFromURLTextField.getStyle().setPadding("0");
         downloadFromURLTextField.setLabel("Download from URL");
         downloadFromURLTextField.setWidth("100%");
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
}
