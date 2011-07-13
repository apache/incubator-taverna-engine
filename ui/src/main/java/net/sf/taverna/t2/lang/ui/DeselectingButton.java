/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

/**
 * @author alanrw
 *
 */
public class DeselectingButton extends JButton {
	
	public DeselectingButton(String name, final ActionListener action, String toolTip) {
		super();
		this.setAction(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				Component parent = DeselectingButton.this.getParent();
				action.actionPerformed(e);
				parent.requestFocusInWindow();
			}		
		});
		this.setText(name);
		this.setToolTipText(toolTip);
	}

	public DeselectingButton(String name, final ActionListener action) {
		this(name, action, null);
	}
	
	public DeselectingButton(final Action action, String toolTip) {
		this((String) action.getValue(Action.NAME), action, toolTip);
	}

	public DeselectingButton(final Action action) {
		this(action, null);
	}
}
