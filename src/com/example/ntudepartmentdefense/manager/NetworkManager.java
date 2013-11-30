package com.example.ntudepartmentdefense.manager;

import de.exitgames.api.loadbalancing.LoadBalancingClient;
import de.exitgames.api.loadbalancing.Player;
import de.exitgames.client.photon.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.LinkedList;



import org.andengine.audio.sound.Sound;
import org.andengine.engine.Engine;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.example.ntudepartmentdefense.layer.NetworkLayer;
import com.example.ntudepartmentdefense.network.PhotonClient;
import com.example.ntudepartmentdefense.scene.GameScene;
import com.example.ntudepartmentdefense.util.Command;
import com.example.ntudepartmentdefense.util.CommandPool;



import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;

public class NetworkManager {
	//====================================================
	// CONSTANTS
	//====================================================
	public static final String SERVER_COLOR = "FF0000";
	public static final String CLIENT_COLOR = "00FF00";
	
	private static CommandPool commandPool = new CommandPool();
	private static final NetworkManager INSTANCE = new NetworkManager();

	private static final short IS_HOST    = 0;
	private static final short IS_GUEST    = 1;
	private static final short IS_UNDEFINED = 2;
	private static final short IS_SINGLE    = 3;
	private static final int DEFAULT_FRAMES = 6;
	//====================================================
	// VARIABLES
	//====================================================
	
	private static short connection = IS_UNDEFINED;
	
	// =================== Connection Setting ================= //
	public static PhotonClient client;
	public static String hostName;
	public static String guestName;
	public static short hostDepartmentID = -1;
	public static short guestDepartmentID = -1;
	public static short mapID = -1;
	public NetworkLayer layer;
	public GameScene    scene;
	public boolean gameStarted =false;
	// =================== Synchronization ================= //

	
	public SparseArray< Queue<Command> > hostCmdMap = new SparseArray< Queue<Command> >();
	public SparseArray< Queue<Command> > guestCmdMap = new SparseArray< Queue<Command> >();
	public SparseIntArray frameMap = new SparseIntArray ();
	public int hostTurn = 0;
	public int guestTurn = 0;
	public short currentUpdates = DEFAULT_FRAMES;


	//====================================================
	// CONSTRUCTOR
	//====================================================
	private NetworkManager(){
		reset();
	}

	//====================================================
	// GETTERS & SETTERS
	//====================================================
	// Retrieves a global instance of the NetworkManager
	public static NetworkManager getInstance(){
		return INSTANCE;
	}
	
	//====================================================
	// PUBLIC METHODS
	//====================================================

	public short getDepartmentID() {
		return ( connection == IS_HOST || connection == IS_SINGLE)? hostDepartmentID : guestDepartmentID;
	}

	public void reset() {

		if ( client != null) {
		    client.disconnect();
		    client = null;
		}
		layer = null;
		scene = null;
		gameStarted = false;
		connection = IS_UNDEFINED;
		mapID = -1;
		
		hostCmdMap.clear();
		guestCmdMap.clear();
		hostTurn = 0;
		guestTurn = 0;
		
	}
	public void setLayer( NetworkLayer layer ) {
		this.layer = layer;
	}
	public boolean isHost( ) {
		return connection == IS_HOST;
	}
	public boolean isGuest( ) {
		return connection == IS_GUEST;
	}
	public static Command obtainBuildCommand( short towerID, short x , short y) {
		return commandPool.obtainCommand( Command.CMD_BUILD, towerID, x, y);
	}
	public static Command obtainCommand() {
		final short i = -1;
		return commandPool.obtainCommand(i,i,i,i);
	}
	public static void recycleCommand( Command cmd ) {
		commandPool.recyclePoolItem(cmd);
	}
	
	
	public synchronized void receiveTurnDownMessage( int turn , Queue< Command > cmds) {
		if ( connection == IS_HOST ) {
			guestTurn = turn;
			guestCmdMap.put(turn+2,cmds);
			return;
		}
		if ( connection == IS_GUEST ) {
			hostTurn = turn;
			hostCmdMap.put(turn+2 , cmds);
			return;
		}
	}

	public synchronized void sendTurnDownMessage() {
		// TODO;
		if ( connection == IS_SINGLE ) {
			frameMap.put(hostTurn+1, currentUpdates);
			return;
		}
		int turn = ( connection == IS_HOST )? hostTurn : guestTurn;
		if ( connection == IS_HOST ) {
			int updates = currentUpdates;
			frameMap.put(turn+2, updates);
			// TODO host sending message
			client.sendUpdatesEvent(turn+2 , updates);
			client.sendTurnDoneEvent(turn);

		}
		else if ( connection == IS_GUEST ){
			client.sendTurnDoneEvent(turn);
		}
	}
	

