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

import java.util.HashMap;
import java.util.Map;

import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.dreamwagon.terranova.model.TerrainTexture;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;

/**
 * Terrain texture settings dialog.
 * 
 * @author J. Demarco
 *
 */
public class TerrainTextureSettings extends AbstractSettingsDialog {

	@Setting(displayName="Terrain Diffuse", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture;
	
	@Setting(displayName="Terrain Normal", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture;
	
	@Setting(displayName="Terrain Diffuse 1", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture1;
	
	@Setting(displayName="Terrain Normal 1", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture1;
	
	@Setting(displayName="Terrain Diffuse 2", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture2;
	
	@Setting(displayName="Terrain Normal 2", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture2;
	
	@Setting(displayName="Terrain Diffuse 3", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture3;
	
	@Setting(displayName="Terrain Normal 3", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture3;
	
	@Setting(displayName="Terrain Diffuse 4", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture4;
	
	@Setting(displayName="Terrain Normal 4", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture4;
	
	@Setting(displayName="Terrain Diffuse 5", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture5;
	
	@Setting(displayName="Terrain Normal 5", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture normalTexture5;
	
	@Setting(displayName="Terrain Diffuse 6", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture6;
	
	@Setting(displayName="Terrain Diffuse 7", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture7;
	
	@Setting(displayName="Terrain Diffuse 8", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture8;
	
	@Setting(displayName="Terrain Diffuse 9", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture9;
	
	@Setting(displayName="Terrain Diffuse 10", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture10;
	
	@Setting(displayName="Terrain Diffuse 11", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture diffuseTexture11;
	
	@Setting(displayName="Terrain Glow Map", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture glowTextureMap;
	
	@Setting(displayName="Terrain Specular Map", settingType = SettingType.TERRAIN_TEXTURE)
	public TerrainTexture speclarTextureMap;
	
	public Map<String, TerrainTexture> terrainTextureMap = new HashMap<String, TerrainTexture>();
	
	public void setDefaults()
	{
		diffuseTexture = new TerrainTexture(TerranovaBaseAssetManager.DEFAULT_TERRAIN_TEXTURE1);
		diffuseTexture.active = true;
		normalTexture = new TerrainTexture(TerranovaBaseAssetManager.DEFAULT_TERRAIN_NORMAL1);
		normalTexture.active = true;
		diffuseTexture1 = new TerrainTexture();
		normalTexture1 = new TerrainTexture();
		diffuseTexture2 = new TerrainTexture();
		normalTexture2 = new TerrainTexture();
		diffuseTexture3 = new TerrainTexture();
		normalTexture3 = new TerrainTexture();
		diffuseTexture4 = new TerrainTexture();
		normalTexture4 = new TerrainTexture();
		diffuseTexture5 = new TerrainTexture();
		normalTexture5 = new TerrainTexture();
		
		diffuseTexture6 = new TerrainTexture();
		diffuseTexture7 = new TerrainTexture();
		diffuseTexture8 = new TerrainTexture();
		diffuseTexture9 = new TerrainTexture();
		diffuseTexture10 = new TerrainTexture();
		diffuseTexture11 = new TerrainTexture();
		
		glowTextureMap= new TerrainTexture();
		speclarTextureMap= new TerrainTexture();
		
		setTextureMap();
	}
	
	public void setTextureMap()
	{
		terrainTextureMap.clear();
		terrainTextureMap.put("DiffuseMap", diffuseTexture);
		terrainTextureMap.put("DiffuseMap_1", diffuseTexture1);
		terrainTextureMap.put("DiffuseMap_2", diffuseTexture2);
		terrainTextureMap.put("DiffuseMap_3", diffuseTexture3);
		terrainTextureMap.put("DiffuseMap_4", diffuseTexture4);
		terrainTextureMap.put("DiffuseMap_5", diffuseTexture5);
		terrainTextureMap.put("DiffuseMap_6", diffuseTexture6);
		terrainTextureMap.put("DiffuseMap_7", diffuseTexture7);
		terrainTextureMap.put("DiffuseMap_8", diffuseTexture8);
		terrainTextureMap.put("DiffuseMap_9", diffuseTexture9);
		terrainTextureMap.put("DiffuseMap_10", diffuseTexture10);
		terrainTextureMap.put("DiffuseMap_11", diffuseTexture11);
		
		terrainTextureMap.put("NormalMap", normalTexture);
		terrainTextureMap.put("NormalMap_1", normalTexture1);
		terrainTextureMap.put("NormalMap_2", normalTexture2);
		terrainTextureMap.put("NormalMap_3", normalTexture3);
		terrainTextureMap.put("NormalMap_4", normalTexture4);
		terrainTextureMap.put("NormalMap_5", normalTexture5);
		
		terrainTextureMap.put("GlowMap", glowTextureMap);
		terrainTextureMap.put("SpecularMap", speclarTextureMap);
	}

	@Override
	public String getName() {
		return TerrainTextureSettings.class.getCanonicalName();
	}

}
