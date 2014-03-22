package at.resch.html.server.util;

import at.resch.html.enums.Browsers;

public class UserAgentParser {

	public static Browsers parseUserAgent(String userAgent) {
		if(userAgent.contains("Firefox"))
			return Browsers.FIREFOX;
		if(userAgent.contains("Trident"))
			return Browsers.INTERNET_EXPLORER;
		if(userAgent.contains("Chrome"))
			return Browsers.CHROME;
		if(userAgent.contains("Safari"))
			return Browsers.SAFARI;
		return Browsers.OPERA;
	}

}
