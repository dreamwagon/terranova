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
package com.dreamwagon.terranova.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dreamwagon.terranova.settings.NoiseLayerSettings;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;
import com.dreamwagon.terranova.ui.SettingsDialogBuilder;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.dreamwagon.terranova.util.ImageUtil;
import com.jme3.texture.Texture;

import io.tlf.jme.jfx.JavaFxUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * @FastNoise heightmap generator implemntation.
 * 
 * @author J. Demarco
 *
 */
public class FastNoiseHeightmapGenerator extends AbstractSettingsDialog implements Generator<Texture>{

	private List<NoiseLayerSettings> noiseLayers = new ArrayList<>();
    private Map<String, Dialog<Void>> noiseLayerSettingsDialogMap = new HashMap<String, Dialog<Void>>();
    
	@Setting(displayName="Heightmap Size", settingType = SettingType.PREDEFINED_INTEGER_LIST, 
			predefinedListValues = {"65", "129", "257", "513", "1025", "2049", "4097", "8193", "16385"})
	public Integer heightmapSize = 513;
		
	private ImageView heightmapPreview = new ImageView();
	private Texture heightmapTexture = null;
	
	VBox layerListVBox;
	
	public FastNoiseHeightmapGenerator() {
		noiseLayers.add( new NoiseLayerSettings());
	}
	
	@Override
	public String getName() {
		return FastNoiseHeightmapGenerator.class.getCanonicalName();
	}
	
	@Override
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node) {
		VBox vBox = (VBox)node;
		
		vBox.getChildren().addAll(heightmapPreview);
		
		HBox upperButtonHBox = new HBox();
		Button addNoiseLayerButton = new Button("Add Noise Layer");
		addNoiseLayerButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	noiseLayers.add( new NoiseLayerSettings());
	        	buildLayerView();
	        }
	    });
		Button resetNoiseLayerButton = new Button("Reset Noise Layers");
		resetNoiseLayerButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	noiseLayers.clear();
	        	noiseLayers.add( new NoiseLayerSettings());
	        	buildLayerView();
	        }
	    });
		upperButtonHBox.getChildren().addAll(addNoiseLayerButton, resetNoiseLayerButton);
		vBox.getChildren().addAll(upperButtonHBox);
	}
	
	@Override
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node) {
		VBox vBox = (VBox)node;
		layerListVBox = new VBox();
		vBox.getChildren().add(layerListVBox);
		buildLayerView();
		
		HBox buttonHbox = new HBox();
		buttonHbox.setSpacing(10);
		Button genHeightmapButton = new Button("Generate Height Map");
		genHeightmapButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	if (noiseLayers.size() > 0) {
		        	applySettings();
		        	heightmapTexture= generate();
		        	ImageUtil.refreshImageView(heightmapPreview, heightmapTexture, 128, 128);
	        	}
	        	else {
	        		//take warning
	        	}
	        }
	    });
		Button saveHeightmapButton = new Button("Save");
		saveHeightmapButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
			    FileChooser fileChooser = new FileChooser();
			    fileChooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("PNG File", "*.png")
		            );
			    Node menuNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.HBG_MENU_BAR_ID);
	    		Window stage = menuNode.getScene().getWindow();
			    File selectedFile = fileChooser.showSaveDialog(stage);
			
			    if(selectedFile == null){
			         //No Directory selected
			    }else{
			    	if (heightmapTexture != null) {
			    		ImageUtil.saveTexture(heightmapTexture, selectedFile.getAbsolutePath());
			    	}
			    }
	        }
	    });
		buttonHbox.getChildren().addAll(genHeightmapButton, saveHeightmapButton);
		vBox.getChildren().addAll(buttonHbox);
	}

	public void buildLayerView() {
		layerListVBox.getChildren().clear();
		resetSettingsDialog();
		for (NoiseLayerSettings nls : noiseLayers) {
			HBox hbox = new HBox();
			hbox.setSpacing(10);
			Text noiseLayerText = new Text(nls.getName());
			Button noiseLayerSettingsButton = new Button("Settings");
			noiseLayerSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
		        @Override
		        public void handle(ActionEvent event) {
		        	Dialog<Void> fastNoiseSettingsDialog = noiseLayerSettingsDialogMap.get(nls.getName());
		        	if (null == fastNoiseSettingsDialog){
		        		SettingsDialogBuilder<NoiseLayerSettings> dialogBuilder = new SettingsDialogBuilder<NoiseLayerSettings>();
		        		fastNoiseSettingsDialog = dialogBuilder.buildSettingDialog(nls, NoiseLayerSettings.class, 480, 660);
		        		noiseLayerSettingsDialogMap.put(nls.getName(), fastNoiseSettingsDialog);
		        	}
		        	else{
		        		if (!fastNoiseSettingsDialog.isShowing()){
		        			fastNoiseSettingsDialog.show();
		        		}
		        	}
		        }
		    });
			hbox.getChildren().addAll(noiseLayerText, noiseLayerSettingsButton);
			layerListVBox.getChildren().add(hbox);
		}	
	}
	
	private void resetSettingsDialog(){
		for  (Entry<String, Dialog<Void>> dlg : noiseLayerSettingsDialogMap.entrySet()) {
			dlg.getValue().close();
		}
		noiseLayerSettingsDialogMap.clear();
	}
	@Override
	public Texture generate() {
		//Apply Dialog settings
		for (NoiseLayerSettings nls : noiseLayers) {
			nls.applySettings();
		}
		Texture value = noiseLayers.get(0).generate().generateTexture(heightmapSize);
		float layerWeight = noiseLayers.get(0).layerWeight;
		for (int i=1; i< noiseLayers.size(); i++) {
			if (i>1) {
				layerWeight = 1;
			}
			Texture next = noiseLayers.get(i).generate().generateTexture(heightmapSize);
			float nextLayerWeight = noiseLayers.get(i).layerWeight;
			value = ImageUtil.combineTextures( value, layerWeight, next, nextLayerWeight);
		}
		
		return value;
	}

}
