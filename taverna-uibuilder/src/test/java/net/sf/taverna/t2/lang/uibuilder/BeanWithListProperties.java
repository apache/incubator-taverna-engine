package net.sf.taverna.t2.lang.uibuilder;

import java.util.List;
import java.util.ArrayList;

/**
 * Sample bean with a couple of list properties
 * 
 * @author Tom Oinn
 * 
 */
public class BeanWithListProperties {

	private List<String> list1;
	private List<BeanWithBoundProps> list2;

	public BeanWithListProperties() {
		this.list1 = new ArrayList<String>();
		this.list2 = new ArrayList<BeanWithBoundProps>();
		list1.add("A list item");
		list1.add("Another item");
		for (int i = 0; i < 10; i++) {
			list2.add(new BeanWithBoundProps());
		}
	}

	public List<String> getList1() {
		return this.list1;
	}

	public List<BeanWithBoundProps> getList2() {
		return this.list2;
	}

}
