package com.dreamwagon.terranova.io;

import com.jme3.scene.Node;

/**
 * Represents a request to save a node to j3o file.
 * 
 * @author J. Demarco
 *
 */
public class SaveRequest {

	public Node node;
	public String filePath;
	
	public SaveRequest(Node node, String filePath) {
		super();
		this.node = node;
		this.filePath = filePath;
	}
}
