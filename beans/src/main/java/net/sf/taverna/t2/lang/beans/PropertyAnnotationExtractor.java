package net.sf.taverna.t2.lang.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for extracting {@link PropertyDescriptor}s from a class which
 * methods have been described using {@link PropertyAnnotation}.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class PropertyAnnotationExtractor {

	protected static Pattern methodPattern = Pattern
			.compile("(get|is|set)(.+)");

	protected WeakHashMap<Class<?>, List<Method>> allMethodsCache = new WeakHashMap<Class<?>, List<Method>>();

	protected WeakHashMap<Class<?>, PropertyDescriptor[]> propertyDescriptorsCache = new WeakHashMap<Class<?>, PropertyDescriptor[]>();

	/**
	 * Find PropertyDescriptors for the given bean class based on descriptions
	 * using {@link PropertyAnnotation}s.
	 * <p>
	 * Annotations will be inherited from interfaces and superclasses.
	 * 
	 * @param beanClass
	 * @return Array of {@link PropertyDescriptor}
	 */
	public PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
		PropertyDescriptor[] cached = propertyDescriptorsCache.get(beanClass);
		if (cached != null) {
			return cached;
		}

		Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();

		for (Method method : allMethods(beanClass)) {
			PropertyAnnotation annotation = method
					.getAnnotation(PropertyAnnotation.class);
			Matcher methodMatcher = methodPattern.matcher(method.getName());
			if (!methodMatcher.matches() && annotation == null) {
				continue;
			}
			String name = PropertyAnnotation.DEFAULT;
			if (annotation != null) {
				annotation.name();
			}
			if (name.equals(PropertyAnnotation.DEFAULT)) {
				name = methodMatcher.group(2);
				if (name.length() < 1) {
					continue;
				}
				// decapitalize first letter
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
			}
			Method writeMethod = null;
			Method readMethod = null;
			if (methodMatcher.group(1).equals("set")) {
				writeMethod = method;
				if (writeMethod.getParameterTypes().length != 1) {
					continue;
				}
			} else {
				readMethod = method;
				if (readMethod.getParameterTypes().length != 0) {
					continue;
				}
			}

			PropertyDescriptor descriptor = descriptors.get(name);
			try {
				if (descriptor == null) {
					descriptor = new PropertyDescriptor(name, readMethod,
							writeMethod);
					descriptors.put(name, descriptor);
				}
				// Set the one we just found
				if (readMethod != null) {
					descriptor.setReadMethod(readMethod);
				}
				if (writeMethod != null) {
					descriptor.setWriteMethod(writeMethod);
				}
			} catch (IntrospectionException ex) {
				throw new RuntimeException("Can't inspect property " + name
						+ " using method " + method, ex);
			}
			if (annotation != null) {
				descriptor.setExpert(annotation.expert());
				descriptor.setHidden(annotation.hidden());
				descriptor.setPreferred(annotation.preferred());
				if (!annotation.displayName()
						.equals(PropertyAnnotation.DEFAULT)) {
					descriptor.setDisplayName(annotation.displayName());
				}
				if (!annotation.shortDescription().equals(
						PropertyAnnotation.DEFAULT)) {
					descriptor.setShortDescription(annotation
							.shortDescription());
				}
			}
		}
		cached = descriptors.values().toArray(
				new PropertyDescriptor[descriptors.size()]);
		propertyDescriptorsCache.put(beanClass, cached);
		return cached;
	}

	/**
	 * Find all {@link Method}s defined in the class, all its superclasses and
	 * interfaces. This might include methods that override each other.
	 * <p>
	 * The list contains first the methods from each of the class's interfaces
	 * (and the methods they inherit from their interfaces), then recurses for
	 * the subclass of this class (including any additional interfaces used in
	 * the superclasses), before finally adding methods declared in the given
	 * class.
	 * <p>
	 * This can be useful to find annotations given to methods that have been
	 * overridden in subclasses.
	 * 
	 * @param theClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Method> allMethods(Class<?> theClass) {
		List<Method> methods = allMethodsCache.get(theClass);
		if (methods == null) {
			methods = new ArrayList<Method>();
			allMethods(theClass, new HashSet<Class>(), methods);
			allMethodsCache.put(theClass, methods);
		}
		return methods;
	}

	@SuppressWarnings("unchecked")
	protected void allMethods(Class<?> theClass, Set<Class> visitedClasses,
			List<Method> foundMethods) {
		if (theClass == null || theClass == Object.class
				|| theClass == Class.class || !visitedClasses.add(theClass)) {
			// Top class or already visted
			return;
		}
		// Let's first dig down into our interfaces
		for (Class anInterface : theClass.getInterfaces()) {
			allMethods(anInterface, visitedClasses, foundMethods);
		}
		// And our superclasses
		allMethods(theClass.getSuperclass(), visitedClasses, foundMethods);
		// Before we find any methods only declared in this class
		// (parent methods are already earlier in the list -
		// note that the new methods might override earlier methods)
		for (Method method : theClass.getDeclaredMethods()) {
			int methodModifiers = method.getModifiers();
			if (!Modifier.isPublic(methodModifiers)
					|| Modifier.isStatic(methodModifiers)) {
				continue;
			}
			assert !foundMethods.contains(method) : "Method discovered twice: "
					+ method;
			foundMethods.add(method);
		}
	}
}
