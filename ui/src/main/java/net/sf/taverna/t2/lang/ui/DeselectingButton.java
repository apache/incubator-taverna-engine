/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

/**
 * @author alanrw
 *
 */
public class DeselectingButton extends JButton {
	
	public DeselectingButton(String name, final AbstractAction action) {
		super();
		this.setAction(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				Component parent = DeselectingButton.this.getParent();
				action.actionPerformed(e);
				parent.requestFocusInWindow();
			}		
		});
		this.setText(name);
	}

}
