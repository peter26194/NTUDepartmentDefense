package com.example.ntudepartmentdefense.gamesync;

import java.util.LinkedList;
import java.util.Queue;

import org.andengine.entity.Entity;

import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.util.MobStage;

public class MobLayer extends Entity {
	private GameSync gameSync;
	private int wave = 1;
	private short[] currentTags;
	private int currentCD = 0;
	private int spawnGapCD;
	private int spawnCD = 10;
	private int spawnCount = 0;
	private int waveSpawnCount = 10;
	private boolean isHost;
	private short departmentID;
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
		departmentID = (isHost)?NetworkManager.hostDepartmentID
									 :NetworkManager.guestDepartmentID;
		spawnGapCD = MobStage.MOB_SPAWN_ATTR[departmentID][0];
		spawnCD = MobStage.MOB_SPAWN_ATTR[departmentID][1];
		waveSpawnCount = MobStage.MOB_SPAWN_ATTR[departmentID][2];
		currentTags = MobStage.getTags(departmentID, 1);
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
				Mob mob = gameSync.createMob(isHost,  0 ,wave);
				mob.attachTags(currentTags);
				attachChild(mob);
				mob.setVisible(true);
				spawnCount++;
				if ( spawnCount == waveSpawnCount ) {
					spawnCount = 0;
					status = CASTLE_WAITING;
					wave++;
					onWaveDone();
				}
			}
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	private void onWaveDone() {
		spawnGapCD = MobStage.MOB_SPAWN_ATTR[departmentID][0];
		spawnCD = MobStage.MOB_SPAWN_ATTR[departmentID][1];
		waveSpawnCount = MobStage.MOB_SPAWN_ATTR[departmentID][2];
		currentTags = MobStage.getTags(departmentID, wave);
		if ( hasTag(MobStage.TAG_BOSS) ) {
			waveSpawnCount = 1;
		}
		if ( hasTag(MobStage.TAG_TRINITY) ){
			waveSpawnCount = 3;
			spawnGapCD*=2;
		}
		if ( hasTag(MobStage.TAG_CROWDED) ) {
			waveSpawnCount *=2 ;
			spawnGapCD/=2;
		}
		if ( hasTag(MobStage.TAG_BULKY) ) {
			waveSpawnCount *= 2 ;
			waveSpawnCount /= 3;
			spawnGapCD *=2;
		}
		if ( hasTag(MobStage.TAG_AMBUSH) ) {
			spawnCD = 1;
		}
	}
	private boolean hasTag( short tag ) {
		if (currentTags == null)
			return false;
		if (currentTags.length == 0 )
			return false;
		for ( short t : currentTags ) {
			if (t==tag)
				return true;
		}
		return false;
	}
}
