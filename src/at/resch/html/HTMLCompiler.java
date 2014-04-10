package at.resch.html;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;
import java.util.TreeSet;

import sun.nio.cs.ext.ISCII91;
import at.resch.html.annotations.CompilerWeight;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.NotSupportedInBrowsers;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.annotations.ResourcePack;
import at.resch.html.components.Warning;
import at.resch.html.elements.BODY;
import at.resch.html.elements.DIV;
import at.resch.html.elements.DOCTYPE;
import at.resch.html.elements.HEAD;
import at.resch.html.elements.HTML;
import at.resch.html.elements.HTMLDocument;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.LINK;
import at.resch.html.elements.PROGRESS;
import at.resch.html.elements.SCRIPT;
import at.resch.html.elements.STYLE;
import at.resch.html.elements.TITLE;
import at.resch.html.enums.Browsers;
import at.resch.html.enums.Style;
import at.resch.html.test.TestPage;

@ResourcePack("bootstrap_2.tar.xz")
public class HTMLCompiler {

	public static String compileObject(Object o, Browsers b) {
		if (o instanceof HTMLElement) {
			HTMLElement html = (HTMLElement) o;
			if (checkCompatibility(html, b)) {
				return ((HTMLElement) o).renderHTML();
			} else {
				addCompatibilityWarning(html);
				return html.renderHTML();
			}
		} else if (isPage(o)) {
			HTMLElement html = compilePage(o);
			if (checkCompatibility(html, b)) {
				return html.renderHTML();
			} else {
				addCompatibilityWarning(html);
				return html.renderHTML();
			}
		} else if (isPartial(o)) {
			HTMLElement html = compilePartial(o);
			if (checkCompatibility(html, b)) {
				return html.renderHTML();
			} else {
				addCompatibilityWarning(html);
				return html.renderHTML();
			}
		} else {
			return o.toString();
		}
	}

	private static boolean isPage(Object o) {
		return o.getClass().isAnnotationPresent(Page.class);
	}

	private static boolean isPartial(Object o) {
		return o.getClass().isAnnotationPresent(Partial.class);
	}

	public static HTMLElement compileObjectToPage(Object o) {
		if (o instanceof HTMLElement) {
			return (HTMLElement) o;
		} else if (isPage(o)) {
			return compilePage(o);
		} else if (isPartial(o)) {
			return compilePartial(o);
		} else {
			return new DIV(o.toString());
		}
	}

