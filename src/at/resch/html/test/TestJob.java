package at.resch.html.test;

import at.resch.html.extensions.JobProgress;
import at.resch.html.extensions.annotations.Job;
import at.resch.html.extensions.annotations.ProgressIdentifier;
import at.resch.html.extensions.annotations.WorkMethod;
import at.resch.html.extensions.enums.JobType;

@Job(jobType=JobType.PARALLEL, progress=true, name="TestJob")
public class TestJob {
	
	private double percent;
	
	@WorkMethod
	public void doYourWork() {
		while (percent <= 100) {
			percent++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@ProgressIdentifier
	public JobProgress progress() {
		return new JobProgress("Doing Work...", percent);
	}
}
