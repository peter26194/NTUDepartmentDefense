package com.example.ntudepartmentdefense.util;

public class BulletParam extends IconParam{
	private float speed;//moving distance in one frame; in unit of grid
	private int damage;
	private int wayOfFlying;
		public static final int HEAD_FIRST = 0;
		public static final int RETOTATE_BY_CENTER = 1;
	
	public BulletParam(float speed, int damage, int wayOfFlying, String img){
		super(img);
		this.speed= speed;
		this.damage = damage;
		this.wayOfFlying = wayOfFlying;
	}
	
	//getters
	public float getSpeed(){
		return speed;
	}
	public int getDamage(){
		return damage;
	}
	public int getWayOfFlying(){
		return wayOfFlying;
	}
}
