package com.example.ntudepartmentdefense.layer;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.example.ntudepartmentdefense.gamesync.GameSprite;
import com.example.ntudepartmentdefense.gamesync.Mob;
import com.example.ntudepartmentdefense.gamesync.Tower;
import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.GameManager;
import com.example.ntudepartmentdefense.manager.NetworkManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.MobParam;
import com.example.ntudepartmentdefense.util.TowerParam;

public class InfoWindow extends Entity{
	private GameWindow gameWindow;
	
	private AnimatedSprite selectedBox;
	private GameSprite subject; 
	private Text[] texts;
		private static final String[] towerStr = {"  ATK ", "RANGE ", " FREQ "};
			private int ATK = 0;
			private float RANGE = 0f;
			private String FREQ = "";
				private static final int SLOW_TOWER_RELOAD_ABOVE = DataManager.SLOW_TOWER_RELOAD_ABOVE;
				private static final int FAST_TOWER_RELOAD_BELOW = DataManager.FAST_TOWER_RELOAD_BELOW;
		private static final String[] mobStr = {"   HP ", "  DEF ", "SPEED "};
			private int HP = 0;
			private int DEF = 0;
			private String SPEED = "";
				private static final int SLOW_MOB_DURATION_ABOVE = DataManager.SLOW_MOB_DURATION_ABOVE;
				private static final int FAST_MOB_DURATION_BELOW = DataManager.FAST_MOB_DURATION_BELOW;
	private ButtonSprite levelUpButton;
	private ButtonSprite removeButton;
	private ButtonSprite focusButton;
		private static final int TOWER = 0;
		private static final int MOB = 1;
				
	InfoWindow(float x, float y, float width, float height, GameWindow gameWindow){
		super(x, y);
		
		//ini texts
		texts = new Text[3];
		for(int i=0; i<3; i++){
			texts[i] = new Text(40, 100+i*40, //TODO set texts position
					ResourceManager.fontDefault32Bold, 
					towerStr[i]+"     ", 
					ResourceManager.getInstance().engine.getVertexBufferObjectManager());
			attachChild(texts[i]);
		}
		
		//create a selected box
		selectedBox = new AnimatedSprite(0, 0, 
				ResourceManager.selectedBoxTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		selectedBox.animate(100);
		attachChild(selectedBox);		
		
		//create buttons
		removeButton = new ButtonSprite(0, 0, 
				ResourceManager.gameRemoveTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager(), 
				new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Tower tower = (Tower) selectedBox.getParent();
				NetworkManager.getInstance().cmdRemove(tower.getXs()[0], tower.getYs()[0]);
			}
		});
		ResourceManager.getInstance().moveCenter(width/2, height-removeButton.getHeight(), removeButton);
		removeButton.setVisible(false);
		attachChild(removeButton);
		gameWindow.registerTouchAreaFor(removeButton);
		
		levelUpButton = new ButtonSprite(0, 0, 
				ResourceManager.gameLevelUpTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager(), 
				new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Tower tower = (Tower) selectedBox.getParent();
				int upperID = DataManager.getInstance().towerParam[tower.getDepartmentID()][tower.getTowerID()].getUpperLevelTower();
				
				if (GameManager.getInstance().getMoney() >= 
						DataManager.getInstance().towerParam[tower.getDepartmentID()][upperID].getBuildCost())
					NetworkManager.getInstance().cmdLevelUp(tower.getXs()[0], tower.getYs()[0]);
			}
		});
		ResourceManager.getInstance().moveCenter(width/2, removeButton.getY()-levelUpButton.getHeight(), levelUpButton);
		levelUpButton.setVisible(false);
		attachChild(levelUpButton);
		gameWindow.registerTouchAreaFor(levelUpButton);
		
		focusButton = new ButtonSprite(0, 0, 
				ResourceManager.gameFocusTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager(), 
				new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Mob mob = (Mob) selectedBox.getParent();
				NetworkManager.getInstance().cmdFocus(mob.getID());
			}
		});
		ResourceManager.getInstance().moveCenter(width/2, height-focusButton.getHeight(), focusButton);
		focusButton.setVisible(false);
		attachChild(focusButton);
		gameWindow.registerTouchAreaFor(focusButton);
	}
	private void setTextureRegion(TiledTextureRegion texture){
		if (subject != null)
			detachChild(subject);
		subject = new GameSprite(40, 0, texture);
		attachChild(subject);
	}
	
	//tower
	public void displayInfoOf(Tower tower){
		TowerParam param = DataManager.getInstance().towerParam[tower.getDepartmentID()][tower.getTowerID()];
		setTextureRegion(param.getTextureRegion());
		
		ATK = DataManager.getInstance().bulletParam[param.getBullet()].getDamage();
		RANGE = param.getRange();
		if ( param.getReloadCD() > SLOW_TOWER_RELOAD_ABOVE){
			FREQ = "SLOW";
		}else if( param.getReloadCD() < FAST_TOWER_RELOAD_BELOW){
			FREQ = "FAST";
		}else{
			FREQ = "MEDIUM";
		}
		setText(tower, towerStr);
		setButton(tower);
		select(tower);
	}
	private void setText(Tower tower, String[] str){
		texts[0].setText(str[0]+Integer.toString(ATK));
		texts[1].setText(str[1]+Float.toString(RANGE));
		texts[2].setText(str[2]+FREQ);
	}
	private void setButton(Tower tower){
		focusButton.setVisible(false);
		
		removeButton.setVisible(true);
		if (DataManager.getInstance().towerParam[tower.getDepartmentID()][tower.getTowerID()]
				.getUpperLevelTower() != -1)
			levelUpButton.setVisible(true);
	}
	
	//mob
	public void displayInfoOf(Mob mob){
		MobParam param = DataManager.getInstance().mobParam[mob.getMobType()];
		setTextureRegion(param.getTextureRegion());
		
		HP = param.getMaxHP();
		DEF = param.getDefense();
		if ( param.getDuration() > SLOW_MOB_DURATION_ABOVE){
			SPEED = "SLOW";
		}else if( param.getDuration() < FAST_MOB_DURATION_BELOW){
			SPEED = "FAST";
		}else{
			SPEED = "MEDIUM";
		}
		setText(mob, mobStr);
		setButton(mob);
		select(mob);
	}
	private void setText(Mob mob, String[] str){
		texts[0].setText(str[0]+Integer.toString(HP));
		texts[1].setText(str[1]+Integer.toString(DEF));
		texts[2].setText(str[2]+SPEED);
	}
	private void setButton(Mob mob){
		removeButton.setVisible(false);
		levelUpButton.setVisible(false);
		
		if (NetworkManager.getInstance().isHost() != mob.ownedByServer)
			focusButton.setVisible(true);
	}
	
	public void select(GameSprite gameSprite){
		if (selectedBox.hasParent())
			selectedBox.detachSelf();
		
		ResourceManager.getInstance().moveCenter(
				ResourceManager.getInstance().getCenterX(gameSprite), 
				ResourceManager.getInstance().getCenterY(gameSprite),
				selectedBox);
		gameSprite.attachChild(selectedBox);
	}
}
