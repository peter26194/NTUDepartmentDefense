package com.example.ntudepartmentdefense.layer;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.State;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.scene.GameScene;



public class GameWindow extends HUD {
	private GameScene parentScene;
	
	private float cameraWidth = ResourceManager.getInstance().cameraWidth;
	private float cameraHeight = ResourceManager.getInstance().cameraHeight;
	
	// The objects that will make up our Game Window
	private LeftWindow leftWindow;
	private Notebook rightWindow;
	
	public GameWindow(GameScene scene) {
		super();
		this.parentScene = scene;
		this.setChildrenIgnoreUpdate(false);
		this.setCamera( ResourceManager.getInstance().engine.getCamera() );
	}
	
	public void onLoad() {
		// Create a info window in the left
		//TODO
		leftWindow = new LeftWindow();
		leftWindow.setVisible(true);
		this.attachChild(leftWindow);
		
		// Create a Notebook in the right
		float righWindowStart = leftWindow.getWidth() + ResourceManager.getInstance().edgeLength;
		rightWindow = new Notebook(righWindowStart, 0,
				ResourceManager.getInstance().cameraWidth - righWindowStart);
		rightWindow.setVisible(true);
		this.attachChild(rightWindow);
		
		//register all buttons in right
		for (int i=0; i<3; i++){
			ButtonSprite[] buttons = rightWindow.getTab(i).getPageContent().getButtons();
			for (int j=0; i<buttons.length; i++)
				registerTouchArea(buttons[j]);
		}
		
		//TODO
		//show building layer
		//SceneManager.getInstance().showLayer(GameWindow.this.parentScene.gameSync.gridLayer, false, false, false);

		//hide building layer
		//GameWindow.this.parentScene.gameSync.gridLayer.hide();
	}
	
	public void unLoad() {
		
	}
}
