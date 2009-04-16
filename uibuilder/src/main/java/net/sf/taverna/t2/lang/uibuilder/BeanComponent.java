package net.sf.taverna.t2.lang.uibuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Superclass of all bean component editors acting on a single (non collection)
 * property within a POJO. This handles bound properties, using reflection to
 * determine whether an appropriate addPropertyChangeListener method exists on
 * the target bean and registering a listener if so. It also registers an
 * ancestor listener to de-register this property listener if the component is
 * removed from the display heirarchy and to re-attach it if the component is
 * added. Implement updateComponent with the code to update the UI state from
 * the underlying object, this is called when a property change is received by
 * the listener (but not used for unbound properties).
 * <p>
 * This superclass also defines the name property:
 * <ul>
 * <li><code>name=SomeName</code> by default field labels use the field name
 * defined in the configuration, including any case used. If this property is
 * defined then the specified name is used instead</li>
 * </ul>
 * 
 * @author Tom Oinn
 */
public abstract class BeanComponent extends JPanel {

	private static final long serialVersionUID = -6044009506938335937L;
	protected Object target;
	protected String propertyName;
	protected Method getMethod, setMethod = null;
	protected boolean editable = true;
	protected Class<?> propertyType;
	protected static Color invalidColour = Color.red;
	protected static Color validColour = Color.black;
	protected static Color uneditedColour = Color.white;
	protected static Color editedColour = new Color(255, 245, 200);
	protected boolean currentValueValid = true;
	protected Object currentObjectValue = null;
	protected JLabel label;
	private boolean useLabel = false;
	protected static int height = 16;
	private Properties properties;
	private PropertyChangeListener propertyListener = null;

	/**
	 * Equivalent to BeanComponent(target, propertyName, true, props)
	 */
	public BeanComponent(Object target, String propertyName, Properties props)
			throws NoSuchMethodException {
		this(target, propertyName, true, props);
	}

	/**
	 * Superclass constructor for BeanComponent instances.
	 * 
	 * @param target
	 *            the object containing the property this component acts as a
	 *            view and controller for
	 * @param propertyName
	 *            name of the property in the target object
	 * @param useLabel
	 *            whether to show the label component (we set this to false for
	 *            wrapped lists, for example)
	 * @param props
	 *            a component specific properties object, passing in any
	 *            properties defined in the configuration passed to UIBuilder
	 * @throws NoSuchMethodException
	 *             if the appropriate get method for the named property can't be
	 *             found
	 */
	public BeanComponent(Object target, String propertyName, boolean useLabel,
			Properties props) throws NoSuchMethodException {
		super();
		setOpaque(false);
		// Find methods
		this.properties = props;
		this.useLabel = useLabel;
		this.target = target;
		this.propertyName = propertyName;

		// If the target implements property change support then we can attach a
		// listener to update the UI if the bound property changes. This
		// listener is attached and detached in response to an ancestor listener
		// so the bound property is only monitored when the component is visible
		// in the UI.
		try {
			target.getClass().getMethod("addPropertyChangeListener",
					String.class, PropertyChangeListener.class);
			setUpPropertyListener();
			addAncestorListener(new AncestorListener() {
				public void ancestorAdded(AncestorEvent event) {
					setUpPropertyListener();
				}

				public void ancestorMoved(AncestorEvent event) {
					// Ignore
				}

				public void ancestorRemoved(AncestorEvent event) {
					tearDownPropertyListener();
				}
			});
		} catch (NoSuchMethodException nsme) {
			// Means we don't have a bound property listener
		}

		getMethod = findMethodWithPrefix("get");
		try {
			setMethod = findMethodWithPrefix("set");
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
			// No set method, mark UI as uneditable
			editable = false;
		}
		propertyType = getPropertyType(target, propertyName);
	}

