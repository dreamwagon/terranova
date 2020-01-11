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

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Uses @HillHeightMap to generate a height map
 * 
 * @author J. Demarco
 *
 */
public class HillHeightMapTerrainGenerator extends AbstractSettingsDialog implements Generator<TerrainQuad>{

	@Setting(displayName="Iterations", settingType = SettingType.INTEGER)
	public Integer iterations = 1000;
	@Setting(displayName="Min Radius", settingType = SettingType.FLOAT)
	public Float minRadius = 50f;
	@Setting(displayName="Max Radius", settingType = SettingType.FLOAT)
	public Float maxRadius = 100f;
	@Setting(displayName="Seed", settingType = SettingType.LONG)
	public long seed = 1;
	@Setting(displayName="Normalized Range", settingType = SettingType.FLOAT)
    public Float normalizeRange = 100f;
    
	@Override
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node){
		VBox vbox = (VBox)node;
		Text text = new Text("Terrain generation will replace existing terrain. Be sure to save terrain if "
				+ "necessary. This generator uses patch size and total size settings from JMETerrainSettings");
		text.setWrappingWidth(470);
		vbox.getChildren().add(text);
	}
	
	@Override
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node){
		VBox vbox = (VBox)node;
		
		Button genButton = new Button("Generate Terrain");
		genButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	applySettings();
	        	String jmeTerrainSettingsName = JMETerrainSettings.class.getCanonicalName();
        		JMETerrainSettings terrainSettings = (JMETerrainSettings) TerranovaUiManager.persistentSettings.get(jmeTerrainSettingsName);
        		TerrainQuad terrainQuad = generate();
        		TerranovaApp.INSTANCE.terrainManager.replaceTerrain(terrainQuad);
        		TerranovaApp.INSTANCE.terrainManager.updateTerrainMaterials(terrainSettings);
	        	}
	     	});
		
		
		vbox.getChildren().add(genButton);
	}
	
	@Override
	public TerrainQuad generate() {

		String jmeTerrainSettingsName = JMETerrainSettings.class.getCanonicalName();
		JMETerrainSettings terrainSettings = (JMETerrainSettings) TerranovaUiManager.persistentSettings.get(jmeTerrainSettingsName);
				
		HillHeightMap heightmap = null;
		HillHeightMap.NORMALIZE_RANGE = normalizeRange;
		
		try {
		    heightmap = new HillHeightMap(terrainSettings.totalSize, iterations, 
		    		minRadius, maxRadius, (byte)seed);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		return new TerrainQuad(getName(), terrainSettings.patchSize, terrainSettings.totalSize, heightmap.getHeightMap());
	}

	@Override
	public String getName() {

		return HillHeightMapTerrainGenerator.class.getCanonicalName();
	}

}