	private static HTMLElement compilePartial(Object o) {
		try {
			Partial p = o.getClass().getAnnotation(Partial.class);
			if (!HTMLElement.class.isAssignableFrom(p.parent())
					|| !HTMLElement.class.isAssignableFrom(p.delimiter())) {
				return null;
			}
			HTMLElement html = (HTMLElement) p.parent().newInstance();
			boolean first = true;
			TreeMap<Double, Method> content = new TreeMap<>();
			double num = 0;
			for (Method method : o.getClass().getMethods()) {
				if (method.getParameterTypes().length == 0)
					continue;
				if (method.getParameterTypes()[0] == HTMLElement.class
						&& method.isAnnotationPresent(Content.class)) {
					if(method.isAnnotationPresent(CompilerWeight.class)) {
						CompilerWeight cw = method.getAnnotation(CompilerWeight.class);
						content.put(cw.value(), method);
						num = cw.value() + 1;
					} else {
						content.put(num, method);
						num++;
					}
				}
			}
			for(Method method : content.values()) {
				Content c = method.getAnnotation(Content.class);
				Class<?> parent = c.parent();
				if (HTMLElement.class.isAssignableFrom(parent)) {
					try {
						HTMLElement h = (HTMLElement) parent.newInstance();
						if (!c.styleClass().equals(""))
							h.setStyleClass(c.styleClass());
						method.invoke(o, h);
						if (!first) {
							html.addObject(p.delimiter().newInstance());
						}
						first = false;
						html.addObject(h);
					} catch (InstantiationException
							| IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
			return html;
		} catch (InstantiationException | IllegalAccessException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static HTMLElement compilePage(Object o) {
		Page p = null;
		Annotation[] pageAnnotations = o.getClass().getAnnotations();
		for (Annotation annotation : pageAnnotations) {
			if (annotation instanceof Page)
				p = (Page) annotation;
		}
		HTMLElement e = new HTMLDocument();
		e.addObject(new DOCTYPE());
		HTML html = new HTML();
		e.addObject(html);
		HEAD head = new HEAD();
		head.addObject(new TITLE(p.title()));
		SCRIPT script = new SCRIPT();
		script.setType("text/javascript");
		script.setSrc("http://code.jquery.com/jquery-1.10.1.min.js");
		if(p.style() == Style.IVORY) {
			head.addObject(new LINK(new HTMLAttribute("rel", "stylesheet"),
					new HTMLAttribute("type", "text/css"), new HTMLAttribute(
							"href", "/css/" + p.style())));
		} else if (p.style() == Style.BOOTSTRAP_DEFAULT) {
			head.addObject(new LINK(new HTMLAttribute("rel", "stylesheet"),
					new HTMLAttribute("type", "text/css"), new HTMLAttribute(
							"href", "/res/bootstrap/css/bootstrap.css")));
			head.addObject(script);
			script = new SCRIPT();
			script.setType("text/javascript");
			script.setSrc("/res/bootstrap/js/bootstrap.js");
			head.addObject(script);
		}
		script = new SCRIPT();
		script.setType("text/javascript");
		script.setSrc("/script/");
		head.addObject(script);
		head.addObject(new STYLE("progress, progress[role] {  -webkit-appearence: none;-moz-appearence: none;appearence: none;border: none;background-size: auto;width: 80%;float: center;height: 2px;}  progress[role]:after {background-image: none;}progress[role] strong {display: none;}progress,progress[role][aria-valuenow] {background: #EBEBE0 !important;}progress {color: blue;}progress::-webkit-progress-bar { background: #EBEBE0;}  progress::-webkit-progress-value {  background: #00CCFF;}  progress::-moz-progress-bar {  background: #00CCFF;}.job {border: 1px #EBEBE0 solid;text-align: center;width: 60%;}.message {width: 300px;float: right;padding: 10px;position: absolute;} .ajax_load {position: absolute; padding: 0px; border: none; margin: 0px; width: 100%; height: 2px}"));
		html.addObject(head);
		BODY body = new BODY();
		body.addAttribute(new HTMLAttribute("onload", "loadActionList();"));
		PROGRESS progress = new PROGRESS();
		progress.setValue("2");
		progress.setMax("4");
		progress.setId("ajax_progress");
		progress.setStyleClass("ajax_load");
		DIV messages = new DIV();
		messages.setStyle("position: absolute; padding: 10px");
		messages.setId("messages");
		body.addObject(progress);
		body.addObject(messages);
		boolean first = true;
		TreeMap<Double, Method> content = new TreeMap<>();
		double num = 0;
		for (Method method : o.getClass().getMethods()) {
			if (method.getParameterTypes().length == 0)
				continue;
			if (method.getParameterTypes()[0] == HTMLElement.class
					&& method.isAnnotationPresent(Content.class)) {
				if(method.isAnnotationPresent(CompilerWeight.class)) {
					CompilerWeight cw = method.getAnnotation(CompilerWeight.class);
					content.put(cw.value(), method);
					num = cw.value() + 1;
				} else {
					content.put(num, method);
					num++;
				}
			}
		}
		for(Method method : content.values()) {
			Content c = method.getAnnotation(Content.class);
			Class<?> parent = c.parent();
			if (HTMLElement.class.isAssignableFrom(parent)) {
				try {
					HTMLElement h = (HTMLElement) parent.newInstance();
					if (!c.styleClass().equals(""))
						h.setStyleClass(c.styleClass());
					method.invoke(o, h);
					if (!first) {
						body.addObject(p.delimiter().newInstance());
					}
					first = false;
					body.addObject(h);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException e1) {
					e1.printStackTrace();
				}

			}
		}
		html.addObject(body);
		return e;
	}

	private static boolean checkCompatibility(HTMLElement html, Browsers b) {
		if (html.getClass().isAnnotationPresent(NotSupportedInBrowsers.class)) {
			NotSupportedInBrowsers not = html.getClass().getAnnotation(
					NotSupportedInBrowsers.class);
			Browsers[] browsers = not.browsers();
			for (Browsers browser : browsers) {
				if (browser == b)
					return false;
			}
		}
		for (Object o : html.getChildren()) {
			if (o instanceof HTMLElement)
				if (!checkCompatibility((HTMLElement) o, b))
					return false;
		}
		return true;
	}

	private static void addCompatibilityWarning(HTMLElement root) {
		for (Object o : root.getChildren()) {
			if (o instanceof HTML) {
				for (Object e : ((HTMLElement) o).getChildren()) {
					if (e instanceof BODY) {
						BODY body = (BODY) e;
						((BODY) e).addObject(new Warning("You are using an incompatible Browser"));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(compileObject(new TestPage(), Browsers.FIREFOX));
	}

}
