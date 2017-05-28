package ru.settletale.client.gui;

import java.util.ArrayList;
import java.util.List;

public abstract class Gui {
	final List<Gui> childs = new ArrayList<>();
	
	public Gui() {
		init();
	}
	
	public void init() {
		initChilds();
	}
	
	public void initChilds() {
		
	}
	
	public void resize() {
		childs.forEach(el -> {
			el.resize();
		});
	}
	
	public void addChild(Gui ch) {
		childs.add(ch);
		ch.resize();
	}
	
	public void render() {
		renderBackground();
		
		childs.forEach(el -> {
			el.render();
		});
	}
	
	public void renderBackground() {
	}
	
	public void update() {
		childs.forEach(el -> {
			el.update();
		});
	}
}