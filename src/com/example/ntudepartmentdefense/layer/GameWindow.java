package com.example.ntudepartmentdefense.layer;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.State;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.manager.SceneManager;
import com.example.ntudepartmentdefense.scene.GameScene;



public class GameWindow extends HUD {
	private GameScene gameScene;
	
	private float cameraWidth = ResourceManager.getInstance().cameraWidth;
	private float cameraHeight = ResourceManager.getInstance().cameraHeight;
	
	// The objects that will make up our Game Window
	private LeftWindow leftWindow;
	private Notebook rightWindow;
	
	public GameWindow(GameScene scene) {
		super();
		this.gameScene = scene;
		this.setChildrenIgnoreUpdate(false);
		this.setCamera( ResourceManager.getInstance().engine.getCamera() );
	}
	
	public void onLoad() {
		// Create a info window in the left
		//TODO
		leftWindow = new LeftWindow(this);
		leftWindow.setVisible(true);
		this.attachChild(leftWindow);
		
		// Create a Notebook in the right
		float righWindowStart = leftWindow.getWidth() + ResourceManager.getInstance().edgeLength;
		rightWindow = new Notebook(righWindowStart, 0,
				ResourceManager.getInstance().cameraWidth - righWindowStart, this);
		rightWindow.setVisible(true);
		this.attachChild(rightWindow);
	}
	
	public void unLoad() {
		
	}
	public LeftWindow getLeftWindow(){
		return leftWindow;
	}
	public Notebook getRightWindow(){
		return rightWindow;
	}
	public void registerTouchAreaFor(Sprite spt){
		registerTouchArea(spt);
	}
	public void showBuildingLayer(){
		SceneManager.getInstance().showLayer(gameScene.gameSync.gridLayer, false, false, false);
	}
	public void hideBuildingLayer(){
		gameScene.gameSync.gridLayer.hide();
	}
}
