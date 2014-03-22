package at.resch.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.resch.html.elements.DIV;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Content {

	public Class<?> parent() default DIV.class;
	public String styleClass () default "";
}
