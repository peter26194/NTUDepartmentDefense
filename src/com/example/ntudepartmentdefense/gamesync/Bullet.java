package com.example.ntudepartmentdefense.gamesync;


import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.math.MathUtils;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.BulletParam;



import java.lang.Math;

public class Bullet extends AnimatedSprite{
	private BulletLayer bulletLayer;
	
	private static final float edgeLength = ResourceManager.getInstance().edgeLength;
	private static final short edgeUnitCount= ResourceManager.getInstance().edgeUnitCount;
	private static final float edgeUnit= ResourceManager.getInstance().edgeUnit;
	
	private Mob target; 
	private int typeID;
	private boolean ownedByServer = false;
	
	private float speed;//moving distance in one frame; in unit of grid
	private int damage;
	
	private int wayOfFlying;
	private static final int HEAD_FIRST = BulletParam.HEAD_FIRST;
	private static final int RETOTATE_BY_CENTER = BulletParam.RETOTATE_BY_CENTER;
	
	//PUBLIC METHODS
	public Bullet(Mob target, float x, float y, int typeID, 
			boolean isServer, BulletLayer bulletLayer){
		super(x, y, DataManager.getInstance().bulletParam[typeID].getTextureRegion(),
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		this.target = target;
		this.typeID = typeID;
		this.ownedByServer = isServer;
		
		//set parameters by DataManager
		this.speed = DataManager.getInstance().bulletParam[typeID].getSpeed();
		this.damage = DataManager.getInstance().bulletParam[typeID].getDamage();
		this.wayOfFlying = DataManager.getInstance().bulletParam[typeID].getWayOfFlying();
		
		//animation
		long animationFrameDuration[] = {100, 200, 300};
		this.animate(animationFrameDuration, 0, 2, true);
	}
	
	public int getDamage(){
		return damage;
	}
	//PROTECTED METHODS
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		this.fly();
		super.onManagedUpdate(pSecondsElapsed);
	}

	//PRIVATE METHODS
	private void fly(){
		float x = ResourceManager.getInstance().getCenterX(this);
		float y = ResourceManager.getInstance().getCenterY(this);
		if ( !target.isLive() ) {
			ResourceManager.getInstance().context.runOnUpdateThread( new Runnable() {
				@Override
				public void run() {
					Bullet.this.detachSelf();
				}
			});
			return;
		}
		float goalX = ResourceManager.getInstance().getCenterX(target);
		float goalY = ResourceManager.getInstance().getCenterY(target);
		
		float dist = ResourceManager.getInstance().dist(x, y, goalX, goalY);
		
		//if this bullet will hit on target in current update
		if (dist<speed){
			hitTarget();
			this.setPosition(goalX, goalY);
			return;
		}
			
		float moveX = speed*edgeUnit * (goalX-x)/dist;
		float moveY = speed*edgeUnit * (goalY-y)/dist;
		this.setPosition(x+moveX, y+moveY);
		
		//set up rotation during flying
		this.setRotationCenterX(ResourceManager.getInstance().getCenterX(this));
		this.setRotationCenterX(ResourceManager.getInstance().getCenterY(this));
		
		switch(wayOfFlying) {
			case RETOTATE_BY_CENTER:
				this.setRotation(30f);//in unit of degree
				break;
			default://case HEAD_FIRST:
				this.setRotation(MathUtils.radToDeg((float) Math.atan2(moveY, moveX)));
				break;
		}
	}
	
	private void hitTarget(){
		this.target.hitBy(this);
		ResourceManager.getInstance().context.runOnUpdateThread( new Runnable() {
			@Override
			public void run() {
				Bullet.this.detachSelf();
			}
		});
	}
}
