package at.resch.html.components.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class HOnKeyDownAction extends HAction {

	public HOnKeyDownAction(Object executeOn, Method method, String name,
			String[] arguments) {
		super(executeOn, method, name, arguments);
	}

	@Override
	protected boolean check() {
		return (method.getParameterTypes()[0] == Updates.class && (method
				.getParameterTypes()[1] == HKeyEventArgs.class || (method
				.getParameterTypes()[1] == String[].class && method
				.getParameterTypes()[2] == HKeyEventArgs.class)));
	}

	@Override
	protected void invokeInternal(Updates updates, String[] args, String param) {
		int keyCode = Integer.parseInt(Session.getCurrent()
				.get("_hcomponent.object." + param + ".event.keyCode")
				.toString());
		HKeyEventArgs eventArgs = new HKeyEventArgs(keyCode);
		try {
			if(method.getParameterTypes()[1] == String.class)
				method.invoke(executeOn, updates, eventArgs);
			else
				method.invoke(executeOn, updates, args, eventArgs);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Session.logger.warn("Failed to invoke HAction " + name, e);
		}
	}

}
