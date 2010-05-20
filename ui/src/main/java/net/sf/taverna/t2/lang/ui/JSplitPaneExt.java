/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Graphics;

import javax.swing.JSplitPane;

/**
 * @author alanrw
 *
 */
public class JSplitPaneExt extends JSplitPane {
	
	protected boolean m_fIsPainted = false;
	protected double m_dProportionalLocation = -1;

	public JSplitPaneExt() {
		super();
	}

	public JSplitPaneExt(int iOrientation) {
		super(iOrientation);
	}

	protected boolean hasProportionalLocation() {
		return (m_dProportionalLocation != -1);
	}

	public void cancelDividerProportionalLocation() {
		m_dProportionalLocation = -1;
	}

	public void setDividerLocation(double dProportionalLocation) {
		if (dProportionalLocation < 0 || dProportionalLocation > 1) {
			throw new IllegalArgumentException(
					"Illegal value for divider location: "
							+ dProportionalLocation);
		}
		m_dProportionalLocation = dProportionalLocation;
		if (m_fIsPainted) {
			super.setDividerLocation(m_dProportionalLocation);
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (hasProportionalLocation()) {
			super.setDividerLocation(m_dProportionalLocation);
		}
		m_fIsPainted=true; 

	}

}
