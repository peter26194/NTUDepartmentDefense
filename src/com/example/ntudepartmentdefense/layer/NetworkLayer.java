package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.manager.SceneManager;
import com.example.ntudepartmentdefense.util.NameDialog;
import com.example.ntudepartmentdefense.util.NameDialog.NameDialogListener;



import android.widget.Toast;






public class NetworkLayer extends ManagedLayer {
	// Constants


	public static final short NETWORK_OPTION = 0;
	public static final short NETWORK_SERVER = 1;
	public static final short NETWORK_SERVER_WAITING = 2;
	public static final short NETWORK_SERVER_CONFIRM = 3;
	
	public static final short NETWORK_CLIENT = 10;
	public static final short NETWORK_CLIENT_WAITING = 11;
	
	public static final short NETWORK_UNLOAD = 20;
	public static final short NETWORK_ANIMATING = 25;
	public static final short NETWORK_SUCCESS = 30;
	
	public static final short MAX_MAP_ID = 3 ;//ResourceManager.MAX_MAP_ID;
	public static final short MAX_DEPARTMENT_ID = 3 ; // ResourceManager.MAX_DEPARTMENT_ID;
	
	// Variables
	private short status = NETWORK_UNLOAD;
	
	private boolean[] unlockMaps = new boolean[ MAX_MAP_ID ];
	private Text   [] mapStrings = new Text[MAX_MAP_ID];
	private Text   [] mapSprites = new Text[MAX_MAP_ID];
	private short selectedMapIndex = 0;
	private Rectangle mapNameBack;
	
	private boolean[] unlockDepartments = new boolean[ MAX_DEPARTMENT_ID ];
	private Text   [] departmentStrings =  new Text[ MAX_DEPARTMENT_ID];
	private Text   [] departmentSprites =  new Text[ MAX_DEPARTMENT_ID];
	private short selectedDepartmentIndex = 0;
	private Rectangle departmentNameBack;
	
	private Text serverNameText;
	private Text clientNameText;
	private Text serverIPText;
	private Text guestNameText;
	
	// Windows
	private Rectangle background;
	private Rectangle mainOptionWindow;
	
	private Rectangle serverOptionWindow;
	private Rectangle serverWaitingWindow;

	private Rectangle clientOptionWindow;
	private Rectangle clientWaitingWindow;
	
	private Rectangle guestJoinedWindow;
	
	// Public Methods
	public NetworkLayer() {
		this.setChildrenIgnoreUpdate(false);
		NetworkManager.getInstance().reset();
	}
	@Override
	public void onLoadLayer() {
		//ResourceManager.loadNetworkResources();
		createAllWindows();
	}

	@Override
	public void onShowLayer() {
		status = NETWORK_ANIMATING;
		onShowBackground();
		onShowMainOptionWindow();
	}


	@Override
	public void onHideLayer() {

	}

	private void onHideAll() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUnloadLayer() {
		//ResourceManager.unloadNetworkResources();
	}
	
	// Private Methods
	private void createAllWindows() {
		// Create Main Option Window
		createBackground();
		createMainOptionWindow();
		createMapSelection();
		createDepartmentSelection();
		createServerWindow();
		createServerWaitingWindow();
		createClientWindow();
		createClientWaitingWindow();
		createGuestJoinedWindow();
	}

	private void createBackground() {
		// TODO Auto-generated method stub
		background = new Rectangle(0,0,ResourceManager.getInstance().cameraWidth
						, ResourceManager.getInstance().cameraHeight ,
						ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		background.setAlpha(0f);
		background.setColor(0f,0f,0f);
		background.setVisible(false);
		this.attachChild(background);
	}
	private void onShowBackground() {
		// TODO Auto-generated method stub
		background.setVisible(true);
		AlphaModifier am = new AlphaModifier( 1f , 0f , 0.5f );
		am.setAutoUnregisterWhenFinished(true);
		background.registerEntityModifier(am);
	}
	private void onHideBackground() {
		// TODO Auto-generated method stub
		AlphaModifier am = new AlphaModifier( 1f , 0.5f , 0f ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				pItem.setVisible(false);
			}
		};
		am.setAutoUnregisterWhenFinished(true);
		background.registerEntityModifier(am);
	}

