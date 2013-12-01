package com.example.ntudepartmentdefense.gamesync;


import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Gauge;
import com.example.ntudepartmentdefense.util.MobStage;



public class Mob extends GameSprite{
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
	private int money;
	private int damage;
	private boolean refresh = false;
	private Gauge hpBar;
	//PUBLIC METHODS
	public Mob(int mobID, short gridX, short gridY, int typeID, 
			boolean isServer, MobLayer mobLayer, int wave){
		super(gridX * edgeUnit, gridY * edgeUnit, 
				DataManager.getInstance().mobParam[typeID].getTextureRegion());
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
		this.defense = 0;
		this.maxHp = wave * wave;
		this.money = wave;
		this.damage = 1;
		this.movingDist = this.edgeUnit/this.duration;
		curHp = maxHp;
		String color = ( isServer)? NetworkManager.SERVER_COLOR : NetworkManager.CLIENT_COLOR;
		hpBar = new Gauge((int)edgeUnit, (int)edgeUnit / 10, color, "#000000", 1f);
		ResourceManager.getInstance().moveCenter(this.getWidth()/2, this.getHeight(), hpBar);
		this.attachChild(hpBar);
		
		this.setVisible(true);
		this.setIgnoreUpdate(false);
		this.walk();
		mobLayer.getGameSync().getGameScene().registerTouchArea(this);
	}
	//getters
	public short getGridX(){
		return this.gridX;
	}
	public short getGridY(){
		return this.gridY;
	}
	public int getMobType(){
		return this.typeID;
	}
	public int getCurrentHP(){
		return this.curHp;
	}
	public int getID(){
		return this.mobID;
	}
	//hit by bullet
	public void hitBy(Bullet bullet){
		this.hpMinus((short)(bullet.getDamage() - this.defense));
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
			
			if (refresh ) {
				curHp += ( 1 + maxHp / 20 );
				if (curHp>= maxHp) 
					curHp = maxHp;
				hpBar.setRatio((float)this.curHp / (float)this.maxHp);
			}
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
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			mobLayer.getGameSync().getGameScene().GameHud.getLeftWindow().getInfoWindow().displayInfoOf(this);
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
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
			GameManager.getInstance().addMoney(money);
		
		curHp = 0;
		refresh = false;
		if ( this.mobLayer.getGameSync().getFocusedMob() == this ) {
			this.mobLayer.getGameSync().unsetFocusedMob();
		}
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
	public void attachTags(short[] currentTags) {
		// TODO Auto-generated method stub
		if (currentTags == null )
			return;
		for ( short t : currentTags ) {
			switch(t) {
			case MobStage.TAG_CROWDED:
				money *= 3;
				money /= 4;
				if ( money == 0 )
					money++;
				break;
			case MobStage.TAG_BOSS:
				maxHp *= 5;
				curHp = maxHp;
				damage += 10;
				defense += 1;
				break;
			case MobStage.TAG_TRINITY:
				maxHp += maxHp / 2 + maxHp * 3;
				curHp = maxHp;
				damage += 6;
				break;
			case MobStage.TAG_BULKY:
				curHp = maxHp *= 2;
				defense++;
				damage++;
				duration*=2;
				money*= 2;
				break;
			case MobStage.TAG_LETHAL:
				damage+= 10;
				break;
			case MobStage.TAG_REFRESH:
				refresh = true;
				break;
			case MobStage.TAG_WEALTHY:
				money += money/2;
				break;
			case MobStage.TAG_FAST:
				duration -= duration / 4;
				break;
			}
		}

	}
	
}
