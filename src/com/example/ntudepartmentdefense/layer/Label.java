package com.example.ntudepartmentdefense.layer;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.ResourceManager;

public class Label extends Sprite{
	protected GameWindow gameWindow;
	protected int index;
	
	Label(float labelWidth, float labelHeight, int index, float notebookWidth, GameWindow gameWindow){
		super(notebookWidth - labelWidth, labelHeight*(index), 
				labelWidth, labelHeight, 
				ResourceManager.gameLabelTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		this.gameWindow = gameWindow;
		this.index = index;
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()){
			gameWindow.getRightWindow().display(index);
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
}
