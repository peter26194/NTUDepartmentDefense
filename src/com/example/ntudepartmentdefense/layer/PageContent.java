package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.IconParam;



public class PageContent extends Sprite{
	private float width;
	private final int BUTTONS_PER_ROW = 2;//buttons per row
	private float SPACE_BETWEEN_BUTTON;//buttons per row
	
	private ButtonSprite[] buttons;
	private Sprite selectedBox;
	
	PageContent(float x, float y, float width, float height, IconParam[] icons){
		super( x, y, width, height, 
				ResourceManager.gameWindowTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		this.width = width;
		SPACE_BETWEEN_BUTTON = width/(BUTTONS_PER_ROW+1);
		
		//create a selected box
		
		//create buttons
		buttons = new ButtonSprite[ icons.length ];
		for(int i=0; i<icons.length; i++){
			buttons[i] = new ButtonSprite(0, 0, 
					icons[i].getTextureRegion().getTextureRegion(0), 
					ResourceManager.getInstance().engine.getVertexBufferObjectManager(), 
					new OnClickListener() {
				@Override
				public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					PageContent.this.select(pButtonSprite);
				}
			});
			
			//set buttons[i] position
			int row = i/BUTTONS_PER_ROW +1;//start from 1
			int column = i%BUTTONS_PER_ROW +1;//start from 1
			ResourceManager.getInstance().moveCenter(
					column*SPACE_BETWEEN_BUTTON, 
					row*SPACE_BETWEEN_BUTTON, 
					buttons[i]);
			attachChild(buttons[i]);
		}
	}
	
	//for registering buttons touch area
	public ButtonSprite[] getButtons(){
		return buttons;
	}
	
	
	private void select(ButtonSprite button){
		//selected.setPositon();
		//TODO
	}
}
