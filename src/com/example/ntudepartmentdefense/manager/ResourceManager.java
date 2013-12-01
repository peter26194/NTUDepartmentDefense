package com.example.ntudepartmentdefense.manager;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;


import com.example.ntudepartmentdefense.MainActivity;
import com.example.ntudepartmentdefense.gamesync.Tower;
import com.example.ntudepartmentdefense.util.BulletParam;
import com.example.ntudepartmentdefense.util.Gauge;
import com.example.ntudepartmentdefense.util.MobParam;
import com.example.ntudepartmentdefense.util.TowerParam;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager extends Object {
	
	//====================================================
	// CONSTANTS
	//====================================================
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	//====================================================
	// VARIABLES
	//====================================================
	// We include these objects in the resource manager for
	// easy accessibility across our project.
	public Engine engine;
	public MainActivity context;
	public float cameraWidth;
	public float cameraHeight;
	public float cameraScaleFactorX;
	public float cameraScaleFactorY;
	public final float edgeLength = 800;
	public final short edgeUnitCount = 20;
	public final float edgeUnit = 40;
	
	// The resource variables listed should be kept public, allowing us easy access
	// to them when creating new Sprite and Text objects and to play sound files.
	// ======================== Game Resources ================= //
	public static ITextureRegion gameMapTextureRegion;
	public static ITextureRegion gameWindowTextureRegion;
	public static ITextureRegion gameLabelTextureRegion;
	public static ITiledTextureRegion selectedBoxTextureRegion;
	// ======================== Title Resources ================= //
	public static ITextureRegion titleLogoTextureRegion;
	public static ITextureRegion titleBackgroundTextureRegion;
	public static ITextureRegion titleCharacterTextureRegion;
	
	public static ITextureRegion titleHostGameTextureRegion;
	public static ITextureRegion titleJoinGameTextureRegion;
	public static ITextureRegion titleCancelTextureRegion;
	
	public static ITextureRegion titleWindowBackTextureRegion;
	
	public static ITiledTextureRegion titleArrowTiledRegion;
	
	// =================== Shared Game and Menu Resources ====== //
	public static ITextureRegion buttonSingleTextureRegion;
	public static ITextureRegion buttonDoubleTextureRegion;
	public static ITextureRegion buttonOptionsTextureRegion;
	public static Sound clickSound;
	public static Font fontDefault32Bold;
	public static Font fontDefault72Bold;

	
	// This variable will be used to revert the TextureFactory's default path when we change it.
	private String mPreviousAssetBasePath = "";

	//====================================================
	// CONSTRUCTOR
	//====================================================
	private ResourceManager(){
	}

	//====================================================
	// GETTERS & SETTERS
	//====================================================
	// Retrieves a global instance of the ResourceManager
	public static ResourceManager getInstance(){
		return INSTANCE;
	}
	
	//====================================================
	// PUBLIC METHODS
	//====================================================
	// Setup the ResourceManager
	public void setup(final Engine pEngine, 
				  	  final MainActivity pContext, 
				  	  final float pCameraWidth,
				  	  final float pCameraHeight){
		engine = pEngine;
		context = pContext;
		cameraWidth = pCameraWidth;
		cameraHeight = pCameraHeight;

	}
	
	public static void loadGameResources() {
		getInstance().loadGameTextures();
	}
	
	public static void loadTitleResources() {
		getInstance().loadTitleTextures();	
		getInstance().loadSharedResources();
	}

	// Unloads all game resources.
	public static void unloadGameResources() {
		getInstance().unloadGameTextures();
	}

	// Unloads all menu resources
	public static void unloadTitleResources() {
		getInstance().unloadTitleTextures();
	}


	// Unloads all shared resources
	public static void unloadSharedResources() {
		getInstance().unloadSounds();
		getInstance().unloadFonts();
	}
	
	//====================================================
	// PRIVATE METHODS
	//====================================================
	// Loads resources used by both the game scenes and menu scenes
	private void loadSharedResources(){
		loadSounds();
		loadFonts();
	}
	
	// ============================ LOAD TEXTURES (GAME) ================= //
	private void loadGameTextures(){
		// Store the current asset base path to apply it after we've loaded our textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our game assets folder to "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		// background texture - only load it if we need to:
		if(gameMapTextureRegion ==null
				|| gameWindowTextureRegion==null
				|| gameLabelTextureRegion==null) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(
					engine.getTextureManager(), 1280, 1280);
			gameMapTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "map_lake.png", 10, 10);//map_lake.png is 720x720
			gameWindowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "window_temp.png", 740, 10);//window.png is 480x800
			gameLabelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "label_temp.png", 1130, 10);//label.png is 64x267
			texture.load();
		}
		if(selectedBoxTextureRegion ==null){
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
					engine.getTextureManager(), 1280, 1280);
			selectedBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					texture, context, "selected.png", 4, 1);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder
						<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			}catch (TextureAtlasBuilderException e) {
				e.printStackTrace();
			}
			texture.load();
		}
		loadTowerTextures();
		loadModTextures();
		loadBulletTextures();
		
		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}
	private void loadTowerTextures(){
		BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
				engine.getTextureManager(), 1280, 1280);
		
		//TODO load by department
		for(int i=0; i<DataManager.getInstance().towerParam.length; i++){
			for(int j=0; j<DataManager.getInstance().towerParam[i].length; j++){
				TowerParam tower = DataManager.getInstance().towerParam[i][j];
				TiledTextureRegion tiled;
				tiled = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
						texture, context, tower.getImage(), 4, 1);//TODO check if atlas is large enough
				tower.setTextureRegion(tiled);
			}
			try {
				texture.build(new BlackPawnTextureAtlasBuilder
						<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			}catch (TextureAtlasBuilderException e) {
				e.printStackTrace();
			}
			
			texture.load();
		}
	}
	private void loadModTextures(){
		BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
				engine.getTextureManager(), 1280, 1280);
		
		for(int i=0; i<DataManager.getInstance().mobParam.length; i++){
			MobParam mob = DataManager.getInstance().mobParam[i];
			TiledTextureRegion tiled;
			tiled = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					texture, context, mob.getImage(), 8, 1);//TODO check if atlas is large enough
			mob.setTextureRegion(tiled);
		}
		
		try {
			texture.build(new BlackPawnTextureAtlasBuilder
					<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
		}catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		texture.load();
	}
	private void loadBulletTextures(){
		BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
				engine.getTextureManager(), 1280, 1280);
		
		for(int i=0; i<DataManager.getInstance().bulletParam.length; i++){
			BulletParam bullet = DataManager.getInstance().bulletParam[i];
			TiledTextureRegion tiled;
			tiled = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					texture, context, bullet.getImage(), 3, 1);//TODO check if atlas is large enough
			bullet.setTextureRegion(tiled);
		}
		
		try {
			texture.build(new BlackPawnTextureAtlasBuilder
					<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
		}catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
		
		texture.load();
	}
	private void loadTechtreeTextures(){
		//TODO
	}
	
	// ============================ UNLOAD TEXTURES (GAME) =============== //
	private void unloadGameTextures(){
		if(gameWindowTextureRegion!=null) {
			if(gameWindowTextureRegion.getTexture().isLoadedToHardware()) {
				gameWindowTextureRegion.getTexture().unload();
				gameWindowTextureRegion = null;
			}
		}
		if(gameMapTextureRegion!=null) {
			if(gameMapTextureRegion.getTexture().isLoadedToHardware()) {
				gameMapTextureRegion.getTexture().unload();
				gameMapTextureRegion = null;
			}
		}
		unloadTowerTextures();
		unloadMobTextures();
		unloadBulletTextures();
		unloadTechtreeTextures();
	}
	//LOAD TEXTURES (GAME DEPARTMENT)
	private void unloadTowerTextures(){
		//TODO
	}
	private void unloadMobTextures(){
		//TODO
	}
	private void unloadBulletTextures(){
		//TODO
	}
	private void unloadTechtreeTextures(){
		//TODO
	}
	
	// ============================ LOAD TEXTURES (TITLE) ================= //
	private void loadTitleTextures(){
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our menu assets folder to "assets/gfx/menu/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/title/");
		
		// background texture:
		if(titleBackgroundTextureRegion==null) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 1280, 800);
			titleBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "background.png",0,0);
			texture.load();
		}
		if(titleCharacterTextureRegion==null) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 566, 646);
			titleCharacterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "character.png",0,0);
			texture.load();
		}
		
		// button texture:
		if(buttonSingleTextureRegion==null 
				|| buttonDoubleTextureRegion==null 
				|| buttonOptionsTextureRegion==null) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 400, 300);
			buttonSingleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_single.png", 0 , 0 );
			buttonDoubleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_double.png", 0 , 100 );
			buttonOptionsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_options.png", 0 , 200 );
			texture.load();
		}
		
		// LOGO
		if ( titleLogoTextureRegion == null ) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 573, 148);
			titleLogoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_title.png", 0 , 0 );
			texture.load();
		}
	
		if ( titleHostGameTextureRegion == null ) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 400, 300);
			titleHostGameTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_host.png", 0 , 0 );
			titleJoinGameTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_join.png", 0 , 100 );
			titleCancelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "main_menu_button_cancel.png", 0 , 200 );
			texture.load();
		}
		
		if ( titleWindowBackTextureRegion == null ) {
			BitmapTextureAtlas texture = new BitmapTextureAtlas(engine.getTextureManager(), 800, 600);
			titleWindowBackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
					texture, context, "network_background.png", 0 , 0 );
			texture.load();
		}
		
		if ( titleArrowTiledRegion == null ) {
			BuildableBitmapTextureAtlas texture 
				= new BuildableBitmapTextureAtlas(engine.getTextureManager(),576,64 );
			titleArrowTiledRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
					texture, context, "arrow_left.png", 9 , 1 );
			try {
				texture.build(new BlackPawnTextureAtlasBuilder
						<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			}catch (TextureAtlasBuilderException e) {
				e.printStackTrace();
			}
			texture.load();
		}
		
		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}
	// ============================ UNLOAD TEXTURES (TITLE) =============== //
	private void unloadTitleTextures(){
		// background texture:
		if(titleBackgroundTextureRegion!=null) {
			if(titleBackgroundTextureRegion.getTexture().isLoadedToHardware()) {
				titleBackgroundTextureRegion.getTexture().unload();
				titleBackgroundTextureRegion = null;
			}
		}
		if(titleCharacterTextureRegion!=null) {
			if(titleCharacterTextureRegion.getTexture().isLoadedToHardware()) {
				titleCharacterTextureRegion.getTexture().unload();
				titleCharacterTextureRegion = null;
			}
		}
		
		// button texture:
		if(buttonSingleTextureRegion!=null) {
			if(buttonSingleTextureRegion.getTexture().isLoadedToHardware()) {
				buttonSingleTextureRegion.getTexture().unload();
				buttonSingleTextureRegion = null;
			}
		}
		if(buttonDoubleTextureRegion!=null) {
			if(buttonDoubleTextureRegion.getTexture().isLoadedToHardware()) {
				buttonDoubleTextureRegion.getTexture().unload();
				buttonDoubleTextureRegion = null;
			}
		}
		if(buttonOptionsTextureRegion!=null) {
			if(buttonOptionsTextureRegion.getTexture().isLoadedToHardware()) {
				buttonOptionsTextureRegion.getTexture().unload();
				buttonOptionsTextureRegion = null;
			}
		}
		
		if(titleLogoTextureRegion!=null) {
			if(titleLogoTextureRegion.getTexture().isLoadedToHardware()) {
				titleLogoTextureRegion.getTexture().unload();
				titleLogoTextureRegion = null;
			}
		}
		
		if(titleHostGameTextureRegion!=null) {
			if(titleHostGameTextureRegion.getTexture().isLoadedToHardware()) {
				titleHostGameTextureRegion.getTexture().unload();
				titleHostGameTextureRegion = null;
			}
		}
		if(titleJoinGameTextureRegion!=null) {
			if(titleJoinGameTextureRegion.getTexture().isLoadedToHardware()) {
				titleJoinGameTextureRegion.getTexture().unload();
				titleJoinGameTextureRegion = null;
			}
		}
		if(titleCancelTextureRegion!=null) {
			if(titleCancelTextureRegion.getTexture().isLoadedToHardware()) {
				titleCancelTextureRegion.getTexture().unload();
				titleCancelTextureRegion = null;
			}
		}
		if(titleWindowBackTextureRegion!=null) {
			if(titleWindowBackTextureRegion.getTexture().isLoadedToHardware()) {
				titleWindowBackTextureRegion.getTexture().unload();
				titleWindowBackTextureRegion = null;
			}
		}
		if(titleArrowTiledRegion!=null) {
			if(titleArrowTiledRegion.getTexture().isLoadedToHardware()) {
				titleArrowTiledRegion.getTexture().unload();
				titleArrowTiledRegion = null;
			}
		}

	}
	
	// =========================== LOAD SOUNDS ======================== //
	private void loadSounds(){
		SoundFactory.setAssetBasePath("sounds/");
		if(clickSound==null) {
			try {
				// Create the clickSound object via the SoundFactory class
				clickSound	= SoundFactory.createSoundFromAsset(engine.getSoundManager(), context, "button-30.wav");
			} catch (final IOException e) {
				Log.v("Sounds Load","Exception:" + e.getMessage());
			}
		}
	}
	// =========================== UNLOAD SOUNDS ====================== //
	private void unloadSounds(){
		if(clickSound!=null)
			if(clickSound.isLoaded()) {
				// Unload the clickSound object. Make sure to stop it first.
				clickSound.stop();
				engine.getSoundManager().remove(clickSound);
				clickSound = null;
			}
	}

	// ============================ LOAD FONTS ========================== //
	private void loadFonts(){
		// Create the Font objects via FontFactory class
		if(fontDefault32Bold==null) {
			fontDefault32Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  32f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault32Bold.load();
		}
		if(fontDefault72Bold==null) {
			fontDefault72Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  72f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault72Bold.load();
		}
	}
	// ============================ UNLOAD FONTS ======================== //
	private void unloadFonts(){
		// Unload the fonts
		if(fontDefault32Bold!=null) {
			fontDefault32Bold.unload();
			fontDefault32Bold = null;
		}
		if(fontDefault72Bold!=null) {
			fontDefault72Bold.unload();
			fontDefault72Bold = null;
		}
	}
	
	// ============================ SHARED METHODS ======================== //
	// hex color to RGB color
	public float[] hex2rgb(String hex) {
		float[] rgb = new float[3];
		if(hex.length()>6)
            hex = hex.substring(1,7);
	    
		rgb[0] = Integer.parseInt(hex.substring(0,2), 16) / 255f;
		rgb[1] = Integer.parseInt(hex.substring(2,4), 16) / 255f;
		rgb[2] = Integer.parseInt(hex.substring(4,6), 16) / 255f;
		return rgb;
	}
	
	// distance between tow points
	public float dist(float startX, float startY, float endX, float endY){
		return (float) Math.sqrt(Math.pow(Math.abs((endX-startX)), 2) 
				+ Math.pow(Math.abs(endY-startY), 2));
	}
	
	public void moveCenter(float centerX, float centerY, Sprite spt){
		spt.setPosition(centerX - spt.getWidth()/2, 
				centerY - spt.getHeight()/2);
	}
	public void moveCenter(float centerX, float centerY, Gauge gauge){
		gauge.setPosition(centerX - gauge.getWidth()/2, 
				centerY - gauge.getHeight()/2);
	}
	public void moveBottomCenter(float bottomCenterX, float bottomCenterY, Tower tower){
		tower.setPosition(bottomCenterX - tower.getWidth()/2, 
				bottomCenterY - tower.getHeight());
	}
	public float getCenterX(Sprite spt){
		float centerX;
		centerX = spt.getX() + spt.getWidth()/2;
		return centerX;
	}
	public float getCenterY(Sprite spt){
		float centerY;
		centerY = spt.getY() + spt.getHeight()/2;
		return centerY;
	}
}