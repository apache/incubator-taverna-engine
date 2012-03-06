package uk.org.taverna.platform.capability.configuration.impl;

import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

@ConfigurationBean(uri = TestUtils.annotatedBeanURI + "/subclass")
public class SubclassTestBean extends TestBean2 {

	public String subclassStringType;
	public String conflicting;




	@Override
	public String toString() {
		return "SubclassActivityTestBean [subclassStringType="
				+ subclassStringType + ", conflicting=" + conflicting
				+ ", stringType=" + stringType + ", stringType2=" + stringType2
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((conflicting == null) ? 0 : conflicting.hashCode());
		result = prime
				* result
				+ ((subclassStringType == null) ? 0 : subclassStringType
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubclassTestBean other = (SubclassTestBean) obj;
		if (conflicting == null) {
			if (other.conflicting != null)
				return false;
		} else if (!conflicting.equals(other.conflicting))
			return false;
		if (subclassStringType == null) {
			if (other.subclassStringType != null)
				return false;
		} else if (!subclassStringType.equals(other.subclassStringType))
			return false;
		return true;
	}

	@ConfigurationProperty(name = "stringType2")
	public void setConflicting(String parameter) {
		conflicting = parameter;
	}

	@Override
	@ConfigurationProperty(name = "overriding")
	public void setStringType(String parameter) {
		subclassStringType = parameter;
	}


}
