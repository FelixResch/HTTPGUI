package at.resch.html.components;

import at.resch.html.annotations.Action;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class ActionHandler {

	public ActionHandler() {

	}

	@Action(name = "hcomponent.handler.onclick.param", args = {})
	public void onClickAction(Updates updates, String param) {
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get(
				"_hcomponent.object." + param);
		if (html.getOnClickListener() != null)
			ContentManager.requestParams(
					"hcomponent.handler.onclick.invoke",
					Session.getCurrent().gethActionManager()
							.getHAction(html.getOnClickListener())
							.getArguments(), param);
	}

	@Action(name = "hcomponent.handler.onclick.invoke", args = { "undefined" })
	public void onClickInvoke(Updates updates, String[] args, String param) {
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get(
				"_hcomponent.object." + param);
		if (html.getOnClickListener() != null)
			Session.getCurrent()
					.gethActionManager()
					.invokeHAction(html.getOnClickListener(), updates, args,
							param);
	}

	@Action(name = "hcomponent.handler.onkeydown.param", args = {"event.keyCode"})
	public void onKeyDownAction(Updates updates, String[] args, String param) {
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get(
				"_hcomponent.object." + param);
		Session.getCurrent().store("_hcomponent.object." + param + ".event.keyCode", args[0]);
		if (html.getOnKeyDownListener() != null) {
			ContentManager.requestParams(
					"hcomponent.handler.onkeydown.invoke",
					Session.getCurrent().gethActionManager()
							.getHAction(html.getOnKeyDownListener())
							.getArguments(), param);
		}
	}

	@Action(name = "hcomponent.handler.onkeydown.invoke", args = { "undefined" })
	public void onKeyDownInvoke(Updates updates, String[] args, String param) {
		HComponent<?> html = (HComponent<?>) Session.getCurrent().get(
				"_hcomponent.object." + param);
		if (html.getOnKeyDownListener() != null)
			Session.getCurrent().gethActionManager().invokeHAction(html.getOnKeyDownListener(), updates, args, param);
	}

	@Action(name = "htabbar.change", args = {})
	public void htabbarChanged(Updates updates, String param) {
		String id = param.split("\\.")[0];
		String selected = param.split("\\.")[1];
		HTabView htv = (HTabView) Session.getCurrent().get(
				"_hcomponent.object." + id);
		Session.getCurrent().store(id + ".current", selected);
		htv.display();
		updates.addUpdate(htv.getContainer().getId(), htv.renderHTML());
	}

}
