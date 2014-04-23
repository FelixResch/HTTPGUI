package at.resch.html.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Action;
import at.resch.html.annotations.Inject;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.annotations.Priority;
import at.resch.html.annotations.Restoreable;
import at.resch.html.components.handlers.HActionHandler;
import at.resch.html.components.handlers.HActionManager;
import at.resch.html.elements.BODY;
import at.resch.html.elements.BR;
import at.resch.html.elements.DIV;
import at.resch.html.elements.HTMLElement;
import at.resch.html.enums.Browsers;
import at.resch.html.events.ActionManager;
import at.resch.html.events.Updates;

public class Session implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -444532493137440777L;

	public static final Logger logger = Logger.getLogger(HTTPGUIServer.class);

	private final HashMap<String, Locatable> pages = new HashMap<>();
	private HashMap<String, Object> store = new HashMap<>();
	private ActionManager actionManager = new ActionManager();
	private HActionManager hActionManager = new HActionManager();

	private Updates updates;
	private String token;
	private BODY body;

	private static final HashMap<String, Class<?>> locatablesClasses = new HashMap<>();
	private static final HashMap<String, Class<?>> pageClasses = new HashMap<>();
	private static final HashMap<String, Class<?>> partialClasses = new HashMap<>();
	private static final HashMap<String, Session> sessions = new HashMap<>();

	public static void addLocatable(String name, Class<?> locatableClass) {
		if (locatableClass.isAnnotationPresent(Location.class)) {
			if (locatablesClasses.containsKey(name)) {
				if (locatableClass.isAnnotationPresent(Priority.class)) {
					Class<?> tmp = locatablesClasses.get(name);
					if (!tmp.isAnnotationPresent(Priority.class)) {
						logger.warn("Overwriting unprioritised Class "
								+ tmp.getCanonicalName() + " with "
								+ locatableClass.getCanonicalName());
						locatablesClasses.put(name, locatableClass);
					} else {
						Priority newClass, oldClass;
						newClass = locatableClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if (newClass.value().compareTo(oldClass.value()) > 0) {
							logger.info("Overwriting Class "
									+ tmp.getCanonicalName() + " with "
									+ locatableClass.getCanonicalName());
						} else {
							logger.warn("Omitting Class: "
									+ locatableClass.getCanonicalName());
						}
					}
				} else {
					logger.warn("Omitting Class: "
							+ locatableClass.getCanonicalName()
							+ "! Identifier "
							+ name
							+ " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
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

	public static HTMLElement getCompiledLocatable(String name, Object... o) {
		return HTMLCompiler.compileObjectToPage(getLocatableInstance(name, o));
	}

	public static Object getLocatableInstance(String name) {
		if (locatablesClasses.containsKey(name)) {
			try {
				Object res = locatablesClasses.get(name).newInstance();
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Locatable : "
						+ name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Locatable found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public static Object getLocatableInstance(String name, Object... o) {
		if (locatablesClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for (int i = 0; i < o.length; i++) {
					types[i] = o.getClass();
				}
				Class<?> component = locatablesClasses.get(name);
				Constructor<?> constr = component.getConstructor(types);
				Object res = constr.newInstance(o);
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | SecurityException
					| IllegalArgumentException | InvocationTargetException e) {
				DIV alert = new DIV("Error while instantiating Locatable : "
						+ name, new BR(), e.getMessage());
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
			if (pageClasses.containsKey(name)) {
				if (pageClass.isAnnotationPresent(Priority.class)) {
					Class<?> tmp = pageClasses.get(name);
					if (!tmp.isAnnotationPresent(Priority.class)) {
						logger.warn("Overwriting unprioritised Class"
								+ tmp.getCanonicalName() + " with "
								+ pageClass.getCanonicalName());
						pageClasses.put(name, pageClass);
					} else {
						Priority newClass, oldClass;
						newClass = pageClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if (newClass.value().compareTo(oldClass.value()) > 0) {
							logger.info("Overwriting Class "
									+ tmp.getCanonicalName() + " with "
									+ pageClass.getCanonicalName());
						} else {
							logger.warn("Omitting Class: "
									+ pageClass.getCanonicalName());
						}
					}
				} else {
					logger.warn("Omitting Class: "
							+ pageClass.getCanonicalName()
							+ "! Identifier "
							+ name
							+ " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
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

	public static HTMLElement getCompiledPage(String name, Object... o) {
		return HTMLCompiler.compileObjectToPage(getPageInstance(name, o));
	}

	public static Object getPageInstance(String name) {
		if (pageClasses.containsKey(name)) {
			try {
				Object res = pageClasses.get(name).newInstance();
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Page : " + name,
						new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Page found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public static Object getPageInstance(String name, Object... o) {
		if (pageClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for (int i = 0; i < o.length; i++) {
					types[i] = o[i].getClass();
				}
				Class<?> component = pageClasses.get(name);
				Constructor<?> constr = component.getConstructor(types);
				Object res = constr.newInstance(o);
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | SecurityException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				DIV alert = new DIV("Error while instantiating Page : " + name,
						new BR(), e.getMessage());
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
			if (partialClasses.containsKey(name)) {
				if (partialClass.isAnnotationPresent(Priority.class)) {
					Class<?> tmp = partialClasses.get(name);
					if (!tmp.isAnnotationPresent(Priority.class)) {
						logger.warn("Overwriting unprioritised Class"
								+ tmp.getCanonicalName() + " with "
								+ partialClass.getCanonicalName());
						partialClasses.put(name, partialClass);
					} else {
						Priority newClass, oldClass;
						newClass = partialClass.getAnnotation(Priority.class);
						oldClass = tmp.getAnnotation(Priority.class);
						if (newClass.value().compareTo(oldClass.value()) > 0) {
							logger.info("Overwriting Class "
									+ tmp.getCanonicalName() + " with "
									+ partialClass.getCanonicalName());
						} else {
							logger.warn("Omitting Class: "
									+ partialClass.getCanonicalName());
						}
					}
				} else {
					logger.warn("Omitting Class: "
							+ partialClass.getCanonicalName()
							+ "! Identifier "
							+ name
							+ " already mapped.\n(Use at.resch.html.annotations.Priority to define overwriting behaviour)");
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

	public static HTMLElement getCompiledPartial(String name, Object... o) {
		return HTMLCompiler.compileObjectToPage(getPartialInstance(name, o));
	}

	public static Object getPartialInstance(String name) {
		if (partialClasses.containsKey(name)) {
			try {
				Object res = partialClasses.get(name).newInstance();
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException e) {
				DIV alert = new DIV("Error while instantiating Partial : "
						+ name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Paartial found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}

	public static Object getPartialInstance(String name, Object... o) {
		if (partialClasses.containsKey(name)) {
			try {
				Class<?>[] types = new Class<?>[o.length];
				for (int i = 0; i < o.length; i++) {
					types[i] = o.getClass();
				}
				Class<?> component = partialClasses.get(name);
				Constructor<?> constr = component.getConstructor(types);
				Object res = constr.newInstance(o);
				performInjection(res);
				return res;
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | SecurityException
					| IllegalArgumentException | InvocationTargetException e) {
				DIV alert = new DIV("Error while instantiating Partial : "
						+ name, new BR(), e.getMessage());
				alert.setStyleClass("alert");
				return alert;
			}
		} else {
			DIV alert = new DIV("No such Partial found: " + name);
			alert.setStyleClass("alert");
			return alert;
		}
	}
	
	public static void performInjection(Object o) {
		Session current = getCurrent();
		Field[] injectionTargets = o.getClass().getFields();
		//System.out.println("Injecting into object of type: " + o.getClass().getCanonicalName());
		for (Field field : injectionTargets) {
			//System.out.print("\t" + field.getName());
			if(field.isAnnotationPresent(Inject.class)) {
				Inject i = field.getAnnotation(Inject.class);
				//System.out.print(" (" + i.value() + ")");
				if(current.get(i.value()) != null) {
					try {
						//System.out.print(" => " + current.get(i.value()));
						field.getType().cast(current.get(i.value()));
						field.set(o, current.get(i.value()));
					} catch (Exception e) {
						logger.error("Couldn't inject into "
								+ field.getDeclaringClass().getCanonicalName()
								+ "." + field.getName());
					}
				}
			}
			//System.out.println();
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

	public static HashMap<String, Session> getSessions() {
		return sessions;
	}

	public long getLastAction() {
		return lastAction;
	}

	private long lastAction = System.currentTimeMillis();

	private void lastAction() {
		lastAction = System.currentTimeMillis();
	}

	public static boolean exists() {
		return sessions.containsKey(Thread.currentThread().getName());
	}

	public static Session getCurrent() {
		if (exists()) {
			sessions.get(Thread.currentThread().getName()).lastAction();
			return sessions.get(Thread.currentThread().getName());
		}
		try {
			return create();
		} catch (SessionRestoredException e) {
			logger.fatal("Fatal Server Error in Session. Server will shut this session down to avoid Data Loss");
			sessions.remove(Thread.currentThread().getName());
			Thread.currentThread().interrupt();
			return null;
		}
	}

	public static void prepareForAction() {
		getCurrent().updates = new Updates();
		getCurrent().store("message.list", new ArrayList<HTMLElement>());
	}

	public Updates getUpdates() {
		return updates;
	}

	public HashMap<String, Object> getStore() {
		return store;
	}

	public static Session create() throws SessionRestoredException {
		return create(null);
	}

	public static Session create(String token) throws SessionRestoredException {
		if (sessions.containsKey(Thread.currentThread().getName())) {
			return sessions.get(Thread.currentThread().getName());
		}
		Session s = new Session();
		boolean restored = false;
		if (token != null) {
			File f = new File("persistence/session/"
					+ Thread.currentThread().getName() + "/" + token);
			if (f.exists()) {
				File[] files = f.listFiles();
				File latest = files[0];
				for (File file : files) {
					if (file.lastModified() > latest.lastModified())
						latest = file;
				}
				try {
					ObjectInputStream ois = new ObjectInputStream(
							new FileInputStream(latest));
					s.store = (HashMap<String, Object>) ois.readObject();
					ois.close();
				} catch (IOException e) {
					logger.warn("Failed to Restore Session. Unknown Error", e);
				} catch (ClassNotFoundException e) {
					logger.warn(
							"Failed to Restore Session. Unknow Class, check for Patches for your application",
							e);
				} catch (Exception e) {
					logger.warn("Failed to Restore Session. Unknown Error", e);
				}
				s.token = (String) s.get("session.token");
				if (s.token == null) {
					s.token = new BigInteger(130, new Random()).toString(32)
							.substring(0, 15);
					s.store("session.token", s.token);
				}
				logger.info("Restoring Session for: "
						+ Thread.currentThread().getName());
				restored = true;
			} else {
				logger.info("Creating new Session for: "
						+ Thread.currentThread().getName());
				s.token = new BigInteger(130, new Random()).toString(32);
				s.store("session.token", s.token);
			}
		} else {
			logger.info("Creating new Session for: "
					+ Thread.currentThread().getName());
			s.token = new BigInteger(130, new Random()).toString(32);
			s.store("session.token", s.token);
		}
		if (token == null || !(s.token.equals(token))) {
			logger.warn("Tokens don't match");
		}
		s.store("client.address", Thread.currentThread().getName());
		Iterator<String> iterator = locatablesClasses.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Class<?> type = locatablesClasses.get(key);
			try {
				Object o = type.newInstance();
				s.addLocatable(o);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		ActionManager actionManager = new ActionManager();
		Reflections reflections = new Reflections("",
				new MethodAnnotationsScanner());
		Set<Method> methods = reflections.getMethodsAnnotatedWith(Action.class);
		// logger.info("Found " + methods.size() + " Actions");
		s.actionManager = actionManager;
		for (Method m : methods) {
			try {
				Action a = m.getAnnotation(Action.class);
				Class<?> class1 = m.getDeclaringClass();
				Object instance = class1.newInstance();
				at.resch.html.events.Action action = new at.resch.html.events.Action(
						a.name(), a.args(), m, instance);
				actionManager.addAction(action);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		HActionManager hActionManager = new HActionManager();
		methods = reflections.getMethodsAnnotatedWith(HActionHandler.class);
		s.sethActionManager(hActionManager);
		for(Method m : methods) {
			HActionHandler hac = m.getAnnotation(HActionHandler.class);
			hActionManager.addHAction(hac, m);
		}
		sessions.put(Thread.currentThread().getName(), s);
		if (restored)
			throw new SessionRestoredException(
					"Session was restored. Please reload the Page to allow the Server to reallocate your Session");
		return s;
	}

	public void addLocatable(Object o) {
		lastAction();
		if (o.getClass().isAnnotationPresent(Location.class)) {
			Location l = o.getClass().getAnnotation(Location.class);
			Locatable lo = new Locatable(l.parent(), o);
			pages.put(l.path(), lo);
		} else {
			System.err.println("Object of Type "
					+ o.getClass().getCanonicalName() + " is not locatable!");
			logger.warn("Object of Type " + o.getClass().getCanonicalName()
					+ " is not locatable!");
		}
	}

	public HTMLElement getPage(String name) {
		lastAction();
		if (pages.containsKey(name)) {
			performInjection(pages.get(name).getO());
			return HTMLCompiler.compileObjectToPage(pages.get(name).getO());
		}
		return HTMLCompiler.compileObjectToPage(getPageInstance("sep", name));
	}

	public String getPage(String name, Browsers browser) {
		lastAction();
		if (pages.containsKey(name)) {
			performInjection(pages.get(name).getO());
			return HTMLCompiler.compileObject(pages.get(name).getO(), browser);
		}
		return HTMLCompiler
				.compileObject(getPageInstance("sep", name), browser);
	}

	public void store(String name, Object object) {
		lastAction();
		if (!(object instanceof Serializable) && !(object.getClass().isAnnotationPresent(Restoreable.class)))
			logger.warn("Stored Object of Type \""
					+ object.getClass().getCanonicalName()
					+ "\" is not Serializeable");
		else
			store.put(name, object);
	}

	public Object get(String name) {
		lastAction();
		if (store.containsKey(name))
			return store.get(name);
		return null;
	}

	public ActionManager getActionManager() {
		lastAction();
		return actionManager;
	}

	private boolean persisted = false;

	public void markPersisted() {
		this.persisted = true;
	}

	public static void cleanUp() {
		Iterator<String> iterator = sessions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Session s = sessions.get(key);
			if (s.persisted)
				iterator.remove();
		}
	}

	public BODY getBody() {
		return body;
	}

	public void setBody(BODY body) {
		this.body = body;
	}

	public HActionManager gethActionManager() {
		return hActionManager;
	}

	public void sethActionManager(HActionManager hActionManager) {
		this.hActionManager = hActionManager;
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
