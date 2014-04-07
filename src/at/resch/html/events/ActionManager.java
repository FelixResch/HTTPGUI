package at.resch.html.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import at.resch.html.HTMLCompiler;
import at.resch.html.components.Warning;
import at.resch.html.enums.Browsers;
import at.resch.html.server.Session;

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
		try {
			Session.prepareForAction();
			Updates updates = Session.getCurrent().getUpdates();
			String actionString = target;
			String cmd = actionString.split("&")[0];
			String args[] = new String[] {};
			if (!actionString.substring(actionString.indexOf("&") + 1).equals("")
					&& actionString.contains("&")) {
				args = actionString.substring(actionString.indexOf("&") + 1).split(
						"&");
			}
			Action action = getAction(cmd);
			if (action == null) {
				System.err.println("No such Action: " + cmd + " from " + target);
				updates.addUpdate(
						"messages",
						HTMLCompiler.compileObject(new Warning("No such Action: "
								+ cmd), browser));
				return updates;
			}
			String cmdArgs[] = action.getArgs();
			boolean is_params = false;
			for(String ar : args) {
				if(ar.startsWith("params")) {
					is_params = true;
				}
			}
			if (args.length - (is_params ? 1 : 0) != cmdArgs.length) {
				updates.addUpdate("messages", HTMLCompiler.compileObject(
						new Warning("Arg count doesn't match"), browser));
				return updates;
			}
			String params = null;
			if (args.length != 0)
				for (int i = 0; i < args.length; i++) {
					if(args[i].startsWith("params")) {
						params = args[i].split("=")[1];
					} else if (!args[i].split("=")[0].equals(cmdArgs[i])) {
						Session.logger.warn("Needed: " + cmdArgs[i] + " Found: " + args[i].split("=")[0]);
						updates.addUpdate("messages", HTMLCompiler.compileObject(
								new Warning("Args order is wrong " + target + " needed: " + Arrays.toString(cmdArgs)), browser));
						return updates;
					} else {
						args[i] = args[i].split("=")[1];
					}
				}
			Method method = action.getMethod();
			Object executeOn = action.getExecuteOn();
			try {
				if (args.length == 0)
					if(is_params)
						method.invoke(executeOn, updates, params);
					else
						method.invoke(executeOn, updates);
				else
					if(is_params)
						method.invoke(executeOn, updates, args, params);
					else
						method.invoke(executeOn, updates, args);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				updates.addUpdate("messages", HTMLCompiler.compileObject(
						new Warning("Some error happened:" + e.getMessage()),
						browser));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				updates.addUpdate("messages", HTMLCompiler.compileObject(
						new Warning("Some error happened:" + e.getMessage()),
						browser));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				updates.addUpdate("messages", HTMLCompiler.compileObject(
						new Warning("Some error happened:" + e.getMessage()),
						browser));
			}
			return updates;
		} catch (Exception e) {
			Session.logger.fatal("Fatal Error in Action", e);
			Updates updates = new Updates();
			updates.addUpdate("messages", new at.resch.html.components.Error("Fatal Error in Action: " + e.getMessage()).renderHTML());
			return updates;
		}
	}

	public String getActions() {
		String actions = "{\"actions\":[";
		Iterator<Action> iterator = this.actions.values().iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next();
			String sen = "{\"action\":\"" + action.getName() + "\", \"args\":[";
			for (int i = 0; i < action.getArgs().length; i++) {
				sen += "{\"Arg\":\"" + action.getArgs()[i] + "\"}";
				if (i != action.getArgs().length - 1) {
					sen += ", ";
				}
			}
			actions += sen + "]}";
			if(iterator.hasNext()) {
				actions += ", ";
			}
		}
		actions += "]}";
		return actions;
	}

}
