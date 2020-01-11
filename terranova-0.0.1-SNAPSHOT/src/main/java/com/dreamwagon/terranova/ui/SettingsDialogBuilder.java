/**
 * Copyright 2020 DREAMWAGON LLC
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted 
 *  provided that the following conditions are met:
 *
 *		1. Redistributions of source code must retain the above copyright notice, this list of 
 *		   conditions and the following disclaimer.
 *
 *   	2. Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   	   conditions and the following disclaimer in the documentation and/or other materials 
 *         provided with the distribution.
 *
 *	   	3. Neither the name of the copyright holder nor the names of its contributors may be used to 
 *		   endorse or promote products derived from this software without specific prior written permission.
 *
 *		THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 *		IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 *		FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 *		FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *		LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 *		HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *		(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 *		OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.dreamwagon.terranova.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.controlsfx.control.RangeSlider;

import com.dreamwagon.terranova.model.TerrainTexture;
import com.dreamwagon.terranova.model.ToggleTexture;
import com.dreamwagon.terranova.util.ImageUtil;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.texture.Texture;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Helper to build JFX dialog from a settings annotated settings class
 * 
 * @author J. Demarco
 *
 */
public class SettingsDialogBuilder<T extends Settings> {

	public static double LABEL_WIDTH = 130;
	
	Alert alertDialog = new Alert(AlertType.ERROR); 
	
	public Dialog<Void> buildSettingDialog(T settingsInstance, Class<T> c, int dialogWidth, int dialogHeight) {

		//Clear any field wrappers from the settings instance. We don't
		//care about those fields any more
		settingsInstance.getFieldWrapperList().clear();
		
		//Create the dialog
		Dialog<Void> _dlg = new Dialog<Void>();
		_dlg.setHeaderText(settingsInstance.getName());
		
		DialogPane dp = _dlg.getDialogPane();
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		
		//Call before build settings to allow caller to
		//build part of the dialog before settings
		settingsInstance.beforeBuildSettings(_dlg, vBox);
		
		List<Field> settingsFields = getFieldsWithSettingAnnotations(settingsInstance, c);
		for (Field f : settingsFields)
		{
			Setting aSetting = f.getAnnotation(Setting.class);
			//Build inputs for this dialog
			try {
				FieldWrapper fw= buildInput(vBox, aSetting, settingsInstance, f);
				settingsInstance.getFieldWrapperList().add(fw);
			} catch (Exception e) {
				alertDialog.setContentText("Unable to build Input Type: " + aSetting.settingType() +
											"Error: "+ e.getMessage());
				alertDialog.show();
				e.printStackTrace();
			}
		}
		
		//Call before build settings to allow caller to
		//build part of the dialog after settings
		settingsInstance.afterBuildSettings(_dlg, vBox);

		//Use scrollpane
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefSize(dialogWidth, dialogHeight);
		scrollPane.setContent(vBox);
		 
	    dp.setContent(scrollPane);
	    dp.setMinSize(dialogWidth, dialogHeight);
	    //dp.getButtonTypes().add(ButtonType.CLOSE);
	    //dp.getButtonTypes().add(ButtonType.OK);
	    
	    _dlg.setWidth(dialogWidth);
	    _dlg.setHeight(dialogHeight);
	    
	    _dlg.initModality(Modality.NONE); 
	    _dlg.getDialogPane().getButtonTypes() .add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
	    _dlg.setOnCloseRequest(event -> _dlg.hide());
	    _dlg.show();
	    
	    return _dlg;
	}
	
	/**
	 * 
	 * @param settingsInstance
	 * @param c
	 * @return
	 */
	public List<Field> getFieldsWithSettingAnnotations(T settingsInstance, Class<T> c)
	{
		return FieldUtils.getFieldsListWithAnnotation(c, Setting.class);
	}
	
