package at.resch.html.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.html.HTMLCollection;

import com.google.common.base.Objects;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.annotations.Priority;
import at.resch.html.components.StandardErrorPage;
import at.resch.html.elements.BR;
import at.resch.html.elements.DIV;
import at.resch.html.elements.HTMLElement;
import at.resch.html.enums.Browsers;

public class Session {

	private final HashMap<String, Locatable> pages = new HashMap<>();

	private static final HashMap<String, Class<?>> locatablesClasses = new HashMap<>();
	private static final HashMap<String, Class<?>> pageClasses = new HashMap<>();
	private static final HashMap<String, Class<?>> partialClasses = new HashMap<>();

	public static void addLocatable(String name, Class<?> locatableClass) {
		if (locatableClass.isAnnotationPresent(Location.class)) {
			if(locatablesClasses.containsKey(name)) {
				if(locatableClass.isAnnotationPresent(Priority.class)){
					Class<?> tmp = locatablesClasses.get(name);
					if(!tmp.isAnnotationPresent(Priority.class)) {
						System.err.println("Overwriting unprioritised Class" + tmp.getCanonicalName() + " with " + locatableClass.getCanonicalName());
						locatablesClasses.put(name, locatableClass);
					} else {
						Priority newClass, oldClass;
						newClass = locatableClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if(newClass.value().compareTo(oldClass.value()) > 0) {
							System.out.println("Overwriting Class " + tmp.getCanonicalName() + " with " + locatableClass.getCanonicalName());
						} else {
							System.out.println("Omitting Class: " + locatableClass.getCanonicalName());
						}
					}
				} else {
					System.out.println("Omitting Class: " + locatableClass.getCanonicalName() + "! Identifier " + name + " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
				}
			} else
				locatablesClasses.put(name, locatableClass);
		}
	}

	public static void removeLocatable(String name) {
		if (locatablesClasses.containsKey(name)) {
			locatablesClasses.remove(name);
		}
	}
	
	public static HTMLElement getCompiledLocatable(String name) {
		return HTMLCompiler.compileObjectToPage(getLocatableInstance(name));
	}
	
	public static HTMLElement getCompiledLocatable(String name, Object ... o) {
		return HTMLCompiler.compileObjectToPage(getLocatableInstance(name, o));
	}
	
	public static Object getLocatableInstance(String name) {
		if(locatablesClasses.containsKey(name)) {
			try {
				return locatablesClasses.get(name).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Locatable : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Locatable found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}
	
	public static Object getLocatableInstance(String name, Object ... o) {
		if(locatablesClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for(int i = 0; i < o.length; i++) {
					types[i] = o.getClass();
				}
				Class<?> component = locatablesClasses.get(name);
				Constructor<?> constr =  component.getConstructor(types);
				Object res = constr.newInstance(o);
				return res;
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
				DIV alert = new DIV("Error while instantiating Locatable : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Locatable found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public static void addPage(String name, Class<?> pageClass) {
		if (pageClass.isAnnotationPresent(Page.class)) {
			if(pageClasses.containsKey(name)) {
				if(pageClass.isAnnotationPresent(Priority.class)){
					Class<?> tmp = pageClasses.get(name);
					if(!tmp.isAnnotationPresent(Priority.class)) {
						System.err.println("Overwriting unprioritised Class" + tmp.getCanonicalName() + " with " + pageClass.getCanonicalName());
						pageClasses.put(name, pageClass);
					} else {
						Priority newClass, oldClass;
						newClass = pageClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if(newClass.value().compareTo(oldClass.value()) > 0) {
							System.out.println("Overwriting Class " + tmp.getCanonicalName() + " with " + pageClass.getCanonicalName());
						} else {
							System.out.println("Omitting Class: " + pageClass.getCanonicalName());
						}
					}
				} else {
					System.out.println("Omitting Class: " + pageClass.getCanonicalName() + "! Identifier " + name + " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
				}
			} else
				pageClasses.put(name, pageClass);
		}
	}

	public static void removePage(String name) {
		if (pageClasses.containsKey(name)) {
			pageClasses.remove(name);
		}
	}
	
	public static HTMLElement getCompiledPage(String name) {
		return HTMLCompiler.compileObjectToPage(getPageInstance(name));
	}
	
	public static HTMLElement getCompiledPage(String name, Object ... o) {
		return HTMLCompiler.compileObjectToPage(getPageInstance(name, o));
	}
	
	public static Object getPageInstance(String name) {
		if(pageClasses.containsKey(name)) {
			try {
				return pageClasses.get(name).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Page : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Page found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}
	
	public static Object getPageInstance(String name, Object ... o) {
		if(pageClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for(int i = 0; i < o.length; i++) {
					types[i] = o[i].getClass();
				}
				Class<?> component = pageClasses.get(name);
				Constructor<?> constr =  component.getConstructor(types);
				Object res = constr.newInstance(o);
				return res;
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				DIV alert = new DIV("Error while instantiating Page : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Page found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public static void addPartial(String name, Class<?> partialClass) {
		if (partialClass.isAnnotationPresent(Partial.class)) {
			if(partialClasses.containsKey(name)) {
				if(partialClass.isAnnotationPresent(Priority.class)){
					Class<?> tmp = partialClasses.get(name);
					if(!tmp.isAnnotationPresent(Priority.class)) {
						System.err.println("Overwriting unprioritised Class" + tmp.getCanonicalName() + " with " + partialClass.getCanonicalName());
						partialClasses.put(name, partialClass);
					} else {
						Priority newClass, oldClass;
						newClass = partialClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if(newClass.value().compareTo(oldClass.value()) > 0) {
							System.out.println("Overwriting Class " + tmp.getCanonicalName() + " with " + partialClass.getCanonicalName());
						} else {
							System.out.println("Omitting Class: " + partialClass.getCanonicalName());
						}
					}
				} else {
					System.out.println("Omitting Class: " + partialClass.getCanonicalName() + "! Identifier " + name + " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
				}
			} else
				partialClasses.put(name, partialClass);
		}
	}

	public static void removePartial(String name) {
		if (partialClasses.containsKey(name)) {
			partialClasses.remove(name);
		}
	}
	
	public static HTMLElement getCompiledPartial(String name) {
		return HTMLCompiler.compileObjectToPage(getPartialInstance(name));
	}
	
	public static HTMLElement getCompiledPartial(String name, Object ... o) {
		return HTMLCompiler.compileObjectToPage(getPartialInstance(name, o));
	}
	
	public static Object getPartialInstance(String name) {
		if(partialClasses.containsKey(name)) {
			try {
				return partialClasses.get(name).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Partial : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Paartial found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}
	
	public static Object getPartialInstance(String name, Object ... o) {
		if(partialClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for(int i = 0; i < o.length; i++) {
					types[i] = o.getClass();
				}
				Class<?> component = partialClasses.get(name);
				Constructor<?> constr =  component.getConstructor(types);
				Object res = constr.newInstance(o);
				return res;
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
				DIV alert = new DIV("Error while instantiating Partial : " + name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Partial found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public void setLocatableObject(String name, Object o) {
		if (pages.containsKey(name)) {
			pages.get(name).setO(o);
		} else {
			Locatable l = new Locatable("main", o);
			pages.put(name, l);
		}
	}

	private Session() {
	}

	public static Session create() {
		Session s = new Session();
		Iterator<String> iterator = locatablesClasses.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			Class<?> type = locatablesClasses.get(key);
			try {
				Object o = type.newInstance();
				s.addLocatable(o);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		return s;
	}

	public void addLocatable(Object o) {
		if (o.getClass().isAnnotationPresent(Location.class)) {
			Location l = o.getClass().getAnnotation(Location.class);
			Locatable lo = new Locatable(l.parent(), o);
			pages.put(l.path(), lo);
		} else {
			System.err.println("Object of Type "
					+ o.getClass().getCanonicalName() + " is not locatable!");
		}
	}

	public HTMLElement getPage(String name) {
		if (pages.containsKey(name)) {
			return HTMLCompiler.compileObjectToPage(pages.get(name).getO());
		}
		return HTMLCompiler.compileObjectToPage(getPageInstance("sep", name));
	}

	public String getPage(String name, Browsers browser) {
		if (pages.containsKey(name)) {
			return HTMLCompiler.compileObject(pages.get(name).getO(), browser);
		}
		return HTMLCompiler.compileObject(getPageInstance("sep", name), browser);
	}

	private class Locatable {
		private String parent;
		private Object o;

		public Locatable() {
			super();
		}

		public Locatable(String parent, Object o) {
			super();
			this.parent = parent;
			this.o = o;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public Object getO() {
			return o;
		}

		public void setO(Object o) {
			this.o = o;
		}

	}
}
