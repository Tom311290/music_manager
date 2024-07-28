package com.codex.ytdl.views.ytdl;

import com.codex.ytdl.views.MainLayout;
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

    public YTDLView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        TextField textField = new TextField();
        Button buttonPrimary = new Button();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        TextField textField2 = new TextField();
        Button buttonPrimary2 = new Button();
        TextField textField3 = new TextField();
        TextField textField4 = new TextField();
        Button buttonPrimary3 = new Button();
        TextArea textArea = new TextArea();
        ProgressBar progressBar = new ProgressBar();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        textField.setLabel("Dowload destination");
        layoutRow.setAlignSelf(FlexComponent.Alignment.CENTER, textField);
        textField.setWidth("100%");
        buttonPrimary.setText("Select");
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary);
        buttonPrimary.setWidth("min-content");
        buttonPrimary.setHeight("36px");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        textField2.setLabel("YouTubeDL.exe location");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.CENTER, textField2);
        textField2.getStyle().set("flex-grow", "1");
        buttonPrimary2.setText("Select");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary2);
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        textField3.setLabel("Download song name format");
        textField3.setWidth("100%");
        textField4.setLabel("Download from URL");
        textField4.setWidth("100%");
        buttonPrimary3.setText("Start");
        buttonPrimary3.setWidth("100%");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        textArea.setLabel("Text area");
        textArea.setWidth("100%");
        textArea.getStyle().set("flex-grow", "1");
        progressBar.setValue(0.5);
        getContent().add(layoutRow);
        layoutRow.add(textField);
        layoutRow.add(buttonPrimary);
        getContent().add(layoutRow2);
        layoutRow2.add(textField2);
        layoutRow2.add(buttonPrimary2);
        getContent().add(textField3);
        getContent().add(textField4);
        getContent().add(buttonPrimary3);
        getContent().add(textArea);
        getContent().add(progressBar);
    }
}
