package net.sf.taverna.t2.lang.uibuilder;

/**
 * Bean containing the various other sub-beans
 * 
 * @author Tom Oinn
 * 
 */
public class TopLevelBean {

	private SampleEnum enumeratedField = SampleEnum.ABCD;
	private BeanWithBoundProps boundBean = new BeanWithBoundProps();
	private BeanWithNestedList nest = new BeanWithNestedList();

	public TopLevelBean() {
		//
	}

	public void setEnumeratedField(SampleEnum enumeratedField) {
		this.enumeratedField = enumeratedField;
	}

	public SampleEnum getEnumeratedField() {
		return enumeratedField;
	}

	public void setBoundBean(BeanWithBoundProps boundBean) {
		this.boundBean = boundBean;
	}

	public BeanWithBoundProps getBoundBean() {
		return boundBean;
	}

	public void setNest(BeanWithNestedList nest) {
		this.nest = nest;
	}

	public BeanWithNestedList getNest() {
		return nest;
	}

}
