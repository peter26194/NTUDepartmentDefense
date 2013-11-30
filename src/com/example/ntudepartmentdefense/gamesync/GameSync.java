package com.example.ntudepartmentdefense.gamesync;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


import org.andengine.entity.Entity;

import com.example.ntudepartmentdefense.layer.GridLayer;
import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Command;



public class GameSync extends Entity{
	
	// ============== Constants ============== 

	private static final short DIRECTION_UP    =  0;
	private static final short DIRECTION_DOWN  =  1;
	private static final short DIRECTION_RIGHT =  2;
	private static final short DIRECTION_LEFT  =  3;
	private static final short DIRECTION_GOAL  =  4;
	private static final short DIRECTION_BLOCK =  5;
	
	private static final short TOWER_LAYER  = 0;
	private static final short BULLET_LAYER = 1;
	private static final short MOB_LAYER    = 2;
	
	private static final float edgeLength = ResourceManager.getInstance().edgeLength;
	private static final short edgeUnitCount= ResourceManager.getInstance().edgeUnitCount;
	private static final float edgeUnit= ResourceManager.getInstance().edgeUnit;
	
	private static final int MAX_MOB_ID = 200;
	// ============== Variables ================
	
	// Synchronization Lock
	private final Integer syncLock = 1;
	
	// Layer Management
	public GridLayer gridLayer = new GridLayer( this );
	public Entity[] hostTuple = new Entity[3];
	public Entity[] guestTuple = new Entity[3];

	
	// Grid Management
	public short[][] serverDirectionMap;
	public short[][] clientDirectionMap;
	private Queue< short[] > bfsQueue;
	public boolean[][] visited;
	public static short[][] spare = new short[ edgeUnitCount][];
	
	// Tower Management
	public short[] serverBase = new short[2];
	public short[] clientBase = new short[2];
	private Tower[][] towerMap ;
	
	// Mob Management
	public short[][] isMobOn;
	private Queue<Integer> mobIDPool = new LinkedList<Integer>();
	private Set<Integer> mobIDs   = new HashSet<Integer>();
	private static Mob[] mobList = new Mob[ MAX_MOB_ID ];

	// Turn Management
	private int updatesLeft = 0;//how many updates left in current turn
	
	
	// ============== Constructor ================
	
	
	public GameSync() {
		super(0, 0);
		this.setChildrenIgnoreUpdate(false);
		
		hostTuple[TOWER_LAYER]  = new TowerLayer( this  );
		hostTuple[BULLET_LAYER] = new BulletLayer( this );
		hostTuple[MOB_LAYER]    = new MobLayer( this , true );
		guestTuple[TOWER_LAYER] = new TowerLayer(this);
		guestTuple[BULLET_LAYER]= new BulletLayer(this);
		guestTuple[MOB_LAYER]   = new MobLayer(this, false);
		
		for ( Entity layer : hostTuple )
			this.attachChild(layer);
		for ( Entity layer : guestTuple )
			this.attachChild(layer);

		this.towerMap = new Tower[ edgeUnitCount][edgeUnitCount];
		this.serverDirectionMap = new short[ edgeUnitCount][edgeUnitCount];
		this.clientDirectionMap = new short[ edgeUnitCount][edgeUnitCount];
		this.isMobOn = new short[ edgeUnitCount][edgeUnitCount];
		for ( int i = 0 ; i < edgeUnitCount ; i++ )
			for ( int j = 0 ; j < edgeUnitCount ; j++)
				isMobOn[i][j] = 0;
		this.visited = new boolean[ edgeUnitCount][edgeUnitCount];
		for ( int i = 0 ; i < edgeUnitCount ; i++ )
			for ( int j = 0 ; j < edgeUnitCount ; j++)
				visited[i][j] = false;
		bfsQueue = new LinkedList< short[]> ();
		for ( int i = 0 ; i < MAX_MOB_ID ; i++ ) {
			mobIDPool.add( i );
		}
		//TODO put castle-building cmd into queue and return castle to GameManager
		//by calling GameManager.getInstance().setMyCastle(Castle);
		//and  GameManager.getInstance().setOpponentCastle(Castle);
		setServerBase( NetworkManager.mapID );
		setClientBase( NetworkManager.mapID );
	}

