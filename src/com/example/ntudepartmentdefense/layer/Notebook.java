package com.example.ntudepartmentdefense.layer;


import org.andengine.entity.Entity;

import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;


public class Notebook extends Entity{
	private GameWindow gameWindow;
	
	// The objects that will make up our Notebook
	private Tab[] tabs;
	private Tab buildTab;
	private Tab skillTab;
	private Tab techtreeTab;
		private static final int BUILD_TAB = 0;
		private static final int SKILL_TAB = 1;
		private static final int TECHTREE_TAB = 2;
	
	Notebook(final float x, final float y, float width, GameWindow gameWindow){
		super(x, y);
		this.gameWindow = gameWindow;
		
		int myDepartment = NetworkManager.getInstance().getDepartmentID();
		tabs = new Tab[3];
		tabs[BUILD_TAB] = new BuildTab(0, width, DataManager.getInstance().towerParam[myDepartment], gameWindow);
		tabs[SKILL_TAB] = new SkillTab(1, width, DataManager.getInstance().towerParam[myDepartment]/*TODO*/, gameWindow);
		tabs[TECHTREE_TAB] = new TechtreeTab(2, width, DataManager.getInstance().towerParam[myDepartment]/*TODO*/, gameWindow);
		
		for(int i=0; i<3; i++) 
			this.attachChild(tabs[i]);
			
	}
	public void display(int index){
		for(int i=0; i<3; i++)
			tabs[i].getPageContent().setVisible(false);
		
		tabs[index].getPageContent().setVisible(true);
	}
	public Tab getTab(int i){
		 return tabs[i];
	}
}
