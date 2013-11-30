package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;


public class LeftWindow extends Entity{
	private GameWindow gameWindow;
	
	// The objects that will make up our left Info Window
	private Sprite backgroundSprite;
	private Text titleText;
	private Text moneyText;
	private InfoWindow info;
	
	LeftWindow(GameWindow gameWindow){
		super(0, 0);
		this.gameWindow = gameWindow;
		
		//create background
		backgroundSprite = new Sprite(0,0, ResourceManager.gameWindowTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		attachChild(backgroundSprite);
		
		// Create a title
		titleText = new Text(backgroundSprite.getWidth()/5, backgroundSprite.getHeight()/10,
				ResourceManager.fontDefault32Bold, 
				"Department X : User Y", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		titleText.setColor(191f, 119f, 91f);
		this.attachChild(titleText);
		
		// Create a money-counting text
		moneyText = new Text(backgroundSprite.getWidth()/5, backgroundSprite.getHeight()/5,
				ResourceManager.fontDefault32Bold, 
				"Money : "+ Integer.toString(GameManager.INITIAL_MONEY), ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		moneyText.setColor(255f, 255f, 0f);
		this.attachChild(moneyText);
		GameManager.getInstance().setMoneyText(moneyText); //linked to GameManager
		
		//create a info panel
		info = new InfoWindow(0, backgroundSprite.getHeight()/2,
				backgroundSprite.getWidth(), backgroundSprite.getHeight()/2);
		this.attachChild(info);
	}
	
	public float getWidth(){
		return backgroundSprite.getWidth();
	}
	
	public InfoWindow getInfoWindow(){
		return info;
	}
}
