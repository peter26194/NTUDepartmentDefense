package com.example.ntudepartmentdefense.scene;


import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import com.example.ntudepartmentdefense.gamesync.GameSync;
import com.example.ntudepartmentdefense.layer.GameWindow;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;


public class GameScene extends ManagedScene {
	// Create an easy to manage HUD that we can attach/detach when the game scene is shown or hidden.
	public GameWindow GameHud = new GameWindow(this);
	public GameScene thisManagedGameScene = this;
	public GameSync gameSync = new GameSync(this);
	public boolean started = false;
	public GameScene() {
		// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
		this(3f);
	};
	
	public GameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		// Setup the touch attributes for the Game Scenes.
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		// Scale the Game Scenes according to the Camera's scale factor.
		this.setPosition(0, 0);
		this.setVisible(true);
		this.setIgnoreUpdate(false);
		//GameHud.setScaleCenter(0f, 0f);
		//GameHud.setScale(ResourceManager.getInstance().cameraScaleFactorX,ResourceManager.getInstance().cameraScaleFactorY);
	}
	
	public void onGameStart() { 
		started = true;
	}
	
	// These objects will make up our loading scene.
	private LoadScene LoadingScene;
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		// Setup and return the loading screen.
		LoadingScene = new LoadScene();
		LoadingScene.onCreateScene();
		return LoadingScene;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
		LoadingScene.onUnloadScene();
		LoadingScene = null;
	}
	
	@Override
	public void onLoadScene() {
		// Load the resources to be used in the Game Scenes.
		ResourceManager.loadGameResources();
		// Create a Sprite to use as the background.
		this.createBackground();
		this.createGameHUD();
		this.createGameMap();
	}
	
	private void createGameMap() {
		gameSync.setPosition(240f,0f);
		attachChild(gameSync);

	}

	private void createGameHUD() {
		GameHud.onLoad();
	}

	private void createBackground() {
		attachChild(new Sprite(280, 40, 
				ResourceManager.getInstance().gameMapTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()));
	}

	@Override
	public void onShowScene() {
		// We want to wait to set the HUD until the scene is shown because otherwise it will appear on top of the loading screen.
		ResourceManager.getInstance().engine.getCamera().setHUD(GameHud);
		GameHud.setVisible(true);//
		gameSync.setVisible(true);
		gameSync.setIgnoreUpdate(false);
	}
	
	@Override
	public void onHideScene() {
		ResourceManager.getInstance().engine.getCamera().setHUD(null);
	}
	
	@Override
	public void onUnloadScene() {
		// detach and unload the scene.
		ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisManagedGameScene.detachChildren();
				thisManagedGameScene.clearEntityModifiers();
				thisManagedGameScene.clearTouchAreas();
				thisManagedGameScene.clearUpdateHandlers();
			}});
		ResourceManager.unloadGameResources();
	}

	@Override
	public void incrementLoading(float pSecondsElapsed) {
		// Keeps track of how long the loading screen has been visible. Set by the SceneManager.
		this.elapsedLoadingScreenTime += pSecondsElapsed;
		float ratio = this.elapsedLoadingScreenTime / this.minLoadingScreenTime;
		if ( ratio >= 0.95f ) {
			ratio = 0.95f;
			NetworkManager.getInstance().sendGameStartMessage();
		}

		if ( started )
			ratio += 0.05f;
		LoadingScene.setRatio(ratio);
	}

	@Override
	public boolean finishLoading() {
		if (  started && this.elapsedLoadingScreenTime >= this.minLoadingScreenTime ) {
			this.elapsedLoadingScreenTime = 0f;
			return true;
		}
		return false;
	}
}