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
package com.dreamwagon.terranova.manager;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.dreamwagon.terranova.model.TerrainTexture;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 * Manages the terrain
 * 
 * @author J. Demarco
 *
 */
public class TerrainManager{
	
	public static String TERRANOVA_TERRAIN_NODE_NAME = "terranova_terrain_node";
	
	public static String TERRANOVA_TERRAIN_QUAD_NAME = "terranova_terrain_quad";
	
	private Node terrainNode;
	
	public TerrainManager()
	{
		terrainNode = new Node(TERRANOVA_TERRAIN_NODE_NAME);
	}
	
	public void replaceTerrain(TerrainQuad terrain)
	{
		clearChildren();
		terrain.setName(TERRANOVA_TERRAIN_QUAD_NAME);
        terrainNode.attachChild(terrain);
	}
	
	public void clearChildren()
	{
		terrainNode.getChildren().clear();
	}
	
	/**
	 * Completely build terrain from scratch including rebuilding the @TerrainQuad
	 * Terrain will be cleared and reloaded to the rootNode
	 * @param settings
	 */
	public void buildTerrain(JMETerrainSettings settings){
		clearChildren();
		TerranovaApp.INSTANCE.getRootNode().detachChild(terrainNode);
				
        Texture heightmapTexture = settings.terrainHeightmapTexure;
        if(null==heightmapTexture){
        	heightmapTexture = TerranovaBaseAssetManager.DEFAULT_TERRAIN_HEIGHTMAP_TEXTURE;
        }
        
		//terrain
        ImageBasedHeightMap imgbhm = new ImageBasedHeightMap(heightmapTexture.getImage(), settings.heightScale);
        imgbhm.load();
        if (settings.smooth)
        {
        	imgbhm.smooth(settings.smoothAmount, settings.smoothRadius);
        }
        
        TerrainQuad terrain = new TerrainQuad(TerranovaBaseAssetManager.DEFAULT_TERRAIN_NAME, 
    			settings.patchSize, settings.totalSize, imgbhm.getHeightMap());
        terrain.setName(TERRANOVA_TERRAIN_QUAD_NAME);
        
        //Add to parent node
        terrainNode.attachChild(terrain);
        
        //Set materials to terrain
        updateTerrainMaterials(settings);
       
        TerranovaApp.INSTANCE.rootNodeQueue.add(terrainNode);
	}
	
	/**
	 * Updates terrain materials only
	 * 
	 * @param settings
	 */
	public void updateTerrainMaterials(JMETerrainSettings settings)
	{
		TerrainQuad terrain = (TerrainQuad) terrainNode.getChild(TERRANOVA_TERRAIN_QUAD_NAME);
        Material mat_terrain = getMaterial(settings); 
        terrain.setMaterial(mat_terrain);
        ShadowMode shadowMode = ShadowMode.valueOf(settings.shadowMode);
        terrain.setShadowMode(shadowMode);
	}
	
	/**
	 * Get the material based on the current @JMETerrainSettings
	 * If terrain is basic or lighting, apply the current textures from
	 * the @TexturemManager
	 * 
	 * @param settings
	 * @return Material
	 */
	public Material getMaterial(JMETerrainSettings settings)
	{
		Material mat_terrain = TerranovaBaseAssetManager.TERRAIN_MATERIAL_WIREFRAME;
		if (settings.terrainMaterial.equals(JMETerrainSettings.TERRAIN_MAT_BASIC)){
			mat_terrain = TerranovaBaseAssetManager.TERRAIN_MATERIAL_BASIC.clone();
			applyBasicTerrainMaterialParameters(mat_terrain, settings);
		}
		else if (settings.terrainMaterial.equals(JMETerrainSettings.TERRAIN_MAT_LIGHTING)){
			mat_terrain = TerranovaBaseAssetManager.TERRAIN_MATERIAL_LIGHTING.clone();
			applyLightingTerrainMaterialParameters( mat_terrain, settings);
		}

		return mat_terrain;
	}
	
