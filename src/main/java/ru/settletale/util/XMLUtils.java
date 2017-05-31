package ru.settletale.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {
	public static void forEachChildElement(Node node, ElementIterFunc func) {
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if(n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			func.run((Element) n);
		}
	}
	
	public static void forEachChildElementWithName(String name, Node node, ElementIterFunc func) {
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if(n.getNodeType() != Node.ELEMENT_NODE || !n.getNodeName().equals(name)) {
				continue;
			}
			func.run((Element) n);
		}
	}
	
	public static void forEachAttribute(Node node, NodeAttrIterFunc func) {
		NamedNodeMap attrs = node.getAttributes();
		
		for(int i = 0; i < attrs.getLength(); i++) {
			func.run((Attr) attrs.item(i));
		}
	}
	
	public static Element getFirstChildElement(String name, Node node) {
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name)) {
				return (Element) n;
			}
		}
		
		return null;
	}
	
	public interface ElementIterFunc {	
		public void run(Element node);
	}
	
	public interface NodeAttrIterFunc {	
		public String run(Attr attr);
	}
}
