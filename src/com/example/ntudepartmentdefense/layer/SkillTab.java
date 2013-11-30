package com.example.ntudepartmentdefense.layer;

import com.example.ntudepartmentdefense.util.IconParam;

public class SkillTab extends Tab{
	SkillTab(int index, float notebookWidth, IconParam[] icons, GameWindow gameWindow){
		super(index, notebookWidth, icons, gameWindow);
	}
	@Override
	protected void createLabel(){
		label = new SkillLabel(labelWidth, labelHeight, index, notebookWidth, gameWindow);
		this.attachChild(label);
		gameWindow.registerTouchAreaFor(label);
	}
}