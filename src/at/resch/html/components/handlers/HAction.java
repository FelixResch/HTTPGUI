package at.resch.html.components.handlers;

import java.lang.reflect.Method;

import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public abstract class HAction {

	protected Object executeOn;
	protected Method method;
	protected String name;
	protected String[] arguments;

	public HAction(Object executeOn, Method method, String name, String[] arguments) {
		this.executeOn = executeOn;
		this.method = method;
		this.name = name;
		this.arguments = arguments;
		if (!check())
			throw new RuntimeException("Error with HAction " + name);
	}

	public void invoke(Updates updates, String[] args, String param) {
		if (!check()) {
			throw new RuntimeException("Error with HAction " + name);
		}
		Session.performInjection(executeOn);
		invokeInternal(updates, args, param);
	}

	protected abstract boolean check();

	protected abstract void invokeInternal(Updates updates, String[] args,
			String param);

	public Object getExecuteOn() {
		return executeOn;
	}

	public void setExecuteOn(Object executeOn) {
		this.executeOn = executeOn;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

}
