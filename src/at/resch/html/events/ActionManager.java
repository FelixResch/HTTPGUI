package at.resch.html.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import at.resch.html.HTMLCompiler;
import at.resch.html.components.Alert;
import at.resch.html.enums.Browsers;

public class ActionManager {

	private HashMap<String, Action> actions = new HashMap<>();

	public void addAction(Action a) {
		actions.put(a.getName(), a);
	}

	public Action getAction(String name) {
		if (actions.containsKey(name))
			return actions.get(name);
		return null;
	}

	public Updates invokeAction(String target, Browsers browser) {
		Updates updates = new Updates();
		String actionString = target.substring(1).split("/")[1];
		String cmd = actionString.split("&")[0];
		String args[] = actionString.substring(actionString.indexOf("/") + 1)
				.split("&");
		Action action = getAction(cmd);
		String cmdArgs[] = action.getArgs();
		if (args.length == cmdArgs.length) {
			updates.addUpdate("messages", HTMLCompiler.compileObject(new Alert(
					"Arg count doesn't match"), browser));
			return updates;
		}
		for (int i = 0; i < args.length; i++) {
			if (!args[i].split("=")[0].equals(cmdArgs[0])) {
				updates.addUpdate("messages", HTMLCompiler.compileObject(
						new Alert("Args order is wrong"), browser));
				return updates;
			}
		}
		Method method = action.getMethod();
		Object executeOn = action.getExecuteOn();
		try {
			method.invoke(executeOn, updates, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			updates.addUpdate(
					"messages",
					HTMLCompiler.compileObject(new Alert("Some error happened:"
							+ e.getMessage()), browser));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			updates.addUpdate(
					"messages",
					HTMLCompiler.compileObject(new Alert("Some error happened:"
							+ e.getMessage()), browser));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			updates.addUpdate(
					"messages",
					HTMLCompiler.compileObject(new Alert("Some error happened:"
							+ e.getMessage()), browser));
		}
		return updates;
	}

	public String getActions() {
		String actions = "{\"actions\":[";
		Iterator<Action> iterator = this.actions.values().iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next();
			String sen = "{\"action\":\"" + action.getName() + "\", \"args\":[";
			for(int i = 0; i < action.getArgs().length; i++) {
				sen += "{\"Arg\":\"" + action.getArgs()[i] + "\"}";
				if(i != action.getArgs().length - 1) {
					sen += ", ";
				}
			}
			actions += sen;
		}
		actions += "]}";
		return actions;
	}

}