	/**
	 * Attempts to create and bind a property change listener to the target
	 * bean, failing silently if the bean doesn't implement the appropriate
	 * methods
	 */
	private synchronized void setUpPropertyListener() {
		if (propertyListener == null) {
			Method addListener = null;
			try {
				addListener = target.getClass().getMethod(
						"addPropertyChangeListener", String.class,
						PropertyChangeListener.class);
			} catch (NoSuchMethodException nsme) {
				return;
			}
			propertyListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					Object newValue = evt.getNewValue();
					if (currentObjectValue == null
							|| (newValue != currentObjectValue && !newValue
									.equals(currentObjectValue))) {
						// System.out.println("Property change, source was "+evt.getSource());
						if (SwingUtilities.isEventDispatchThread()) {
							updateComponent();
						} else {
							// try {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									updateComponent();
								}
							});
							// } catch (InterruptedException e) {
							// e.printStackTrace();
							// } catch (InvocationTargetException e) {
							// e.printStackTrace();
							// }
						}
					}
				}
			};
			try {
				// System.out.println(propertyName + " : " + propertyListener);
				addListener.invoke(target, propertyName, propertyListener);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * If the property listener for bound properties exists then this method
	 * unregisters it from the target, attempting to use a variety of the
	 * standard method names to do so
	 */
	private synchronized void tearDownPropertyListener() {
		if (propertyListener == null) {
			return;
		}
		try {
			Method removeListener = null;
			try {
				removeListener = target.getClass().getMethod(
						"removePropertyChangeListener", String.class,
						PropertyChangeListener.class);
				removeListener.invoke(target, propertyName, propertyListener);
			} catch (NoSuchMethodException nsme) {
				try {
					removeListener = target.getClass().getMethod(
							"removePropertyChangeListener",
							PropertyChangeListener.class);
					removeListener.invoke(target, propertyListener);
				} catch (NoSuchMethodException nsme2) {
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		propertyListener = null;
	}

	/**
	 * Called by the bound property listener, implementing components must
	 * update their UI state in this method to match the value of the underlying
	 * component. This method is always called in the AWT dispatch thread so
	 * implementations can safely modify the state of UI components without
	 * worrying about swing thread handling
	 */
	protected abstract void updateComponent();

	/**
	 * Adds the label to the component if labels are enabled
	 */
	protected void addLabel() {
		if (useLabel) {
			String labelName = propertyName;
			if (getProperties().containsKey("name")) {
				labelName = getProperties().getProperty("name");
			}
			label = new JLabel(labelName);
			label.setOpaque(false);
			label.setPreferredSize(new Dimension(
					label.getPreferredSize().width, height));
			JPanel labelPanel = new JPanel();
			labelPanel.setOpaque(false);
			labelPanel.add(label);
			labelPanel.add(Box.createHorizontalStrut(5));
			add(labelPanel, BorderLayout.WEST);
		}
	}

	/**
	 * Return the type of the property on the target object, trying to locate a
	 * matching 'get' method for the supplied property name and returning the
	 * class of that method's return type.
	 * <p>
	 * Attempts to, in order :
	 * <ol>
	 * <li>Call the method and get the concrete type of the returned value</li>
	 * <li>Get the declared return type of the get method</li>
	 * </ol>
	 * 
	 * @param target
	 * @param propertyName
	 * @return
	 * @throws NoSuchMethodException
	 *             if the get method can't be found for the given property name
	 *             on the target
	 */
	public static Class<?> getPropertyType(Object target, String propertyName)
			throws NoSuchMethodException {
		Method getMethod = findMethodWithPrefix("get", target, propertyName);
		try {
			Object value = getMethod.invoke(target);
			if (value != null) {
				return value.getClass();
			}
		} catch (InvocationTargetException ite) {
			//
		} catch (IllegalArgumentException e) {
			// 			
		} catch (IllegalAccessException e) {
			// 
		}
		// if (target instanceof ListHandler.ListItem) {
		// return ((ListHandler.ListItem) target).getTargetClass();
		// }
		return getMethod.getReturnType();
	}

	/**
	 * Searches for the specified method on the target object, throwing an
	 * exception if it can't be found. Searches for
	 * target.[prefix][propertyname]()
	 * 
	 * @throws NoSuchMethodException
	 */
	static Method findMethodWithPrefix(String prefix, Object target,
			String propertyName) throws NoSuchMethodException {
		for (Method m : target.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase(prefix + propertyName)) {
				return m;
			}
		}
		throw new NoSuchMethodException("Can't find method matching '" + prefix
				+ propertyName + "' in " + target.getClass().getCanonicalName());
	}

	/**
	 * Calls the static findMethodWithPrefix passing in the property name and
	 * the target assigned to this instance
	 * 
	 * @param prefix
	 *            a string prefix to use when finding the name, searches for
	 *            [prefix][propertyname]()
	 * @return a Method matching the query
	 * @throws NoSuchMethodException
	 *             if the method can't be found.
	 */
	protected final Method findMethodWithPrefix(String prefix)
			throws NoSuchMethodException {
		return findMethodWithPrefix(prefix, target, propertyName);
	}

	/**
	 * Returns the toString value of the current bean property, or the empty
	 * string if the get method returns null
	 */
	protected final String getPropertyAsString() {
		Object value = getProperty();
		if (value == null) {
			return "";
		}
		currentObjectValue = value;
		return value.toString();
	}

	/**
	 * Uses reflection to call the get[property name] method on the target bean
	 * and returns the result
	 * 
	 * @return current value of the bean property
	 */
	protected final Object getProperty() {
		try {
			Object value = getMethod.invoke(target);
			return value;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the property on the object to the current object value for this UI
	 * component, effectively pushing any changes into the underlying target
	 * bean
	 */
	protected final void setProperty() {
		if (currentValueValid && editable) {
			if (setMethod != null) {
				try {
					setMethod.invoke(target, currentObjectValue);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * If using labels (as determined by the useLabel property) this sets the
	 * preferred width of the label for this component
	 * 
	 * @param newWidth
	 *            new label width in pixels
	 */
	public void setLabelWidth(int newWidth) {
		if (useLabel) {
			label.setPreferredSize(new Dimension(newWidth, height));
		}
	}

	/**
	 * If using labels (as determined by the useLabel property) this returns the
	 * current preferred size of the label associated with this component
	 * 
	 * @return label width in pixels
	 */
	public int getLabelWidth() {
		if (useLabel) {
			return this.label.getPreferredSize().width;
		} else {
			return 0;
		}
	}

	/**
	 * If using the label then set its text (foreground) colour
	 * 
	 * @param colour
	 */
	public void setLabelColour(Color colour) {
		if (useLabel) {
			this.label.setForeground(colour);
		}
	}

	/**
	 * Get the properties object associated with this component. This is
	 * generated from the configuration passed to UIBuilder
	 * 
	 * @return a Properties object containing named properties applying to this
	 *         component in the UI
	 */
	public Properties getProperties() {
		return this.properties;
	}
}
