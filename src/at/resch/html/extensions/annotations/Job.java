package at.resch.html.extensions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.resch.html.extensions.enums.JobType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Job {
	public String name();
	public JobType jobType() default JobType.PARALLEL;
	public boolean progress() default false;
}
