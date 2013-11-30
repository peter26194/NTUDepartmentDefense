package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.IconParam;



public class Tab extends Entity{
	private Notebook parentNotebook;
	
	private float cameraHeight = ResourceManager.getInstance().cameraHeight;
	private float notebookWidth;
	private float labelHeight = cameraHeight/3;
	private float labelWidth = ResourceManager.gameLabelTextureRegion.getWidth();
	
	// The objects that will make up our Notebook Tab
	private PageContent content;
	private Sprite label;
	
	Tab(int index, float notebookWidth, IconParam[] icons, Notebook parentNotebook){
		super();
		this.notebookWidth = notebookWidth;
		this.parentNotebook = parentNotebook;
		setTag(index);
		
		content = new PageContent( 0, 0, 
				notebookWidth - labelWidth, cameraHeight, icons);
		
		label = new Sprite( notebookWidth - labelWidth, labelHeight*(index-1), 
				labelWidth, labelHeight, 
				ResourceManager.gameLabelTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown()){
					Tab.this.parentNotebook.display(Tab.this.getTag());
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}};
		this.attachChild(content);
		this.attachChild(label);
	}
	
	public PageContent getPageContent(){
		return content;
	}
}
