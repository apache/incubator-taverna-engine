package net.sf.taverna.t2.reference.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Applied to methods in Dao implementations which delete data in the backing
 * store.
 * 
 * @author Stuart Owen
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DeleteIdentifiedOperation {
}