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

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.RangeSlider;

import com.dreamwagon.terranova.util.Rangef;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * 
 * @author J. Demarco
 *
 */
public abstract class AbstractSettingsDialog implements Settings{
	
	protected List<FieldWrapper> fieldWrapperList = new ArrayList<FieldWrapper>();

	public void beforeBuildSettings(Dialog<Void> parentDialog, Node node) {}
	
	public void afterBuildSettings(Dialog<Void> parentDialog, Node node) {}
	
	public List<FieldWrapper> getFieldWrapperList() {
		return fieldWrapperList;
	}
	
	/**
	 * Apply the settings from the inputs to associated field of the calling settings instance.
	 */
	public void applySettings()
	{
		String nodeValue = null;
		Rangef rangedfloatValue = null;
		for (FieldWrapper fieldWrapper : fieldWrapperList)
		{
			if (fieldWrapper.node instanceof TextField){
				nodeValue = ((TextField)fieldWrapper.node).getText();
			}
			else if (fieldWrapper.node instanceof Slider){
				nodeValue = String.valueOf(((Slider)fieldWrapper.node).getValue());
			}
			else if (fieldWrapper.node instanceof ChoiceBox) {
				nodeValue = ((ChoiceBox)fieldWrapper.node).getValue().toString();
			}
			else if (fieldWrapper.node instanceof RangeSlider) {
				double valueMin = ((RangeSlider)fieldWrapper.node).getLowValue();
				double valueMax = ((RangeSlider)fieldWrapper.node).getHighValue();
				rangedfloatValue = new Rangef((float)valueMin, (float)valueMax);
			}
			//If Texture/ToggleTexture/TerrainTexure do nothing
			
			try {
				if (Float.class.isAssignableFrom(fieldWrapper.field.getType())){
					Float nodeFloatValue = Float.valueOf(nodeValue);
					fieldWrapper.field.set(this, nodeFloatValue);
				}
				else if (Integer.class.isAssignableFrom(fieldWrapper.field.getType())){
					Integer nodeIntValue = Integer.valueOf(nodeValue);
					fieldWrapper.field.set(this, nodeIntValue);
				}
				else if (Long.class.isAssignableFrom(fieldWrapper.field.getType())){
					Long nodeIntValue = Long.valueOf(nodeValue);
					fieldWrapper.field.set(this, nodeIntValue);
				}
				else if (String.class.isAssignableFrom(fieldWrapper.field.getType())){
					fieldWrapper.field.set(this, nodeValue);
				}
				else if (Rangef.class.isAssignableFrom(fieldWrapper.field.getType())){
					fieldWrapper.field.set(this, rangedfloatValue);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	
	}
}
