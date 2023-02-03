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
package com.dreamwagon.terranova.settings;

import java.io.File;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.dreamwagon.terranova.model.ToggleTexture;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * JME Terrain Settings Dialog
 * 
 * @author J. Demarco
 *
 */
public class JMETerrainSettings extends AbstractSettingsDialog{

	public static final String TERRAIN_MAT_WIREFRAME="Wireframe";
	
	public static final String TERRAIN_MAT_BASIC="Terrain Basic";
	
	public static final String TERRAIN_MAT_LIGHTING="Terrain Lighting";
	
	public TerrainTextureSettings terrainTextureSettings;
	
	public Texture terrainHeightmapTexure;
	
	@Setting(displayName="Terrain Alpha Map 1", settingType = SettingType.TEXTURE)
	public Texture terrainAlphaMapTexure1;
	
	@Setting(displayName="Terrain Alpha Map 2", settingType = SettingType.TOGGLE_TEXTURE)
	public ToggleTexture terrainAlphaMapTexure2;
	
	@Setting(displayName="Terrain Alpha Map 3", settingType = SettingType.TOGGLE_TEXTURE)
	public ToggleTexture terrainAlphaMapTexure3;
	
	HBox terrainPreviewHbox = new HBox();
	ImageView heightMapImageView = new ImageView();
	
	@Setting(displayName="Patch Size", settingType = SettingType.INTEGER)
	public Integer patchSize = 64;
	
	@Setting(displayName="Total Size", settingType = SettingType.PREDEFINED_INTEGER_LIST, 
			predefinedListValues = {"65", "129", "257", "513", "1025", "2049", "4097", "8193", "16385"})
	public Integer totalSize = 513;
	
	@Setting(displayName="Height Scale", settingType = SettingType.RANGED_FLOAT, floatRangeMin=.01f, floatRangeMax=1f)
	public Float heightScale = 1f;
		
	@Setting(displayName="Shadow Mode", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"Off", "CastAndReceive", "Receive", "Inherit"})
	public String shadowMode = "Receive";
	
	@Setting(displayName="Terrain Material", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {TERRAIN_MAT_WIREFRAME, TERRAIN_MAT_BASIC, TERRAIN_MAT_LIGHTING})
	public String terrainMaterial = TERRAIN_MAT_WIREFRAME;
	
	@Setting(displayName="Smooth Terrain", settingType = SettingType.BOOLEAN)
	public Boolean smooth = Boolean.FALSE;
	
	@Setting(displayName="Smooth Amount", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=1f)
	public Float smoothAmount = 1f;
	
	@Setting(displayName="Smooth Radius", settingType = SettingType.INTEGER)
	public Integer smoothRadius = 1;
	
	public JMETerrainSettings(TerrainTextureSettings terrainTextureSettings)
	{
		this.terrainTextureSettings = terrainTextureSettings;
		terrainPreviewHbox.setId("terrainPreviewHbox");
		heightMapImageView.setId("terrainPreviewImageView");
		
		terrainAlphaMapTexure1 = TerranovaBaseAssetManager.DEFAULT_TERRAIN_ALPHAMAP;
		terrainAlphaMapTexure2 = new ToggleTexture(TerranovaBaseAssetManager.DEFAULT_BLACK_TEXTURE);
		terrainAlphaMapTexure3 = new ToggleTexture(TerranovaBaseAssetManager.DEFAULT_BLACK_TEXTURE);
	}
	
	@Override
	public String getName() {
		return JMETerrainSettings.class.getCanonicalName();
	}
	
	@Override
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node)
	{
		VBox vbox = (VBox)node;
		
		refreshTerrainPreviewImageView();
		
		Label label1 = new Label("Height Map");
		label1.setMinWidth(SettingsDialogBuilder.LABEL_WIDTH);
		Button importButton = new Button("Import");
		importButton.setOnAction(importHeightmapHandler);
		
		terrainPreviewHbox.getChildren().clear();
		terrainPreviewHbox.getChildren().addAll(label1, heightMapImageView, importButton);
		terrainPreviewHbox.setSpacing(10);
		
		if (!vbox.getChildren().contains(terrainPreviewHbox))
		{
			vbox.getChildren().add(terrainPreviewHbox);
		}
	}
	
	@Override
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node)
	{
		VBox vbox = (VBox)node;
		
		VBox buttonContainerVbox = new VBox();
		
		//Build Terrain and materials and lighting
		Button rebuildAllButton = new Button("Build All");
		rebuildAllButton.setOnAction(buildAllHandler);
		
		//Apply materials
		Button applyMaterialsButton = new Button("Apply Material and Light Settings");
		applyMaterialsButton.setOnAction(applyUpdatedMaterialsAndLightingHandler);
		
		vbox.getChildren().addAll(rebuildAllButton, applyMaterialsButton);
		
		vbox.getChildren().add(buttonContainerVbox);
	}
	
	public void refreshTerrainPreviewImageView()
	{
		if (null == terrainHeightmapTexure)
		{
			terrainHeightmapTexure = TerranovaBaseAssetManager.DEFAULT_TERRAIN_HEIGHTMAP_TEXTURE;
		}
		
		Image terrainImage = ImageUtil.convertJmeTextureToJfxImage(terrainHeightmapTexure);
		heightMapImageView.setImage(terrainImage);
		heightMapImageView.setFitHeight(64);
		heightMapImageView.setFitWidth(64);
		
	}
	
    EventHandler<ActionEvent> importHeightmapHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
 	
	        	//Get the stage
        		Node sourceNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.MAIN_MENU_BAR_ID);
        		Window stage = sourceNode.getScene().getWindow();
	        	File file = TerranovaUiManager.TEXTURE_FILE_CHOOSER.showOpenDialog(stage);
	            if (file != null) {
	            	
	            	loadHeightmapFileLocal(file);
	            	refreshTerrainPreviewImageView();
	            }
        }
    };
    
    EventHandler<ActionEvent> applyUpdatedMaterialsAndLightingHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	applySettings();
        	updateTerrainMaterials();
        }
    };
    
    EventHandler<ActionEvent> buildAllHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	applySettings();
        	buildTerrain();
        	updateTerrainMaterials();
        }
    };
      
    public void loadHeightmapFileLocal(File file)
    {
    	String path = file.getAbsolutePath();
    	String assetMgrRelativePath = path.replace(TerranovaBaseAssetManager.USER_HOME_DIRECTORY, "");
    		
    	terrainHeightmapTexure = TerranovaApp.INSTANCE.getAssetManager().loadTexture(assetMgrRelativePath);
    	
    	applySettings();
	    buildTerrain();
    }
    
    public void buildTerrain() {
    	TerranovaApp.INSTANCE.terrainManager.buildTerrain(this);
    }
    
    public void updateTerrainMaterials()
    {
    	TerranovaApp.INSTANCE.terrainManager.updateTerrainMaterials(this);
    }
}
