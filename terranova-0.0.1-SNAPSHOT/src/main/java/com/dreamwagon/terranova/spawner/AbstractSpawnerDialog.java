package com.dreamwagon.terranova.spawner;

import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class AbstractSpawnerDialog extends AbstractSettingsDialog implements Spawner<Object>{

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public Object spawn(TerrainQuad t) {
		return null;
	}

}