	// ============== Public Methods ================
	
	// Castle Set-up
	public void setServerBase( short mapID ) {
		short x = 4;//GameManager.getMapHostX( mapID );
		short y = 4;//GameManager.getMapHostY( mapID );
		synchronized ( syncLock ) {
			serverBase[0] = x;
			serverBase[1] = y;
			isMobOn[x][y] ++;
			updateDirectionMap( serverBase , clientDirectionMap);
		}
	}
	public void setClientBase(  short mapID ) {
		short x = 12;//GameManager.getMapGuestX( mapID );
		short y = 12;//GameManager.getMapGuestY( mapID );
		synchronized ( syncLock ) {
			clientBase[0] = x;
			clientBase[1] = y;
			isMobOn[x][y] ++;
			updateDirectionMap( clientBase , serverDirectionMap);
		}
	}

	
	// Tower Set-up
	public boolean build( Tower tower, boolean isHost ) {
		synchronized ( syncLock ) {
			short[] x = tower.getXs();
			short[] y = tower.getYs();
			int count = x.length;
			if (blocksOrUnblocks(  x, y , true ) ) {
				for ( int i = 0 ; i < count ; i++)
					towerMap[ x[i]][ y[i]] = tower;
				if (isHost)
					this.hostTuple[TOWER_LAYER].attachChild(tower);
				else
					this.guestTuple[TOWER_LAYER].attachChild(tower);
				tower.setVisible(true);
				tower.setIgnoreUpdate(false);
				return true;
			}else return false;
		}

	}
	public boolean startRemove( Tower tower ) {
		synchronized ( syncLock ) {
			tower.remove();
			return true;
		}

	}
	public boolean endRemove( Tower tower ) {
		synchronized ( syncLock ) {
			short[] x = tower.getXs();
			short[] y = tower.getYs();
			int count = x.length;
			if (blocksOrUnblocks(  x, y , false ) ) {
				for ( int i = 0 ; i < count ; i++)
					towerMap[ x[i]][ y[i]] = null;
			}
			return true;
		}

	}
	public boolean canBuild( short x , short y){
		synchronized ( syncLock ) {
			if ( isMobOn[ x][ y] >= 1 ) 
				return false;
				if ( serverDirectionMap[ x][ y] == DIRECTION_BLOCK )
					return false;
			}
			return true;
	}
	public boolean canBuild( short[] x , short[] y){
		// TODO
		synchronized ( syncLock ) {
			int count = x.length;
			for ( int i = 0 ; i < count ; i++) {
				if ( isMobOn[ x[i]][ y[i]] >= 1) 
					return false;
				if ( serverDirectionMap[ x[i] ] [y[i]] == DIRECTION_BLOCK )
					return false;
			}
			return true;
		}
	}

	// Mob Set-up
	public short getDirection( short x, short y , boolean isServer) {
		// TODO
		synchronized ( syncLock ) {
			if ( isServer )
				return serverDirectionMap[x][y];
			else 
				return clientDirectionMap[x][y];
		}

	}

