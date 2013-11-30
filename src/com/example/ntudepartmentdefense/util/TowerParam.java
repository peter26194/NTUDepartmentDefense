package com.example.ntudepartmentdefense.util;

public class TowerParam extends IconParam{
	private float range;
	private int bulletType;
	private int reloadCD;
	private int buildCD;
	private int removeCD;
	
	public TowerParam(float range, int bullet, int reloadCD, int bulidCD, int removeCD, String img){
		super(img);
		
		this.range = range;
		this.bulletType = bullet;
		this.reloadCD = reloadCD;
		this.buildCD = bulidCD;
		this.removeCD = removeCD;
	}
	
	//getters
	public float getRange(){
		return range;
	}
	public int getBullet(){
		return bulletType;
	}
	public int getReloadCD(){
		return reloadCD;
	}
	public int getBuildCD(){
		return buildCD;
	}
	public int getRemoveCD(){
		return removeCD;
	}
	
}
