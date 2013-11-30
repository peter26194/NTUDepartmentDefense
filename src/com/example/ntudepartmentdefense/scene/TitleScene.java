package com.example.ntudepartmentdefense.scene;


import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.math.MathUtils;

import com.example.ntudepartmentdefense.layer.NetworkLayer;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.manager.SceneManager;


public class TitleScene extends ManagedScene {
	
	final TitleScene thisManagedGameScene = this;
	
	private static final TitleScene INSTANCE = new TitleScene();
	public static TitleScene getInstance(){
		return INSTANCE;
	}
	
	public TitleScene() {
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	// No loading screen means no reason to use the following methods.
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		return null;
	}
	@Override
	public void onLoadingScreenUnloadAndHidden() {
	}
	@Override
	public void incrementLoading(float pSecondsElapsed) {}
	@Override
	public boolean finishLoading() {
		return false;
	}
	
	// The objects that will make up our Main Menu
	private Sprite BackgroundSprite;
	private Sprite CharacterSprite;
	private ButtonSprite SinglePlayerButton;
	private ButtonSprite MultiPlayerButton;
	private ButtonSprite OptionsButton;
	private Sprite Logo;
	
	@Override
	public void onLoadScene() {
		// Load the menu resources
		ResourceManager.loadTitleResources();
		this.createBackground();
		this.createSinglePlayerButton();
		this.createMultiPlayerButton();
		this.createOptionsButton();
		this.createTitle();
	}
	private void createTitle() {
		Logo = new Sprite(0,0, ResourceManager.titleLogoTextureRegion , ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		Logo.setPosition((ResourceManager.getInstance().cameraWidth) * 3/4 - Logo.getWidth() / 2 
				       ,  ResourceManager.getInstance().cameraHeight /5    - Logo.getHeight() / 2);
		Logo.setAlpha(0f);
		Logo.setColor(0.7f,0.7f,0.7f);
		AlphaModifier am = new AlphaModifier(1f, 0f, 1f);
		ColorModifier cm = new ColorModifier(1f, 0.7f,1f, 0.7f,1f, 0.7f,1f);
		SequenceEntityModifier sem = new SequenceEntityModifier( am, cm );
		Logo.registerEntityModifier(sem);
		this.attachChild(Logo);
	}
	private void createOptionsButton() {
		OptionsButton = new ButtonSprite(
				MultiPlayerButton.getX(), 
				MultiPlayerButton.getY() + MultiPlayerButton.getHeight() + 50,
				ResourceManager.buttonOptionsTextureRegion, 
				ResourceManager.buttonOptionsTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		this.attachChild(OptionsButton);
		
		// Implement button listener.
		// TODO
		this.registerTouchArea(OptionsButton);
	}
	private void createMultiPlayerButton() {
		MultiPlayerButton = new ButtonSprite(
				SinglePlayerButton.getX(),
				SinglePlayerButton.getY()+SinglePlayerButton.getHeight() + 50, 
				ResourceManager.buttonDoubleTextureRegion, 
				ResourceManager.buttonDoubleTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY ) {
				if ( pSceneTouchEvent.isActionDown() ) {
					ResourceManager.clickSound.play();
					SceneManager.getInstance().showLayer( new NetworkLayer() ,false,false,true);
					return true;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};

		this.attachChild(MultiPlayerButton);
		this.registerTouchArea(MultiPlayerButton);
		
	}
	private void createSinglePlayerButton() {
		SinglePlayerButton = new ButtonSprite(
				ResourceManager.getInstance().cameraWidth - 500, // ResourceManager.getInstance().cameraWidth / 3f, -ResourceManager.buttonTiledTextureRegion.getTextureRegion(0).getWidth())/3f,
				ResourceManager.getInstance().cameraHeight / 3f,//-ResourceManager.buttonTiledTextureRegion.getTextureRegion(0).getHeight())*(1f/3f), 
				ResourceManager.buttonSingleTextureRegion, 
				ResourceManager.buttonSingleTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY ) {
				if ( pSceneTouchEvent.isActionDown() ) {
					ResourceManager.clickSound.play();
					NetworkManager.getInstance().initSingle();
					SceneManager.getInstance().showGameScene();
					return true;
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.attachChild(SinglePlayerButton);
		this.registerTouchArea(SinglePlayerButton);
	}
	private void createBackground() {
		BackgroundSprite = new Sprite(
				0,
				0,
				ResourceManager.titleBackgroundTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() );

		CharacterSprite = new Sprite(
				-600,
				200,
				ResourceManager.titleCharacterTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		this.attachChild(BackgroundSprite);
		this.attachChild(CharacterSprite);

	}
	
	
	public void onShowScene() {
		// TODO
		FadeInModifier fi = new FadeInModifier( 1.5f );
		MoveByModifier move = new MoveByModifier( 1.5f , 700 , 0 );
		ParallelEntityModifier pem = new ParallelEntityModifier( fi , move );
		CharacterSprite.registerEntityModifier(pem);
	}
	public void onHideScene() {
		// TODO
	}
	@Override
	public void onUnloadScene() {
		ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisManagedGameScene.detachChildren();
				thisManagedGameScene.clearEntityModifiers();
				thisManagedGameScene.clearTouchAreas();
				thisManagedGameScene.clearUpdateHandlers();
			}});
		ResourceManager.unloadTitleResources();
	}


}