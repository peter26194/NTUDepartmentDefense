package com.example.ntudepartmentdefense.gamesync;

import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Gauge;



public class Castle extends Tower{
	private Gauge hpBar;
	
	//public
	public Castle(short gridX, short gridY, int towerID, int departmentID,
			boolean isServer, TowerLayer towerLayer) {
		super(gridX, gridY, towerID, departmentID, isServer, towerLayer, null);
		// We assume castle won't shoot bullet
		String color = ( isServer)? NetworkManager.SERVER_COLOR : NetworkManager.CLIENT_COLOR;
		hpBar = new Gauge((int)edgeUnit, (int)edgeUnit / 10, color, "#000000", 1f);
		ResourceManager.getInstance().moveCenter(0, this.getHeight(), hpBar);
		this.attachChild(hpBar);
	}
	
	//private
	public void updateHPbar(int curHP){
		hpBar.setRatio(curHP / GameManager.INITIAL_HP);
	}
	
}
