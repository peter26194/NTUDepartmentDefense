package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.Entity;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;


public class Notebook extends Entity{
	// The objects that will make up our Notebook
	private Tab[] tabs;
	private Tab buildTab;
	private Tab skillTab;
	private Tab techtreeTab;
		private static final int BUILD_TAB = 0;
		private static final int SKILL_TAB = 1;
		private static final int TECHTREE_TAB = 2;
	
	Notebook(final float x, final float y, float width){
		super(x, y);
		
		int myDepartment = NetworkManager.getInstance().getDepartmentID();
		tabs = new Tab[3];
		tabs[BUILD_TAB] = new Tab(1, width, DataManager.getInstance().towerParam[myDepartment], this);
		tabs[SKILL_TAB] = new Tab(2, width, DataManager.getInstance().towerParam[myDepartment+1]/*TODO*/, this);
		tabs[TECHTREE_TAB] = new Tab(3, width, DataManager.getInstance().towerParam[myDepartment]/*TODO*/, this);
		
		for(int i=0; i<3; i++) 
			this.attachChild(tabs[i]);
			
	}
	public void display(int index){
		for(int i=0; i<3; i++)
			tabs[i].setVisible(false);
		
		getChildByTag(index).setVisible(true);
	}
	public Tab getTab(int i){
		 return tabs[i];
	}
}
