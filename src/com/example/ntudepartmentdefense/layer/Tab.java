package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.IconParam;



public class Tab extends Entity{
	protected GameWindow gameWindow;
	protected int index;
	protected float cameraHeight = ResourceManager.getInstance().cameraHeight;
	protected float notebookWidth;
	protected float labelHeight = cameraHeight/3;
	protected float labelWidth = ResourceManager.gameLabelTextureRegion.getWidth();
	
	// The objects that will make up our Notebook Tab
	protected PageContent content;
	protected Label label;
	
	Tab(int index, float notebookWidth, IconParam[] icons, GameWindow gameWindow){
		super();
		this.gameWindow = gameWindow;
		this.notebookWidth = notebookWidth;
		this.index = index;
		
		setTag(index);
		
		content = new PageContent( 0, 0, 
				notebookWidth - labelWidth, cameraHeight, icons, gameWindow);
		this.attachChild(content);
		
		createLabel();
	}
	protected void createLabel(){
		label = new Label(labelWidth, labelHeight, index, notebookWidth, gameWindow);
		this.attachChild(label);
		gameWindow.registerTouchAreaFor(label);
	}
	
	public PageContent getPageContent(){
		return content;
	}
}
