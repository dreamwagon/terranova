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
package com.dreamwagon.terranova.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.dreamwagon.terranova.generator.FastNoiseHeightmapGenerator;
import com.dreamwagon.terranova.generator.HillHeightMapTerrainGenerator;
import com.dreamwagon.terranova.generator.SplatMapGenerator;
import com.dreamwagon.terranova.io.SaveRequest;
import com.dreamwagon.terranova.io.TerranovaProject;
import com.dreamwagon.terranova.manager.TreeManager;
import com.dreamwagon.terranova.manager.TreePrototypeManager;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.dreamwagon.terranova.settings.TerrainTextureSettings;
import com.dreamwagon.terranova.util.IOUtil;
import com.dreamwagon.terranova.util.ImageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;

import io.tlf.jme.jfx.JavaFxUI;
import io.tlf.jme.jfx.util.JfxPlatform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * 
 * @author J. Demarco
 *
 */
public class TerranovaUiManager
{  
	public static float DEFAULT_FLY_CAM_SPEED = 200;
	
	public static float FLY_CAM_SPEED = DEFAULT_FLY_CAM_SPEED;
	
	public static Vector3f CAM_DEFAULT_LOCATION = new Vector3f(400, 350, 400);
	
	public static Vector3f CAM_DEFAULT_DIRECTION = new Vector3f(-0.525f, -0.65f, -0.550f);
	
	 // create a alert 
    public static Alert CONFIRM_EXIT_ALERT;
    
    public static Alert HELP_INFO_ALERT;
    
    public static Alert ERROR_ALERT = new Alert(AlertType.ERROR); 
	
    public static final FileChooser TEXTURE_FILE_CHOOSER = new FileChooser();
    
    public static final FileChooser MODEL_FILE_CHOOSER = new FileChooser();
    
    //public static String MAIN_MENU_BAR_ID = "main_menu_bar";
    
    public static String HBG_MENU_BAR_ID = "hbg_menu_bar";
    
    public static Map<String, Settings> persistentSettings = new HashMap<String, Settings>();
    public static Map<String, Dialog<Void>> persistentDialogMap = new HashMap<String, Dialog<Void>>();
    
    public static void showErrorMessage(String errorMessage) {
    	ERROR_ALERT.setContentText(errorMessage);
    	ERROR_ALERT.show();
    }
    
