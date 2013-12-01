package com.example.ntudepartmentdefense.util;

import com.example.ntudepartmentdefense.manager.DataManager;

public class TowerParam extends IconParam{
	private float range;
	private int bulletType;
	private int reloadCD;
	private int buildCD;
	private int removeCD;
	private int upperLevelTower;//-1 if no upper-level tower
	private int buildCost;
	
	public TowerParam(float range, int bullet, int reloadCD, int bulidCD, int removeCD, int upperLevelTower, int buildCost, String img){
		super(img);
		
		this.range = range;
		this.bulletType = bullet;
		this.reloadCD = reloadCD;
		this.buildCD = bulidCD;
		this.removeCD = removeCD;
		
		this.upperLevelTower = upperLevelTower;
		this.buildCost = buildCost;
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
	public int getUpperLevelTower(){
		return upperLevelTower;
	}
	public int getBuildCost() {
		return buildCost;
	}
}
