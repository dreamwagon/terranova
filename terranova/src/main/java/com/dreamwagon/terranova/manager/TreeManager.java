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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.settings.NoiseLayerSettings;
import com.dreamwagon.terranova.spawner.AbstractSpawnerDialog;
import com.dreamwagon.terranova.spawner.Spawner;
import com.dreamwagon.terranova.spawner.SpeckleTreeSpawner;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Settings;
import com.dreamwagon.terranova.ui.SettingsDialogBuilder;
import com.dreamwagon.terranova.ui.TerranovaUiManager;

import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;

import io.tlf.jme.jfx.JavaFxUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Tree & Grass Manager contains a list of 0-n @Spawner instances for creating the 
 * Tree and grass instances for a terrain.
 * 
 * -Create a spawner
 * -Modify the spawner settings to your desired values
 * -Spawn trees and grass
 * 
 * 
 * @author J. Demarco
 *
 */
public class TreeManager extends AbstractSettingsDialog{

    private Map<String, Dialog<Void>> spawnerSettingsDialogMap = new HashMap<String, Dialog<Void>>();
    
	List<Spatial> trees = new ArrayList<Spatial>();
	
	private List<AbstractSpawnerDialog> treeSpawners = new ArrayList<>();
	
	private Map<String, Class<? extends Spawner>> availableTreeSpawners = new HashMap<>();
	
	private ChoiceBox<Object> treeSpawnerChoiceBox;
	
	private com.jme3.scene.Node treeGeomNode = new com.jme3.scene.Node();
	
	public TreeManager()
	{		
		//Set the spawners available in the tree manager
		availableTreeSpawners.put(SpeckleTreeSpawner.class.getCanonicalName(), SpeckleTreeSpawner.class);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node) {
		
		VBox vBox =  (VBox)node;
		VBox innerVBox = new VBox();
		HBox treeSpawnerHBox = new HBox();
		treeSpawnerHBox.setSpacing(10);
		
		Button addTreeSpawnerButton = new Button("Add Tree Spawner");
		addTreeSpawnerButton.setOnAction(new EventHandler<ActionEvent>() {
	        @SuppressWarnings("unchecked")
			@Override
	        public void handle(ActionEvent event) {
	        	String className = (String) treeSpawnerChoiceBox.getValue();
	        	if (null != className){
	        		Class<?> spawnerClass;
					try {
						spawnerClass = Class.forName(className);	
						AbstractSpawnerDialog spawner = (AbstractSpawnerDialog) spawnerClass.getDeclaredConstructor().newInstance();
						innerVBox.getChildren().clear();
						treeSpawners.add(spawner);
						buildSpawnerView(parentDialog, innerVBox);
					} catch (Exception e) {
						TerranovaUiManager.showErrorMessage("Unable to create spawner: " + className);
						e.printStackTrace();
					}
	        		
	        	}
	        	
	        }
	    });
		
		ObservableList<Object> choices = FXCollections.observableArrayList(availableTreeSpawners.keySet());
		treeSpawnerChoiceBox = new ChoiceBox<Object>();
		treeSpawnerChoiceBox = new ChoiceBox<Object>(choices);
		treeSpawnerHBox.getChildren().addAll(treeSpawnerChoiceBox, addTreeSpawnerButton);
		
		vBox.getChildren().add(treeSpawnerHBox);
		vBox.getChildren().add(innerVBox);
		
	}
	
	@Override
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node) {
		
		VBox vBox =  (VBox)node;
		HBox treeButtonHBox = new HBox();
		treeButtonHBox.setSpacing(10);
		Button clearTreesButton = new Button("Clear Trees");
		clearTreesButton.setOnAction(clearTreeHandler);
		
		Button execeSpawnersButton = new Button("Spawn Trees");
		execeSpawnersButton.setOnAction(executeSpawnersHandler);
		
		treeButtonHBox.getChildren().addAll(execeSpawnersButton, clearTreesButton);
		vBox.getChildren().add(treeButtonHBox);
	}
	
	private void resetSettingsDialog(){
		for  (Entry<String, Dialog<Void>> dlg : spawnerSettingsDialogMap.entrySet()) {
			dlg.getValue().close();
		}
		spawnerSettingsDialogMap.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void executeSpawners(){
		
		TerrainQuad terrainQuad = TerranovaApp.INSTANCE.terrainManager.getTerrainQuad();
		for (AbstractSpawnerDialog spawner : treeSpawners){
			trees.addAll((List<Spatial>) spawner.spawn(terrainQuad));
		}
		//com.jme3.scene.Node geomNode = new com.jme3.scene.Node();
		
	     //TODO batch as an option?
		for (Spatial tree : trees){
			treeGeomNode.attachChild(tree);
		}
		//com.jme3.scene.Node optimizedTreeGeom = (com.jme3.scene.Node) GeometryBatchFactory.optimize(geomNode);
		
		//for (Spatial s1:optimizedTreeGeom.getChildren()) {
		//	 s1.setShadowMode(ShadowMode.Cast);
		//	 }
		TerranovaApp.INSTANCE.rootNodeQueue.add(treeGeomNode);	
		
	}

	@Override
	public String getName() {
		return TreeManager.class.getCanonicalName();
	}
	
    EventHandler<ActionEvent> executeSpawnersHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	if (!(treeSpawners.size() >0)){
        		TerranovaUiManager.showErrorMessage("No spawners are configured! Please add at least one spawner.");
        	}
        	else {	
        	 executeSpawners();
        	}
        }
    };
    
    /**
     * Removes all trees from the scene and clears the list of trees ready already spawned.
     */
    EventHandler<ActionEvent> clearTreeHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	JavaFxUI.getInstance().runInJmeThread(() -> {
        		treeGeomNode.detachAllChildren();
        	});
        	trees.clear();
        }
    };
    
	private void buildSpawnerView(Dialog<Void> parentDialog, VBox vBox){
		for(AbstractSpawnerDialog spawner : treeSpawners){
			HBox spawnerHBox = new HBox();
			spawnerHBox.setSpacing(10);
			String spawnerKey = spawner.getClass().getCanonicalName();
			Text spawnerNameText = new Text(spawnerKey);
			Button spawnerSettingsButton = new Button("Settings");
			spawnerSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
		        @SuppressWarnings("unchecked")
				@Override
		        public void handle(ActionEvent event) {
		        	Dialog<Void> spawnerSettingsDialog = spawnerSettingsDialogMap.get(spawner.toString());
		        	if (null == spawnerSettingsDialog){
		        		SettingsDialogBuilder<AbstractSpawnerDialog> dialogBuilder = new SettingsDialogBuilder<>();
		        		spawnerSettingsDialog = dialogBuilder.buildSettingDialog(spawner, (Class<AbstractSpawnerDialog>) spawner.getClass() , 480, 660);
		        		spawnerSettingsDialogMap.put(spawner.toString(), spawnerSettingsDialog);
		        	}
		        	else{
		        		if (!spawnerSettingsDialog.isShowing()){
		        			spawnerSettingsDialog.show();
		        		}
		        	}
		        }
		    });
			spawnerHBox.getChildren().addAll(spawnerNameText, spawnerSettingsButton);
			vBox.getChildren().add(spawnerHBox);
		}
	}
}