	public synchronized void sendHelloMessage() {
		if ( connection == IS_SINGLE ) 
			return;
		client.sendHelloEvent();
	}
	public synchronized void receiveHelloMessange() {
		// TODO
		
		//TEST ONLY
		ResourceManager.clickSound.play();
	}
	public synchronized void sendGameStartMessage() {
		if( connection == IS_HOST ) {
			int updates = currentUpdates;
			client.sendGameStartEvent(updates);
			onGameStart(updates);
		}
		if ( connection == IS_SINGLE) {
			int updates = currentUpdates;
			onGameStart(updates);
		}
	}
	public synchronized void sendHostRejectMessage() {
		if ( connection != IS_HOST  )
			return;
		client.sendAcceptOrRejectEvent(false);
	}
	public synchronized void sendHostAcceptMessage() {
		if ( connection != IS_HOST  )
			return;
		client.sendAcceptOrRejectEvent(true);
	}
	public synchronized void sendLoadingMessage( short percentage ) {
		if ( connection == IS_SINGLE )
			return;
		client.sendLoadingEvent(percentage);
		
	}
	public synchronized void sendLocalCommand( Command cmd ) {
		if ( connection == IS_SINGLE ) {
			int turn = hostTurn;
			Queue<Command> queue = hostCmdMap.get(turn+1);
			if ( queue == null ) {
				queue = new LinkedList< Command >();
				hostCmdMap.put(turn+1, queue);
			}
			queue.add(cmd);
		}
		else if ( connection == IS_HOST ) {
			int turn = hostTurn;
			Queue<Command> queue = hostCmdMap.get(turn+2);
			if ( queue == null ) {
				queue = new LinkedList< Command >();
				hostCmdMap.put(turn+2, queue);
			}
			queue.add(cmd);
			client.sendCmdEvent(turn, cmd);
		}
		else if ( connection == IS_GUEST ) {
			int turn = guestTurn;
			Queue<Command> queue = guestCmdMap.get(turn+2);
			if ( queue == null ) {
				queue = new LinkedList< Command >();
				guestCmdMap.put(turn+2, queue);
			}
			queue.add(cmd);
			client.sendCmdEvent(turn, cmd);
		}
	}
	public synchronized void cmdBuild( short towerID, short x , short y ) {
		sendLocalCommand( obtainBuildCommand( towerID, x,y ) );
	}
	public synchronized void increaseTurn( ) {
		if ( connection == IS_HOST || connection == IS_SINGLE) 
			hostTurn++;
		else if ( connection == IS_GUEST )
			guestTurn++;
	}

	public synchronized boolean canNextTurn() {
		//TODO
		if ( connection == IS_SINGLE)
			return true;
		else {
			int turn = ( connection == IS_HOST )? hostTurn : guestTurn;
			int enemyTurn =  ( connection == IS_HOST )? guestTurn : hostTurn;
			return (turn - enemyTurn) <= 0;
		}
	}
	public synchronized Queue< Command > fetchHostCmd( ) {
		int turn = ( connection == IS_HOST || connection == IS_SINGLE )? hostTurn : guestTurn;
		Queue<Command> queue = hostCmdMap.get(turn);
		hostCmdMap.remove(turn);
		return queue;
	}
	public synchronized Queue< Command > fetchGuestCmd() {
		int turn = ( connection == IS_HOST || connection == IS_SINGLE )? hostTurn : guestTurn;
		Queue<Command> queue = guestCmdMap.get(turn);
		guestCmdMap.remove(turn);
		return queue;
	}
	public synchronized int fetchUpdates() {
		if ( connection == IS_SINGLE )
			return DEFAULT_FRAMES;
		int turn = ( connection == IS_HOST )? hostTurn : guestTurn;
		Integer updates = frameMap.get(turn);
		frameMap.removeAt(turn);
		if ( updates == null ) 
			return DEFAULT_FRAMES;
		else 
			return updates;

	}
	public boolean initHost( String n, short dID, short mID) {
		if ( connection != IS_UNDEFINED )
			return false;
		connection = IS_HOST;
		hostName = n;
		hostDepartmentID = dID;
		mapID = mID;
		client = new PhotonClient( hostName , hostDepartmentID);
		new Thread(client).start();
		return true;


	}
	public boolean initGuest( String n, short ID , String host) {
		if ( connection != IS_UNDEFINED )
			return false;
		connection = IS_GUEST;
		guestName = n;
		guestDepartmentID = ID;
		hostName = host;
		client = new PhotonClient( guestName , guestDepartmentID );
		new Thread(client).start();
		return true;
	}
	public boolean initSingle( ) {
		if ( connection != IS_UNDEFINED )
			return false;
		connection = IS_SINGLE;
		// TEST ONLY
		hostDepartmentID = 0;
		mapID = 0;
		hostName = "single";
		return true;
	}
	
