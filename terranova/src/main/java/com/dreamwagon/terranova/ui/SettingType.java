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

public enum SettingType {

	//Specific types. Not validated. 
	//They create simple inputs in the UI
	INTEGER(0), 
	STRING(1),
	FLOAT(2),
	LONG(3),
	VECTOR2F(4),
	VECTOR3F(5),
	VECTOR4F(6),
	
	//Ranged types 
	//These create slider input(s) in the 
	//UI to pick a value with in a range
	RANGED_FLOAT(7),
	RANGED_VECTOR2(8),
	RANGED_VECTOR3(9),
	
	//Two value range
	//These create two inputs to select a min an max value
	//Most useful in number generation
	//The field should be defined as a Range, Rangef or
	//RangeV
	TWO_VAL_RANGED_INTEGER(10),
	TWO_VAL_RANGED_FLOAT(11),
	TWO_VAL_RANGED_VECTOR3F(12),
	
	//predefined types 
	//These create a select box in the UI
	PREDEFINED_INTEGER_LIST(13),
	PREDEFINED_FLOAT_LIST(14),
	PREDEFINED_LONG_LIST(15),
	PREDEFINED_STRING_LIST(16),
	PREDEFINED_ENUMERATION(17),
	PREDEFINED_COLOR_LIST(18),
	
	//Should only be placed on
	//com.jme3.texture.Texture
	TEXTURE(19),
	//Should only be placed on
	//ToggleTexture
	TOGGLE_TEXTURE(20),
	//Should only be placed on
	//TerrainTexture
	TERRAIN_TEXTURE(21),
	
	//Creates a checkbox
	BOOLEAN(22),
	
	READ_ONLY(23);
	
	private int index =0;

	  SettingType(int index) {
	    this.index = index;
	  }
	  
	  public int getIndex() {
		return index;
	}
}