	/**
	 * Build basic terrain material
	 * 
	 * @param mat_terrain
	 * @param settings
	 */
	public void applyBasicTerrainMaterialParameters(Material mat_terrain, JMETerrainSettings settings)
	{
		// create from texture manager textures
		if (settings.terrainAlphaMapTexure1!=null) {
			mat_terrain.setTexture("Alpha", settings.terrainAlphaMapTexure1);
		}
        for (int m=0;m<3;m++){
        	//Use the top 3 textures as lighting material
			String diffuseMap = "DiffuseMap";
			if (m>0) {
				diffuseMap +="_" + m;
			}
			TerrainTexture terrainTexture = settings.terrainTextureSettings.terrainTextureMap.get(diffuseMap);
			if (terrainTexture.active && terrainTexture.texture!=null)
			{
		        mat_terrain.setTexture("Tex" +(m+1), terrainTexture.texture);
		        mat_terrain.setFloat("Tex"+(m+1)+"Scale", terrainTexture.scale);
			}
        }
	}
	
	/**
	 * Apply the material and lighting settings for the terrain (Terrain Lighting)
	 * 
	 * @param mat_terrain
	 * @param settings
	 */
	public void applyLightingTerrainMaterialParameters(Material mat_terrain, JMETerrainSettings settings)
	{
		//create alphamaps from texture manager or generated textures
		if (settings.terrainAlphaMapTexure1 != null){
			mat_terrain.setTexture("AlphaMap", settings.terrainAlphaMapTexure1);
		}
		if (settings.terrainAlphaMapTexure2.active && settings.terrainAlphaMapTexure2.texture != null){
			mat_terrain.setTexture("AlphaMap_1", settings.terrainAlphaMapTexure2.texture);
		}
		if (settings.terrainAlphaMapTexure3.active && settings.terrainAlphaMapTexure3.texture != null){
			mat_terrain.setTexture("AlphaMap_2", settings.terrainAlphaMapTexure3.texture);
		}
        
		//Terrain textures
		for (int m=0;m<12;m++){
			String diffuseMap = "DiffuseMap";
			String diffuseMapScale  ="DiffuseMap_";

			if (m>0) {
				diffuseMap +="_" + m;
			}
			diffuseMapScale+=m+"_scale";
			
			TerrainTexture terrainTexture = settings.terrainTextureSettings.terrainTextureMap.get(diffuseMap);
			
			if (terrainTexture.active && terrainTexture.texture!=null)
			{
				Texture texture = terrainTexture.texture;
				texture.setWrap(terrainTexture.wrapMode);
				mat_terrain.setTexture(diffuseMap, texture);
				mat_terrain.setFloat(diffuseMapScale, terrainTexture.scale);
			}
		}
		//Normal maps
		for (int m=0;m<6;m++){
			
			String normapMap = "NormalMap";
			if (m>0) {
				normapMap +="_" + m;
			}
			
			TerrainTexture normalTexture = settings.terrainTextureSettings.terrainTextureMap.get(normapMap);
			if (normalTexture.active && normalTexture.texture!=null){
				Texture texture = normalTexture.texture;
				mat_terrain.setTexture(normapMap, texture);
				texture.setWrap(normalTexture.wrapMode);
			}
		}
		
		//Specular
		TerrainTexture specularTexture = settings.terrainTextureSettings.terrainTextureMap.get("SpecularMap");
		if (specularTexture.active && specularTexture.texture!=null)
		{
			Texture texture = specularTexture.texture;
			texture.setWrap(specularTexture.wrapMode);
			mat_terrain.setTexture("SpecularMap", texture);
		}
		//Glow
		TerrainTexture glowTexture = settings.terrainTextureSettings.terrainTextureMap.get("GlowMap");
		if (glowTexture.active && glowTexture.texture!=null)
		{
			Texture texture = glowTexture.texture;
			texture.setWrap(glowTexture.wrapMode);
			mat_terrain.setTexture("GlowMap", texture);
		}
	}
	
	public TerrainQuad getTerrainQuad()
	{
		return (TerrainQuad) terrainNode.getChild(TERRANOVA_TERRAIN_QUAD_NAME);
	}
}