    static EventHandler<ActionEvent> quitHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	CONFIRM_EXIT_ALERT.show();
        }
    };
    
    static EventHandler<ActionEvent> resetCameraHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	TerranovaApp.INSTANCE.resetCameraToDefaults();
        }
    };
    
    static EventHandler<ActionEvent> helpHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	HELP_INFO_ALERT.show();
        }
    };
    
    static EventHandler<ActionEvent> quitOkHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	//Exit Terranova
        	TerranovaApp.INSTANCE.stop();
        }
    };
    
    /**
     * Open Terrain Manager Handler
     */
    static EventHandler<ActionEvent> openTerrainManagerHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

        	String terrainSettingsName = JMETerrainSettings.class.getCanonicalName();
        	JMETerrainSettings jmeTerrainSavedSettings = (JMETerrainSettings) persistentSettings.get(terrainSettingsName);

        	Dialog<Void> terrainSettingsDialog = persistentDialogMap.get(terrainSettingsName);
        	if (null == terrainSettingsDialog)
        	{
        		SettingsDialogBuilder<JMETerrainSettings> dialogBuilder = new SettingsDialogBuilder<JMETerrainSettings>();
        		terrainSettingsDialog = dialogBuilder.buildSettingDialog(jmeTerrainSavedSettings, JMETerrainSettings.class, 310, 770);
        		persistentDialogMap.put(terrainSettingsName, terrainSettingsDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!terrainSettingsDialog.isShowing())
        		{
        			terrainSettingsDialog.show();
        		}
        	}
        }
    };
    
    /**
     * Open Terrain Texture Manager Handler
     */
    static EventHandler<ActionEvent> openTerrainTextureManagerHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String terrainSettingsName = JMETerrainSettings.class.getCanonicalName();
        	String terrainTextureSettingsName = TerrainTextureSettings.class.getCanonicalName();
        	//
        	JMETerrainSettings jmeTerrainSavedSettings = (JMETerrainSettings) persistentSettings.get(terrainSettingsName);
        	TerrainTextureSettings terrainTextureSavedSettings = jmeTerrainSavedSettings.terrainTextureSettings;

        	Dialog<Void> terrainTextureSettingsDialog = persistentDialogMap.get(terrainTextureSettingsName);
        	if (null == terrainTextureSettingsDialog)
        	{
        		SettingsDialogBuilder<TerrainTextureSettings> dialogBuilder = new SettingsDialogBuilder<TerrainTextureSettings>();
        		terrainTextureSettingsDialog = dialogBuilder.buildSettingDialog(terrainTextureSavedSettings, TerrainTextureSettings.class, 260, 770);
        		persistentDialogMap.put(terrainTextureSettingsName, terrainTextureSettingsDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!terrainTextureSettingsDialog.isShowing())
        		{
        			terrainTextureSettingsDialog.show();
        		}
        	}
        }
    };
    
    /**
     * 
     */
    static EventHandler<ActionEvent> openTreePrototypeManagerHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String treePrototypeManagerName = TreePrototypeManager.class.getCanonicalName();
        	Dialog<Void> treePrototypeManagerDialog = persistentDialogMap.get(treePrototypeManagerName);
        	if (null == treePrototypeManagerDialog)
        	{
        		SettingsDialogBuilder<TreePrototypeManager> dialogBuilder = new SettingsDialogBuilder<TreePrototypeManager>();
        		treePrototypeManagerDialog = dialogBuilder.buildSettingDialog(TerranovaApp.INSTANCE.treePrototypeManager, 
        				TreePrototypeManager.class, 510, 590);
        		persistentDialogMap.put(treePrototypeManagerName, treePrototypeManagerDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!treePrototypeManagerDialog.isShowing())
        		{
        			treePrototypeManagerDialog.show();
        		}
        	}
        }
    };
    
    /**
     * 
     */
    static EventHandler<ActionEvent> openTreeManagerHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String treeManagerName = TreeManager.class.getCanonicalName();
        	Dialog<Void> treeManagerDialog = persistentDialogMap.get(treeManagerName);
        	if (null == treeManagerDialog)
        	{
        		SettingsDialogBuilder<TreeManager> dialogBuilder = new SettingsDialogBuilder<TreeManager>();
        		treeManagerDialog = dialogBuilder.buildSettingDialog(TerranovaApp.INSTANCE.treeManager, 
        				TreeManager.class, 510, 700);
        		persistentDialogMap.put(treeManagerName, treeManagerDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!treeManagerDialog.isShowing())
        		{
        			treeManagerDialog.show();
        		}
        	}
        }
    };
    
    static EventHandler<ActionEvent> openSplatmapGeneratorHandler= new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String splatmapGeneratorName = SplatMapGenerator.class.getCanonicalName();
        	Dialog<Void> splatmapGeneratorDialog = persistentDialogMap.get(splatmapGeneratorName);
        	if (null == splatmapGeneratorDialog)
        	{
        		SettingsDialogBuilder<SplatMapGenerator> dialogBuilder = new SettingsDialogBuilder<SplatMapGenerator>();
        		splatmapGeneratorDialog = dialogBuilder.buildSettingDialog(TerranovaApp.INSTANCE.splatMapGenerator, 
        				SplatMapGenerator.class, 480, 650);
        		persistentDialogMap.put(splatmapGeneratorName, splatmapGeneratorDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!splatmapGeneratorDialog.isShowing())
        		{
        			splatmapGeneratorDialog.show();
        		}
        	}
        }
    };
    static EventHandler<ActionEvent> openFastNoiseHeightmapGeneratorHandler= new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String heightmapGeneratorName = FastNoiseHeightmapGenerator.class.getCanonicalName();
        	Dialog<Void> heightmapGeneratorDialog = persistentDialogMap.get(heightmapGeneratorName);
        	if (null == heightmapGeneratorDialog)
        	{
        		SettingsDialogBuilder<FastNoiseHeightmapGenerator> dialogBuilder = new SettingsDialogBuilder<FastNoiseHeightmapGenerator>();
        		heightmapGeneratorDialog = dialogBuilder.buildSettingDialog(TerranovaApp.INSTANCE.fastNoiseHeightmapGenerator, 
        				FastNoiseHeightmapGenerator.class, 480, 650);
        		persistentDialogMap.put(heightmapGeneratorName, heightmapGeneratorDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!heightmapGeneratorDialog.isShowing())
        		{
        			heightmapGeneratorDialog.show();
        		}
        	}
        }
    };
    /**
     * 
     */
    static EventHandler<ActionEvent> openHillHeightmapTerrainGeneratorHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	String hillHeightMapTerrainGeneratorName = HillHeightMapTerrainGenerator.class.getCanonicalName();
        	Dialog<Void> hillHeightMapTerrainGeneratorDialog = persistentDialogMap.get(hillHeightMapTerrainGeneratorName);
        	if (null == hillHeightMapTerrainGeneratorDialog)
        	{
        		SettingsDialogBuilder<HillHeightMapTerrainGenerator> dialogBuilder = new SettingsDialogBuilder<HillHeightMapTerrainGenerator>();
        		hillHeightMapTerrainGeneratorDialog = dialogBuilder.buildSettingDialog(TerranovaApp.INSTANCE.hillHeightMapTerrainGenerator, 
        				HillHeightMapTerrainGenerator.class, 480, 380);
        		persistentDialogMap.put(hillHeightMapTerrainGeneratorName, hillHeightMapTerrainGeneratorDialog);
        	}
        	else
        	{
        		//We only show one instance of this dialog
        		if (!hillHeightMapTerrainGeneratorDialog.isShowing())
        		{
        			hillHeightMapTerrainGeneratorDialog.show();
        		}
        	}
        }
    };
    
    static EventHandler<ActionEvent> exportJ3OHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("J3O File", "*.j3o")
	            );
		    Node menuNode = JavaFxUI.getInstance().getChild(HBG_MENU_BAR_ID);
    		Window stage = menuNode.getScene().getWindow();
		    File selectedFile = fileChooser.showSaveDialog(stage);
		
		    if(selectedFile == null){
		         //No Directory selected
		    }else{
		         //System.out.println(selectedDirectory.getAbsolutePath());
		         SaveRequest saveRequest = new SaveRequest(TerranovaApp.INSTANCE.getRootNode(),
		        		 selectedFile.getAbsolutePath() );
		         TerranovaApp.INSTANCE.saveRequestQueue.add(saveRequest);
		    }
        }
    };
    
    /**
     * Save project
     */
    static EventHandler<ActionEvent> loadProjectHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	try {
    		    FileChooser fileChooser = new FileChooser();
    		    fileChooser.getExtensionFilters().addAll(
    	                new FileChooser.ExtensionFilter("JSON File", "*.json")
    	            );
    		    Node menuNode = JavaFxUI.getInstance().getChild(HBG_MENU_BAR_ID);
        		Window stage = menuNode.getScene().getWindow();
    		    File selectedFile = fileChooser.showOpenDialog(stage);
    		    
    		    if(selectedFile == null ){
	   		         //No Directory selected
	   		    }else{
	   		    	String json = Files.readString(Paths.get(selectedFile.getAbsolutePath()));
	        		TerranovaProject project = new ObjectMapper().readValue(json, TerranovaProject.class);
	        		project.loadProject();
	   		    }
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    };
    
    /**
     * Save project
     */
    static EventHandler<ActionEvent> saveProjectHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("JSON File", "*.json")
	            );
		    Node menuNode = JavaFxUI.getInstance().getChild(HBG_MENU_BAR_ID);
    		Window stage = menuNode.getScene().getWindow();
		    File selectedFile = fileChooser.showSaveDialog(stage);
		
		    if(selectedFile == null ){
		         //No Directory selected
		    }else{
		    	//Save the project
		    	IOUtil.saveProject(selectedFile);
		    }
        }
    };
    
    
    
    static EventHandler<ActionEvent> exportHeightmapHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("PNG File", "*.png")
	            );
		    Node menuNode = JavaFxUI.getInstance().getChild(HBG_MENU_BAR_ID);
    		Window stage = menuNode.getScene().getWindow();
		    File selectedFile = fileChooser.showSaveDialog(stage);
		
		    if(selectedFile == null ){
		         //No Directory selected
		    }else{
		         //System.out.println(selectedFile.getAbsolutePath());
		         TerrainQuad terrainQuad = TerranovaApp.INSTANCE.terrainManager.getTerrainQuad();
		         ImageUtil.saveHeightmapTextureFromTerrain(terrainQuad, selectedFile.getAbsolutePath());
		    }
        }
    };
    
	public static void createBaseUi()
	{

			CONFIRM_EXIT_ALERT = new Alert(AlertType.CONFIRMATION);
			CONFIRM_EXIT_ALERT.setTitle("Quit Confirmation");
			CONFIRM_EXIT_ALERT.setHeaderText("Are you sure you want to quit?");
			final Button okButton = (Button) CONFIRM_EXIT_ALERT.getDialogPane().lookupButton( ButtonType.OK );
			okButton.setOnAction(quitOkHandler);
			
			HELP_INFO_ALERT= new Alert(AlertType.INFORMATION);
			HELP_INFO_ALERT.setTitle("Terranova Help");
			HELP_INFO_ALERT.setHeaderText("");
			HELP_INFO_ALERT.getDialogPane().setContent(getHelpContent());
			//HELP_INFO_ALERT.setContentText("Key F - Toggle fly camera on/off");
			
			//File Choosers
			TEXTURE_FILE_CHOOSER.setTitle("Import Texture");
			TEXTURE_FILE_CHOOSER.setInitialDirectory(
                new File(TerranovaBaseAssetManager.USER_HOME_DIRECTORY)
            );                 
			TEXTURE_FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Texture Files", "*.jpg", "*.JPG", "*.jpeg", "*.JPEG",
                								"*.png", "*.PNG", "*.gif", "*.GIF", "*.tga", "*.TGA", 
                								"*.dds", "*.DDS", "*.hdr", "*.HDR", "*.pfm", "*.PFM", 
                								"*.bmp", "*.BMP"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
			
			MODEL_FILE_CHOOSER.setTitle("Import Model");
			MODEL_FILE_CHOOSER.setInitialDirectory(
                new File(TerranovaBaseAssetManager.USER_HOME_DIRECTORY)
            );                 
			MODEL_FILE_CHOOSER.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("Model Files", "*.gltf", "*.GLTF", "*.j3o", "*.J3O",
	                								"*.fbx", "*.FBX", "*.xbuf", "*.XBUF", "*.obj", "*.OBJ", 
	                								"*.xml", "*.XML", "*.meshxml", "*.MESHXML", "*.glb", "*.GLB"),
	                new FileChooser.ExtensionFilter("All Files", "*.*")
	            );
			
			//Create the 'main' top menu
			createMainMenu();         
	}
	
	/**
	 * Builds the main menu
	 */
	public static void createMainMenu()
	{		  
		JFXHamburger hamburger = new JFXHamburger();
		JFXDrawer drawer = new JFXDrawer();
		drawer.setPrefWidth(0);
		
		hamburger.setId(HBG_MENU_BAR_ID);

	    hamburger.setAlignment(Pos.TOP_LEFT);
	    hamburger.setPadding(new Insets(5));
	    hamburger.setStyle("-fx-background-color: #fff;");
	    HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
	    
	    hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            drawer.toggle();
        });
        drawer.setOnDrawerOpening((event) -> {
        	
        	task.setRate(task.getRate() * 1);
        	task.playFromStart();
            drawer.setMinWidth(220);
        });
        drawer.setOnDrawerClosed((event) -> {
        	
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.setMinWidth(0);
        });    
	    
		//File main menu item
		TreeItem<TreeMenuData> menuFile = new TreeItem<TreeMenuData>(new TreeMenuData("File", null));
		TreeItem<TreeMenuData> menuItemOpenProject = new TreeItem<TreeMenuData>(new TreeMenuData("Open Project",loadProjectHandler));

		
		//New Submenu
		TreeItem<TreeMenuData> menuItemSave = new TreeItem<TreeMenuData>(new TreeMenuData("Save Project",saveProjectHandler));
		//MenuItem menuItemSave = new MenuItem("Save Project");
		//menuItemSave.setOnAction(saveProjectHandler);
		
		TreeItem<TreeMenuData> menuExport = new TreeItem<TreeMenuData>(new TreeMenuData("Export",null));
		//Menu menuExport = new Menu("Export");
		
		TreeItem<TreeMenuData> menuItemExpRoot = new TreeItem<TreeMenuData>(new TreeMenuData("Root Node to jm3o (full scene)",exportJ3OHandler));
		TreeItem<TreeMenuData> menuItemExpHeightmapPng = new TreeItem<TreeMenuData>(new TreeMenuData("Terrain to Heightmap",exportHeightmapHandler));
		
		//MenuItem menuItemExpRoot = new MenuItem("Root Node to jm3o (full scene)");
		//menuItemExpRoot.setOnAction(exportJ3OHandler);   
		//MenuItem menuItemExpTerrainOnly = new MenuItem("Terrain Node to jm3o");
		//MenuItem menuItemExpTreesOnly= new MenuItem("Trees Node to jm3o");
		//MenuItem menuItemExpGrassOnly = new MenuItem("Grass Node to jm3o");
		//MenuItem menuItemExpObjectOnly = new MenuItem("Model Node to jm3o");
		//MenuItem menuItemExpHeightmapPng = new MenuItem("Terrain to Heightmap");
		//menuItemExpHeightmapPng.setOnAction(exportHeightmapHandler);   
		//MenuItem menuItemExpObjectJson = new MenuItem("All to json");
		
		menuExport.getChildren().add(menuItemExpRoot);
		menuExport.getChildren().add(menuItemExpHeightmapPng);
		//menuExport.getItems().add(menuItemExpTerrainOnly);
		//menuExport.getItems().add(menuItemExpTreesOnly);
		//menuExport.getItems().add(menuItemExpGrassOnly);
		//menuExport.getItems().add(menuItemExpObjectOnly);
		//menuExport.getItems().add(menuItemExpHeightmapPng);
		//menuExport.getChildren().add(menuItemExpObjectJson);
		
		TreeItem<TreeMenuData> menuItemQuit = new TreeItem<TreeMenuData>(new TreeMenuData("Quit",quitHandler));
		//MenuItem menuItemQuit = new MenuItem("Quit");
		//menuItemQuit.setOnAction(quitHandler);
		
		menuFile.getChildren().add(menuItemOpenProject);
		menuFile.getChildren().add(menuItemSave);
		menuFile.getChildren().add(menuExport);
		menuFile.getChildren().add(menuItemQuit);
		
		//View main menu item
		TreeItem<TreeMenuData> menuView = new TreeItem<TreeMenuData>(new TreeMenuData("View", null));
		//Menu menuView = new Menu("View");
		
		TreeItem<TreeMenuData> menuItemTerrainManager = new TreeItem<TreeMenuData>(new TreeMenuData("Terrain Manager", openTerrainManagerHandler));
		//MenuItem menuItemTerrainManager = new MenuItem("Terrain Manager");
		//menuItemTerrainManager.setOnAction(openTerrainManagerHandler);
		
		TreeItem<TreeMenuData> menuItemTreePrototypeManager = new TreeItem<TreeMenuData>(new TreeMenuData("Tree Prototype Manager", openTreePrototypeManagerHandler));
		//MenuItem menuItemTreePrototypeManager = new MenuItem("Tree Prototype Manager");
		//menuItemTreePrototypeManager.setOnAction(openTreePrototypeManagerHandler);
		
		TreeItem<TreeMenuData> menuItemTreeManager = new TreeItem<TreeMenuData>(new TreeMenuData("Tree & Grass Manager", openTreeManagerHandler));
		//MenuItem menuItemTreeManager = new MenuItem("Tree & Grass Manager");
		//menuItemTreeManager.setOnAction(openTreeManagerHandler);
		
		TreeItem<TreeMenuData> menuItemTerrainTextureManager = new TreeItem<TreeMenuData>(new TreeMenuData("Terrain Texture Manager", openTerrainTextureManagerHandler));
		//MenuItem menuItemTerrainTextureManager = new MenuItem("Terrain Texture Manager");
		//menuItemTerrainTextureManager.setOnAction(openTerrainTextureManagerHandler);
		
		//MenuItem menuItemObjectManager = new MenuItem("Object Manager");
		menuView.getChildren().add(menuItemTerrainManager);
		menuView.getChildren().add(menuItemTerrainTextureManager);
		menuView.getChildren().add(menuItemTreePrototypeManager);
		menuView.getChildren().add(menuItemTreeManager);
		//menuView.getItems().add(menuItemObjectManager);

		TreeItem<TreeMenuData> menuTools = new TreeItem<TreeMenuData>(new TreeMenuData("Tools", null));
		//Menu menuTools = new Menu("Tools");
		//MenuItem menuTerrainMixer = new MenuItem("Terrain Mixer");
		
		//TODO build terrain mixer interface
		//TreeItem<TreeMenuData> menuTerrainMixer = new TreeItem<TreeMenuData>(new TreeMenuData("Terrain Mixer", openTerrainTextureManagerHandler));

		TreeItem<TreeMenuData> menuTerrainGenerator = new TreeItem<TreeMenuData>(new TreeMenuData("Terrain Generators", null));
		//Menu menuTerrainGenerator = new Menu("Terrain Generators");
		menuTools.getChildren().add(menuTerrainGenerator);
		TreeItem<TreeMenuData> menuHillTerrainGenerator = new TreeItem<TreeMenuData>(new TreeMenuData("Hill Height Map Terrain Generator", openHillHeightmapTerrainGeneratorHandler));
		//MenuItem menuHillTerrainGenerator = new MenuItem("Hill Height Map Terrain Generator");
		//menuTerrainGenerator.setOnAction(openHillHeightmapTerrainGeneratorHandler);
		menuTerrainGenerator.getChildren().add(menuHillTerrainGenerator);
		
		TreeItem<TreeMenuData> menuTextureGenerators = new TreeItem<TreeMenuData>(new TreeMenuData("Texture Generators", null));
		//Menu menuTextureGenerators = new Menu("Texture Generators");
		menuTools.getChildren().add(menuTextureGenerators);
		
		TreeItem<TreeMenuData> menuSlatmapGenerator = new TreeItem<TreeMenuData>(new TreeMenuData("Slatmap Generator", openSplatmapGeneratorHandler));
		//MenuItem menuSlatmapGenerator = new MenuItem("Slatmap Generator");
		//menuSlatmapGenerator.setOnAction(openSplatmapGeneratorHandler);
		menuTextureGenerators.getChildren().add(menuSlatmapGenerator);
		
		TreeItem<TreeMenuData> menuFastNoiseHeightmapGenerator = new TreeItem<TreeMenuData>(new TreeMenuData("Fast Noise Heightmap Generator", openFastNoiseHeightmapGeneratorHandler));
		//MenuItem menuFastNoiseHeightmapGenerator = new MenuItem("Fast Noise Heightmap Generator");
		//menuFastNoiseHeightmapGenerator.setOnAction(openFastNoiseHeightmapGeneratorHandler);
		menuTextureGenerators.getChildren().add(menuFastNoiseHeightmapGenerator);
		
		TreeItem<TreeMenuData> menuHelp = new TreeItem<TreeMenuData>(new TreeMenuData("Help", null));
		//Menu menuHelp = new Menu("Help");
		TreeItem<TreeMenuData> menuHelpResetCamera = new TreeItem<TreeMenuData>(new TreeMenuData("Reset Camera", resetCameraHandler));
		//MenuItem menuHelpResetCamera = new MenuItem("Reset Camera");
		//menuHelpResetCamera.setOnAction(resetCameraHandler);
		TreeItem<TreeMenuData> menuViewHelp = new TreeItem<TreeMenuData>(new TreeMenuData("View Help", helpHandler));
		//MenuItem menuViewHelp = new MenuItem("View Help");
		//menuViewHelp.setOnAction(helpHandler);
		menuHelp.getChildren().addAll(menuHelpResetCamera, menuViewHelp);
		
		BorderPane borderPane = new BorderPane();
		AnchorPane anchorPane = new AnchorPane(hamburger);
		borderPane.setCenter(anchorPane);
		
		ScrollPane scrollPane = new ScrollPane();
		VBox vbox = new VBox();
		
		TreeItem<TreeMenuData> base = new TreeItem<TreeMenuData>(new TreeMenuData("Actions",null));
		base.setExpanded(true);
		base.getChildren().addAll(menuFile, menuView, menuTools, menuHelp);
	      
		TreeView<TreeMenuData> view = new TreeView<TreeMenuData>(base);
		vbox.getChildren().addAll(view);

		view.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
		    @Override
		    public void handle(MouseEvent mouseEvent)
		    {            
		        if(mouseEvent.getClickCount() == 2)
		        {
		            TreeItem item = (TreeItem)view.getSelectionModel().getSelectedItem();
		            TreeMenuData data = (TreeMenuData) item.getValue();
		            if (data.getEvent() != null)
		            {
		            	data.getEvent().handle(null);
		            }
		            //System.out.println("Selected Text : " + data.getName());

		        }
		    }
		});
		
	    scrollPane.setContent(vbox);
	    
	    
		borderPane.setLeft(drawer);
			
	   
	    drawer.setDefaultDrawerSize(230);
	    drawer.setSidePane(scrollPane);
        drawer.setMinWidth(0);
        
	    JavaFxUI.getInstance().attachChild(borderPane);
	    
        
		 JfxPlatform.runInFxThread(() ->{
			//Bind the menu bar width to the width of the window
			Window stage = borderPane.getScene().getWindow();
			hamburger.prefWidthProperty().bind(stage.widthProperty());
			borderPane.prefWidthProperty().bind(stage.widthProperty());
		 });
	}

	public static ScrollPane getHelpContent()
	{
		String boldStyle = "-fx-font-weight: bold;";
		VBox helpVBox = new VBox();
		helpVBox.setSpacing(10);
		ScrollPane helpScrollPane = new ScrollPane();
		helpScrollPane.setPrefSize(430, 300);
		helpScrollPane.setContent(helpVBox);
		
		Text keyMappingText = new Text("Key Mapping");
		keyMappingText.setStyle(boldStyle);
		
		Text textCam = new Text();
		textCam.setText("Key F - Toggle fly camera on/off");
		
		Text assetsText = new Text("Assets");
		assetsText.setStyle(boldStyle);

		Text externalAssetsHelpText = new Text("Terranova supports loading external assets. By default the user home "
				+ "directory is used. Custom assets (Textures, Models etc) should be placed in a folder struction under the "
				+ "user home directory." );
		externalAssetsHelpText.setWrappingWidth(400);
		
		Text genAssetText = new Text("Generated Assets");
		genAssetText.setStyle(boldStyle);
		
		Text genAssetHelpText = new Text("Generated textures such as alphamaps and heightmaps must be saved as assets and then "
				+ "imported into terrain settings. Direct linking of textures from generator to terrain settings is currently not supported." );
		genAssetHelpText.setWrappingWidth(400);
		
		Text exportSettingText = new Text("Export Settings");
		exportSettingText.setStyle(boldStyle);
		
		Text saveProjectHelpText = new Text("Saving and loading project is currently experimental. "
				+ "Only terrain and texture settings will be saved. "
				+ "To prevent data loss, please export scene to j3o as backup. Project "
				+ "save/load will be fully supported when terranova reaches beta." );
		saveProjectHelpText.setWrappingWidth(400);
		
		Text exportSettingHelpText = new Text("Terrain to heightmap - This option will save a "
				+ "heightmap png from the raw the terrain data (Commonly used to save generated terrain to heightmap). This will LIKELY be "
				+ "different from the heightmap image preview in the terrain settings dialog. "
				+ "To save the exact heightmap used to create the terrain use: Export-> Heightmap." );
		exportSettingHelpText.setWrappingWidth(400);
		
		helpVBox.getChildren().addAll(keyMappingText, textCam);
		helpVBox.getChildren().add(new Separator());
		helpVBox.getChildren().addAll(assetsText, externalAssetsHelpText, genAssetText, genAssetHelpText, exportSettingText, 
				saveProjectHelpText, exportSettingHelpText);
		return helpScrollPane;
	}
}
