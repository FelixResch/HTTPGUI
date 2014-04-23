package at.resch.html.extensions;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.reflections.Reflections;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.CustomHandler;
import at.resch.html.annotations.Restoreable;
import at.resch.html.extensions.annotations.Job;
import at.resch.html.extensions.annotations.ProgressIdentifier;
import at.resch.html.extensions.annotations.WorkMethod;
import at.resch.html.extensions.enums.JobType;
import at.resch.html.server.Session;

@Restoreable
@CustomHandler("/cus/jobs/*")
public class TimeJobs implements HttpRequestHandler {

	private ArrayList<JobInfo> secondJobs;
	private ArrayList<JobInfo> minuteJobs;
	private ArrayList<JobInfo> hourJobs;
	private ArrayList<JobInfo> parallelJobs;
	private ArrayList<JobInfo> progressJobs;
	
	private SecondJobsExecutor secondExecutor;
	private MinuteJobsExecutor minuteExecutor;
	private HourJobsExecutor hourExecutor;
	
	public TimeJobs() {
		if(Session.getCurrent() != null)
			Session.getCurrent().store("job.service", this);
		secondJobs = new ArrayList<>();
		minuteJobs = new ArrayList<>();
		hourJobs = new ArrayList<>();
		parallelJobs = new ArrayList<>();
		progressJobs = new ArrayList<>();
		Reflections reflections = new Reflections();
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Job.class);
		for (Class<?> c : classes) {
			if(c.isAnnotationPresent(Job.class)) {
				Job job = c.getAnnotation(Job.class);
				JobInfo info = new JobInfo();
				info.isProgress = job.progress();
				info.jobType = job.jobType();
				info.name = job.name();
				try {
					Object o = c.newInstance();
					info.O = o;
					for (Method m : c.getMethods()) {
						if(m.isAnnotationPresent(ProgressIdentifier.class)) {
							info.progress = m;
						} else if (m.isAnnotationPresent(WorkMethod.class)) {
							info.work = m;
						}
					}
					if(info.work == null || (job.progress() && info.progress == null)) {
						Session.logger.error("Couldn't create Job " + info.name + "! No Work or Progress Method defined!");
						continue;
					}
				} catch (Exception e) {
					Session.logger.error("Couldn't create Job " + info.name);
					continue;
				}
				switch(info.jobType) {
				case SECOND:
					secondJobs.add(info);
					break;
				case MINUTE:
					minuteJobs.add(info);
					break;
				case HOUR:
					hourJobs.add(info);
					break;
				}
			}
		}
		startExecutors();
	}
	
	private void startExecutors() {
		secondExecutor = new SecondJobsExecutor();
		secondExecutor.start();
		minuteExecutor = new MinuteJobsExecutor();
		minuteExecutor.start();
		hourExecutor = new HourJobsExecutor();
		hourExecutor.start();
		for (JobInfo job : parallelJobs) {
			new ParallelJobExecutor(job).start();
		}
		Session.logger.info("All Jobs up and running!");
	}
	
	public void newParallelJob(Object o) {
		if(o.getClass().isAnnotationPresent(Job.class)) {
			Job job = o.getClass().getAnnotation(Job.class);
			JobInfo info = new JobInfo();
			if(job.jobType() != JobType.PARALLEL)
				return;
			info.isProgress = job.progress();
			info.jobType = job.jobType();
			info.name = job.name();
			info.O = o;
			for (Method m : o.getClass().getMethods()) {
				if(m.isAnnotationPresent(ProgressIdentifier.class) && m.getReturnType() == JobProgress.class) {
					info.progress = m;
				} else if (m.isAnnotationPresent(WorkMethod.class)) {
					info.work = m;
				}
			}
			if(info.work == null && info.progress == null) {
				Session.logger.error("Couldn't create Job " + info.name + "! No Work or Progress Method defined!");
				return;
			}
			parallelJobs.add(info);
			if(info.isProgress)
				progressJobs.add(info);
			new ParallelJobExecutor(info).start();
		}
	}
	
	private static class JobInfo {
		public Object O;
		public JobType jobType;
		public boolean isProgress;
		public String name;
		public Method progress;
		public Method work;
	}
	
	private class SecondJobsExecutor extends Thread {
		
		public SecondJobsExecutor() {
			setName("Second Job Executor");
		}
		
		public void run() {
			Session.logger.debug("Launching Second Jobs");
			while(true) {
				try {
					long begin = System.currentTimeMillis();
					Iterator<JobInfo> iterator = secondJobs.iterator();
					while (iterator.hasNext()) {
						JobInfo job = iterator.next();
						try {
							job.work.invoke(job.O);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							Session.logger.fatal("Job " + job.name + " had a fatal Error. Removing from TaskList!", e);
							iterator.remove();
						}
					}
					long end = System.currentTimeMillis();
					if(end - begin > 1000)
						Session.logger.warn("Second Jobs Timed out! Probably the Server is overloaded");
					else
						Thread.sleep(1000 - (end - begin));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class MinuteJobsExecutor extends Thread {
		
		public MinuteJobsExecutor() {
			setName("Minute Job Executor");
		}
		
		public void run() {
			Session.logger.debug("Launching Minute Jobs");
			while(true) {
				try {
					long begin = System.currentTimeMillis();
					Iterator<JobInfo> iterator = minuteJobs.iterator();
					while (iterator.hasNext()) {
						JobInfo job = iterator.next();
						try {
							job.work.invoke(job.O);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							Session.logger.fatal("Job " + job.name + " had a fatal Error. Removing from TaskList!", e);
							iterator.remove();
						}
					}
					long end = System.currentTimeMillis();
					if(end - begin > 60000)
						Session.logger.warn("Second Jobs Timed out! Probably the Server is overloaded");
					else
						Thread.sleep(60000 - (end - begin));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class HourJobsExecutor extends Thread {
		
		public HourJobsExecutor() {
			setName("Hour Job Executor");
		}
		
		public void run() {
			Session.logger.debug("Launching Hour Jobs");
			while(true) {
				try {
					long begin = System.currentTimeMillis();
					Iterator<JobInfo> iterator = hourJobs.iterator();
					while (iterator.hasNext()) {
						JobInfo job = iterator.next();
						try {
							job.work.invoke(job.O);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							Session.logger.fatal("Job " + job.name + " had a fatal Error. Removing from TaskList!", e);
							iterator.remove();
						}
					}
					long end = System.currentTimeMillis();
					if(end - begin > 3600000)
						Session.logger.warn("Second Jobs Timed out! Probably the Server is overloaded");
					else
						Thread.sleep(3600000 - (end - begin));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ParallelJobExecutor extends Thread {
		
		private JobInfo info;
		
		public ParallelJobExecutor (JobInfo info) {
			setName(info.name);
			this.info = info;
		}
		
		public void run() {
			try {
				info.work.invoke(info.O);
				parallelJobs.remove(info);
				if(info.isProgress)
					progressJobs.remove(info);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				Session.logger.error("Job Failed", e);
			}
		}
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}
		String sse = "retry: 500\ndata:";
		for (JobInfo job : progressJobs) {
			try {
				JobProgress prog = (JobProgress) job.progress.invoke(job.O);
				String html = HTMLCompiler.compileObjectToPage(prog).renderHTML();
				html = html.replace("\n", "\ndata:");
				sse += html;
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				Session.logger.error("Couldn't request progress!", e);
			}
		}
		if(progressJobs.isEmpty())
			sse += "Currently no Jobs!";
		sse += "\n\n";
		response.setStatusCode(200);
		response.setEntity(new StringEntity(sse, ContentType.parse("text/event-stream")));
	}

}
