package at.resch.html.components.handlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HActionHandler {
	public String name();
	public Class<? extends HAction> eventType(); 
	public String[] args() default {"hidden"};
}
