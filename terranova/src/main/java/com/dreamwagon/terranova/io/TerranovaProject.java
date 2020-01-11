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
package com.dreamwagon.terranova.io;

import java.util.Map.Entry;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.model.TerrainTexture;
import com.dreamwagon.terranova.model.ToggleTexture;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.dreamwagon.terranova.settings.TerrainTextureSettings;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture.WrapMode;

import javafx.scene.control.Dialog;

/**
 * Serializable project for saving and loading a project to/from json.
 * 
 * @author J. Demarco
 *
 */
public class TerranovaProject {

	public ProjectTerrainSettings projectTerrainSettings = new ProjectTerrainSettings();
	
	public ProjectTerrainTextureSettings projectTerrainTextureSettings = new ProjectTerrainTextureSettings();
	

    @JsonIgnore
    public void buildProjectForSave()
    {
    	this.projectTerrainSettings = copyTerrainSettings();
    	this.projectTerrainTextureSettings = copyTerrainTextureSettings();
    }
    
    @JsonIgnore
    public void loadProject()
    {
    	//Close and remove all of the current dialogs in the app forcing them to be rebuilt.
    	for (Entry<String, Dialog<Void>> dlg  : TerranovaUiManager.persistentDialogMap.entrySet())
    	{
    		dlg.getValue().close();
    	}
    	TerranovaUiManager.persistentDialogMap.clear();
    	
    	//Load JMETerrainSettings
    	String terrainSettingsName = JMETerrainSettings.class.getCanonicalName();
		JMETerrainSettings terrainSettings = (JMETerrainSettings)TerranovaUiManager.persistentSettings.get(terrainSettingsName);
		buildTerrainSettings(terrainSettings, this.projectTerrainSettings);
		
		//Load Texture Settings
		buildTerrainTextureSettings(terrainSettings.terrainTextureSettings, this.projectTerrainTextureSettings);
		terrainSettings.terrainTextureSettings.setTextureMap();
		
		//Build the terrain
		TerranovaApp.INSTANCE.terrainManager.buildTerrain(terrainSettings);
		
    }
    
    public static class ProjectTerrainSettings {
    	
    	public String terrainHeightmapTexurePath;
    	public String terrainAlphaMapTexure1Path;
    	public ProjectTerrainTexture terrainAlphaMapTexure2;
    	public ProjectTerrainTexture terrainAlphaMapTexure3;
    	
    	public Integer patchSize =1;
    	public Integer totalSize;
    	public Float heightScale;
    	public String shadowMode;
    	public String terrainMaterial;
    	public Boolean smooth = Boolean.FALSE;
    	public Float smoothAmount = 1f;
    	public Integer smoothRadius = 1;
    }
    
    public static class ProjectTerrainTexture {
    	public float scale;
    	public String wrapMode;
    	public String assetKey;
    	public boolean active;
    }
    
    public static class ProjectTerrainTextureSettings {
    	public ProjectTerrainTexture diffuseTexture;
    	public ProjectTerrainTexture normalTexture;
    	public ProjectTerrainTexture diffuseTexture1;
    	public ProjectTerrainTexture normalTexture1;
    	public ProjectTerrainTexture diffuseTexture2;
    	public ProjectTerrainTexture normalTexture2;
    	public ProjectTerrainTexture diffuseTexture3;
    	public ProjectTerrainTexture normalTexture3;
    	public ProjectTerrainTexture diffuseTexture4;
    	public ProjectTerrainTexture normalTexture4;
    	public ProjectTerrainTexture diffuseTexture5;
    	public ProjectTerrainTexture normalTexture5;
    	public ProjectTerrainTexture diffuseTexture6;
    	public ProjectTerrainTexture diffuseTexture7;
    	public ProjectTerrainTexture diffuseTexture8;
    	public ProjectTerrainTexture diffuseTexture9;
    	public ProjectTerrainTexture diffuseTexture10;
    	public ProjectTerrainTexture diffuseTexture11;
    	public ProjectTerrainTexture glowTextureMap;
    	public ProjectTerrainTexture speclarTextureMap;
    } 
    
