package com.example.ntudepartmentdefense.manager;

import com.example.ntudepartmentdefense.util.BulletParam;
import com.example.ntudepartmentdefense.util.MobParam;
import com.example.ntudepartmentdefense.util.Position;
import com.example.ntudepartmentdefense.util.TowerParam;

public class DataManager {
	//singleton
	private static final DataManager INSTANCE = new DataManager();
	DataManager(){
	}
	public static DataManager getInstance(){
		return INSTANCE;
	}
	public static final int SLOW_TOWER_RELOAD_ABOVE = 100;
	public static final int FAST_TOWER_RELOAD_BELOW = 50;
	
	public static final int SLOW_MOB_DURATION_ABOVE = 30;
	public static final int FAST_MOB_DURATION_BELOW = 15;
	
	//members
	
	//tower
	public TowerParam[] eeTowerParam = new TowerParam[] {
		//TowerParam(float range, int bullet, int reloadCD, int bulidCD, int removeCD, String img)
		new TowerParam(0f, 0, 0, 900, 0, -1, -1, "ee/towers/castle_ee.png"), //towerID == 0
		new TowerParam(3.5f, 0, 30, 120, 240, -1, -1, "shared/towers/fence.png"), //towerID == 1
	};
	public TowerParam[] medTowerParam = new TowerParam[] {
		//TowerParam(float range, int bullet, int reloadCD, int bulidCD, int removeCD, String img)
		new TowerParam(0f, 0, 0, 900, 0, -1, -1, "med/towers/castle_med.png"), //towerID == 0
		new TowerParam(3.5f, 0, 30, 120, 240, -1, -1, "shared/towers/fence.png"), //towerID == 1
	};
	public TowerParam[][] towerParam = new TowerParam[][] {
		eeTowerParam, //departmentID == 0
		medTowerParam, //departmentID == 1
	};
	
	// xxx
	// xox
	public Position archCastleType = new Position(
			new short[] {-1, -1, 0, 1, 1},
			new short[] {0,  -1,-1, 0, -1});
	
	public Position[] castlePosition = new Position[] {
		archCastleType,//departmentID == 0
		archCastleType,//departmentID == 1
	};

	//mob
	public MobParam[] mobParam = new MobParam[] {
		//MobParam(int duration, int defense, int hp, String img)
		new MobParam(15,  "shared/mobs/high_school_girl.png"), //typeID == 0
	};
	
	//bullet
	private static final int HEAD_FIRST = BulletParam.HEAD_FIRST;
	private static final int RETOTATE_BY_CENTER = BulletParam.RETOTATE_BY_CENTER;
	
	public BulletParam[] bulletParam = new BulletParam[] {
		//BulletParam(float speed, int damage, int wayOfFlying, String img)
		new BulletParam(2.5f, 1, HEAD_FIRST, "shared/bullets/basic.png"), //typeID == 0
	};
}
