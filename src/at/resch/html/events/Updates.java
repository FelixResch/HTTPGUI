package at.resch.html.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Updates {

	private ArrayList<Update> updates = new ArrayList<>();
	
	public Updates() {
	}
	
	public void addUpdate(String element, String html) {
		updates.add(new Update(element, html));
	}
	
	public String getJSON() {
		String ret = "{\n\"updates\":\n\t[";
		Iterator<Update> iterator = updates.iterator();
		while(iterator.hasNext()) {
			Update u = iterator.next();
			String key = u.key;
			String element = "\n\t\t{\n\t\t\t\"element\":";
			element += "\"" + key + "\", ";
			element += "\"html\":\"" + u.value.replace("\"", "\\\"").replace("\n", "") + "\"\n\t\t}";
			ret += element;
			if(iterator.hasNext())
				ret += ",\n";
		}
		ret += "\n\t]\n}";
		return ret;
	}
	
	private class Update {
		public String key;
		public String value;
		
		public Update(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

}
