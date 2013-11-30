package com.example.ntudepartmentdefense.gamesync;

import org.andengine.entity.Entity;

public class TowerLayer extends Entity{
	private GameSync gameSync;
	
	TowerLayer(GameSync gameSync){
		this.gameSync = gameSync;
	}
	
	//getter
	public GameSync getGameSync(){
		return gameSync;
	}
	
	public void allTowers(){
		int childrenSum = this.getChildCount();
		for (int i = 0; i < childrenSum; i++){
			//getChildByIndex(i);
		}
	}
}
