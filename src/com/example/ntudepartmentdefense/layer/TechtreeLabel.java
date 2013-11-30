package com.example.ntudepartmentdefense.layer;

import org.andengine.input.touch.TouchEvent;

public class TechtreeLabel extends Label{
	TechtreeLabel(float labelWidth, float labelHeight, int index, float notebookWidth, GameWindow gameWindow){
		super(labelWidth, labelHeight, index, notebookWidth, gameWindow);
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		gameWindow.hideBuildingLayer();
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
}
