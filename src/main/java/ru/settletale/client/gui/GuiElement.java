package ru.settletale.client.gui;

import org.joml.Vector2f;

public abstract class GuiElement {
	Vector2f position;
	IPositionLogic positionOnWindowResizeLogic;
	IOnClickLogic clickLogic;
	
	public GuiElement() {
		this.position = new Vector2f(0);
		this.positionOnWindowResizeLogic = () -> null;
		this.clickLogic = () -> {};
	}
	
	public GuiElement setPositionOnWindowResizeLogic(IPositionLogic rl) {
		this.positionOnWindowResizeLogic = rl;
		return this;
	}
	
	public GuiElement onClickLogic(IOnClickLogic ocl) {
		this.clickLogic = ocl;
		return this;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public void setPosition(Vector2f v) {
		this.position = v;
	}
	
	public void render() {
	}
	
	public void update() {
	}
	
	public void resize() {
		Vector2f v = positionOnWindowResizeLogic.getPosition();
		
		if(v != null) {
			setPosition(v);
		}
	}
}
