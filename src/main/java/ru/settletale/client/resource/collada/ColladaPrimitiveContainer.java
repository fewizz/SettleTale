package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.client.resource.collada.Input.Semantic;
import ru.settletale.util.XMLUtils;

public abstract class ColladaPrimitiveContainer {
	public final Mesh mesh;
	public final List<Input> inputs;
	final boolean usesNormal;
	
	public ColladaPrimitiveContainer(Mesh mesh, Element element) {
		this.mesh = mesh;
		inputs = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("input", element, e -> {
			inputs.add(new Input(e));
		});
		
		usesNormal = getInput(Semantic.NORMAL) != null;
	}
	
	public abstract int getTotalUsedVertexCount();
	
	public boolean usesNormal() {
		return usesNormal;
	}
	
	public Input getInput(Semantic s) {
		for(Input i : inputs) {
			if(i.semantic == s) {
				return i;
			}
		}
		
		return null;
	}
}