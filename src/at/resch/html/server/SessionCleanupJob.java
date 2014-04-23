package at.resch.html.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import at.resch.html.annotations.Restoreable;
import at.resch.html.extensions.annotations.Job;
import at.resch.html.extensions.annotations.WorkMethod;
import at.resch.html.extensions.enums.JobType;

@Job(name="Session Cleanup Job", jobType=JobType.SECOND)
public class SessionCleanupJob {

	public SessionCleanupJob() {
	}
	
	@WorkMethod
	public void doSomeWork() {
		long timestamp = System.currentTimeMillis();
		for(String key : Session.getSessions().keySet()) {
			Session s = Session.getSessions().get(key);
			if(key.equals("main"))
				continue;
			if(timestamp - s.getLastAction() > 1800000) {
				persitSession(s, key, timestamp);
			}
		}
		Session.cleanUp();
	}

	public static void persitSession(Session s, String key, long timestamp) {
		Session.logger.info("Persisting Session " + key);
		File f = new File("persistence/session/" + key + "/" + s.get("session.token") + "/" + timestamp + ".per");
		f.getParentFile().mkdirs();
		HashMap<String, Object> store = s.getStore();
		Iterator<String> iterator_ = store.keySet().iterator();
		{
			while(iterator_.hasNext()) {
				String key_ = iterator_.next();
				Class<?> c = store.get(key_).getClass();
				if(c.isAnnotationPresent(Restoreable.class) || store.get(key_) instanceof Serializable)
					iterator_.remove();
			}
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(s.getStore());
			oos.close();
			s.markPersisted();
		} catch (NotSerializableException e) {
			Session.logger.fatal("Persistence not possible for Session: " + key);
			Iterator<String> iterator = store.keySet().iterator();
			Session.logger.debug("Store Persistence Check: " + (store instanceof Serializable ? "[ OK ]" : "[FAIL]"));
			while(iterator.hasNext()) {
				String out = "";
				String key_ = iterator.next();
				out += key_ + " => ";
				out += s.get(key_).toString();
				out += " Persistence Check " + (s.get(key_) instanceof Serializable ? "[ OK ]" : "[FAIL]");
				Session.logger.debug(out);
			}
		} catch (IOException e) {
			Session.logger.fatal("Persistence Failed for Session: " + key, e);
		}
	}
	
}
