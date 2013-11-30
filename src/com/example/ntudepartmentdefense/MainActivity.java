package com.example.ntudepartmentdefense;


import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.manager.SceneManager;
import com.example.ntudepartmentdefense.scene.TitleScene;


import android.view.KeyEvent;

public class MainActivity extends BaseGameActivity {

	static float DESIGN_SCREEN_WIDTH_PIXELS = 1280f;
	static float DESIGN_SCREEN_HEIGHT_PIXELS = 800f;

	// ====================================================
	// VARIABLES
	// ====================================================
	// These variables will be set in onCreateEngineOptions().
	public float cameraWidth;
	public float cameraHeight;
	
	// If a Layer is open when the Back button is pressed, hide the layer.
	// If a Game scene or non-MainMenu is active, go back to the MainMenu.
	// Otherwise, exit the game.
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
					System.exit(0);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	// ====================================================
	// CREATE ENGINE OPTIONS
	// ====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {

		// Set the Camera's Width & Height according to the device with which you design the game.
		cameraWidth  = DESIGN_SCREEN_WIDTH_PIXELS  ;
		cameraHeight = DESIGN_SCREEN_HEIGHT_PIXELS ;
		
		// Create the EngineOptions.
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), new Camera(0, 0, cameraWidth, cameraHeight));
		
		// Enable sounds.
		engineOptions.getAudioOptions().setNeedsSound(true);
		// Enable music.
		engineOptions.getAudioOptions().setNeedsMusic(true);
		// Turn on Dithering to smooth texture gradients.
		engineOptions.getRenderOptions().setDithering(true);

		// Set the Wake Lock options to prevent the engine from dumping textures when focus changes.
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	// ====================================================
	// CREATE RESOURCES
	// ====================================================
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// Setup the ResourceManager.
		ResourceManager.getInstance().setup(this.getEngine(), 
											this, 
				                            cameraWidth, cameraHeight);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ====================================================
	// CREATE SCENE
	// ====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// Register an FPSLogger to output the game's FPS during development.
		mEngine.registerUpdateHandler(new FPSLogger());
		// Tell the SceneManager to show the MainMenu.
		SceneManager.getInstance().showTitleScene();
		// Set the MainMenu to the Engine's scene.
		pOnCreateSceneCallback.onCreateSceneFinished(TitleScene.getInstance());
	}

	// ====================================================
	// POPULATE SCENE
	// ====================================================
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		// Our SceneManager will handle the population of the scenes, so we do nothing here.
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}