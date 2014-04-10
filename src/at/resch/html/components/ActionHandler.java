package at.resch.html.components;

import at.resch.html.annotations.Action;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class ActionHandler {

	public ActionHandler() {
		
	}
	
	@Action(name="hcomponent.handler.onclick.param", args={})
	public void onClickAction(Updates updates, String param) {
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get("_hcomponent.object." + param);
		if(html.getOn_click_listener() != null)
			ContentManager.requestParams("hcomponent.handler.onclick.invoke", html.getOn_click_listener().getArguments(), param);
	}
	
	@Action(name="hcomponent.handler.onclick.invoke", args={"undefined"}) 
	public void onClickInvoke(Updates updates, String[] args, String param){
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get("_hcomponent.object." + param);
		if(html.getOn_click_listener() != null)
			html.getOn_click_listener().invoke(updates, args, param);
	}

}