	private void createServerWaitingWindow() {
		// TODO Auto-generated method stub
		final float windowHeight = 300f;
		final float windowWidth  = 300f;
		serverWaitingWindow = new Rectangle( ResourceManager.getInstance().cameraWidth / 2 - windowWidth / 2
			       , ResourceManager.getInstance().cameraHeight /2 - windowHeight / 2
			       , windowWidth
			       , windowHeight
			       , ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER_WAITING ) {
						NetworkManager.getInstance().sendHelloMessage();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};;
		serverWaitingWindow.setColor(0f,0f,0f);
		serverWaitingWindow.setVisible(false);
		

		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"CANCEL", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
	
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , cancelText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER_WAITING ) {
						NetworkManager.getInstance().reset();
						NetworkLayer.this.onHideServerWaitingWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		serverWaitingWindow.attachChild(cancelButton);
		cancelButton.setPosition( serverWaitingWindow.getWidth() /2 - cancelButton.getWidth() / 2,
				serverWaitingWindow.getHeight() - cancelButton.getHeight() );
		this.registerTouchArea(cancelButton);
		this.registerTouchArea(serverWaitingWindow);
		this.attachChild(serverWaitingWindow);
		
	}
	protected void onShowServerWaitingWindow() {
		status = NETWORK_ANIMATING;
		serverWaitingWindow.setVisible(true);
		AlphaModifier move = new AlphaModifier( 1f , 0f ,1f) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_SERVER_WAITING;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		serverWaitingWindow.registerEntityModifier(move);
	}
	protected void onHideServerWaitingWindow() {
		status = NETWORK_ANIMATING;
		AlphaModifier move = new AlphaModifier( 1f , 1f, 0f ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_SERVER;
				serverWaitingWindow.setVisible(false);
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		serverWaitingWindow.registerEntityModifier(move);
		
	}

	
	private void createServerWindow() {
		final float windowHeight = 600f;
		final float windowWidth  = 800f;
		
		// Window
		serverOptionWindow = new Rectangle( -1* windowWidth
			       , ResourceManager.getInstance().cameraHeight / 2 - windowHeight/ 2
			       , windowWidth
			       , windowHeight
			       , ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		serverOptionWindow.setVisible(false);
		
		
		// Buttons
		Text hostText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"HOST", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		hostText.setColor(255f, 255f, 255f);

		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"CANCEL", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
		
		Rectangle hostButton = new Rectangle(0,0,hostText.getWidth() , hostText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER ) {
						NetworkManager.getInstance().reset();
						NetworkManager.getInstance().setLayer(NetworkLayer.this);
						NetworkManager.getInstance().initHost( serverNameText.getText().toString() 
								                              ,selectedDepartmentIndex
								                              ,selectedMapIndex);
						NetworkLayer.this.onShowServerWaitingWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		hostButton.setColor(0f, 0f, 0f);
		hostButton.attachChild(hostText);
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , cancelText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER ) {
						NetworkLayer.this.onHideServerWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		serverOptionWindow.attachChild(hostButton);
		serverOptionWindow.attachChild(cancelButton);
		hostButton.setPosition( serverOptionWindow.getWidth() * 0.25f - hostButton.getWidth()/2 ,
				serverOptionWindow.getHeight() * 11/12 - hostButton.getHeight() );
		cancelButton.setPosition( serverOptionWindow.getWidth() * 0.75f - cancelButton.getWidth() / 2  ,
				serverOptionWindow.getHeight() * 11/12 - cancelButton.getHeight() );
		this.registerTouchArea(hostButton);
		this.registerTouchArea(cancelButton);
		
		// NameString input region
		serverNameText =  new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"Name of Host", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		serverNameText.setColor(255f, 255f, 255f);
		Rectangle nameBack = new Rectangle(0,0 , 600 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER ) {
						NameDialog serverNameDialog = new NameDialog();
						serverNameDialog.setListener( new NameDialog.NameDialogListener() {
							
							@Override
							public void onNameDialogOK(String name) {
								if ( name == null || name.trim().length() == 0)
									NetworkLayer.this.updateServerName("Host Name");
								else NetworkLayer.this.updateServerName(name);


							}
						});
						serverNameDialog.show( ResourceManager.getInstance().context.getFragmentManager(), "Server Name");
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		nameBack.setColor(0f, 0f, 0f);
		nameBack.attachChild(serverNameText);
		serverNameText.setPosition( nameBack.getWidth() / 2  - serverNameText.getWidth() / 2,
								    nameBack.getHeight() / 2 - serverNameText.getHeight() / 2 );
		
		serverOptionWindow.attachChild(nameBack);
		nameBack.setPosition( serverOptionWindow.getWidth() / 2 - nameBack.getWidth() / 2, 
							  50 - nameBack.getHeight() / 2);
		this.registerTouchArea(nameBack);
		
		
		// Map Selection
		
		serverOptionWindow.attachChild(mapNameBack);
		mapNameBack.setPosition( serverOptionWindow.getWidth() / 4 - mapNameBack.getWidth() / 2, 
							    100 + 50 - mapNameBack.getHeight() / 2);
	
		// Department Selection


	
		this.attachChild(serverOptionWindow);
	}
	private void createMapSelection() {
		// TODO Auto-generated method stub
		for ( int i = 0 ; i < MAX_MAP_ID ; i ++ ){
			unlockMaps[i] = false;
			// mapStrings[i] = DataManager.getInstance().getMapNameText(i);
			// mapSprites[i] = DataManager.getInstance().getMapSprite(i);
		}
		
		unlockMaps[0] = true;
		// ====== TEST ONLY
		mapStrings[0] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP00", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapStrings[0].setColor(255f, 255f, 255f); 
		mapSprites[0] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP00", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapSprites[0].setColor(255f, 255f, 255f); 
		unlockMaps[1] = true;
		mapStrings[1] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP01", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapStrings[1].setColor(255f, 255f, 255f); 
		mapSprites[1] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP01", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapSprites[1].setColor(255f, 255f, 255f); 
		unlockMaps[2] = true;
		mapStrings[2] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP02", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapStrings[2].setColor(255f, 255f, 255f); 
		mapSprites[2] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MAP02", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		mapSprites[2].setColor(255f, 255f, 255f); 
		
		// ======
		
		selectedMapIndex = 0;
		mapNameBack = new Rectangle(0,0 , 300 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		mapNameBack.setColor(0f, 0f, 0f);
		for( int i = 0 ; i < MAX_MAP_ID ; i++  ) {
			if ( !unlockMaps[i] )
				continue;
			Text selectedMapName = mapStrings[i];
			mapNameBack.attachChild(selectedMapName);
			selectedMapName.setPosition( mapNameBack.getWidth() / 2  - selectedMapName.getWidth() / 2,
					 mapNameBack.getHeight() / 2 - selectedMapName.getHeight() / 2 );
			selectedMapName.setVisible(false);
			Text selectedMapSprite = mapSprites[ i ];
			mapNameBack.attachChild(  selectedMapSprite );
			selectedMapSprite.setVisible(false);
			selectedMapSprite.setPosition( mapNameBack.getWidth() / 2 - selectedMapSprite.getWidth() / 2
										  , mapNameBack.getHeight() + 150 - selectedMapSprite.getHeight() / 2);
		}

		

		Rectangle mapLeftButton = new Rectangle(0,0 , 80 , 80,  
						ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER || status == NETWORK_CLIENT ) {
						NetworkLayer.this.onMapArrowTouched(false);
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		mapLeftButton.setColor(0f, 0f, 0f);
		
		Text left = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"<<", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		left.setPosition( mapLeftButton.getWidth()/2  - left.getWidth() / 2 
						 ,mapLeftButton.getHeight()/2 - left.getHeight() / 2 );
		mapLeftButton.attachChild( left );
		mapNameBack.attachChild( mapLeftButton );
		mapLeftButton.setPosition( 0, mapNameBack.getHeight() / 2 - mapLeftButton.getHeight() / 2 );
		Rectangle mapRightButton = new Rectangle(0,0 , 80 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER || status == NETWORK_CLIENT) {
						NetworkLayer.this.onMapArrowTouched(true);
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		mapRightButton.setColor(0f, 0f, 0f);
		Text right = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				">>", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		right.setPosition( mapRightButton.getWidth()/2  - right.getWidth() / 2 
						 ,mapRightButton.getHeight()/2 - right.getHeight() / 2 );
		mapRightButton.attachChild( right );
		mapNameBack.attachChild( mapRightButton );
		mapRightButton.setPosition( mapNameBack.getWidth() - mapRightButton.getWidth() 
				                 , mapNameBack.getHeight() / 2 - mapRightButton.getHeight() / 2 );
		
		
		this.registerTouchArea(mapLeftButton);
		this.registerTouchArea(mapRightButton);
		mapStrings[ selectedMapIndex ].setVisible(true);
		mapSprites[ selectedMapIndex ].setVisible(true);

		
	}
	private synchronized void onMapArrowTouched( boolean right ) {
		mapStrings[ selectedMapIndex ].setVisible(false);
		mapSprites[ selectedMapIndex ].setVisible(false);
		do {
			selectedMapIndex += (right) ? 1 : MAX_MAP_ID - 1;
			selectedMapIndex %= MAX_MAP_ID;
		} while( !unlockMaps[selectedMapIndex] ); 
		mapStrings[ selectedMapIndex ].setVisible(true);
		mapSprites[ selectedMapIndex ].setVisible(true);
	}
	private void createDepartmentSelection() {
		// TODO Auto-generated method stub
		for ( int i = 0 ; i < MAX_DEPARTMENT_ID ; i ++ ){
			unlockDepartments[i] = false;
			// mapStrings[i] = DataManager.getInstance().getDepartmentNameText(i);
			// mapSprites[i] = DataManager.getInstance().getDepartmentSprite(i);
		}
		
		unlockDepartments[0] = true;
		// ====== TEST ONLY
		departmentStrings[0] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"EE", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentStrings[0].setColor(255f, 255f, 255f); 
		departmentSprites[0] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"EE", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentSprites[0].setColor(255f, 255f, 255f); 
		unlockDepartments[1] = true;
		departmentStrings[1] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"LAW", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentStrings[1].setColor(255f, 255f, 255f); 
		departmentSprites[1] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"LAW", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentSprites[1].setColor(255f, 255f, 255f); 
		unlockDepartments[2] = true;
		departmentStrings[2] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MED", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentStrings[2].setColor(255f, 255f, 255f); 
		departmentSprites[2] = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"MED", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		departmentSprites[2].setColor(255f, 255f, 255f); 
		
		// ======
		
		selectedMapIndex = 0;
		departmentNameBack = new Rectangle(0,0 , 300 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		departmentNameBack.setColor(0f, 0f, 0f);
		for( int i = 0 ; i < MAX_DEPARTMENT_ID ; i++  ) {
			if ( !unlockDepartments[i] )
				continue;
			Text selectedDepartmentName = departmentStrings[i];
			departmentNameBack.attachChild(selectedDepartmentName);
			selectedDepartmentName.setPosition( departmentNameBack.getWidth() / 2  - selectedDepartmentName.getWidth() / 2,
					departmentNameBack.getHeight() / 2 - selectedDepartmentName.getHeight() / 2 );
			selectedDepartmentName.setVisible(false);
			Text selectedDepartmentSprite = departmentSprites[ i ];
			departmentNameBack.attachChild(  selectedDepartmentSprite );
			selectedDepartmentSprite.setVisible(false);
			selectedDepartmentSprite.setPosition( departmentNameBack.getWidth() / 2 - selectedDepartmentSprite.getWidth()/2
										  , departmentNameBack.getHeight() + 150 - selectedDepartmentSprite.getHeight() / 2);
		}

		

		Rectangle departmentLeftButton = new Rectangle(0,0 , 80 , 80,  
						ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER|| status == NETWORK_CLIENT ) {
						NetworkLayer.this.onDepartmentArrowTouched(false);
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		departmentLeftButton.setColor(0f, 0f, 0f);
		
		Text left = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"<<", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		left.setPosition( departmentLeftButton.getWidth()/2  - left.getWidth() / 2 
						 ,departmentLeftButton.getHeight()/2 - left.getHeight() / 2 );
		departmentLeftButton.attachChild( left );
		departmentNameBack.attachChild( departmentLeftButton );
		departmentLeftButton.setPosition( 0, departmentNameBack.getHeight() / 2 - departmentLeftButton.getHeight() / 2 );
		Rectangle departmentRightButton = new Rectangle(0,0 , 80 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER || status == NETWORK_CLIENT) {
						NetworkLayer.this.onDepartmentArrowTouched(true);
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		departmentRightButton.setColor(0f, 0f, 0f);
		Text right = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				">>", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		right.setPosition( departmentRightButton.getWidth()/2  - right.getWidth() / 2 
						 ,departmentRightButton.getHeight()/2 - right.getHeight() / 2 );
		departmentRightButton.attachChild( right );
		departmentNameBack.attachChild( departmentRightButton );
		departmentRightButton.setPosition( departmentNameBack.getWidth() - departmentRightButton.getWidth() 
				                 , departmentNameBack.getHeight() / 2 - departmentRightButton.getHeight() / 2 );
		
		
		this.registerTouchArea(departmentLeftButton);
		this.registerTouchArea(departmentRightButton);
		departmentStrings[ selectedDepartmentIndex ].setVisible(true);
		departmentSprites[ selectedDepartmentIndex ].setVisible(true);

		
	}

	protected void onDepartmentArrowTouched(boolean right) {
		departmentStrings[ selectedDepartmentIndex ].setVisible(false);
		departmentSprites[ selectedDepartmentIndex ].setVisible(false);
		do {
			selectedDepartmentIndex += (right) ? 1 : MAX_DEPARTMENT_ID - 1;
			selectedDepartmentIndex %= MAX_DEPARTMENT_ID;
		} while( !unlockDepartments[selectedDepartmentIndex] ); 
		departmentStrings[ selectedDepartmentIndex ].setVisible(true);
		departmentSprites[ selectedDepartmentIndex ].setVisible(true);
		
	}
	protected void onShowServerWindow() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		status = NETWORK_ANIMATING;
		
		serverOptionWindow.attachChild(departmentNameBack);
		departmentNameBack.setPosition( serverOptionWindow.getWidth() *0.75f - departmentNameBack.getWidth() / 2, 
							    100 + 50 - departmentNameBack.getHeight() / 2);
		

		serverOptionWindow.setVisible(true);
		MoveXModifier move = new MoveXModifier( 1f , -1* serverOptionWindow.getWidth() ,
				ResourceManager.getInstance().cameraWidth/ 2 -  serverOptionWindow.getWidth()  /2 ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_SERVER;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		serverOptionWindow.registerEntityModifier(move);
	}
	protected void onHideServerWindow() {
		status = NETWORK_ANIMATING;
		// TODO Auto-generated method stub
		

		MoveXModifier move = new MoveXModifier( 1f 
				, ResourceManager.getInstance().cameraWidth/ 2 -  serverOptionWindow.getWidth() / 2 
				, -1* serverOptionWindow.getWidth()) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_OPTION;
				serverOptionWindow.setVisible(false);
				ResourceManager.getInstance().context.runOnUpdateThread( new Runnable() {
					@Override
					public void run() {
						if ( departmentNameBack.hasParent() )
							departmentNameBack.detachSelf();
					}
				});
				
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		serverOptionWindow.registerEntityModifier(move);
	}

	private void updateServerName(String serverName) {
		
		serverNameText.setText(serverName);
		Rectangle nameBack = (Rectangle)serverNameText.getParent();
		serverNameText.setPosition( nameBack.getWidth() / 2  - serverNameText.getWidth() / 2,
			    nameBack.getHeight() / 2 - serverNameText.getHeight() / 2 );

	}
	
	private void createMainOptionWindow() {
		// Button Gap
		final float buttonGap = 100f;
		// init texts
		Text hostText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"HOST", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		hostText.setColor(255f, 255f, 255f);

		Text joinText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"JOIN", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		joinText.setColor(255f, 255f, 255f);
		
		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"CANCEL", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
		final float windowHeight = hostText.getHeight();
		final float windowWidth = hostText.getWidth() + buttonGap +
								  joinText.getWidth() + buttonGap +
								  cancelText.getWidth();
		// init buttons
		Rectangle hostButton = new Rectangle(0,0,hostText.getWidth() , windowHeight,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_OPTION ) {
						NetworkLayer.this.onShowServerWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		hostButton.setColor(0f, 0f, 0f);
		hostButton.attachChild(hostText);
		Rectangle joinButton = new Rectangle(0,0,joinText.getWidth() , windowHeight,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_OPTION ) {
						NetworkLayer.this.onShowClientWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		joinButton.setColor(0f, 0f, 0f);
		joinButton.attachChild(joinText);
		
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , windowHeight,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_OPTION ) {
						NetworkLayer.this.onHideOptionWindow();
						NetworkLayer.this.onHideBackground();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		
		mainOptionWindow = new Rectangle( ResourceManager.getInstance().cameraWidth / 2 - windowWidth / 2
					       ,-1* windowHeight
					       ,windowWidth
					       ,windowHeight
					       ,ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		mainOptionWindow.attachChild(hostButton);
		mainOptionWindow.attachChild(joinButton);
		mainOptionWindow.attachChild(cancelButton);
		hostButton.setPosition(0,0);
		joinButton.setPosition(buttonGap + hostButton.getWidth(), 0);
		cancelButton.setPosition( joinButton.getX()+ buttonGap + joinButton.getWidth() , 0);
		this.registerTouchArea(hostButton);
		this.registerTouchArea(joinButton);
		this.registerTouchArea(cancelButton);
		mainOptionWindow.setVisible(false);
		this.attachChild(mainOptionWindow);
	}
	private void onShowMainOptionWindow() {
		// TODO Auto-generated method stub
		status = NETWORK_ANIMATING;
		mainOptionWindow.setVisible(true);
		MoveByModifier move = new MoveByModifier( 1f , 0 , mainOptionWindow.getHeight() ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_OPTION;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		mainOptionWindow.registerEntityModifier(move);
	}
	protected void onHideOptionWindow() {
		status = NETWORK_ANIMATING;
		MoveByModifier move = new MoveByModifier( 1f , 0 , -1* mainOptionWindow.getHeight() ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_UNLOAD;
				mainOptionWindow.setVisible(false);
				SceneManager.getInstance().hideLayer();
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		mainOptionWindow.registerEntityModifier(move);
	}

	private void createClientWindow() {
		// TODO Auto-generated method stub
		final float windowHeight = 600f;
		final float windowWidth  = 800f;
		clientOptionWindow = new Rectangle( -1* windowWidth
			       , ResourceManager.getInstance().cameraHeight / 2 - windowHeight/ 2
			       , windowWidth
			       , windowHeight
			       , ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		clientOptionWindow.setVisible(false);
		
		Text joinText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"JOIN", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		joinText.setColor(255f, 255f, 255f);

		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"CANCEL", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
		
		Rectangle joinButton = new Rectangle(0,0,joinText.getWidth() , joinText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT ) {
						NetworkManager.getInstance().reset();
						NetworkManager.getInstance().setLayer(NetworkLayer.this);
						NetworkManager.getInstance().initGuest( clientNameText.getText().toString() , selectedDepartmentIndex, serverIPText.getText().toString().trim() );
						NetworkLayer.this.onShowClientWaitingWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		joinButton.setColor(0f, 0f, 0f);
		joinButton.attachChild(joinText);
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , cancelText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT ) {
						NetworkLayer.this.onHideClientWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		clientOptionWindow.attachChild(joinButton);
		clientOptionWindow.attachChild(cancelButton);

		
		
		// NameString input region
		clientNameText =  new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"Name of client", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		clientNameText.setColor(255f, 255f, 255f);
		Rectangle nameBack = new Rectangle(0,0 , 600 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT ) {
						NameDialog clientNameDialog = new NameDialog();
						clientNameDialog.setListener( new NameDialog.NameDialogListener() {
							
							@Override
							public void onNameDialogOK(String name) {
								if ( name == null || name.trim().length() == 0)
									NetworkLayer.this.updateClientName("Name");
								else NetworkLayer.this.updateClientName(name);


							}
						});
						clientNameDialog.show( ResourceManager.getInstance().context.getFragmentManager(), "Client Name");
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		nameBack.setColor(0f, 0f, 0f);
		nameBack.attachChild(clientNameText);
		clientNameText.setPosition( nameBack.getWidth() / 2  - clientNameText.getWidth() / 2,
								    nameBack.getHeight() / 2 - clientNameText.getHeight() / 2 );
		
		clientOptionWindow.attachChild(nameBack);
		nameBack.setPosition( clientOptionWindow.getWidth() / 2 - nameBack.getWidth() / 2, 
							  50 - nameBack.getHeight() / 2);
		this.registerTouchArea(nameBack);
		
		
		
		// IPString input region
		serverIPText =  new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"127.120.120.121", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		serverIPText.setColor(255f, 255f, 255f);
		Rectangle ipBack = new Rectangle(0,0 , 600 , 80,  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT ) {
						NameDialog ipDialog = new NameDialog();
						ipDialog.setListener( new NameDialog.NameDialogListener() {
							
							@Override
							public void onNameDialogOK(String ip) {
								if ( ip == null || ip.trim().length() == 0)
									NetworkLayer.this.updateIPString("127.0.0.1");
								else NetworkLayer.this.updateIPString(ip);


							}
						});
						ipDialog.show( ResourceManager.getInstance().context.getFragmentManager(), "IP");
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		ipBack.setColor(0f, 0f, 0f);
		ipBack.attachChild(serverIPText);
		serverIPText.setPosition( ipBack.getWidth() / 2  - serverIPText.getWidth() / 2,
				                  ipBack.getHeight() / 2 - serverIPText.getHeight() / 2 );
		
		clientOptionWindow.attachChild(ipBack);
		ipBack.setPosition( clientOptionWindow.getWidth() / 2 - ipBack.getWidth() / 2, 
				nameBack.getY() + nameBack.getHeight()/2 + 100 - ipBack.getHeight() / 2);
		this.registerTouchArea(ipBack);
		
		
	
		// Department Selection
		
		
		// Reposition
		joinButton.setPosition( clientOptionWindow.getWidth() * 0.75f - joinButton.getWidth()/2 ,
				ipBack.getY() + ipBack.getHeight() / 2 + 175 - joinButton.getHeight() / 2 );
		cancelButton.setPosition( clientOptionWindow.getWidth() * 0.75f - cancelButton.getWidth() / 2  ,
				joinButton.getY() + joinButton.getHeight() / 2 + 150 - cancelButton.getHeight() / 2 );
		this.registerTouchArea(joinButton);
		this.registerTouchArea(cancelButton);
		
		
		this.attachChild(clientOptionWindow);
	}
	protected void updateIPString(String string) {
		serverIPText.setText(string);
		Rectangle ipBack = (Rectangle)serverIPText.getParent();
		serverIPText.setPosition( ipBack.getWidth() / 2  - serverIPText.getWidth() / 2,
				ipBack.getHeight() / 2 - serverIPText.getHeight() / 2 );

		
	}
	protected void updateClientName(String clientName) {
		clientNameText.setText(clientName);
		Rectangle nameBack = (Rectangle)clientNameText.getParent();
		clientNameText.setPosition( nameBack.getWidth() / 2  - clientNameText.getWidth() / 2,
			    nameBack.getHeight() / 2 - clientNameText.getHeight() / 2 );
		
	}
	protected void onShowClientWindow() {

		
		status = NETWORK_ANIMATING;
		clientOptionWindow.attachChild(departmentNameBack);
		departmentNameBack.setPosition( clientOptionWindow.getWidth() *0.25f - departmentNameBack.getWidth() / 2, 
							   250 - departmentNameBack.getHeight() / 2);
	
		clientOptionWindow.setVisible(true);
		MoveXModifier move = new MoveXModifier( 1f ,ResourceManager.getInstance().cameraWidth ,
				ResourceManager.getInstance().cameraWidth/ 2 -  serverOptionWindow.getWidth()  /2 ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_CLIENT;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		clientOptionWindow.registerEntityModifier(move);
	}
	protected void onHideClientWindow() {
		status = NETWORK_ANIMATING;
		// TODO Auto-generated method stub

		MoveXModifier move = new MoveXModifier( 1f 
				, ResourceManager.getInstance().cameraWidth/ 2 -  clientOptionWindow.getWidth() / 2 
				, ResourceManager.getInstance().cameraWidth) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_OPTION;
				clientOptionWindow.setVisible(false);
				ResourceManager.getInstance().context.runOnUpdateThread( new Runnable() {
					@Override
					public void run() {
						if ( departmentNameBack.hasParent() )
							departmentNameBack.detachSelf();
					}
				});
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		clientOptionWindow.registerEntityModifier(move);
	}

	private void createClientWaitingWindow() {
		// TODO Auto-generated method stub
		final float windowHeight = 300f;
		final float windowWidth  = 300f;
		clientWaitingWindow = new Rectangle( ResourceManager.getInstance().cameraWidth / 2 - windowWidth / 2
			       , ResourceManager.getInstance().cameraHeight /2 - windowHeight / 2
			       , windowWidth
			       , windowHeight
			       , ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT_WAITING ) {
						NetworkManager.getInstance().sendHelloMessage();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		clientWaitingWindow.setColor(0f,0f,0f);
		clientWaitingWindow.setVisible(false);
		

		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"CANCEL", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
	
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , cancelText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_CLIENT_WAITING ) {
						NetworkManager.getInstance().reset();
						NetworkLayer.this.onHideClientWaitingWindow();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		clientWaitingWindow.attachChild(cancelButton);
		cancelButton.setPosition( clientWaitingWindow.getWidth() /2 - cancelButton.getWidth() / 2,
				clientWaitingWindow.getHeight() - cancelButton.getHeight() );
		this.registerTouchArea(cancelButton);
		
	
		this.registerTouchArea(clientWaitingWindow);
		this.attachChild(clientWaitingWindow);
		
	}
	protected void onShowClientWaitingWindow() {
		status = NETWORK_ANIMATING;
		clientWaitingWindow.setVisible(true);
		AlphaModifier move = new AlphaModifier( 1f , 0f ,1f) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_CLIENT_WAITING;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		clientWaitingWindow.registerEntityModifier(move);
	}
	protected void onHideClientWaitingWindow() {
		status = NETWORK_ANIMATING;
		AlphaModifier move = new AlphaModifier( 1f , 1f, 0f ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_CLIENT;
				clientWaitingWindow.setVisible(false);
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		clientWaitingWindow.registerEntityModifier(move);
		
	}
	private void createGuestJoinedWindow() {
		// TODO Auto-generated method stub
		final float windowHeight = 300f;
		final float windowWidth  = 300f;
		guestJoinedWindow = new Rectangle( ResourceManager.getInstance().cameraWidth / 2 - windowWidth / 2
			       , ResourceManager.getInstance().cameraHeight /2 - windowHeight / 2
			       , windowWidth
			       , windowHeight
			       , ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) ;
		
		guestJoinedWindow.setColor(0f,0f,0f);
		guestJoinedWindow.setVisible(false);
		
		guestNameText =  new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"GUESTNAMEBUFFER", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		
		guestNameText.setColor(255f, 255f, 255f);
		
		guestJoinedWindow.attachChild(guestNameText);
		guestNameText.setPosition( guestJoinedWindow.getWidth()  / 2 - guestNameText.getWidth() / 2 
								  ,guestJoinedWindow.getHeight() / 4 - guestNameText.getHeight()/ 2);
		
		Text acceptText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"ACCEPT", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		acceptText.setColor(255f, 255f, 255f);
		
	
		Rectangle acceptButton = new Rectangle(0,0,acceptText.getWidth() , acceptText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER_CONFIRM ) {
						NetworkManager.getInstance().sendHostAcceptMessage();
						NetworkManager.getInstance().onGameAccept();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		acceptButton.setColor(0f, 0f, 0f);
		acceptButton.attachChild(acceptText);
		guestJoinedWindow.attachChild(acceptButton);
		acceptButton.setPosition( guestJoinedWindow.getWidth() / 2 - acceptButton.getWidth() / 2,
				guestJoinedWindow.getHeight() /2 - acceptButton.getHeight() /2);
		this.registerTouchArea(acceptButton);
		
		
		
		
		Text cancelText = new Text(0, 0, ResourceManager.fontDefault32Bold, 
				"REJECT", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		cancelText.setColor(255f, 255f, 255f);
		
	
		Rectangle cancelButton = new Rectangle(0,0,cancelText.getWidth() , cancelText.getHeight(),  
				ResourceManager.getInstance().engine.getVertexBufferObjectManager() ) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					if ( status == NETWORK_SERVER_CONFIRM ) {
						NetworkLayer.this.onHideGuestJoinedWindow();
						NetworkManager.getInstance().sendHostRejectMessage();
					}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		cancelButton.setColor(0f, 0f, 0f);
		cancelButton.attachChild(cancelText);
		guestJoinedWindow.attachChild(cancelButton);
		cancelButton.setPosition( guestJoinedWindow.getWidth() / 2 - cancelButton.getWidth() / 2,
				guestJoinedWindow.getHeight() *3/4- cancelButton.getHeight() /2);
		this.registerTouchArea(cancelButton);
		this.attachChild(guestJoinedWindow);
		
	}
	protected void onShowGuestJoinedWindow() {
		status = NETWORK_ANIMATING;
		guestNameText.setText(NetworkManager.guestName);
		guestNameText.setPosition( guestJoinedWindow.getWidth()  / 2 - guestNameText.getWidth() / 2 
				  ,guestJoinedWindow.getHeight() / 4 - guestNameText.getHeight()/ 2);
		guestJoinedWindow.setVisible(true);
		AlphaModifier move = new AlphaModifier( 1f , 0f ,1f) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_SERVER_CONFIRM;
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		guestJoinedWindow.registerEntityModifier(move);
	}
	protected void onHideGuestJoinedWindow() {
		status = NETWORK_ANIMATING;
		AlphaModifier move = new AlphaModifier( 1f , 1f, 0f ) {
			@Override
			protected void onModifierFinished( IEntity pItem ) {
				status = NETWORK_SERVER_WAITING;
				guestJoinedWindow.setVisible(false);
			}
		};
		move.setAutoUnregisterWhenFinished(true);
		guestJoinedWindow.registerEntityModifier(move);
		
	}
	public void onGuestJoined() {
		if ( status == NETWORK_SERVER_WAITING ) {
			NetworkLayer.this.onShowGuestJoinedWindow();
		}
	}
	public void onGameReject() {
		if ( status == NETWORK_CLIENT_WAITING ) {
			NetworkLayer.this.onHideClientWaitingWindow();
			ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ResourceManager.getInstance().context, "Declined", Toast.LENGTH_SHORT).show();
				}
			});
		}
		else if ( status == NETWORK_SERVER_WAITING ) {
			NetworkLayer.this.onHideServerWaitingWindow();
			ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ResourceManager.getInstance().context, "Declined", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
