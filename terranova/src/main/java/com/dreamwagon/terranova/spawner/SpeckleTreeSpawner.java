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
package com.dreamwagon.terranova.spawner;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.manager.TreePrototypeManager;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;
import com.dreamwagon.terranova.util.Rangef;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 * Randomly spawns trees completely over a @TerrainQuad  
 * 
 * @author J. Demarco
 *
 */
public class SpeckleTreeSpawner extends AbstractSpawnerDialog{
	
	@Setting(displayName="Seed", settingType = SettingType.INTEGER)
	public int seed =325523;
	
	@Setting(displayName="Max Instances", settingType = SettingType.INTEGER)
	public int maxInstances =500;
	
	@Setting(displayName="Tree Spacing Range", settingType = SettingType.TWO_VAL_RANGED_FLOAT, floatRangeMin = 50, floatRangeMax = 1000)
	public Rangef treeSpacingDistance = new Rangef(50, 450);
	
	@Setting(displayName="Tree Height Range", settingType = SettingType.TWO_VAL_RANGED_FLOAT, floatRangeMin = 0, floatRangeMax = 513)
	public Rangef treeHeightDistance = new Rangef(0, 513);
	
	/**
	 * Spawn trees evenly (or speckeled) over a @TerrainQuad quad using the given @SpeckleTreeSpawnerSettings
	 */
	@Override
	public List<Spatial> spawn(TerrainQuad terrainQuad) {
		applySettings();
		List<Spatial> trees = new ArrayList<Spatial>();
		List<TerrainPatch> terrainPatchList = new ArrayList<TerrainPatch>();
    	terrainQuad.getAllTerrainPatches(terrainPatchList);
    	for(TerrainPatch patch : terrainPatchList){
    		List<Spatial> patchTreeInstances = spawnPatch(patch);
    		//TODO! should let the tree list be built past max
    		//instances and then pull trees out of the list, back down = max instances 
    		//for even distribution
    		for(Spatial patchTree : patchTreeInstances) {
    			if (trees.size() < maxInstances) {
    				trees.add(patchTree);
    			}
    		}
    		
    	}
    	return trees;
	}

	/**
	 * Spawn Trees for a given @TerrainPatch
	 * 
	 * @param patch
	 * @param settings
	 */
	public List<Spatial> spawnPatch(TerrainPatch patch) {
		
		 List<Spatial> treeList = new ArrayList<Spatial>();
		 Mesh mesh = patch.getMesh();
		 Transform transform = patch.getWorldTransform();
		 ThreadLocalRandom tlRandom = ThreadLocalRandom.current();
		 
		 //TODO get trees from this spawners instance settings, not the main prototype manager.
		 TreePrototypeManager treePrototypeManager =TerranovaApp.INSTANCE.treePrototypeManager;
		 
		 VertexBuffer vB = mesh.getBuffer(VertexBuffer.Type.Position);
	        
	        if (vB.getNumComponents() != 3) {
	            throw new RuntimeException("Position Buffer doesn't have 3-component vectors! -> numComponents: " + vB.getNumComponents());
	        }

	        FloatBuffer buf = (FloatBuffer)vB.getDataReadOnly();
	        Vector3f vTemp = new Vector3f();
	        
	        int count = 0;
	        int genIndex = tlRandom.nextInt(treeSpacingDistance.getMinIntValue(), treeSpacingDistance.getMaxIntValue() + 1);

	        for (int i = 0; i < vB.getNumElements(); i++) 
	        {
	        	vTemp.set(buf.get(i * 3), buf.get(i * 3 + 1), buf.get(i * 3 + 2));
	        	
	        	if (count> genIndex)
	        	{
	        		Vector3f worldSpaceVert = new Vector3f();
		    	    transform.transformVector(vTemp, worldSpaceVert);
		    	    //TODO apply tree weights. 
		    	    //set scale, rotation from random
	        		Spatial tree = treePrototypeManager.getRandomTree(tlRandom).model.clone();
	        		tree.setShadowMode(ShadowMode.Cast);
	        		tree.scale((float)tlRandom.nextDouble(.9f, 2.44f));
	        		//TangentBinormalGenerator.generate(tree);
	        		
	        		float rotationY =(float)tlRandom.nextDouble(Math.PI *2);
	        		
	        		tree.rotate(0, rotationY, 0);
	        		tree.setLocalTranslation(worldSpaceVert);
	        		
	        		if (treeHeightDistance.isInRange(tree.getWorldTransform().getTranslation().y)) {
	        			treeList.add(tree);
	        		}
	        		//hit or miss, we still reset the counter
	        		count=0;
	        		genIndex = tlRandom.nextInt(treeSpacingDistance.getMinIntValue(), treeSpacingDistance.getMaxIntValue() + 1);
	        	}
	        	
	        	count++;
		 }

	     
	     return treeList;
	}
}
