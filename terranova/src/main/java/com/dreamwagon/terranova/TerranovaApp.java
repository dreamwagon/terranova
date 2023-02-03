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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dreamwagon.terranova.constants.Constants;
import com.dreamwagon.terranova.generator.FastNoiseHeightmapGenerator;
import com.dreamwagon.terranova.generator.HillHeightMapTerrainGenerator;
import com.dreamwagon.terranova.generator.SplatMapGenerator;
import com.dreamwagon.terranova.io.SaveRequest;
import com.dreamwagon.terranova.manager.TerrainManager;
import com.dreamwagon.terranova.manager.TreeManager;
import com.dreamwagon.terranova.manager.TreePrototypeManager;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.dreamwagon.terranova.settings.TerrainTextureSettings;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.dreamwagon.terranova.util.IOUtil;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;

import io.tlf.jme.jfx.JavaFxUI;

/**
 * 
 * @author J. Demarco
 *
 */
public class TerranovaApp extends SimpleApplication {

	public static TerranovaApp INSTANCE = null;
	
	public static boolean flyCamEnabled = false;
	
	public static FlyCamAppState flyCamState = new FlyCamAppState();
	 
	public static String ACTION_TOGGLE_FLYCAM = "TOGGLE_FLY_CAM";
	
	public TerrainManager terrainManager;
	
	public TreeManager treeManager;
	
	public TerrainTextureSettings terrainTextureSettings;
	
	public TreePrototypeManager treePrototypeManager;
	
	//Generators
	public HillHeightMapTerrainGenerator hillHeightMapTerrainGenerator;
	
	public SplatMapGenerator splatMapGenerator;
	
	public FastNoiseHeightmapGenerator fastNoiseHeightmapGenerator;
	
	//Queue of nodes to be added to the root node in simple update
	public List<Node> rootNodeQueue = new CopyOnWriteArrayList<Node>();
	
	public List<SaveRequest> saveRequestQueue= new ArrayList<SaveRequest>();
	
    public static void main(String... args) {
    	INSTANCE = new TerranovaApp();
    	
    	AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setFrameRate(120);
        appSettings.setTitle("Terranova");
        //appSettings.setResizable(true);
        
        INSTANCE.setSettings(appSettings);
        INSTANCE.setShowSettings(false);
    	INSTANCE.start();
    }

    TerranovaApp() {
        super(new StatsAppState(), flyCamState);
    }


    @Override
    public void simpleInitApp() {

    	//Register user home file locator
    	assetManager.registerLocator(TerranovaBaseAssetManager.USER_HOME_DIRECTORY, FileLocator.class);
    	
    	//Register inputs
    	registerInput();
    	
        JavaFxUI.initialize(this);
        
        //Set default background color
        getViewPort().setBackgroundColor(Constants.DEFAULT_UI_BACKGROUND_COLOR);
        
        //Remove the default mapping to exit the app on ESC
        getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        
		//Set dialogs
		JavaFxUI.getInstance().runInJavaFxThread(() -> {
			TerranovaUiManager.createBaseUi();
		});
        
        //Load all base assets used by the app
        TerranovaBaseAssetManager.loadBaseAssets(assetManager);
        
        //Add some sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-1f, -1f, -1f)).normalize());
        sun.setColor(ColorRGBA.White.clone().multLocal(1));
        rootNode.addLight(sun);
        
        /* Drop shadows */
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.8f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);

        //dlsr.displayDebug();
        viewPort.addProcessor(dlsr);

        //DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        //dlsf.setLight(sun);
        //dlsf.setEnabled(true);
        //FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        //fpp.addFilter(dlsf);
        //viewPort.addProcessor(fpp);
        
        terrainManager = new TerrainManager();
        
        treeManager = new TreeManager();
       
        terrainTextureSettings = new TerrainTextureSettings();
        terrainTextureSettings.setDefaults();
                
        JMETerrainSettings jmeTerrainSettings = new JMETerrainSettings(terrainTextureSettings);
        TerranovaUiManager.persistentSettings.put(jmeTerrainSettings.getName(), jmeTerrainSettings);
        
        terrainManager.buildTerrain(jmeTerrainSettings);
        
        treePrototypeManager = new TreePrototypeManager();
        treePrototypeManager.setDefaults();
        
        //generators
        hillHeightMapTerrainGenerator = new HillHeightMapTerrainGenerator();
        TerranovaUiManager.persistentSettings.put(hillHeightMapTerrainGenerator.getName(), hillHeightMapTerrainGenerator);
        
        splatMapGenerator = new SplatMapGenerator();
        TerranovaUiManager.persistentSettings.put(splatMapGenerator.getName(), splatMapGenerator);
        
        fastNoiseHeightmapGenerator = new FastNoiseHeightmapGenerator();
        TerranovaUiManager.persistentSettings.put(fastNoiseHeightmapGenerator.getName(), fastNoiseHeightmapGenerator);
        
        //set cam defaults 
    	resetCameraToDefaults();
    	
        //Start without camera active
        stateManager.detach(flyCamState);
        flyCamEnabled = false;
    }
    
    @Override
    public void simpleUpdate(float tpf) {
    	
    	//Add any nodes in the queue
    	for (Node node : rootNodeQueue)
    	{
    		rootNode.attachChild(node);
    	}
    	if (!rootNodeQueue.isEmpty())
    	{
    		rootNodeQueue.clear();
    	}
    	
    	for (SaveRequest saveRequest : saveRequestQueue)
    	{
    		IOUtil.exportNode(saveRequest.node, saveRequest.filePath);
    	}
    	if (!saveRequestQueue.isEmpty())
    	{
    		saveRequestQueue.clear();
    	}
    	
    }

    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean pressed, float tpf){
        	
        	if (name.equals(ACTION_TOGGLE_FLYCAM)){
        		//Button up
        		if (!pressed){
        			if (flyCamEnabled){
        				INSTANCE.stateManager.detach(flyCamState);
        				flyCamEnabled = false;
        			}
        			else{
        				INSTANCE.stateManager.attach(flyCamState);
        				flyCamEnabled = true;
        			}
        		}
        	}
        }
    };
    
    public void registerInput( ) {
        
        inputManager.addMapping(ACTION_TOGGLE_FLYCAM, new KeyTrigger(KeyInput.KEY_F));
        inputManager.addListener(actionListener, ACTION_TOGGLE_FLYCAM);
    }
    
    public void resetCameraToDefaults()
    {
    	TerranovaUiManager.FLY_CAM_SPEED = TerranovaUiManager.DEFAULT_FLY_CAM_SPEED;
    	cam.setLocation(TerranovaUiManager.CAM_DEFAULT_LOCATION);
    	cam.lookAt(Vector3f.ZERO, new Vector3f(Vector3f.UNIT_Y));
    	flyCamState.getCamera().setMoveSpeed(TerranovaUiManager.FLY_CAM_SPEED);
    }
    

}
