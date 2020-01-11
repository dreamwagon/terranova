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
package com.dreamwagon.terranova.model;

import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class TerrainTexture extends ToggleTexture{

	public static final float DEFAULT_TEXTURE_SCALE = 64f;
	public static final WrapMode DEFAULT_TEXTURE_WRAP_MODE = WrapMode.Repeat;
	
	public float scale;
	
	public WrapMode wrapMode = WrapMode.Repeat;
	
	public TerrainTexture() {
		this.scale = DEFAULT_TEXTURE_SCALE;
		this.wrapMode = DEFAULT_TEXTURE_WRAP_MODE;
	}
	
	public TerrainTexture(Texture texture) {
		super(texture);
		this.scale = DEFAULT_TEXTURE_SCALE;
		this.wrapMode = DEFAULT_TEXTURE_WRAP_MODE;
	}
	
	public TerrainTexture(Texture texture, float scale, WrapMode wrapMode) {
		super(texture);
		this.scale = scale;
		this.wrapMode = wrapMode;
	}
	
	public TerrainTexture(Texture texture, float scale, WrapMode wrapMode, boolean active) {
		super(texture, active);
		this.scale = scale;
		this.wrapMode = wrapMode;
	}


}
