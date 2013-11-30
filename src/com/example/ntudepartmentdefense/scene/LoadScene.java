package com.example.ntudepartmentdefense.scene;


import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;

import com.example.ntudepartmentdefense.manager.ResourceManager;
import com.example.ntudepartmentdefense.util.Gauge;



public class LoadScene extends Scene {
	private Text loadingText;
	private Gauge loadingBar;
	public void onCreateScene() {
		// TODO
		this.setBackgroundEnabled(true);
		loadingText = new Text(0, 0,
				               ResourceManager.fontDefault32Bold,
				               "Loading...",
				               ResourceManager.getInstance().engine.getVertexBufferObjectManager() );
		loadingText.setPosition(ResourceManager.getInstance().cameraWidth/2f - loadingText.getWidth()/2f, 
				                ResourceManager.getInstance().cameraHeight - loadingText.getHeight()/2f - 100 );
		loadingBar = new Gauge( 400, 50 , "#367C2B" , "#333333" , 0.5f); 
		loadingBar.setPosition( ResourceManager.getInstance().cameraWidth / 2f - 200 , ResourceManager.getInstance().cameraHeight / 2f - 25);
		this.attachChild(loadingText);
		this.attachChild(loadingBar);
	}

	public void setRatio( float pRatio ) {
		loadingBar.setRatio(pRatio);
	}
	public void onUnloadScene() {
		loadingText.detachSelf();
		loadingBar.detachSelf();
	}
}
