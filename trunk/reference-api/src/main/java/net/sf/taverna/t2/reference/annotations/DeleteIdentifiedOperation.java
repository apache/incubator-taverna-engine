package net.sf.taverna.t2.reference.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to methods in Dao implementations which delete data in the
 * backing store.
 * 
 * @author Stuart Owen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeleteIdentifiedOperation {

	//

}