    //Utility methods
	public static ProjectTerrainSettings copyTerrainSettings()
	{
		String terrainSettingsName = JMETerrainSettings.class.getCanonicalName();
		JMETerrainSettings terrainSettings = (JMETerrainSettings)TerranovaUiManager.persistentSettings.get(terrainSettingsName);
		ProjectTerrainSettings terrainSettingsToSave = new ProjectTerrainSettings();
		
		terrainSettingsToSave.terrainHeightmapTexurePath = terrainSettings.terrainHeightmapTexure.getKey().getName();
		terrainSettingsToSave.terrainAlphaMapTexure1Path = terrainSettings.terrainAlphaMapTexure1.getKey().getName();
		terrainSettingsToSave.terrainAlphaMapTexure2 = copyToggleTexture(terrainSettings.terrainAlphaMapTexure2);
		terrainSettingsToSave.terrainAlphaMapTexure3 = copyToggleTexture(terrainSettings.terrainAlphaMapTexure3);
    	
		terrainSettingsToSave.heightScale = terrainSettings.heightScale;
		terrainSettingsToSave.patchSize = terrainSettings.patchSize;
		terrainSettingsToSave.shadowMode = terrainSettings.shadowMode;
		terrainSettingsToSave.smooth = terrainSettings.smooth;
		terrainSettingsToSave.smoothAmount = terrainSettings.smoothAmount;
		terrainSettingsToSave.smoothRadius = terrainSettings.smoothRadius;
		terrainSettingsToSave.terrainMaterial = terrainSettings.terrainMaterial;
		return terrainSettingsToSave;
	}
	
	public static void buildTerrainSettings(JMETerrainSettings terrainSettings, ProjectTerrainSettings pTerrainSettings)
	{
		AssetManager assetManager = TerranovaApp.INSTANCE.getAssetManager();
		terrainSettings.terrainHeightmapTexure = assetManager.loadTexture(pTerrainSettings.terrainHeightmapTexurePath);
		terrainSettings.terrainAlphaMapTexure1 = assetManager.loadTexture(pTerrainSettings.terrainAlphaMapTexure1Path);
    	//public ProjectTerrainTexture terrainAlphaMapTexure2;
    	//public ProjectTerrainTexture terrainAlphaMapTexure3;
    	
		terrainSettings.heightScale = pTerrainSettings.heightScale;
		terrainSettings.patchSize = pTerrainSettings.patchSize;
		terrainSettings.shadowMode = pTerrainSettings.shadowMode;
		terrainSettings.smooth = pTerrainSettings.smooth;
		terrainSettings.smoothAmount = pTerrainSettings.smoothAmount;
		terrainSettings.smoothRadius = pTerrainSettings.smoothRadius;
		terrainSettings.terrainMaterial = pTerrainSettings.terrainMaterial;
	}
	
	public static void buildTerrainTextureSettings(TerrainTextureSettings terrainTextureSettings, ProjectTerrainTextureSettings pTerrainTxSettings)
	{
		terrainTextureSettings.diffuseTexture = buildTerrainTexture( pTerrainTxSettings.diffuseTexture);
		terrainTextureSettings.normalTexture= buildTerrainTexture( pTerrainTxSettings.normalTexture);
		terrainTextureSettings.diffuseTexture1= buildTerrainTexture( pTerrainTxSettings.diffuseTexture1);
		terrainTextureSettings.normalTexture1= buildTerrainTexture( pTerrainTxSettings.normalTexture1);
		terrainTextureSettings.diffuseTexture2= buildTerrainTexture( pTerrainTxSettings.diffuseTexture2);
		terrainTextureSettings.normalTexture2= buildTerrainTexture( pTerrainTxSettings.normalTexture2);
		terrainTextureSettings.diffuseTexture3= buildTerrainTexture( pTerrainTxSettings.diffuseTexture3);
		terrainTextureSettings.normalTexture3= buildTerrainTexture( pTerrainTxSettings.normalTexture3);
		terrainTextureSettings.diffuseTexture4= buildTerrainTexture( pTerrainTxSettings.diffuseTexture4);
		terrainTextureSettings.normalTexture4= buildTerrainTexture( pTerrainTxSettings.normalTexture4);
		terrainTextureSettings.diffuseTexture5= buildTerrainTexture( pTerrainTxSettings.diffuseTexture5);
		terrainTextureSettings.normalTexture5= buildTerrainTexture( pTerrainTxSettings.normalTexture5);
		terrainTextureSettings.diffuseTexture6= buildTerrainTexture( pTerrainTxSettings.diffuseTexture6);
		terrainTextureSettings.diffuseTexture7= buildTerrainTexture( pTerrainTxSettings.diffuseTexture7);
		terrainTextureSettings.diffuseTexture8= buildTerrainTexture( pTerrainTxSettings.diffuseTexture8);
		terrainTextureSettings.diffuseTexture9= buildTerrainTexture( pTerrainTxSettings.diffuseTexture9);
		terrainTextureSettings.diffuseTexture10= buildTerrainTexture( pTerrainTxSettings.diffuseTexture10);
		terrainTextureSettings.diffuseTexture11= buildTerrainTexture( pTerrainTxSettings.diffuseTexture11);
		terrainTextureSettings.glowTextureMap= buildTerrainTexture( pTerrainTxSettings.glowTextureMap);
		terrainTextureSettings.speclarTextureMap= buildTerrainTexture( pTerrainTxSettings.speclarTextureMap);
	}
	
