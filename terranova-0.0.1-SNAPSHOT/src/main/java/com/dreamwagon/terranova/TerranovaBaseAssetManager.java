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
package com.dreamwagon.terranova;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class TerranovaBaseAssetManager {

	//Constants
	public static String USER_HOME_DIRECTORY = System.getProperty("user.home");
	
	public static String EXPORT_PATH = USER_HOME_DIRECTORY;
	
	//Textures
	public static Texture DEFAULT_BLACK_TEXTURE;
	
	public static Texture DEFAULT_TERRAIN_HEIGHTMAP_TEXTURE;

	public static Texture DEFAULT_TERRAIN_ALPHAMAP;
	
	public static Texture DEFAULT_TERRAIN_TEXTURE1;
	
	public static Texture DEFAULT_TERRAIN_NORMAL1;
	
	//Materials
	public static Material TERRAIN_MATERIAL_BASIC;
	
	public static Material TERRAIN_MATERIAL_LIGHTING;
	
	public static Material TERRAIN_MATERIAL_WIREFRAME;
	
	public static String DEFAULT_TERRAIN_NAME= "DEFAULT_TERRAIN";
	
	
	//Models
	public static Spatial SAMPLE_TREE_1= null;
	
	public static Spatial SAMPLE_TREE_2= null;
	
	public static Spatial SAMPLE_TREE_3= null;
	
	public static Spatial SAMPLE_TREE_4= null;
	
	public static Spatial SAMPLE_TREE_5= null;
	
	public static void loadBaseAssets(AssetManager assetManager)
	{
		DEFAULT_BLACK_TEXTURE = assetManager.loadTexture("Textures/Default/defaultTerrainHeightmap.png");
		DEFAULT_TERRAIN_HEIGHTMAP_TEXTURE = DEFAULT_BLACK_TEXTURE;
		
		DEFAULT_TERRAIN_ALPHAMAP = assetManager.loadTexture("Textures/Default/defaultTerrainAlphamap.png"); 
		
		TERRAIN_MATERIAL_BASIC = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		
		TERRAIN_MATERIAL_LIGHTING = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
		TERRAIN_MATERIAL_LIGHTING.setBoolean("useTriPlanarMapping", false);
		TERRAIN_MATERIAL_LIGHTING.setFloat("Shininess", 0.0f);
		
		TERRAIN_MATERIAL_WIREFRAME= new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		TERRAIN_MATERIAL_WIREFRAME.getAdditionalRenderState().setWireframe(true);
		TERRAIN_MATERIAL_WIREFRAME.setColor("Color", ColorRGBA.Green);
		
		DEFAULT_TERRAIN_TEXTURE1 = assetManager.loadTexture("Textures/Default/defaultTerrainTexture1.png");
		DEFAULT_TERRAIN_NORMAL1 = assetManager.loadTexture("Textures/Default/defaultTerrainNormal1.png");
		
		//load models
		SAMPLE_TREE_1 = assetManager.loadModel("Models/Trees/sample_tree_1.gltf");
		SAMPLE_TREE_2 = assetManager.loadModel("Models/Trees/sample_tree_2.gltf");
		SAMPLE_TREE_3 = assetManager.loadModel("Models/Trees/sample_tree_3.gltf");
		SAMPLE_TREE_4 = assetManager.loadModel("Models/Trees/sample_tree_4.gltf");
		SAMPLE_TREE_5 = assetManager.loadModel("Models/Trees/sample_tree_5.gltf");
		
	}
}
