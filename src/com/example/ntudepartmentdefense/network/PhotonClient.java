package com.example.ntudepartmentdefense.network;

import java.util.HashMap;
import java.util.Random;

import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.util.Command;



import android.util.Log;
import de.exitgames.api.loadbalancing.ClientState;
import de.exitgames.api.loadbalancing.ErrorCode;
import de.exitgames.api.loadbalancing.EventCode;
import de.exitgames.api.loadbalancing.LoadBalancingClient;
import de.exitgames.api.loadbalancing.OperationCode;
import de.exitgames.client.photon.EventData;
import de.exitgames.client.photon.OperationResponse;
import de.exitgames.client.photon.StatusCode;
import de.exitgames.client.photon.enums.DebugLevel;


public class PhotonClient extends LoadBalancingClient implements Runnable{

	public static final byte PHOTON_MSG_HELLO     = 1;
	public static final byte PHOTON_MSG_ACCEPT    = 2;
	public static final byte PHOTON_MSG_REJECT    = 3;
	public static final byte PHOTON_MSG_LOADING   = 4;
	public static final byte PHOTON_MSG_START     = 5;
	public static final byte PHOTON_MSG_TURN_DONE = 6;
	public static final byte PHOTON_MSG_FRAME     = 7;
	public static final byte PHOTON_CMD           = 8;
	
	public static final byte KEY_PROGRESS = 0;
	public static final byte KEY_UPDATES  = 1;
	public static final byte KEY_TURN     = 2;
	public static final byte KEY_CMD      = 3;
	public static final String KEY_MAP    = "map";
	public static final String KEY_DEPARTMENT = "department";
	public static final byte CONTROL_CHANNEL = 1;
	public static final byte CMD_CHANNEL = 2;
	
	private static final String APPID =  "fe7b6e95-1911-4bb1-acc7-97626e77c9ea";
	private static final int SLEEP_TIME = 10;
	private final String name;
	private final short departmentID;
	private boolean toTerminate = false;
	public PhotonClient( String n , short id ) {
		super();
		name = n;
		departmentID = id;
	}
	