	public void mobLock( short x, short y) {
		// TODO
		synchronized ( syncLock ) {
			isMobOn[x][y] ++;
		}

	}
	public void mobUnlock( short x , short y) {
		synchronized ( syncLock ) {
			isMobOn[x][y] --;
		}
	}
	public Mob createMob( boolean isServer , int typeID) {
		// TODO
		short[] base = ( isServer ) ? serverBase : clientBase;
		synchronized ( syncLock ) {
			int mobID = mobIDPool.remove();
			mobIDs.add(mobID);
			MobLayer mlayer = (isServer) ? (MobLayer) hostTuple [ MOB_LAYER ]
										 : (MobLayer) guestTuple[ MOB_LAYER ];
			mobList[mobID] = new Mob( mobID , base[0] , base[1] , typeID , isServer , mlayer );
			return mobList[mobID];
		}

	}
	public boolean killMob( Mob mob ) {
		// TODO
		synchronized ( syncLock ) {
			int mobID = mob.getID();
			mobList[mobID] = null;
			mobIDs.remove(mobID);
			mobIDPool.add(mobID);
			return true;
		}

	}
	public boolean arriveMob( Mob mob ) {
		synchronized ( syncLock ) {
			//TODO
			return true;
		}

	}

	// Focus

	public Mob getTowerTarget( Tower tower) {
		synchronized ( syncLock ) {
			Mob mob = null;
			for ( Integer i : mobIDs ) {
				if ( tower.canAttack( mobList[i] ) ) {
					mob = mobList[i];
					break;
				}
			}
			return mob; // no target
		}
	}



	

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		//TODO renew turn and opnTurn

		if ( updatesLeft == 0 ) {
			if ( !isWaitingTurn() ) 
				this.onTurnDone();
			if ( canNextTurn() ) 
				onNextTurn();
			else 
				onWaitTurn();
		}
		if ( updatesLeft > 0 ) 
			updatesLeft--;
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	
	private boolean updateDirectionMap( short[] base , short[][] map) {
	
		for ( int i = 0 ; i < edgeUnitCount ; i++ )
			for ( int j = 0 ; j < edgeUnitCount ; j++)
				visited[i][j] = false;
		bfsQueue.clear();
		visited[ base[0] ][ base[1] ] = true;
		map[ base[0]][base[1]] = DIRECTION_GOAL;
		bfsQueue.add( new short[]{base[0],base[1]});
		while( !bfsQueue.isEmpty() ) {
			short[] node = bfsQueue.remove();
			short[] next = new short[] {  node[0] , node[1] };
			next[0]--;
			if ( checkValid( next , map) ) {
				visited[ next[0]][next[1]] = true;
				map[ next[0] ][ next[1] ] = DIRECTION_RIGHT;
				bfsQueue.add( new short[] { next[0] , next[1]} );
			}
			next[0] += 2;
			if ( checkValid( next , map) ) {
				visited[ next[0]][next[1]] = true;
				map[ next[0] ][ next[1] ] = DIRECTION_LEFT;
				bfsQueue.add( new short[] { next[0] , next[1]} );
			}
			next[0]--;
			next[1]--;
			if ( checkValid( next , map) ) {
				visited[ next[0]][next[1]] = true;
				map[ next[0] ][ next[1] ] = DIRECTION_DOWN;
				bfsQueue.add( new short[] { next[0] , next[1]} );
			}	
			next[1] += 2;
			if ( checkValid( next , map) ) {
				visited[ next[0]][next[1]] = true;
				map[ next[0] ][ next[1] ] = DIRECTION_UP;
				bfsQueue.add( new short[] { next[0] , next[1]} );
			}	
		}
		for ( int i = 0 ; i < edgeUnitCount ; i++ )
			for ( int j = 0 ; j < edgeUnitCount ; j++) {
				if ( isMobOn[i][j] >= 1 && !visited[i][j]) {
					return false ;
				}
			}
		return true;
	}

	private boolean checkValid( short[] xy , short[][] map) {
		if ( xy[0] < 0 || xy[1] < 0 )
			return false;
		if ( xy[0] >= edgeUnitCount || xy[1] >= edgeUnitCount ) 
			return false;
		if ( visited[ xy[0] ][xy[1] ] )
			return false;
		if ( map[ xy[0]][ xy[1] ] == GameSync.DIRECTION_BLOCK ) 
			return false;
		return true;
	}

