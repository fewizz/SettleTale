package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Collada {
	public final Asset asset;
	public final List<Effect> effectsList;
	public final List<Material> materialsList;
	public final List<Geometry> geometriesList;
	public final List<VisualScene> visualScenesList;
	
	public Collada(Document doc) {
		Element colladaElement = XMLUtils.getFirstChildElement("COLLADA", doc);
		asset = new Asset(XMLUtils.getFirstChildElement("asset", colladaElement));
		
		effectsList = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("effect", XMLUtils.getFirstChildElement("library_effects", colladaElement), element -> {
			Effect e = new Effect(element);
			effectsList.add(e);
		});
		
		materialsList = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("material", XMLUtils.getFirstChildElement("library_materials", colladaElement), element -> {
			Material m = new Material(this, element);
			materialsList.add(m);
		});
		
		geometriesList = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("geometry", XMLUtils.getFirstChildElement("library_geometries", colladaElement), element -> {
			Geometry geometry = new Geometry(element);
			geometriesList.add(geometry);
		});
		
		visualScenesList = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("visual_scene", XMLUtils.getFirstChildElement("library_visual_scenes", colladaElement), element -> {
			VisualScene vs = new VisualScene(this, element);
			visualScenesList.add(vs);
		});
	}
	
	public Effect getEffectByURL(String url) {
		url = url.replace("#", "");
		
		for(Effect e : effectsList) {
			if(e.id.equals(url))
				return e;
		}
		
		return null;
	}
	
	public Geometry getGeometryByURL(String url) {
		url = url.replace("#", "");
		
		for(Geometry g : geometriesList) {
			if(g.id.equals(url))
				return g;
		}
		
		return null;
	}
	
	public Material getMaterialByURL(String url) {
		url = url.replace("#", "");
		
		for(Material m : materialsList) {
			if(m.id.equals(url))
				return m;
		}
		
		return null;
	}
}
