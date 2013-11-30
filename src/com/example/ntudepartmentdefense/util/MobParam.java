package com.example.ntudepartmentdefense.util;

public class MobParam extends IconParam{
	private int duration;
	private int defense;
	private int maxHp;
	
	public MobParam(int duration, int defense, int hp, String img){
		super(img);
		
		this.duration= duration;
		this.defense = defense;
		this.maxHp = hp;
	}
	
	//getters
	public int getDuration(){
		return duration;
	}
	public int getDefense(){
		return defense;
	}
	public int getMaxHP(){
		return maxHp;
	}
}