	public static TerrainTexture buildTerrainTexture( ProjectTerrainTexture projectTerrainTexture)
	{
		AssetManager assetManager = TerranovaApp.INSTANCE.getAssetManager();
		TerrainTexture terrainTexture = new TerrainTexture();
		if (projectTerrainTexture.assetKey != null)
		{
			terrainTexture.texture = assetManager.loadTexture(projectTerrainTexture.assetKey);
			terrainTexture.active = projectTerrainTexture.active;
			terrainTexture.wrapMode = WrapMode.valueOf(projectTerrainTexture.wrapMode);
			terrainTexture.scale = terrainTexture.scale;
		}
		return terrainTexture;
	}
	
	public static ProjectTerrainTextureSettings copyTerrainTextureSettings()
	{
		String terrainSettingsName = JMETerrainSettings.class.getCanonicalName();
		JMETerrainSettings terrainSettings = (JMETerrainSettings)TerranovaUiManager.persistentSettings.get(terrainSettingsName);
		TerrainTextureSettings terrainTextureSettings = terrainSettings.terrainTextureSettings;
		ProjectTerrainTextureSettings terrainTextureSettingsToSave = new ProjectTerrainTextureSettings();
		
		terrainTextureSettingsToSave.diffuseTexture = copyTerrainTexture( terrainTextureSettings.diffuseTexture);
    	terrainTextureSettingsToSave.normalTexture= copyTerrainTexture( terrainTextureSettings.normalTexture);
    	terrainTextureSettingsToSave.diffuseTexture1= copyTerrainTexture( terrainTextureSettings.diffuseTexture1);
    	terrainTextureSettingsToSave.normalTexture1= copyTerrainTexture( terrainTextureSettings.normalTexture1);
    	terrainTextureSettingsToSave.diffuseTexture2= copyTerrainTexture( terrainTextureSettings.diffuseTexture2);
    	terrainTextureSettingsToSave.normalTexture2= copyTerrainTexture( terrainTextureSettings.normalTexture2);
    	terrainTextureSettingsToSave.diffuseTexture3= copyTerrainTexture( terrainTextureSettings.diffuseTexture3);
    	terrainTextureSettingsToSave.normalTexture3= copyTerrainTexture( terrainTextureSettings.normalTexture3);
    	terrainTextureSettingsToSave.diffuseTexture4= copyTerrainTexture( terrainTextureSettings.diffuseTexture4);
    	terrainTextureSettingsToSave.normalTexture4= copyTerrainTexture( terrainTextureSettings.normalTexture4);
    	terrainTextureSettingsToSave.diffuseTexture5= copyTerrainTexture( terrainTextureSettings.diffuseTexture5);
    	terrainTextureSettingsToSave.normalTexture5= copyTerrainTexture( terrainTextureSettings.normalTexture5);
    	terrainTextureSettingsToSave.diffuseTexture6= copyTerrainTexture( terrainTextureSettings.diffuseTexture6);
    	terrainTextureSettingsToSave.diffuseTexture7= copyTerrainTexture( terrainTextureSettings.diffuseTexture7);
    	terrainTextureSettingsToSave.diffuseTexture8= copyTerrainTexture( terrainTextureSettings.diffuseTexture8);
    	terrainTextureSettingsToSave.diffuseTexture9= copyTerrainTexture( terrainTextureSettings.diffuseTexture9);
    	terrainTextureSettingsToSave.diffuseTexture10= copyTerrainTexture( terrainTextureSettings.diffuseTexture10);
    	terrainTextureSettingsToSave.diffuseTexture11= copyTerrainTexture( terrainTextureSettings.diffuseTexture11);
    	terrainTextureSettingsToSave.glowTextureMap= copyTerrainTexture( terrainTextureSettings.glowTextureMap);
    	terrainTextureSettingsToSave.speclarTextureMap= copyTerrainTexture( terrainTextureSettings.speclarTextureMap);
		
		return terrainTextureSettingsToSave;
	}
	
	public static ProjectTerrainTexture copyToggleTexture(ToggleTexture toggleTexture)
	{
		ProjectTerrainTexture ptx = new ProjectTerrainTexture();
		if (toggleTexture.texture != null)
		{
			ptx.assetKey = toggleTexture.texture.getKey().getName();
			ptx.active = toggleTexture.active;
		}
		return ptx;
	}
	
	public static ProjectTerrainTexture copyTerrainTexture(TerrainTexture terrainTexture)
	{
		ProjectTerrainTexture ptx = new ProjectTerrainTexture();
		if (terrainTexture.texture != null)
		{
			ptx.assetKey = terrainTexture.texture.getKey().getName();
			ptx.active = terrainTexture.active;
			ptx.scale = terrainTexture.scale;
			ptx.wrapMode = terrainTexture.wrapMode.name();
		}
		return ptx;
	}
}
