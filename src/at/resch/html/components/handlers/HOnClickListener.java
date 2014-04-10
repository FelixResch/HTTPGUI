package at.resch.html.components.handlers;

import at.resch.html.events.Updates;

public interface HOnClickListener {

	public abstract String[] getArguments();
	public abstract void invoke(Updates u, String[] args, String param);
}
