package com.dreamwagon.terranova.generator;

import java.io.File;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.settings.JMETerrainSettings;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;
import com.dreamwagon.terranova.ui.TerranovaUiManager;
import com.dreamwagon.terranova.util.ImageUtil;
import com.dreamwagon.terranova.util.MathUtil;
import com.jayfella.fastnoise.FastNoise;
import com.jayfella.fastnoise.GradientPerturb;
import com.jayfella.fastnoise.NoiseLayer;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;

import io.tlf.jme.jfx.JavaFxUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Generates an alpha (Splat) map based on terrain heights or slopes.
 * 
 * @author J. Demarco
 *
 */
public class SplatMapGenerator extends AbstractSettingsDialog implements Generator<Texture>{

	@Setting(displayName="Red Threshold", settingType = SettingType.RANGED_FLOAT, floatRangeMin=.0f, floatRangeMax=512)
	public Float redThreshold = 100f;
	
	@Setting(displayName="Green Threshold", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=512)
	public Float greenThreshold = 50f;
	
	@Setting(displayName="Blue Threshold", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=512)
	public Float blueThreshold = 25f;

	@Setting(displayName="Red/Green Blend Distance", settingType = SettingType.FLOAT)
	public Float redGreenBlendDistance = 20f;
	
	@Setting(displayName="Green/Blue Blend Distance", settingType = SettingType.FLOAT)
	public Float greenBlueBlendDistance = 20f;
	
	@Setting(displayName="Use Noise", settingType = SettingType.BOOLEAN)
	public Boolean useNoise = Boolean.FALSE;
	
	@Setting(displayName="R Noise Scale", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=1)
	public Float rNoiseScale = .25f;
	
	@Setting(displayName="R Noise Depth", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=3)
	public Float rNoiseDepth = .25f;
	
	@Setting(displayName="G Noise Scale", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=1)
	public Float gNoiseScale = .25f;
	
	@Setting(displayName="G Noise Depth", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=3)
	public Float gNoiseDepth = .25f;
	
	@Setting(displayName="B Noise Scale", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=1)
	public Float bNoiseScale = .25f;
	
	@Setting(displayName="B Noise Depth", settingType = SettingType.RANGED_FLOAT, floatRangeMin=0f, floatRangeMax=3)
	public Float bNoiseDepth = .25f;
	
	private ImageView splatPreview = new ImageView();
	private Texture splatTexture = null;
	
	NoiseLayer noiseLayerR = new NoiseLayer();
	NoiseLayer noiseLayerG = new NoiseLayer();
	NoiseLayer noiseLayerB = new NoiseLayer();
	
	public SplatMapGenerator(){
		createNoiseLayer(noiseLayerR);
		createNoiseLayer(noiseLayerG);
		createNoiseLayer(noiseLayerB);
	}
	
