package ru.settletale.client.gui;

import java.util.ArrayList;
import java.util.List;

public abstract class Gui {
	final List<GuiElement> elements = new ArrayList<>();
	
	public Gui() {
		init();
	}
	
	public void init() {
		initElements();
	}
	
	public void initElements() {
		
	}
	
	public GuiElement getElement(int index) {
		return elements.get(index);
	}
	
	public void resize() {
		elements.forEach(el -> {
			el.resize();
		});
	}
	
	public void addElement(GuiElement el) {
		elements.add(el);
		el.resize();
	}
	
	public void render() {
		renderBackground();
		
		elements.forEach(el -> {
			el.render();
		});
	}
	
	public void renderBackground() {
	}
	
	public void update() {
		elements.forEach(el -> {
			el.update();
		});
	}
}