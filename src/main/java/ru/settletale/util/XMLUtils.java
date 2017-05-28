package ru.settletale.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {
	public static void forEachChildNode(Node node, NodeIterFunc func) {
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			func.run(n);
		}
	}
	
	public static Node getFirstChildNodeWithName(Node node, String name) {
		NodeList nl = node.getChildNodes();
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeName().equals(name)) {
				return n;
			}
		}
		
		return null;
	}
	
	public interface NodeIterFunc {	
		public void run(Node node);
	}
}
