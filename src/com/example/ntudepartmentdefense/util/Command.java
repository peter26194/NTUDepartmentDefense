package com.example.ntudepartmentdefense.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Command {
	public static final short CMD_BUILD       = 1;//
	public static final short CMD_REMOVE      = 2;//
	public static final short CMD_LEVEL_UP    = 3;//
	public static final short CMD_SKILL       = 4;//
	public static final short CMD_UNLOCK_TREE = 5;//
	public static final short CMD_FOCUS       = 6;//
	private short mType = -1;
	private short mID = -1;
	private short mX = -1 ;
	private short mY = -1 ;
	private String mAddons; 
	public Command(final short pType, 
    		final short pID,
    		final short pX,
    		final short pY) {
		// TODO Auto-generated constructor stub
		mType = pType;
		mID = pID;
		mX = pX;
		mY = pY;
		mAddons = null;
	}
	public Command() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		String ret = new String("");
		ret += String.format("%d %d %d %d" , mType, mID , mX , mY);
		if ( mAddons != null )
			ret += " " + mAddons;
		return ret;
	}
	public boolean setByString( String str ) {
		if ( str == null )
			return false;
		String[] strs = str.split(" " , 5);
		if ( strs.length < 4 )
			return false;
		mType = Short.parseShort(strs[0]);
		mID   = Short.parseShort(strs[1]);
		mX    = Short.parseShort(strs[2]);
		mY    = Short.parseShort(strs[3]);
		if ( strs.length == 5 ) {
			mAddons = strs[4];
		}
		return true;
	}
	public void set(final short pType, 
            		final short pID,
            		final short pX,
            		final short pY) {
		mType = pType;
		mID = pID;
		mX = pX;
		mY = pY;
		mAddons = null;

	}
	public void setAddons( final String pA ) {
		mAddons = pA;
	}
	public short getType() {
		// TODO Auto-generated method stub
		return mType;
	}
	public short getX() {
		// TODO Auto-generated method stub
		return mX;
	}
	public short getY() {
		// TODO Auto-generated method stub
		return mY;
	}
	public short getID() {
		// TODO Auto-generated method stub
		return mID;
	}
}
