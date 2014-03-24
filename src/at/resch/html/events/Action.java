package at.resch.html.events;

import java.lang.reflect.Method;

public class Action {

	private String name;
	private String[] args;
	private Method method;
	private Object executeOn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Action(String name, String[] args, Method method, Object executeOn) {
		super();
		this.name = name;
		this.args = args;
		this.method = method;
		this.executeOn = executeOn;
	}

	public Object getExecuteOn() {
		return executeOn;
	}

	public void setExecuteOn(Object executeOn) {
		this.executeOn = executeOn;
	}

}
