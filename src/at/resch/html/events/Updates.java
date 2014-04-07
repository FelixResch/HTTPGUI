package at.resch.html.events;

import java.util.HashMap;
import java.util.Iterator;

public class Updates {

	private HashMap<String, String> updates = new HashMap<>();
	
	public Updates() {
	}
	
	public void addUpdate(String element, String html) {
		updates.put(element, html);
	}
	
	public String getJSON() {
		String ret = "{\n\"updates\":\n\t[";
		Iterator<String> iterator = updates.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			String element = "\n\t\t{\n\t\t\t\"element\":";
			element += "\"" + key + "\", ";
			element += "\"html\":\"" + updates.get(key).replace("\"", "\\\"").replace("\n", "") + "\"\n\t\t}";
			ret += element;
			if(iterator.hasNext())
				ret += ",\n";
		}
		ret += "\n\t]\n}";
		return ret;
	}
	

}
