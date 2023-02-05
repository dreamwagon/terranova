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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.dreamwagon.terranova.model.TreePrototype;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.dreamwagon.terranova.util.ModelUtil;

import com.jme3.scene.Spatial;

import io.tlf.jme.jfx.JavaFxUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Manages trees that are available to be placed via @Spawner types.
 * 
 * @author J. Demarco
 *
 */
public class TreePrototypeManager extends AbstractSettingsDialog{

	private List<TreePrototype> treePrototypeList = new ArrayList<TreePrototype>();
	
	public void setDefaults()
	{
		treePrototypeList.clear();
		treePrototypeList.add(new TreePrototype(TerranovaBaseAssetManager.SAMPLE_TREE_1));
		treePrototypeList.add(new TreePrototype(TerranovaBaseAssetManager.SAMPLE_TREE_2));
		treePrototypeList.add(new TreePrototype(TerranovaBaseAssetManager.SAMPLE_TREE_3));
		treePrototypeList.add(new TreePrototype(TerranovaBaseAssetManager.SAMPLE_TREE_4));
		treePrototypeList.add(new TreePrototype(TerranovaBaseAssetManager.SAMPLE_TREE_5));
	}
	/**
	 * Returns a random tree @TreePrototype from the list
	 * @return
	 */
	public TreePrototype getRandomTree(ThreadLocalRandom tlRandom) {
		
		int index = tlRandom.nextInt(0, treePrototypeList.size());
		return treePrototypeList.get(index);
	}
	@Override
	public String getName() {
		return TreePrototypeManager.class.getCanonicalName();
	}
	@Override
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node) {

		VBox vBox = (VBox)node;		
		VBox innerVBox = new VBox();
		
		Button importButton = new Button("Add Tree");
		importButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	 	
		        	//Get the stage
	        		Node sourceNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.HBG_MENU_BAR_ID);
	        		Window stage = sourceNode.getScene().getWindow();
		        	File file = TerranovaUiManager.MODEL_FILE_CHOOSER.showOpenDialog(stage);
		            if (file != null) {
		            	
		            	try {
		            		Spatial newSpatial = ModelUtil.loadModelFile(file);
		            		TreePrototype treePrototype = new TreePrototype(newSpatial);
		            		treePrototypeList.add(treePrototype);
		    	        	innerVBox.getChildren().clear();
		    	        	buildTreePrototypeView(parentDialog, innerVBox);

						} catch (Exception e) {
							TerranovaUiManager.showErrorMessage("Unable to load Model: " + e.getMessage());
							e.printStackTrace();
						} 
		            }
	        }
	    });
		Button resetButton = new Button("Reset to Defaults");
		resetButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	setDefaults();
	        	innerVBox.getChildren().clear();
	        	buildTreePrototypeView(parentDialog, innerVBox);
	        }
		});
		
		vBox.getChildren().addAll(importButton, resetButton, innerVBox);
		buildTreePrototypeView(parentDialog, innerVBox);
	}
	
	private void buildTreePrototypeView(Dialog<Void> parentDialog, VBox vBox)
	{
		for(TreePrototype tp : treePrototypeList){
			String treePrototypeKey = tp.model.getKey().getName();
			HBox treePrototypeHBox = new HBox();
			treePrototypeHBox.setId(treePrototypeKey);
			treePrototypeHBox.prefWidthProperty().bind(parentDialog.widthProperty());
			
			//Separator separator = new Separator();
			//separator.setOrientation(Orientation.HORIZONTAL);
			Button deleteButton = new Button("Remove");

			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
		        @Override
		        public void handle(ActionEvent event) {
		        	vBox.getChildren().remove(treePrototypeHBox);
		        	treePrototypeList.remove(tp);
		        }
			});
			Label treeInfoLabel = new Label(treePrototypeKey);
			treeInfoLabel.setWrapText(true);
			treeInfoLabel.prefWidthProperty().bind(parentDialog.widthProperty().subtract(100));
			treePrototypeHBox.getChildren().addAll(treeInfoLabel, deleteButton);
			
			vBox.getChildren().add(treePrototypeHBox);
		}
	}
}
