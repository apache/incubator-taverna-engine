package net.sf.taverna.t2.lang.uibuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * The list handler is used to allow the reflection based UI builder to handle
 * lists of non-bean value types such as String etc.
 * 
 * @author Tom Oinn
 * 
 */
public class ListHandler extends
		ArrayList<ListHandler.ListItem> {

	private static final long serialVersionUID = -1361470859975889856L;

	private List<Object> wrappedList;
	
	public ListHandler(List<Object> theList) {
		this.wrappedList = theList;
		for (Object o : wrappedList) {
			this.add(new ListItem(o));
		}
	}

	/**@Override
	public boolean add(ListHandler.ListItem newItem) {
		wrappedList.add((T) newItem.getValue());
		return super.add(newItem);
	}*/

	/**
	 * Simple container class to handle list items, allowing them to present a
	 * bean interface
	 * 
	 * @author Tom Oinn
	 * 
	 */
	class ListItem {
		Object value;

		public ListItem(Object o) {
			this.value = o;
		}

		public void setValue(Object o) {
			try {
			wrappedList.set(indexOf(this), o);
			this.value = o;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
				
		public Object getValue() {
			return this.value;
		}
	}

}
