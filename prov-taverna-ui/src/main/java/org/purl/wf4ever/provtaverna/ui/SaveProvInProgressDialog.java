package org.purl.wf4ever.provtaverna.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;

/**
 * Dialog that is popped up while we are exporting workflow run provenance. 
 * This is to let the user know that Taverna is doing something.
 * 
 * @author Alex Nenadic
 *
 */
public class SaveProvInProgressDialog extends JDialog implements PropertyChangeListener{

	private static final long serialVersionUID = 3022516542431968398L;

	public SaveProvInProgressDialog() {
		
		super((JFrame) null, "Saving provenance bundle", true);

		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10,10,10,10));
		
		JPanel textPanel = new JPanel();
		JLabel text = new JLabel(WorkbenchIcons.workingIcon);
		text.setText("Saving provenance bundle...");
		text.setBorder(new EmptyBorder(10,0,10,0));
		textPanel.add(text);
		panel.add(textPanel, BorderLayout.CENTER);

		setContentPane(panel);
		setPreferredSize(new Dimension(300, 100));

		pack();		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("state".equals(evt.getPropertyName())
                && SwingWorker.StateValue.DONE == evt.getNewValue()) {
            this.setVisible(false);
            this.dispose();
        }		
	}

}
