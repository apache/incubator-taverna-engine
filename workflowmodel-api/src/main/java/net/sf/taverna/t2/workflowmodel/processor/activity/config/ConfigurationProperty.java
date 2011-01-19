package net.sf.taverna.t2.workflowmodel.processor.activity.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigurationProperty {
	
	String name();
	
	String label() default "";

	String description() default "";

	boolean required() default true;
		
}
