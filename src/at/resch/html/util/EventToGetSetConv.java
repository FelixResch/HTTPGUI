package at.resch.html.util;

public class EventToGetSetConv {

	private static String stringToConvert = "private static final String EVENT_ON_ABORT = \"onabort\";private static final String EVENT_ON_CAN_PLAY = \"oncanplay\";private static final String EVENT_ON_CAN_PLAY_THROUGH = \"oncanplaythrough\";private static final String EVENT_ON_DURATION_CAHNGE = \"ondurationchange\";private static final String EVENT_ON_EMPTIED = \"onemptied\";private static final String EVENT_ON_ENDED = \"onended\";private static final String EVENT_ON_LOADED_DATA = \"onloadeddata\";private static final String EVENT_ON_LOADED_META_DATA = \"onloadedmetadata\";private static final String EVENT_ON_LOAD_START = \"onloadstart\";private static final String EVENT_ON_PAUSE = \"onpause\";private static final String EVENT_ON_PLAY = \"onplay\";private static final String EVENT_ON_PLAYING = \"onplaying\";private static final String EVENT_ON_PROGRESS = \"onprogress\";private static final String EVENT_ON_RATE_CHANGE = \"onratechange\";private static final String EVENT_ON_READY_STATE_CHANGE = \"onreadystatechange\";private static final String EVENT_ON_SEEKED = \"onseeked\";private static final String EVENT_ON_SEEKING = \"onseeking\";private static final String EVENT_ON_STALLED = \"onstalled\";private static final String EVENT_ON_SUSPEND = \"onsuspend\";private static final String EVENT_ON_TIME_UPDATE = \"ontimeupdate\";private static final String EVENT_ON_VOLUME_CHANGE = \"onvolumechange\";private static final String EVENT_ON_WAITING = \"onwaiting\";";

	public static void main(String[] args) {
		String[] events = stringToConvert.split(";");
		for (int i = 0; i < events.length; i++) {
			String event_ = events[i];
			String event = event_.substring(28, event_.indexOf("=")).trim();
			String name = generateMethodName(event);
			String val = event_.substring(event_.indexOf("\"") + 1,
					event_.lastIndexOf("\""));
			System.out.print(("\tpublic void set" + name + "Handler(String "
					+ val + ") {\n\t\tset(" + event + " , " + val
					+ ");\n\t}\n\t\n\tpublic String get" + name
					+ "Handler() {\n\t\treturn get(" + event + ");\n\t}\n\t\n"));
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
