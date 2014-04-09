package at.resch.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.resch.html.enums.Mode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RenderMode {
	public Mode value() default Mode.SMOOTH;
}
