package com.example.ntudepartmentdefense.layer;

import org.andengine.input.touch.TouchEvent;

public class BuildLabel extends Label{
	BuildLabel(float labelWidth, float labelHeight, int index, float notebookWidth, GameWindow gameWindow){
		super(labelWidth, labelHeight, index, notebookWidth, gameWindow);
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		gameWindow.showBuildingLayer();
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
}
