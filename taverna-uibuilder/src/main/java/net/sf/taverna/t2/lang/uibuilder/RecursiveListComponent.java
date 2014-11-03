package net.sf.taverna.t2.lang.uibuilder;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.sf.taverna.t2.lang.uibuilder.UIBuilder;

/**
 * Handles lists where the elements in the list are beans with declared fields
 * in the UIBuilder configuration.
 * 
 * @author Tom Oinn
 * 
 */
public class RecursiveListComponent extends AbstractListComponent {

	private static final long serialVersionUID = -3760308074241973969L;

	@SuppressWarnings("unchecked")
	public RecursiveListComponent(String fieldName, List theList,
			Properties props, Map<String, Properties> fieldProps,
			Class<?> newItemClass, List<String> subFields, String parent)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		super(fieldName, theList, props, fieldProps, newItemClass, subFields,
				parent);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addNewItemToList(Object o) throws IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException {
		getUnderlyingList().add(0, o);
		updateListContents();
	}

	@Override
	protected void deleteItemAtIndex(int index)
			throws IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		getUnderlyingList().remove(index);
		updateListContents();
	}

	@Override
	protected List<JComponent> getListComponents()
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		List<JComponent> result = new ArrayList<JComponent>();
		for (Object target : getUnderlyingList()) {
			JPanel listItem = new JPanel();
			listItem.setOpaque(false);
			listItem.setLayout(new BoxLayout(listItem, BoxLayout.PAGE_AXIS));
			UIBuilder.buildEditor(target, getSubFields(), getFieldProperties(),
					listItem, getParentFieldName());
			result.add(listItem);
		}
		return result;
	}

	@Override
	public List<Component> getSeparatorComponents() {
		List<Component> result = new ArrayList<Component>();
		result.add(Box.createVerticalStrut(3));
		result.add(new JSeparator(SwingConstants.HORIZONTAL));
		result.add(Box.createVerticalStrut(3));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void itemMoved(int fromIndex, int toIndex)
			throws IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		Object toMove = getUnderlyingList().remove(fromIndex);
		getUnderlyingList().add(toIndex, toMove);
		updateListContents();

	}

}
