package com.example.ntudepartmentdefense.layer;

import org.andengine.entity.Entity;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.example.ntudepartmentdefense.gamesync.GameSprite;
import com.example.ntudepartmentdefense.gamesync.Mob;
import com.example.ntudepartmentdefense.gamesync.Tower;
import com.example.ntudepartmentdefense.manager.DataManager;
import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.MobParam;
import com.example.ntudepartmentdefense.util.TowerParam;

public class InfoWindow extends Entity{
	private float width;
	
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
	
				InfoWindow(float x, float y, float width, float height){
		super(x, y);
		this.width = width;
		
		//ini texts
		texts = new Text[3];
		for(int i=0; i<3; i++){
			texts[i] = new Text(40, 100+i*40, //TODO set texts position
					ResourceManager.fontDefault32Bold, 
					towerStr[i]+"     ", 
					ResourceManager.getInstance().engine.getVertexBufferObjectManager());
			attachChild(texts[i]);
		}
	}
	private void setText(Tower tower, String[] str){
		texts[0].setText(str[0]+Integer.toString(ATK));
		texts[1].setText(str[1]+Float.toString(RANGE));
		texts[2].setText(str[2]+FREQ);
	}
	private void setText(Mob mob, String[] str){
		texts[0].setText(str[0]+Integer.toString(HP));
		texts[1].setText(str[1]+Integer.toString(DEF));
		texts[2].setText(str[2]+SPEED);
	}
	private void setTextureRegion(TiledTextureRegion texture){
		if (subject != null)
			detachChild(subject);
		subject = new GameSprite(40, 0, texture);
		attachChild(subject);
	}
	
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
	}
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
	}
}