	@Override
	public void run()
    {
		this.setMasterServerAddress("app-asia.exitgamescloud.com:5055");
		this.connectToMaster(APPID, "1.0", name);
		this.getPlayer().m_customProperties.put(KEY_DEPARTMENT, departmentID);
		while (!toTerminate)
		{
			this.loadBalancingPeer.service();
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e)     {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	


    public boolean joinGame(String name)
    {
        // you don't have to wrap OpJoinRoom like we do here! We just wanted all OP calls in this class...
        if (this.opJoinRoom(name+"_room", this.getPlayer().m_customProperties))
        {
        	// TODO
        	return true;
        }
        
        return false;
    }

    /**
     * Called by our demo form to create a new room (and set a few properties for it).
     * @param name Name of the room
     */
    public boolean hostGame( short mapID )
    {
        // make up some custom properties (key is a string for those)
        HashMap<Object, Object> mapProperties = new HashMap<Object, Object>();
        mapProperties.put(KEY_MAP, mapID);
        // tells the master to create the room and pass on our locally set properties of "this" player
        return this.opCreateRoom(name+"_room", true, true, (byte)2, mapProperties, new String[] { KEY_MAP });
    }

 
    public boolean sendHelloEvent()
    {
        return this.loadBalancingPeer.opRaiseEvent(PHOTON_MSG_HELLO, null, false, CONTROL_CHANNEL);       // this is received by OnEvent()
    }
    public boolean sendAcceptOrRejectEvent( boolean accept ) {
    	byte code = ( accept) ?PHOTON_MSG_ACCEPT : PHOTON_MSG_REJECT;
    	return this.loadBalancingPeer.opRaiseEvent(code, null, false, CONTROL_CHANNEL);
    }
    public boolean sendLoadingEvent( int progress ) {
    	 HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
         eventContent.put(KEY_PROGRESS, Integer.valueOf(progress).toString() );
         return this.loadBalancingPeer.opRaiseEvent(PHOTON_MSG_LOADING, eventContent, false, CONTROL_CHANNEL);
    } 
    public boolean sendGameStartEvent( int updates ) {
    	HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
    	eventContent.put(KEY_UPDATES, Integer.valueOf(updates).toString() );
    	return this.loadBalancingPeer.opRaiseEvent(PHOTON_MSG_START, eventContent, false, CONTROL_CHANNEL);
    }
    public boolean sendTurnDoneEvent( int turn ) {
    	HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
    	eventContent.put(KEY_TURN, Integer.valueOf(turn).toString() );
    	return this.loadBalancingPeer.opRaiseEvent(PHOTON_MSG_TURN_DONE, eventContent, true, CMD_CHANNEL);
    }
    public boolean sendUpdatesEvent( int turn , int updates ) {
    	HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
    	eventContent.put(KEY_TURN, Integer.valueOf(turn).toString() );
    	eventContent.put(KEY_UPDATES, Integer.valueOf(updates).toString() );
    	return this.loadBalancingPeer.opRaiseEvent(PHOTON_MSG_FRAME, eventContent, true, CMD_CHANNEL);
    }
    public boolean sendCmdEvent( int turn, Command cmd) {
    	HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
    	eventContent.put(KEY_CMD, cmd.toString() );
    	return this.loadBalancingPeer.opRaiseEvent(PHOTON_CMD, eventContent, true, CMD_CHANNEL);
    }



	/**
	 * Debug output of low level api (and this client).
	 */
	@Override
	public void debugReturn(DebugLevel level, String message)
	{
		switch(level)
		{
			case OFF:
				Log.println(Log.ASSERT, "CLIENT", message);
				break;
			case ERROR:
				Log.e("CLIENT", message);
				break;
			case WARNING:
				Log.w("CLIENT", message) ;
				break;
			case INFO:
				Log.i("CLIENT", message);
				break;
			case ALL:
				Log.d("CLIENT", message);
				break;
			default:
				Log.e("CLIENT", message);
				break;
		}
	}

    /**
     * Uses the connection's statusCodes to advance the internal state and call ops as needed.
     * In this client, we also update the form, cause new data might be available to display.
     * @param statusCode
     */
    @Override
    public void onStatusChanged(StatusCode statusCode)
    {
        super.onStatusChanged(statusCode);
        switch (statusCode)
        {
            case Connect:
                break;
            case Disconnect:
            	if (getState() == ClientState.Disconnecting){
                	this.toTerminate = true;
                	NetworkManager.getInstance().onDisconnect();
            	}
            	break;
            case TimeoutDisconnect:
            case DisconnectByServer:
            case DisconnectByServerLogic:
            	break;
            default:
                break;
        }
    }

    /**
     * Uses the photonEvent's provided by the server to advance the internal state and call ops as needed.
     * In this demo client, we check for a particular event (1) and count these. After that, we update the view / gui
     * @param eventData
     */
    @Override
    public void onEvent(EventData eventData)
    {
        super.onEvent(eventData);
        int turn, updates;
        String cmd;
        switch (eventData.Code)
        {
            case PHOTON_MSG_HELLO:
            	NetworkManager.getInstance().onHelloReceived();
                break;
            case PHOTON_MSG_ACCEPT:
            	NetworkManager.getInstance().onGameAccept();
            	break;
            case PHOTON_MSG_REJECT:
            	NetworkManager.getInstance().onGameReject();
            	break;
            case PHOTON_MSG_LOADING:
            	int progress = Integer.parseInt( eventData.Parameters.get(KEY_PROGRESS).toString() );
            	NetworkManager.getInstance().onLoadingProgress( progress );
            	break;
            case PHOTON_MSG_START:
            	updates = Integer.parseInt( eventData.Parameters.get(KEY_UPDATES).toString() );
            	NetworkManager.getInstance().onGameStart( updates );
            	break;
            case PHOTON_MSG_TURN_DONE:
            	turn = Integer.parseInt( eventData.Parameters.get(KEY_TURN).toString() );
            	NetworkManager.getInstance().onTurnDone( turn );
            case PHOTON_MSG_FRAME:
            	turn = Integer.parseInt( eventData.Parameters.get(KEY_TURN).toString() );
            	updates = Integer.parseInt( eventData.Parameters.get(KEY_UPDATES).toString() );
            	NetworkManager.getInstance().onFrameNotice( turn , updates );
            	break;
            case PHOTON_CMD:
            	turn = Integer.parseInt( eventData.Parameters.get(KEY_TURN).toString() );
            	cmd = eventData.Parameters.get(KEY_CMD).toString();
            	NetworkManager.getInstance().onReceiveCmd( turn , cmd );
            	break;
            case EventCode.GameList:
            case EventCode.GameListUpdate:
            case EventCode.PropertiesChanged:
                break;
            case EventCode.Join:
            	NetworkManager.getInstance().onPlayerJoined();
                break;
            case EventCode.Leave:
            	NetworkManager.getInstance().onPlayerLeft();
                break;
        }

        // update the form / gui

    }
    @Override
    public void onOperationResponse(OperationResponse operationResponse)
    {

    	super.onOperationResponse(operationResponse);
    	switch (operationResponse.OperationCode)
    	{
    	case OperationCode.JoinLobby:
    		NetworkManager.getInstance().onConnectionReady();
    		this.loadBalancingPeer.setTrafficStatsEnabled(true);
    		break;
    	case OperationCode.JoinGame:
    		switch(operationResponse.ReturnCode ) {
    		case ErrorCode.GameDoesNotExist:
    		case ErrorCode.GameClosed:
    		case ErrorCode.GameFull:
    		case ErrorCode.GameIdAlreadyExists:
    			NetworkManager.getInstance().onGameReject();
    			break;
    		default:
    			break;
    		}
    	default:
    		break;
    	}
    }

    public int getRTTinMS() {
    	return this.loadBalancingPeer.getRoundTripTime();
   }
}
