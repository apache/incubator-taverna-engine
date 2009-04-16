package net.sf.taverna.t2.lang.uibuilder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Static methods to build bean editor UIs through reflection and (minimal)
 * configuration
 * 
 * @author Tom Oinn
 * 
 */
public abstract class UIBuilder {

	/**
	 * Build an editor component for the specified target, the configuration
	 * string determines which properties are exposed to the UI and whether to
	 * drill into nested collections and composite bean types
	 * 
	 * @param target
	 * @param configuration
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	public static JPanel buildEditor(Object target, String configuration)
			throws UIConstructionException {
		String[] fields = configuration.split(";");
		return buildEditor(target, fields);
	}

	public static JPanel buildEditor(Object target, String[] fields)
			throws UIConstructionException {
		// Now go through the configuration...
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		JPanel contents = new JPanel();
		contents.setOpaque(false);
		contents.setLayout(new BoxLayout(contents, BoxLayout.PAGE_AXIS));
		List<String> fieldNames = new ArrayList<String>();
		Map<String, Properties> fieldProps = new HashMap<String, Properties>();
		for (String field : fields) {
			String fieldName = field.split(":")[0];
			fieldNames.add(fieldName);
			if (field.split(":").length > 1) {
				String propertiesString = field.split(":")[1];
				String[] props = propertiesString.split(",");
				if (props.length == 0) {
					props = new String[] { propertiesString };
				}
				Properties properties = new Properties();
				for (String prop : props) {
					if (prop.contains("=")) {
						String[] p = prop.split("=");
						properties.put(p[0], p[1]);
					} else {
						properties.put(prop, "true");
					}
				}
				fieldProps.put(fieldName, properties);
			}
		}
		try {
			buildEditor(target, fieldNames, fieldProps, contents, "");
		} catch (Exception ex) {
			throw new UIConstructionException(
					"Unable to construct UI from POJO", ex);
		}
		result.add(contents, BorderLayout.NORTH);
		result.add(Box.createVerticalGlue(), BorderLayout.CENTER);
		return result;
	}

	@SuppressWarnings("unchecked")
	static void buildEditor(Object target, List<String> fieldNames,
			Map<String, Properties> fieldProps, Container contents,
			String parent) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException {

		// Get all top level fields to render in this pass
		List<String> activeFields = new ArrayList<String>();
		for (String field : fieldNames) {
			if (!field.contains(".")) {
				activeFields.add(field);
			}
		}
		// For each field generate the appropriate component
		for (String field : activeFields) {

			// Fetch the properties block for this field
			Properties props = getProperties(field, parent, fieldProps);

			// First check whether there are any subfields for this field, in
			// which case we need to treat it differently
			boolean simpleField = true;
			for (String subField : fieldNames) {
				if (!subField.equals(field) && subField.startsWith(field + ".")) {
					simpleField = false;
					break;
				}
			}
			List<String> subFields = new ArrayList<String>();
			// Create filtered list of the field names
			// to ensure that this is now the top level
			// field and recurse
			String newParent = field;
			if (!parent.equals("")) {
				newParent = parent + "." + field;
			}
			for (String f : fieldNames) {
				if (f.startsWith(field + ".")) {
					subFields.add(f.substring(field.length() + 1));
				}
			}

			// Secondly check whether this is a list
			boolean listField = false;
			Class<?> fieldType = BeanComponent.getPropertyType(target, field);
			if (List.class.isAssignableFrom(fieldType)) {
				listField = true;
			}

			// Now handle the four possible cases. If a non-list non-compound
			// field we have a terminator and can obtain an appropriate subclass
			// of BeanComponent to render it
			if (!listField) {
				// If a non-list non-compound field we have a terminator and can
				// obtain an appropriate subclass of BeanComponent to render it
				if (simpleField) {
					if (fieldType.isEnum()) {
						contents
								.add(new BeanEnumComboBox(target, field, props));
					} else if (Boolean.class.isAssignableFrom(fieldType)
							|| boolean.class.isAssignableFrom(fieldType)) {
						contents.add(new BeanCheckBox(target, field, props));
					} else {
						if (props.get("type") != null) {
							if (props.get("type").equals("textarea")) {
								BeanTextArea bta = new BeanTextArea(target,
										field, props);
								contents.add(bta);
							} else {
								contents.add(new BeanTextField(target, field,
										props));
							}
						} else {
							contents
									.add(new BeanTextField(target, field, props));
						}
					}
				} else {
					Object value = BeanComponent.findMethodWithPrefix("get",
							target, field).invoke(target);
					if (value != null) {
						String displayName = field;
						if (props.containsKey("name")) {
							displayName = props.getProperty("name");
						}
						JPanel itemContainer = new JPanel();
						itemContainer.setOpaque(false);
						itemContainer.setBorder(BorderFactory
								.createTitledBorder(displayName));
						itemContainer.setLayout(new BoxLayout(itemContainer,
								BoxLayout.PAGE_AXIS));
						buildEditor(value, subFields, fieldProps,
								itemContainer, newParent);
						contents.add(itemContainer);
					}
				}
			} else {
				// Handle the case where this is a simple field (i.e. no
				// sub-fields defined) but is a list type. In this case we need
				// to build an appropriate wrapper list panel around this
				// returned list.
				List value = (List) BeanComponent.findMethodWithPrefix("get",
						target, field).invoke(target);
				// If the 'new' property is defined then fetch the Class object
				// to be used to instantiate new list items
				Class<?> newItemClass = null;
				if (props.containsKey("new")) {
					String newItemClassName = props.getProperty("new");
					newItemClass = target.getClass().getClassLoader()
							.loadClass(newItemClassName);
				}
				if (value != null) {
					if (simpleField) {
						contents.add(new WrappedListComponent(field, value,
								props, fieldProps, newItemClass, subFields,
								newParent));
						// contents.add(buildWrappedList(field, value, props,
						// newItemClass));
					} else {
						contents.add(new RecursiveListComponent(field, value,
								props, fieldProps, newItemClass, subFields,
								newParent));
						// contents.add(buildList(field, value, props,
						// fieldProps,
						// newItemClass, subFields, newParent));
					}
				}
			}
		}
		// Finally align any labels where appropriate
		Alignment.alignInContainer(contents);
	}

	private static Properties getProperties(String field, String parent,
			Map<String, Properties> props) {
		String fullName = parent;
		if (!parent.equals("")) {
			fullName = fullName + ".";
		}
		fullName = fullName + field;
		if (props.containsKey(fullName)) {
			return props.get(fullName);
		} else {
			return new Properties();
		}
	}

}
