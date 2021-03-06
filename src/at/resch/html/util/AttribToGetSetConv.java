package at.resch.html.util;

public class AttribToGetSetConv {

	private static String stringToConvert = "private static final String ATTRIB_AUTOPLAY = \"autoplay\";private static final String ATTRIB_CONTROLS = \"controls\";private static final String ATTRIB_HEIGHT = \"height\";private static final String ATTRIB_LOOP = \"loop\";private static final String ATTRIB_MUTED = \"muted\";private static final String ATTRIB_POSTED = \"poster\";private static final String ATTRIB_PRELOAD = \"preload\";private static final String ATTRIB_SRC = \"src\";private static final String ATTRIB_WIDTH = \"width\";";

	public static void main(String[] args) {
		String[] events = stringToConvert.split(";");
		for (int i = 0; i < events.length; i++) {
			String event_ = events[i];
			String event = event_.substring(28, event_.indexOf("=")).trim();
			String name = generateMethodName(event);
			String val = event_.substring(event_.indexOf("\"") + 1,
					event_.lastIndexOf("\""));
			System.out.print(("\tpublic void set" + name + "(String "
					+ val + ") {\n\t\tset(" + event + " , " + val
					+ ");\n\t}\n\t\n\tpublic String get" + name
					+ "() {\n\t\treturn get(" + event + ");\n\t}\n\t\n"));
		}
	}

	private static String generateMethodName(String name) {
		String[] parts = name.substring(name.indexOf("_") + 1).split("_");
		String ret = "";
		for (String p : parts) {
			ret += p.substring(0, 1).toUpperCase()
					+ p.substring(1).toLowerCase();
		}
		return ret;
	}

}
