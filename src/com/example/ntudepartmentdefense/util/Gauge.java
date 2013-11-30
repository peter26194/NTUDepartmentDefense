package com.example.ntudepartmentdefense.util;


import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;

import com.example.ntudepartmentdefense.manager.ResourceManager;

public class Gauge extends Entity {
	private Rectangle frontRec;
	private Rectangle backRec;
	private int width;
	private int height;
	private String frontColor;
	private String backColor;
	private float initRatio;

	public Gauge( int width, int height , String frontColor, String backColor, float initRatio) {
		// the upper layer constructor
		super(0, 0);
		
		this.width = width;
		this.height = height;
		this.frontColor = frontColor;
		this.backColor = backColor;
		this.initRatio = initRatio;
		
		this.backRec = new Rectangle(0, 0, width, height, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		this.frontRec = new Rectangle(0, 0, width*initRatio, height, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		
		float[] rgb = ResourceManager.getInstance().hex2rgb(backColor);
		this.backRec.setColor(rgb[0], rgb[1], rgb[1]);
		rgb = ResourceManager.getInstance().hex2rgb(frontColor);
		this.frontRec.setColor(rgb[0], rgb[1], rgb[1]);
		
		this.attachChild(backRec);
		this.attachChild(frontRec);
	}
	
	public void setRatio( float ratio) {
		this.initRatio = ratio;
		frontRec.setWidth(width*initRatio);
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	
	public void onDestory() {
		// DO NOTHING
	}
}