	// Connection Handlers
	public synchronized void onConnectionReady() {
		ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ResourceManager.getInstance().context, "Connection Ready", Toast.LENGTH_SHORT).show();
			}
			
		});
		if ( connection == IS_HOST ) {
			client.hostGame(mapID);
			ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ResourceManager.getInstance().context, "Creating room: " + hostName + "...", Toast.LENGTH_SHORT).show();
				}
				
			});
		}
		else if ( connection == IS_GUEST ) {
			ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ResourceManager.getInstance().context, "Joining room:" + hostName + "...", Toast.LENGTH_SHORT).show();
				}
				
			});
			if ( !client.joinGame(hostName) )
				ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ResourceManager.getInstance().context, "Joining failed", Toast.LENGTH_SHORT).show();
					}
					
				});
		}
	}

	public void onHelloReceived() {
		// TODO Auto-generated method stub
		receiveHelloMessange();
	}
	
	public void onGameAccept() {
		SceneManager.getInstance().hideLayer();
		SceneManager.getInstance().showGameScene();
	}
	public void onGameReject() {
		layer.onGameReject();
		client.opLeaveRoom();
		reset();
	}
	public void onDisconnect() {
		NetworkManager.getInstance().reset();
		SceneManager.getInstance().hideLayer();
		SceneManager.getInstance().showTitleScene();
		ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ResourceManager.getInstance().context, "Connection Failure", Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	public void onLoadingProgress( int progress) {
		// TODO
	}
	public void setGameScene( GameScene g ) {
		scene = g;
	}
	public void onGameStart( int updates ) {
		frameMap.put(0, updates);
		frameMap.put(1, updates);
		gameStarted = true;
		scene.onGameStart();
	}
	
	public void onTurnDone( int turn ) {
		if ( connection == IS_HOST ) {
			guestTurn = turn + 1;
		}
		if ( connection == IS_GUEST ) {
			hostTurn = turn + 1 ;
		}
	}
	public void onFrameNotice( int turn, int updates ) {
		if ( connection == IS_GUEST ) {
			frameMap.put(turn+2, updates);
		}
	}
	public void onReceiveCmd( int turn , String cmd ) {
		if ( cmd == null ) 
			return;
		Command c = obtainCommand();
		if ( !c.setByString(cmd) )
			return;
		if ( connection == IS_GUEST ) {
			Queue<Command> queue = hostCmdMap.get(turn+2);
			if ( queue == null ) {
				queue = new LinkedList< Command >();
				hostCmdMap.put(turn+2, queue);
			}
			queue.add(c);
		}
		else if ( connection == IS_HOST ) {
			Queue<Command> queue = guestCmdMap.get(turn+2);
			if ( queue == null ) {
				queue = new LinkedList< Command >();
				guestCmdMap.put(turn+2, queue);
			}
			queue.add(c);
		}
	}
	
	public void onPlayerJoined() {
		if ( connection == IS_HOST ) {
			if ( client.getCurrentRoom().getPlayersCount() == 1) {// self
				ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ResourceManager.getInstance().context, "Game Created", Toast.LENGTH_SHORT).show();
					}
					
				});
				return ;
			}
			else {
				TypedHashMap<Integer , Player> players = client.getCurrentRoom().getPlayers();
				for (Entry<Integer, Player> player : players.entrySet())
				{
					if  ( player.getValue().getName() != hostName ) {
						guestName = player.getValue().getName();
						guestDepartmentID = Short.parseShort( player.getValue()
				                                                    .getAllProperties()
				                                                    .get(PhotonClient.KEY_DEPARTMENT)
				                                                    .toString() );
						break;
					}
				}
				
				layer.onGuestJoined();
			}
		}
		else if ( connection == IS_GUEST ) {
			// Must be self
			ResourceManager.getInstance().context.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ResourceManager.getInstance().context, "Game Joined", Toast.LENGTH_SHORT).show();
				}
				
			});
			TypedHashMap<Integer , Player> players = client.getCurrentRoom().getPlayers();
			for (Entry<Integer, Player> player : players.entrySet())
			{
				if  ( player.getValue().getName() != guestName ) {
					hostDepartmentID = Short.parseShort( player.getValue()
			                                                    .getAllProperties()
			                                                    .get(PhotonClient.KEY_DEPARTMENT)
			                                                    .toString() );
					break;
				}
			}	
		}
	}
	public void onPlayerLeft() {
		if ( gameStarted ) {
			client.opLeaveRoom();
			client.disconnect();
			SceneManager.getInstance().showTitleScene();
			return;
		}
		if ( connection == IS_GUEST ) {
			onGameReject();
			return;
		}
	}
	//====================================================
	// PRIVATE METHODS
	//====================================================
	// Loads resources used by both the game scenes and menu scenes

	
	// ============================  ================= //

}