	private boolean blocksOrUnblocks( short[] x , short[] y , boolean block){
		int count = x.length;
		if ( block ) {
			for ( int i = 0 ; i < count ; i++) {
				if ( isMobOn[ x[i] ][ y[i] ] >= 1 )
					return false;
			}
			for ( int i= 0 ; i < edgeUnitCount ; i++ )
				spare[i] = serverDirectionMap[i].clone();
			
			for ( int i = 0 ; i < count ; i++) 
				serverDirectionMap[ x[i]][y[i]] = DIRECTION_BLOCK;
			if ( !updateDirectionMap( clientBase , serverDirectionMap) ) {
				for ( int i= 0 ; i < edgeUnitCount ; i++ )
					serverDirectionMap[i] = spare[i].clone();
				return false;
			}
			for ( int i= 0 ; i < edgeUnitCount ; i++ )
				spare[i] = clientDirectionMap[i].clone();
			
			for ( int i = 0 ; i < count ; i++) 
				clientDirectionMap[ x[i]][y[i]] = DIRECTION_BLOCK;
			if ( !updateDirectionMap( serverBase , clientDirectionMap) ) {
				for ( int i= 0 ; i < edgeUnitCount ; i++ )
					clientDirectionMap[i] = spare[i].clone();
				return false;
			}

		}
		else {
			for ( int i = 0 ; i < count ; i++)  {
				serverDirectionMap[ x[i]][y[i]] = DIRECTION_LEFT;
				clientDirectionMap[ x[i]][y[i]] = DIRECTION_LEFT;
			}
			updateDirectionMap( clientBase , serverDirectionMap) ;
			updateDirectionMap( serverBase , clientDirectionMap) ;

		}
		return true;

	}

	
	
	// ============== Private Methods ================

	private boolean isWaitingTurn() {
		// TODO Auto-generated method stub
		return this.isChildrenIgnoreUpdate();
	}

	private void onWaitTurn() {
		// TODO Auto-generated method stub
		this.setChildrenIgnoreUpdate(true);
		
	}

	private void onNextTurn() {
		// TODO Auto-generated method stub
		NetworkManager.getInstance().increaseTurn();
		processCmd( NetworkManager.getInstance().fetchHostCmd() , true);
		processCmd( NetworkManager.getInstance().fetchGuestCmd() , false );
		this.setChildrenIgnoreUpdate(false);
		updatesLeft = NetworkManager.getInstance().fetchUpdates(); // fetchNewUpdates;
		
	}

	private short getClientDepartment() {
		// TODO Auto-generated method stub
		return NetworkManager.guestDepartmentID;
	}

	private short getServerDepartment() {
		// TODO Auto-generated method stub
		return NetworkManager.hostDepartmentID;
	}

	private void processCmd( Queue<Command> queue, boolean isServer) {
		// TODO Auto-generated method stub
		if ( queue == null )
			return;
		short departmentID = ( isServer) ?getServerDepartment() : getClientDepartment();
		while( !queue.isEmpty() ) {
			Command cmd = queue.remove();
			short type = cmd.getType();
			switch( type ) {
			case Command.CMD_BUILD:
				TowerLayer tlayer = (isServer) ? (TowerLayer) hostTuple [ TOWER_LAYER ]
											  : (TowerLayer) guestTuple[ TOWER_LAYER ];
				BulletLayer blayer= (isServer) ? (BulletLayer) hostTuple [ BULLET_LAYER ]
											   : (BulletLayer) guestTuple[ BULLET_LAYER ];
				build( new Tower( cmd.getX() , cmd.getY()
						         ,cmd.getID(), departmentID
						         ,isServer
						         ,tlayer, blayer) 
				      ,isServer);
				break;
			default:
				break;
			}

			NetworkManager.recycleCommand(cmd);
		}
	}

	private boolean canNextTurn() {
		// TODO Auto-generated method stub
		return NetworkManager.getInstance().canNextTurn();
	}

	private void onTurnDone() {
		// TODO Auto-generated method stub
		NetworkManager.getInstance().sendTurnDownMessage();

	}

}
