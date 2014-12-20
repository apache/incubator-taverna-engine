package net.sf.taverna.t2.workflowmodel.processor.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigurationProperty {
	// TODO document this
	String name();

	String label() default "";

	String description() default "";

	boolean required() default true;

	OrderPolicy ordering() default OrderPolicy.DEFAULT;

	enum OrderPolicy {
		DEFAULT, NON_ORDERED
	}

	String uri() default "";
}
