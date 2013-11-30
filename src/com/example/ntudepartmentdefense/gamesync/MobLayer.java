package com.example.ntudepartmentdefense.gamesync;

import java.util.LinkedList;
import java.util.Queue;

import org.andengine.entity.Entity;

public class MobLayer extends Entity {
	private GameSync gameSync;
	private int currentCD = 0;
	private int spawnGapCD = 60;
	private int spawnCD = 10;
	private int spawnCount = 0;
	private int waveSpawnCount = 10;
	private boolean isHost;
	private static final short CASTLE_WAITING   = 0;
	private static final short CASTLE_SPAWNING = 1;
	private short status;
	//PUBLIC METHODS
	MobLayer(GameSync gameSync, boolean isHost){
		this.gameSync = gameSync;
		this.isHost = isHost;
		this.setIgnoreUpdate(false);
		this.setChildrenIgnoreUpdate(false);
		this.setVisible(true);
		status = CASTLE_WAITING;
	}
	//getter
		public GameSync getGameSync(){
			return gameSync;
		}
		
	//PROTECTED METHODS
	@Override
	protected void onManagedUpdate( float pSecondsElapsed) {
		currentCD++;
		if ( status == CASTLE_WAITING ) {
			if ( currentCD == spawnCD ) {
				currentCD = 0;
			    status = CASTLE_SPAWNING;
			}
		}
		else if ( status == CASTLE_SPAWNING )  {
			if ( currentCD == spawnGapCD ) {
				currentCD = 0;
				Mob mob = gameSync.createMob(isHost,  0 );
				attachChild(mob);
				mob.setVisible(true);
				spawnCount++;
				if ( spawnCount == waveSpawnCount ) {
					spawnCount = 0;
					status = CASTLE_WAITING;
				}
			}
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
	
}
