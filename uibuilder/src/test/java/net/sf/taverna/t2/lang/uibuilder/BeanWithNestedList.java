package net.sf.taverna.t2.lang.uibuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple bean with a list of the BeanWithListProperties to check nested list
 * management
 * 
 * @author Tom Oinn
 * 
 */
public class BeanWithNestedList {

	private List<BeanWithListProperties> list;

	public BeanWithNestedList() {
		this.list = new ArrayList<BeanWithListProperties>();
		for (int i = 0; i < 3; i++) {
			list.add(new BeanWithListProperties());
		}
	}

	public List<BeanWithListProperties> getList() {
		return this.list;
	}

}
