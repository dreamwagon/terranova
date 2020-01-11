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
package com.dreamwagon.terranova.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.dreamwagon.terranova.TerranovaApp;
import com.dreamwagon.terranova.TerranovaBaseAssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * 
 * @author J. Demarco
 *
 */
public class ImageUtil {

    public static Texture loadTextureFile(File file) throws IllegalArgumentException, IllegalAccessException
    {
    	String path = file.getAbsolutePath();
    	String assetMgrRelativePath = path.replace(TerranovaBaseAssetManager.USER_HOME_DIRECTORY, "");
    		
    	return TerranovaApp.INSTANCE.getAssetManager().loadTexture(assetMgrRelativePath);
    }

	public static Image convertJmeTextureToJfxImage(Texture texture){
		com.jme3.texture.Image jmeImage = texture.getImage();
		return copyPixels(jmeImage);
	}
	
	public static WritableImage copyPixels(com.jme3.texture.Image jmeImage){
		ImageRaster r = ImageRaster.create(jmeImage);
		
		int width = jmeImage.getWidth();
		int height = jmeImage.getHeight();
		WritableImage writableImage = new WritableImage(width, height);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
        
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
            	ColorRGBA jmeColor = r.getPixel(x, y);
                Color color = new Color(jmeColor.r, jmeColor.g, jmeColor.b, jmeColor.a);
                pixelWriter.setColor(x, y, color);
            }
        }
        return writableImage;
	}
	
    public static Texture loadTexureFromImageFile(File file)
    {
    	Texture2D texture2D = null;
    	try {
    		BufferedImage bufferedImage = ImageIO.read(file); 
    		com.jme3.texture.Image jmeImage = new AWTLoader().load(bufferedImage, false);
    		texture2D = new Texture2D();
    		texture2D.setImage(jmeImage);
    		
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return texture2D;
    }
    
    /**
     * 
     * @param terrain
     * @param path
     * @param fileName (PNG)
     */
    public static void saveTexture( Texture texture, String filePath)
    {
    	com.jme3.texture.Image jmeimage = texture.getImage();
    	ImageRaster raster = ImageRaster.create(jmeimage);
    	int size = jmeimage.getHeight();
    	BufferedImage image = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
    	java.awt.Color tmpColor = null;
    	for (int x = 0; x < size ; x++) {
    		for (int y = 0; y < size ; y++) {
    			ColorRGBA color = raster.getPixel(x, y);
    			tmpColor = new java.awt.Color(color.r, color.g, color.b); 
    			image.setRGB(x, y, tmpColor.getRGB());
    		}
    	}
	    File outputfile = new File(filePath);
	    try {
	        ImageIO.write(image, "png", outputfile);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
    }
    
    /**
     * 
     * @param terrain
     * @param path
     * @param fileName (PNG)
     */
    public static void saveHeightmapTextureFromTerrain( TerrainQuad terrain, String filePath)
    {
    	float[] heightmaps  =  terrain.getHeightMap();
    	int size = (int)Math.sqrt(heightmaps.length);
    	       
        int counter = 0;
    	BufferedImage image = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
    	java.awt.Color tmpColor = null;
    	for (int x = 0; x < size ; x++) {
    		for (int y = 0; y < size ; y++) {
    			int color = Math.round(heightmaps[counter]); 
    			tmpColor = new java.awt.Color(color, color, color); 
    			image.setRGB(y, x, tmpColor.getRGB());
    			counter = counter+1;
    		}
    	}
	    File outputfile = new File(filePath);
	    try {
	        ImageIO.write(image, "png", outputfile);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
    }
    public static void refreshImageView(ImageView imageView, Texture texture)
    {
    	refreshImageView(imageView, texture, 64, 64);
    }
    
    public static void refreshImageView(ImageView imageView, Texture texture, int height, int width)
    {
		Image image = ImageUtil.convertJmeTextureToJfxImage(texture);
		imageView.setImage(image);
		imageView.setFitHeight(height);
		imageView.setFitWidth(width);
    }
    
    /**
     * 
     * @param texture1
     * @param texture1ColorScalar
     * @param texture2
     * @param texture2ColorScalar
     * @return new Texture
     */
    public static Texture combineTextures(Texture texture1, float texture1ColorScalar, Texture texture2, float texture2ColorScalar) {
    	com.jme3.texture.Image image1 = texture1.getImage();
    	com.jme3.texture.Image image2 = texture2.getImage();
    	int size = image1.getWidth();
    	
    	com.jme3.texture.Image combinedImage = new com.jme3.texture.Image(Format.BGR8, size, size, 
    			BufferUtils.createByteBuffer(size * size * 4), null, ColorSpace.Linear);
    	ImageRaster combineImageRaster = ImageRaster.create(combinedImage);
    	ImageRaster image1Raster = ImageRaster.create(image1);
    	ImageRaster image2Raster = ImageRaster.create(image2);
    	    	
		for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
            	ColorRGBA iColor1 = image1Raster.getPixel(x, y);
            	ColorRGBA iColor2 = image2Raster.getPixel(x, y);
            	ColorRGBA iColor1Mult = iColor1.mult(texture1ColorScalar);
            	ColorRGBA iColor2Mult = iColor2.mult(texture2ColorScalar);
            	combineImageRaster.setPixel(x, y, iColor1Mult.add(iColor2Mult));
            }
		}
    	return new Texture2D(combinedImage);
    }
}