	/**
	 * 
	 * @param stackPane
	 * @param aSetting
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public FieldWrapper buildInput(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		FieldWrapper fieldWrapper = null;
		
		if (aSetting.settingType().getIndex() <= SettingType.LONG.getIndex())
		{
			fieldWrapper = buildBasicTextField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() == SettingType.RANGED_FLOAT.getIndex())
		{
			fieldWrapper = buildSliderField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() >= SettingType.PREDEFINED_INTEGER_LIST.getIndex() &&
				aSetting.settingType().getIndex() <= SettingType.PREDEFINED_COLOR_LIST.getIndex())
		{
			fieldWrapper = buildSelectField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() >= SettingType.TWO_VAL_RANGED_INTEGER.getIndex() &&
				aSetting.settingType().getIndex() <= SettingType.TWO_VAL_RANGED_VECTOR3F.getIndex())
		{
			fieldWrapper = buildRangefSliderField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() == SettingType.BOOLEAN.getIndex())
		{
			fieldWrapper = buildCheckboxField(vBox, aSetting, settingsInstance, field);
		}	
		else if (aSetting.settingType().getIndex() == SettingType.TEXTURE.getIndex())
		{
			fieldWrapper = buildTextureLoaderField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() == SettingType.TOGGLE_TEXTURE.getIndex())
		{
			fieldWrapper = buildToggleTextureLoaderField( vBox,  aSetting, settingsInstance, field);
		}
		else if (aSetting.settingType().getIndex() == SettingType.TERRAIN_TEXTURE.getIndex())
		{
			fieldWrapper = buildTerrainTextureLoaderField( vBox,  aSetting, settingsInstance, field);
		}
		return fieldWrapper;
	}
	
	/**
	 * 
	 * @param stackPane
	 * @param aSetting
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public FieldWrapper buildBasicTextField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{	
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		TextField textField = new TextField();
		FieldWrapper fieldWrapper= new FieldWrapper(field, textField);
		//textField.setUserData();
		HBox hb = new HBox();
		Object value = field.get(settingsInstance);
		textField.setText(value.toString());
		hb.getChildren().addAll(label1, textField);
		hb.setSpacing(10);
		
		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	
	public FieldWrapper buildSliderField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		
		Object value = field.get(settingsInstance);
	    Slider slider = new Slider(aSetting.floatRangeMin(), aSetting.floatRangeMax(), (Float)value);  
		FieldWrapper fieldWrapper= new FieldWrapper(field, slider);

		Label sliderValue = new Label(Double.toString(slider.getValue()));
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	sliderValue.setText(String.format("%.2f", new_val));
            }
        });
		
		hb.getChildren().addAll(label1, slider, sliderValue);
		hb.setSpacing(10);
		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	
	public FieldWrapper buildSelectField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		ChoiceBox<Object> choiceBox = createChoiceBox(aSetting);
		FieldWrapper fieldWrapper= new FieldWrapper(field, choiceBox);
		
		Object value = field.get(settingsInstance);
		choiceBox.setValue(value.toString());		
		hb.getChildren().addAll(label1, choiceBox);
		hb.setSpacing(10);
		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	public FieldWrapper buildRangefSliderField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		RangeSlider rangedSlider = new RangeSlider(aSetting.floatRangeMin(), aSetting.floatRangeMax(), 
					aSetting.floatRangeMin(), aSetting.floatRangeMax());
		rangedSlider.setShowTickMarks(true); 
		rangedSlider.setShowTickLabels(true); 
		rangedSlider.setBlockIncrement(1);
		FieldWrapper fieldWrapper= new FieldWrapper(field, rangedSlider);
		
		Object value = field.get(settingsInstance);
		
		hb.getChildren().addAll(label1, rangedSlider);
		hb.setSpacing(10);
		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	
	public FieldWrapper buildCheckboxField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		Boolean value = (Boolean)field.get(settingsInstance);
		CheckBox checkBox = new CheckBox("");
		checkBox.setSelected(value);
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	try {
					field.set(settingsInstance, newValue);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
		    }
		});
		
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);

		FieldWrapper fieldWrapper= new FieldWrapper(field, checkBox);

		hb.getChildren().addAll(label1, checkBox);
		hb.setSpacing(10);
		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	
	public FieldWrapper buildTextureLoaderField(VBox vBox, Setting aSetting, T settingsInstance, Field field) throws IllegalArgumentException, IllegalAccessException
	{
		Texture txValue = (Texture) field.get(settingsInstance);
		
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		ImageView imageView = new ImageView();
		ImageUtil.refreshImageView(imageView, txValue);
		
		Button importButton = new Button("Import");
		importButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	 	
		        	//Get the stage
	        		Node sourceNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.MAIN_MENU_BAR_ID);
	        		Window stage = sourceNode.getScene().getWindow();
		        	File file = TerranovaUiManager.TEXTURE_FILE_CHOOSER.showOpenDialog(stage);
		            if (file != null) {
		            	
		            	try {
							Texture newTexture = ImageUtil.loadTextureFile(file);
							field.set(settingsInstance, newTexture);
							ImageUtil.refreshImageView(imageView, newTexture);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						} 	
		            }
	        }
	    });
		
		hb.getChildren().addAll(label1, imageView, importButton);
		hb.setSpacing(10);
		
		FieldWrapper fieldWrapper= new FieldWrapper(field, imageView);

		vBox.getChildren().add(hb);
		
		return fieldWrapper;
	}
	
	public FieldWrapper buildToggleTextureLoaderField(VBox vBox, Setting aSetting, T settingsInstance, Field field) 
			throws IllegalArgumentException, IllegalAccessException
	{
		ToggleTexture toggle = (ToggleTexture) field.get(settingsInstance);
		
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		ImageView imageView = new ImageView();
		ImageUtil.refreshImageView(imageView, toggle.texture);
		
		Button importButton = new Button("Import");
		importButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	 	
		        	//Get the stage
	        		Node sourceNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.MAIN_MENU_BAR_ID);
	        		Window stage = sourceNode.getScene().getWindow();
		        	File file = TerranovaUiManager.TEXTURE_FILE_CHOOSER.showOpenDialog(stage);
		            if (file != null) {
		            	
		            	try {
							Texture newTexture = ImageUtil.loadTextureFile(file);
							toggle.texture = newTexture;
							ImageUtil.refreshImageView(imageView, newTexture);
						} catch (Exception e) {
							alertDialog.setContentText("Unable to load Texture: " + e.getMessage());
							alertDialog.show();
							e.printStackTrace();
						} 
		            }
	        }
	    });
		VBox buttonBox = new VBox();
		CheckBox activeChk = new CheckBox("active");
		activeChk.setSelected(toggle.active);
		
		activeChk.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	toggle.active = newValue;
		    }
		});
		buttonBox.getChildren().addAll(importButton, activeChk);
		hb.getChildren().addAll(label1, imageView, buttonBox);
		hb.setSpacing(10);
		
		FieldWrapper fieldWrapper= new FieldWrapper(field, imageView);
		vBox.getChildren().add(hb);
		return fieldWrapper;
	}
	
	public FieldWrapper buildTerrainTextureLoaderField(VBox vBox, Setting aSetting, T settingsInstance, Field field) 
			throws IllegalArgumentException, IllegalAccessException
	{
		TerrainTexture terrainTxr = (TerrainTexture) field.get(settingsInstance);
		
		HBox hb = new HBox();
		Label label1 = new Label(aSetting.displayName());
		label1.setMinWidth(LABEL_WIDTH);
		ImageView imageView = new ImageView();
		if (terrainTxr.texture!=null)
		{
			ImageUtil.refreshImageView(imageView, terrainTxr.texture);
		}
		
		Button importButton = new Button("Import");
		importButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	 	
		        	//Get the stage
	        		Node sourceNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.MAIN_MENU_BAR_ID);
	        		Window stage = sourceNode.getScene().getWindow();
		        	File file = TerranovaUiManager.TEXTURE_FILE_CHOOSER.showOpenDialog(stage);
		            if (file != null) {
		            	
		            	try {
							Texture newTexture = ImageUtil.loadTextureFile(file);
							terrainTxr.texture = newTexture;
							ImageUtil.refreshImageView(imageView, newTexture);
						} catch (Exception e) {
							alertDialog.setContentText("Unable to load Texture: " + e.getMessage());
							alertDialog.show();
							e.printStackTrace();
						} 
		            }
	        }
	    });
		
		TextField txScaleTextField = new TextField();
		txScaleTextField.setText(String.valueOf(terrainTxr.scale));
		txScaleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
		    System.out.println("textfield changed from " + oldValue + " to " + newValue);
		});
		
		VBox buttonBox = new VBox();
		CheckBox activeChk = new CheckBox("active");
		activeChk.setSelected(terrainTxr.active);
		activeChk.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	terrainTxr.active = newValue;
		    }
		});
		buttonBox.getChildren().addAll(importButton, activeChk, txScaleTextField);
		hb.getChildren().addAll(label1, imageView, buttonBox);
		hb.setSpacing(10);
		
		FieldWrapper fieldWrapper= new FieldWrapper(field, imageView);
		vBox.getChildren().add(hb);
		return fieldWrapper;
	}
	
	
	
	public ChoiceBox<Object> createChoiceBox(Setting aSetting)
	{
		ChoiceBox<Object> choiceBox = null;
		
		if (aSetting.settingType().getIndex() >=  SettingType.PREDEFINED_INTEGER_LIST.getIndex() &&
			aSetting.settingType().getIndex() <=  SettingType.PREDEFINED_COLOR_LIST.getIndex()){
			ObservableList<Object> choices = FXCollections.observableArrayList(aSetting.predefinedListValues());
			choiceBox = new ChoiceBox<Object>(choices);
		}
		else{
			//Should probably throw exception and popup an error dialog
		}

		return choiceBox;
	}
    

}