	public void createNoiseLayer(NoiseLayer noiseLayer)
	{
		noiseLayer.setFrequency(0.02f);
		noiseLayer.setInterp(FastNoise.Interp.Quintic);
		noiseLayer.setFractalOctaves(5);
		noiseLayer.setFractalLacunarity(1.7f);
		noiseLayer.setFractalGain(0.6f);
        noiseLayer.setSeed(FastMath.nextRandomInt());
        noiseLayer.setNoiseType(FastNoise.NoiseType.PerlinFractal);
        noiseLayer.setGradientPerturb(GradientPerturb.Fractal);
        noiseLayer.setFractalType(FastNoise.FractalType.FBM);
        noiseLayer.setGradientPerturbAmp(30);
	}
	@Override
	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node) {
		VBox vBox = (VBox)node;
		
		vBox.getChildren().addAll(splatPreview);
	}
	
	@Override
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node) {
		VBox vBox = (VBox)node;
		HBox buttonHbox = new HBox();
		Button genSplatButton = new Button("Generate Splat Map");
		genSplatButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	        	applySettings();
	        	splatTexture= generate();
	        	ImageUtil.refreshImageView(splatPreview, splatTexture, 128, 128);
	        }
	    });
		Button saveButton = new Button("Save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
			    FileChooser fileChooser = new FileChooser();
			    fileChooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("PNG File", "*.png")
		            );
			    Node menuNode = JavaFxUI.getInstance().getChild(TerranovaUiManager.HBG_MENU_BAR_ID);
	    		Window stage = menuNode.getScene().getWindow();
			    File selectedFile = fileChooser.showSaveDialog(stage);
			
			    if(selectedFile == null){
			         //No Directory selected
			    }else{
			    	if (splatTexture != null) {
			    		ImageUtil.saveTexture(splatTexture, selectedFile.getAbsolutePath());
			    	}
			    }
	        }
	    });
		buttonHbox.getChildren().addAll(genSplatButton, saveButton);
		vBox.getChildren().addAll(buttonHbox);
	}
	
	@Override
	public Texture generate() {
		TerrainQuad terrainQuad = TerranovaApp.INSTANCE.terrainManager.getTerrainQuad();
    	float[] heightmaps  =  terrainQuad.getHeightMap();
    	int size = (int)Math.sqrt(heightmaps.length);
    	
		Image splatImage = new Image(Format.BGR8, size, size, BufferUtils.createByteBuffer(size * size * 4), null, ColorSpace.Linear);
		ImageRaster splatImageRaster = ImageRaster.create(splatImage);
		int counter = 0;
		
		float rgRange = redGreenBlendDistance/2;
		float gbRange = greenBlueBlendDistance/2;
		
		float redLimit = redThreshold + rgRange;
		float redGreenLimit = redThreshold - rgRange;

		float greenLimit = greenThreshold + gbRange;
		float greenBlueLimit = greenThreshold - rgRange;
		
		for (int y = size -1; y >= 0; y--) {
            for (int x = 0; x < size; x++) {
            	
            	ColorRGBA colorInterp = new ColorRGBA(); 	
            	float height = heightmaps[counter];
            	
            	if (height >= redLimit)
            	{
            		colorInterp = ColorRGBA.Red.clone();
            	}
            	//Simple interpolation
            	else if (height < redLimit && height >= redThreshold)
            	{
            		float green = 1-MathUtil.normalize(height, redThreshold, redLimit);
            		colorInterp = new ColorRGBA( 1, green, 0, 1);
            	}
            	else if (height < redThreshold && height >= redGreenLimit )
            	{
            		float red = MathUtil.normalize(height, redGreenLimit, redThreshold);
            		colorInterp = new ColorRGBA( red, 1, 0, 1);
            	}
            	else if (height < redGreenLimit && height >= greenLimit)
            	{
            		colorInterp = ColorRGBA.Green.clone();
            	}
            	else if (height < greenLimit && height >= greenThreshold)
            	{
            		float blue = 1-MathUtil.normalize(height, greenThreshold, greenLimit);
            		colorInterp = new ColorRGBA( 0, 1, blue, 1);
            	}
            	else if (height < greenThreshold && height >= greenBlueLimit )
            	{
            		float green = MathUtil.normalize(height, greenBlueLimit, greenThreshold);
            		colorInterp = new ColorRGBA( 0, green, 1, 1);
            	}
            	else 
            	{
            		colorInterp = ColorRGBA.Blue.clone();
            	}
            	
            	if (useNoise) {
            		Vector3f f = new Vector3f(x * rNoiseScale, y * rNoiseScale, 0.5f);
            		float noiseR = FastMath.clamp(noiseLayerR.getPrimaryNoise().getNoise(f.x, f.y, f.z) * rNoiseDepth, -1, 1);
            		float noiseG = FastMath.clamp(noiseLayerG.getPrimaryNoise().getNoise(f.x, f.y, f.z) * gNoiseDepth, -1, 1);
            		float noiseB = FastMath.clamp(noiseLayerB.getPrimaryNoise().getNoise(f.x, f.y, f.z) * bNoiseDepth, -1, 1);
            		
            		noiseR = FastMath.abs(noiseR);
            		noiseG = FastMath.abs(noiseG);
            		noiseB = FastMath.abs(noiseB);
            		colorInterp.r += noiseR;
            		colorInterp.g += noiseG;
            		colorInterp.b += noiseB;
            	}
            	//Finally set the pixel
            	splatImageRaster.setPixel(x, y, colorInterp);
            	
            	counter++;
            }
        }

		return new Texture2D(splatImage);
	}

	@Override
	public String getName() {
		return SplatMapGenerator.class.getCanonicalName();
	}

}
