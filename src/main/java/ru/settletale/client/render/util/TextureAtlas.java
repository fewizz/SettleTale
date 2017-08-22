package ru.settletale.client.render.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.settletale.client.gl.Texture2D;

public class TextureAtlas extends Texture2D {
	final Map<Rectangle, Texture2D> regions = new HashMap<>();
	List<Rectangle> freeRegions;
	
	public Rectangle append(Texture2D tex) {
		check();
		
		if(freeRegions == null) {
			freeRegions = new ArrayList<>();
			freeRegions.add(new Rectangle(this.width(), this.height()));
		}
		Rectangle newRegion = null;
		
		for(Rectangle freeR : freeRegions) {
			if(freeR.width < tex.width() || freeR.height < tex.height())
				continue;
			
			newRegion = new Rectangle(freeR.x, freeR.y, tex.width(), tex.height());
			occupy(newRegion);
			regions.put(newRegion, tex);
			
			break;
		}
		
		return newRegion;
	}
	
	private void occupy(Rectangle r) {
		for(Rectangle or : freeRegions) {
			if(!or.intersects(r)) {
				continue;
			}
			
			freeRegions.remove(or);
			
			if(r.y > or.y) {
				freeRegions.add(new Rectangle(or.x, or.y, or.width, r.y - or.y));
			}
			
			if(r.y + r.height < or.y + or.height) {
				freeRegions.add(new Rectangle(or.x, r.y + r.height, or.width, or.y + or.height - (r.y + r.height)));
			}
			
			if(r.x > or.x) {
				freeRegions.add(new Rectangle(or.x, or.y, r.x - or.x, or.height));
			}
			
			if(r.x + r.width < or.x + or.width) {
				freeRegions.add(new Rectangle(r.x + r.width, or.y, or.width, or.x + or.width - (r.x + r.width)));
			}
			
		}
	}
}
