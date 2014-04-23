package at.resch.html.components.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

import at.resch.html.components.ContentManager;
import at.resch.html.enums.MessageType;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class HActionManager {

	private HashMap<String, HAction> actions = new HashMap<>();

	public void addHAction(HActionHandler hActionHandler, Method m) {
		try {
			Class<? extends HAction> eventType = hActionHandler.eventType();
			Constructor<? extends HAction> construct = eventType
					.getConstructor(Object.class, Method.class, String.class, String[].class);
			Object executeOn = m.getDeclaringClass().newInstance();
			String name = hActionHandler.name();
			String[] arguments = hActionHandler.args();
			HAction action = construct.newInstance(executeOn, m, name, arguments);
			addHAction(action);
		} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			Session.logger.warn(
					"Couldn't create HAction " + hActionHandler.name(), e);
		}
	}

	public void addHAction(HAction a) {
		actions.put(a.getName(), a);
	}

	public HAction getHAction(String name) {
		if (actions.containsKey(name))
			return actions.get(name);
		return null;
	}

	public Updates invokeHAction(String target, Updates updates, String[] args,
			String param) {
		try {
			String actionString = target;
			String cmd = actionString.split("&")[0];
			HAction action = getHAction(cmd);
			if (action == null) {
				ContentManager.addMessage(MessageType.ERROR, "No such Action: "
						+ target);
				return updates;
			}

			action.invoke(updates, args, param);
			return updates;
		} catch (Exception e) {
			Session.logger.fatal("Fatal Error in HAction", e);
			updates.addUpdate("messages", new at.resch.html.components.Error(
					"Fatal Error in HAction: " + e.getMessage()).renderHTML());
			return updates;
		}
	}

	public Collection<HAction> getHActionSet() {
		return actions.values();
	}

}
