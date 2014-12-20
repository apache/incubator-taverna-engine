package net.sf.taverna.t2.lang.uibuilder;

/**
 * Superinterface for components which have a label and which may be mutually
 * aligned within a panel. This assumes the component is laid out with a label
 * to the left of the main editing area, and that we want to ensure that all
 * editing areas line up and can do this by setting the preferred size of the
 * label.
 * 
 * @author Tom Oinn
 * 
 */
public interface AlignableComponent {

	/**
	 * Set the preferred width of the label for this alignable component
	 * 
	 * @param newWidth
	 */
	public void setLabelWidth(int newWidth);

	/**
	 * Get the current preferred width of the label for this alignable component
	 * 
	 * @return
	 */
	public int getLabelWidth();

}
