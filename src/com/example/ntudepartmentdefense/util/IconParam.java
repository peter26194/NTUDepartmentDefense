package com.example.ntudepartmentdefense.util;

import org.andengine.opengl.texture.region.TiledTextureRegion;

public class IconParam{
	private TiledTextureRegion textureRegion;
	private String image;
	
	IconParam(String img){
		this.image = img;
	}
	//getters
	public String getImage(){
		return image;
	}
	
	public TiledTextureRegion getTextureRegion(){
		return textureRegion;
	}
	
	//setter
	public void setTextureRegion(TiledTextureRegion texture){
		textureRegion = texture;
	}
}
