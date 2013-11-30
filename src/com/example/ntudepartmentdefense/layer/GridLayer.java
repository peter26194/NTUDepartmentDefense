package com.example.ntudepartmentdefense.layer;


import java.util.LinkedList;
import java.util.Queue;


import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.vbo.IVertexBufferObject;
import org.andengine.util.adt.list.SmartList;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.example.ntudepartmentdefense.gamesync.GameSync;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.manager.SceneManager;


public class GridLayer extends ManagedLayer{
	private GameSync gameSync;
	
	public static short[][] spare;
	public boolean[][] visited;
	
	private Queue< short[] > bfsQueue;
	private final float edgeLength = ResourceManager.getInstance().edgeLength;
	private final short edgeUnitCount= ResourceManager.getInstance().edgeUnitCount;
	private final float edgeUnit= ResourceManager.getInstance().edgeUnit;
	
	Rectangle mapCoat;
	Rectangle selectedGrid;
	
	public GridLayer(final GameSync gameSync) {
		this.setChildrenIgnoreUpdate(false);
		this.gameSync = gameSync;
		this.visited = new boolean[ edgeUnitCount][edgeUnitCount];
		for ( int i = 0 ; i < edgeUnitCount ; i++ )
			for ( int j = 0 ; j < edgeUnitCount ; j++)
				visited[i][j] = false;
		bfsQueue = new LinkedList< short[]> ();
		
		selectedGrid = new Rectangle(0,0,edgeUnit,edgeUnit,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		selectedGrid.setVisible(false);

		// Create and attach a background that hides the Layer when touched.
		final float BackgroundX = 240f, BackgroundY = 0f;
		final float BackgroundWidth = edgeLength, BackgroundHeight = edgeLength;
		mapCoat = new Rectangle(BackgroundX, BackgroundY,
				BackgroundWidth,BackgroundHeight,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionMove() 
						&& pTouchAreaLocalX < this.getWidth() && pTouchAreaLocalX > 0 
						&& pTouchAreaLocalY < this.getHeight() && pTouchAreaLocalY > 0){
					short[] gridXY = xy2gridXY(pTouchAreaLocalX, pTouchAreaLocalY);
					selectedGrid.setPosition(edgeUnit*gridXY[0], edgeUnit*gridXY[1]);
					selectedGrid.setVisible(true);
					if( gameSync.canBuild(gridXY[0], gridXY[1]) ){
						selectedGrid.setColor(0f, 1f, 0f, 1f);//green
					}else{
						selectedGrid.setColor(1f, 0f, 0f, 1f);//red
					}
				}else if(pSceneTouchEvent.isActionUp() 
						&& pTouchAreaLocalX < this.getWidth() && pTouchAreaLocalX > 0 
						&& pTouchAreaLocalY < this.getHeight() && pTouchAreaLocalY > 0) {
					short[] gridXY = xy2gridXY(pTouchAreaLocalX, pTouchAreaLocalY);
					selectedGrid.setPosition(edgeUnit*gridXY[0], edgeUnit*gridXY[1]);
					if( gameSync.canBuild(gridXY[0], gridXY[1]) ){
						NetworkManager.getInstance().cmdBuild((short)gameSync.getGameScene().GameHud.getRightWindow().getTab(0).getPageContent().getSelected(), gridXY[0], gridXY[1] );
					}else{
						selectedGrid.setColor(1f, 0f, 0f, 1f);
						//TODO some feedback
					}
					selectedGrid.setVisible(false);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		mapCoat.setColor(0f, 0f, 0f, 0.0f);
		mapCoat.attachChild(selectedGrid);
	}
	
	private short[] xy2gridXY(float x, float y){
		short[] gridXY = new short[2];
		gridXY[0] = (short) (x/edgeUnit);
		gridXY[1] = (short) (y/edgeUnit);
		return gridXY;
	}

	@Override
	public void onLoadLayer() {

		this.attachChild(mapCoat);
		this.registerTouchArea(mapCoat);
		
		
	}
	private AlphaModifier fadeIn ;
	@Override
	public void onShowLayer() {
		ResourceManager.clickSound.play();
		this.setVisible(true);
		fadeIn = new AlphaModifier( 0.5f , 0.0f , 0.2f);
		mapCoat.clearEntityModifiers();
		mapCoat.registerEntityModifier(fadeIn);
	}
	
	private AlphaModifier fadeOut ; 
	
	public void hide() {
		ResourceManager.clickSound.play();
		fadeOut = new AlphaModifier( 0.5f , 0.2f , 0.0f) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				SceneManager.getInstance().hideLayer();
			}
		};
		mapCoat.clearEntityModifiers();
		mapCoat.registerEntityModifier(fadeOut);

	}
	@Override
	public void onHideLayer() {
		setVisible(false);
	}
	@Override
	public void onUnloadLayer() {
		
	}
}
