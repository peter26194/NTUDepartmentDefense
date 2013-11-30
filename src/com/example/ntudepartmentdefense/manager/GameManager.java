package com.example.ntudepartmentdefense.manager;


import org.andengine.entity.text.Text;

import com.example.ntudepartmentdefense.gamesync.Castle;

public class GameManager {

	private static GameManager INSTANCE;
	
	public static final int INITIAL_HP = 10;
	public static final int INITIAL_MONEY = 50;
	
	private Castle myCastle;
	private Castle opponentCastle;
	private int mHP;
	private int mOpponentHP;
	
	private Text moneyDisplay;
	private int mMoney;

	GameManager(){
	}
	
	public static GameManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new GameManager();
		}
		return INSTANCE;
	}
	//getters and setters
	public void setMyCastle(Castle castle){
		myCastle = castle;
	}
	public void setOpponentCastle(Castle castle){
		opponentCastle = castle;
	}
	public int getHP() {
		return this.mHP;
	}
	public void minusHP(int minused) {
		this.mHP -= minused;
		myCastle.updateHPbar(mHP);
	}
	public int getOpponentHP() {
		return this.mOpponentHP;
	}
	public void minusOpponentHP(int minused) {
		this.mOpponentHP -= minused;
		opponentCastle.updateHPbar(mOpponentHP);
	}
	public void setMoneyText(Text text){
		moneyDisplay = text;
	}
	public int getMoney() {
		return this.mMoney;
	}
	public void addMoney(int gainedMoney) {
		this.mMoney += gainedMoney;
		moneyDisplayUpdate();
	}
	public void minusMoney(int minusedMoney) {
		this.mMoney -= minusedMoney;
		moneyDisplayUpdate();
	}
	
	//private
	private void moneyDisplayUpdate() {
		moneyDisplay.setText("Money : "+ Integer.toString(this.mMoney));
	}
	// Resetting the game
	public void resetGame(){
		this.mHP = GameManager.INITIAL_HP;
		this.mOpponentHP = GameManager.INITIAL_HP;
		this.mMoney = GameManager.INITIAL_MONEY;
	}
}