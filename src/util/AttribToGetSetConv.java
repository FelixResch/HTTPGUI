package util;

public class AttribToGetSetConv {

	private static String stringToConvert = "private static final String ATTRIB_ALT = \"alt\";private static final String ATTRIB_COORDS = \"coords\";private static final String ATTRIB_SHAPE = \"shape\";";

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
