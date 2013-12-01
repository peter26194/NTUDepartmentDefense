package com.example.ntudepartmentdefense.gamesync;

import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Gauge;



public class Tower extends GameSprite{
	protected TowerLayer towerLayer;
	protected BulletLayer bulletLayer;
	
	protected static final float edgeLength = ResourceManager.getInstance().edgeLength;
	protected static final short edgeUnitCount= ResourceManager.getInstance().edgeUnitCount;
	protected static final float edgeUnit= ResourceManager.getInstance().edgeUnit;
	
	public boolean isSelected() {
		return true;
	}
	
	protected boolean ownedByServer = false;
	public boolean ownedByServer() {
		return ownedByServer;
	}
	protected int status;
	protected static final int TOWER_IDLE = 0;
	protected static final int TOWER_RELOAD = 1;
	protected static final int TOWER_BUILD = 2;
	protected static final int TOWER_REMOVE = 3;
	
	protected int departmentID;
	protected int towerID;
	protected short[] gridX;
	protected short[] gridY;
	
	protected float range;//in unit of grid
	protected int bulletType;
	protected int reloadCD;
	protected int buildCD;
	protected int removeCD;
	protected int currentCD;
	protected Mob target = null;
	protected Gauge progressBar;
	
	//PROTECTED METHODS
	public Tower(short gridX, short gridY, 
			int towerID, int departmentID,
			boolean isServer, TowerLayer towerLayer, BulletLayer bulletLayer) {
		super( gridX * edgeUnit, gridY * edgeUnit, edgeUnit, edgeUnit, 
				DataManager.getInstance().towerParam[departmentID][towerID].getTextureRegion());
		this.gridX = new short[1];
		this.gridX[0] = gridX;
		this.gridY = new short[1];
		this.gridY[0] = gridY;
		this.towerID = towerID;
		this.departmentID = departmentID;
		
		//relocate
		ResourceManager.getInstance().moveBottomCenter(gridX * edgeUnit + edgeUnit/2, (gridY+1) * edgeUnit, this);
		
		//set parameters by Data Manager
		this.range = DataManager.getInstance().towerParam[departmentID][towerID].getRange();
		this.bulletType = DataManager.getInstance().towerParam[departmentID][towerID].getBullet();
		this.reloadCD = DataManager.getInstance().towerParam[departmentID][towerID].getReloadCD();
		this.buildCD = DataManager.getInstance().towerParam[departmentID][towerID].getBuildCD();
		this.removeCD = DataManager.getInstance().towerParam[departmentID][towerID].getRemoveCD();
		
		this.currentCD = 0;
		this.ownedByServer = isServer;
		this.towerLayer = towerLayer;
		this.bulletLayer = bulletLayer;
		String color = ( isServer)? NetworkManager.SERVER_COLOR : NetworkManager.CLIENT_COLOR;
		progressBar = new Gauge((int)edgeUnit, (int)edgeUnit / 10, color, "#333333", 0f);
		progressBar.setPosition( 0f ,  edgeUnit - edgeUnit / 10);
		
		if ( buildCD != 0 ) {
			status = TOWER_BUILD;
			progressBar.setVisible(true);
			progressBar.setIgnoreUpdate(false);
		}
		else {
			status = TOWER_IDLE;
			progressBar.setVisible(false);
			progressBar.setIgnoreUpdate(true);
		}
		this.attachChild(progressBar);
		this.setChildrenIgnoreUpdate(false);
		towerLayer.getGameSync().getGameScene().registerTouchArea(this);
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			towerLayer.getGameSync().getGameScene().GameHud.getLeftWindow().getInfoWindow().displayInfoOf(this);
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	public void remove(){
		status = TOWER_REMOVE;
		progressBar.setVisible(true);
		progressBar.setIgnoreUpdate(false);
	}
	//getters
	public short[] getXs(){
		return gridX;
	}
	public short[] getYs(){
		return gridY;
	}
	public int getDepartmentID(){
		return this.departmentID;
	}
	public int getTowerID(){
		return this.towerID;
	}
	public boolean canAttack(Mob target){
		if ( !target.isLive() )
			return false;
		if ( target == null)
			return false;
		if ( !( target.ownedByServer ^ this.ownedByServer) )
			return false;
		float dist = ResourceManager.getInstance().dist(this.getX(), this.getY(), 
				target.getX(), target.getY());
		return dist <= range*edgeUnit;
	}
	//PROTECTED METHODS
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		switch( status ) {
		case TOWER_BUILD:
			currentCD++;
			if ( currentCD == buildCD ) {
				progressBar.setVisible(false);
				progressBar.setRatio(0f);
				progressBar.setIgnoreUpdate(true);
				status = TOWER_IDLE;
				currentCD = 0;
			}
			else {
				progressBar.setRatio( (float)currentCD / (float) buildCD );
			}
			break;
		case TOWER_REMOVE:
			currentCD++;
			if ( currentCD == removeCD ) {
				this.towerLayer.getGameSync().endRemove(this);
				this.detachSelf();
			}
			else {
				progressBar.setRatio((float)currentCD / (float) buildCD );
			}
			break;
		case TOWER_IDLE:
			if ( reloadCD == 0 ) 
				return;
			// short circuit
			if (target == null || !canAttack(target) ) 
				findTarget();
			if (target != null) 
				shoot();
			break;
		case TOWER_RELOAD:
			currentCD++;
			if ( currentCD == reloadCD ) {
				status = TOWER_IDLE;
				currentCD = 0;
			}
		default:
			break;
		}
		super.onManagedUpdate(pSecondsElapsed);
	}
	//PRIVATE METHODS
	private void findTarget(){
		target = towerLayer.getGameSync().getTowerTarget(this);
	}
	
	private void shoot(){
		Bullet bullet = new Bullet(target, gridX[0] * edgeUnit + edgeUnit/2, gridY[0] * edgeUnit + edgeUnit/2, 
				bulletType, ownedByServer, bulletLayer);
		bulletLayer.attachChild(bullet);
		status = TOWER_RELOAD;
		
		long animationFrameDuration[] = {100, 200, 300};
		this.animate(animationFrameDuration, 1, 3, 1);
	}
}
