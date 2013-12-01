package com.example.ntudepartmentdefense.gamesync;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import com.example.ntudepartmentdefense.manager.ResourceManager;

public class GameSprite extends AnimatedSprite{
	
	public GameSprite(float x, float y, TiledTextureRegion TiledTextureRegion){
		super(x, y, 
				TiledTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
	}
	public GameSprite(float x, float y, float width, float height, TiledTextureRegion TiledTextureRegion){
		super(x, y, width, height, 
				TiledTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			displayInfo();
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	protected void displayInfo(){
		//for overriden
	}
}
