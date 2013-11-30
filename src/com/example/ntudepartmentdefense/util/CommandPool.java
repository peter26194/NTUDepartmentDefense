package com.example.ntudepartmentdefense.util;

import org.andengine.util.adt.pool.GenericPool;

public class CommandPool extends GenericPool<Command> {
 
	
	
	public CommandPool(){

	}
	

	@Override
	protected Command onAllocatePoolItem() {
		final short i = (short) -1;
		return new Command (i,i,i,i);
	}


	public synchronized Command obtainCommand(final short pType, 
			                                  final short pID,
			                                  final short pX,
			                                  final short pY) {
		Command cmd = super.obtainPoolItem();
		cmd.set(pType, pID, pX, pY);
		return cmd;
	}
	

	@Override
	protected void onHandleRecycleItem(Command pItem) {
		super.onHandleRecycleItem(pItem);
		final short i = (short) -1;
		pItem.set(i,i,i,i);
	}
}
