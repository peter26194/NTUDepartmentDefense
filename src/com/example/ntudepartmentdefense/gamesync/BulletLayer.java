package com.example.ntudepartmentdefense.gamesync;

import org.andengine.entity.Entity;

public class BulletLayer  extends Entity{
	private GameSync gameSync;
	
	BulletLayer(GameSync gameSync){
		this.gameSync = gameSync;
	}
	
	//getter
	public GameSync getGameSync(){
		return gameSync;
	}
}
