package com.example.ntudepartmentdefense.gamesync;


import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Gauge;



public class Mob extends AnimatedSprite{
	private MobLayer mobLayer;
	private static final float edgeLength = ResourceManager.getInstance().edgeLength;
	private static final short edgeUnitCount= ResourceManager.getInstance().edgeUnitCount;
	private static final float edgeUnit= ResourceManager.getInstance().edgeUnit;
	
	private int mobID; 
	private int typeID;
	private short gridX;
	private short gridY;
	public boolean ownedByServer = false;
	
	private int status;
	private static final int MOB_MOVE_UP = 0;
	private static final int MOB_MOVE_DOWN = 1;
	private static final int MOB_MOVE_RIGHT = 2;
	private static final int MOB_MOVE_LEFT = 3;
	private static final int MOB_ARRIVED  =  4;
	
	private int duration;//how many frames in one grid
	private float movingDist;
	private int movingCount = 0;
	private int defense;
	private int curHp;
	private int maxHp;
	private Gauge hpBar;
	//PUBLIC METHODS
	public Mob(int mobID, short gridX, short gridY, int typeID, 
			boolean isServer, MobLayer mobLayer){
		super(gridX * edgeUnit, gridY * edgeUnit, DataManager.getInstance().mobParam[typeID].getTextureRegion(), 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		//TODO this.setScaleX();
		setGridPosition( gridX , gridY);
		this.mobID = mobID;
		this.gridX = gridX;
		this.gridY = gridY;
		this.mobLayer = mobLayer;
		this.typeID = typeID;
		this.ownedByServer = isServer;
		this.status = this.getDirection();
		this.mobLayer.getGameSync().mobLock(gridX, gridY);
		short[] target = this.targetGrid();
		this.mobLayer.getGameSync().mobLock(target[0], target[1]);
		
		//set parameters by DataManager
		this.duration = DataManager.getInstance().mobParam[typeID].getDuration();
		this.defense = DataManager.getInstance().mobParam[typeID].getDefense();
		this.maxHp = DataManager.getInstance().mobParam[typeID].getMaxHP();
		
		this.movingDist = this.edgeUnit/this.duration;
		curHp = maxHp;
		String color = ( isServer)? NetworkManager.SERVER_COLOR : NetworkManager.CLIENT_COLOR;
		hpBar = new Gauge((int)edgeUnit, (int)edgeUnit / 10, color, "#000000", 1f);
		ResourceManager.getInstance().moveCenter(this.getWidth()/2, this.getHeight(), hpBar);
		this.attachChild(hpBar);
		
		this.setVisible(true);
		this.setIgnoreUpdate(false);
		this.walk();
	}
	//hit by bullet
	public void hitBy(Bullet bullet){
		this.hpMinus((short)(bullet.getDamage() - this.defense));
	}
	
	//getters
	public short getGridX(){
		return this.gridX;
	}
	public short getGridY(){
		return this.gridY;
	}
	public int getID(){
		return this.mobID;
	}
	public boolean ownedByServer() {
		return ownedByServer;
	}
	
	private void setGridPosition( short x , short y ) {
		this.setPosition(x * edgeUnit + edgeUnit / 2 - this.getWidth() / 2 ,
				         y * edgeUnit + edgeUnit / 2 - this.getHeight() );
	}
	//PROTECTED METHODS
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		//moving
		float x = this.getX();
		float y = this.getY();
		switch( status ) {
		case MOB_MOVE_UP:
			this.setPosition(x, y - movingDist);
			break;
		case MOB_MOVE_DOWN:
			this.setPosition(x, y + movingDist);
			break;
		case MOB_MOVE_RIGHT:
			this.setPosition(x + movingDist, y);
			break;
		case MOB_MOVE_LEFT:
			this.setPosition(x - movingDist, y);
			break;
		default:
			//do nothing
			break;
		}
		
		//check new direction
		this.movingCount ++;
		if(this.movingCount >= this.duration){
			this.mobLayer.getGameSync().mobUnlock(this.gridX, this.gridY);
			
			short[] target = this.targetGrid();
			this.gridX = target[0];
			this.gridY = target[1];
			this.setGridPosition(this.gridX , this.gridY);
			this.status = this.getDirection();
			
			target = this.targetGrid();
			this.mobLayer.getGameSync().mobLock(target[0], target[1]);
			this.walk();
			movingCount = 0;
			if (this.status == MOB_ARRIVED)
				this.onArrived();
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public boolean isLive() {
		return curHp > 0;
	}
	//PRIVATE METHODS
	private short getDirection(){
		return this.mobLayer.getGameSync().getDirection( gridX, gridY , ownedByServer);
	}
	
	private void hpMinus(short minus){
		this.curHp -= minus;
		if (this.curHp <= 0){
			this.onDead();
		}else{
			hpBar.setRatio((float)this.curHp / (float)this.maxHp);
		}
	}
	private void onDead(){
		//add money if this mob is not mine
		if (! ownedByServer)
			GameManager.getInstance().addMoney(1);
		
		curHp = 0;
		this.mobLayer.getGameSync().mobUnlock(this.gridX, this.gridY);
		
		short[] target = this.targetGrid();
		this.mobLayer.getGameSync().mobUnlock(target[0], target[1]);
		
		//turn back its index to GameSync pool
		this.mobLayer.getGameSync().killMob(this);
		ResourceManager.getInstance().context.runOnUpdateThread( new Runnable() {
			@Override
			public void run() {
				Mob.this.detachSelf();
			}
		});
	}
	private void onArrived(){
		this.mobLayer.getGameSync().arriveMob(this);
		this.onDead();
	}
	private short[] targetGrid(){
		short[] target = new short[]{this.gridX, this.gridY};
		switch( status ) {
		case MOB_MOVE_UP:
			target[1]-=1;
			break;
		case MOB_MOVE_DOWN:
			target[1]+=1;
			break;
		case MOB_MOVE_RIGHT:
			target[0]+=1;
			break;
		case MOB_MOVE_LEFT:
			target[0]-=1;
			break;
		default://case MOB_ARRIVED:
			//do nothing
			break;
		}
		return target;
	}
	private void walk(){
		long animationFrameDuration[] = {100, 200};
		switch( status ) {
		case MOB_MOVE_UP:
			this.animate(animationFrameDuration, 2, 3, true);
			break;
		case MOB_MOVE_DOWN:
			this.animate(animationFrameDuration, 0, 1, true);
			break;
		case MOB_MOVE_RIGHT:
			this.animate(animationFrameDuration, 4, 5, true);
			break;
		case MOB_MOVE_LEFT:
			this.animate(animationFrameDuration, 6, 7, true);
			break;
		default:
			//do nothing
			break;
		}
	}
	
}
