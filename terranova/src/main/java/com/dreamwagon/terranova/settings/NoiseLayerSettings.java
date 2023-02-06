package com.dreamwagon.terranova.settings;

import com.dreamwagon.terranova.fastnoise.FastNoise.CellularDistanceFunction;
import com.dreamwagon.terranova.fastnoise.FastNoise.CellularReturnType;
import com.dreamwagon.terranova.fastnoise.FastNoise.FractalType;
import com.dreamwagon.terranova.fastnoise.FastNoise.Interp;
import com.dreamwagon.terranova.fastnoise.FastNoise.NoiseType;
import com.dreamwagon.terranova.fastnoise.GradientPerturb;
import com.dreamwagon.terranova.fastnoise.NoiseLayer;
import com.dreamwagon.terranova.generator.Generator;
import com.dreamwagon.terranova.ui.AbstractSettingsDialog;
import com.dreamwagon.terranova.ui.Setting;
import com.dreamwagon.terranova.ui.SettingType;

/**
 * Noise Layer settings
 * 
 * @author J. Demarco
 *
 */
public class NoiseLayerSettings extends AbstractSettingsDialog implements Generator<NoiseLayer>{

	public NoiseLayer noiseLayer;

	@Setting(displayName="Layer Weight", settingType = SettingType.RANGED_FLOAT, floatRangeMin=.0f, floatRangeMax=1f)
	public Float layerWeight = 1f;
	
	@Setting(displayName="Seed", settingType = SettingType.INTEGER)
	public Integer seed = 1;
	
	@Setting(displayName="Frequency", settingType = SettingType.FLOAT)
	public Float frequency = 0.01f;
	
	@Setting(displayName="Strength", settingType = SettingType.INTEGER)
	public Integer strength = 1;
	
	@Setting(displayName="Noise Type", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"Value", "ValueFractal", "Perlin", "PerlinFractal", "Simplex", "SimplexFractal", "Cellular", "WhiteNoise", "Cubic", "CubicFractal"})
	public String noiseType = "Simplex";
	
	@Setting(displayName="Interp", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"Linear", "Hermite", "Quintic"})
	public String interp = "Quintic";
	
	@Setting(displayName="Fractal Type", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"FBM", "Billow", "RigidMulti"})
	public String fractalType = "FBM";
	
	@Setting(displayName="Cellular Distance Function", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"Euclidean", "Manhattan", "Natural"})
	public String cellularDistanceFunction = "Euclidean";
	
	@Setting(displayName="Cellular Return Type", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"CellValue", "NoiseLookup", "Distance", "Distance2", "Distance2Add", "Distance2Sub", "Distance2Mul", "Distance2Div"})
	public String cellularReturnType = "CellValue";
	
	@Setting(displayName="Gradien tPerturb", settingType = SettingType.PREDEFINED_ENUMERATION,
			predefinedListValues= {"Off", "On", "Fractal"})
	public String gradientPerturb = "Off";
	 
	@Setting(displayName="Fractal Octaves", settingType = SettingType.INTEGER)
	public Integer fractalOctaves = 8;
	
	@Setting(displayName="Gradient Perturb Amp", settingType = SettingType.FLOAT)
	public Float gradientPerturbAmp = 2.2f;
	
	@Setting(displayName="Scale X", settingType = SettingType.FLOAT)
	public Float scaleX = 1f;
	
	@Setting(displayName="Scale Y", settingType = SettingType.FLOAT)
	public Float scaleY = 1f;
	
	@Override
	public String getName() {
		return toString();
	}

	@Override
	public NoiseLayer generate() {
		noiseLayer = new NoiseLayer();
		noiseLayer.setSeed(seed);
		noiseLayer.setFrequency(frequency);
		noiseLayer.setInterp(Interp.valueOf(interp));
		noiseLayer.setNoiseType(NoiseType.valueOf(noiseType));
		noiseLayer.setStrength(strength);
		noiseLayer.setFractalOctaves(fractalOctaves);
		noiseLayer.setFractalType(FractalType.valueOf(fractalType));
		noiseLayer.setCellularDistanceFunction(CellularDistanceFunction.valueOf(cellularDistanceFunction));
		noiseLayer.setCellularReturnType(CellularReturnType.valueOf(cellularReturnType));
		noiseLayer.setGradientPerturb(GradientPerturb.valueOf(gradientPerturb));
		noiseLayer.setGradientPerturbAmp(gradientPerturbAmp);
		noiseLayer.setScale(scaleX, scaleY);
		return noiseLayer;
	}
}
