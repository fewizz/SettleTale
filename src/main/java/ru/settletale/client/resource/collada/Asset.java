package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Asset {
	public final UpAxis upAxis;

	public Asset(Element element) {
		upAxis = UpAxis.getByName(XMLUtils.getFirstChildElement("up_axis", element).getTextContent());
	}

	public enum UpAxis {
		X_UP,
		Y_UP,
		Z_UP;

		public static void chaneToYUpAxis(UpAxis from, float[] array) {
			float x;
			float y;
			float z;
			
			switch (from) {
				case X_UP:
					x = array[1];
					y = array[2];
					z = array[0];
					array[0] = x;
					array[1] = y;
					array[2] = z;
					return;
				case Z_UP:
					x = array[2];
					y = array[0];
					z = array[1];
					array[0] = x;
					array[1] = y;
					array[2] = z;
					return;

				default:
					return;
			}
		}

		public static UpAxis getByName(String name) {
			for (UpAxis a : values()) {
				if (a.name().equals(name)) {
					return a;
				}
			}

			return null;
		}
	}
}
