/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;


/**
 * @author alanrw
 *
 */
public class LineEnabledTextPanel extends JPanel {
	
	private JTextComponent textComponent = null;
	private Document document;
	private GotoLineAction gotoLineAction = null;

	public LineEnabledTextPanel(final JTextComponent component) {
		
		this.setLayout(new BorderLayout());
		textComponent = component;
		updateDocument();
		
		JScrollPane scrollPane = new JScrollPane(textComponent );
		scrollPane.setPreferredSize(textComponent.getPreferredSize() );

		this.add(scrollPane, BorderLayout.CENTER);;
		
		final JLabel caretLabel = new JLabel("Line: 1 Column: 0");
		
		setCaretListener(new CaretListener() {

			public void caretUpdate(CaretEvent e) {
				int caretPosition = getCaretPosition();
				Element root = document.getRootElements()[0];
				int elementIndex = root.getElementIndex(caretPosition);
				int relativeOffset = caretPosition - root.getElement(elementIndex).getStartOffset();
		        caretLabel.setText("Line: " + (elementIndex + 1) + " Column: " + relativeOffset);

			}});
		this.add(caretLabel, BorderLayout.SOUTH);

		KeyStroke gotoLineKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.META_MASK);

		gotoLineAction = new GotoLineAction();
		textComponent.getInputMap().put(gotoLineKeystroke, "gotoLineKeystroke");
		textComponent.getActionMap().put("gotoLineKeystroke", gotoLineAction);

	}
	
	private void updateDocument() {
	    document = ((JTextComponent) textComponent).getDocument();
	}
	
	private void setCaretListener(CaretListener listener) {
	    ((JTextComponent) textComponent).addCaretListener(listener);
	}
	
	private int getCaretPosition() {
	    return ((JTextComponent) textComponent).getCaretPosition();
	}
	
	private void setCaretPosition(int position) {
	    ((JTextComponent) textComponent).setCaretPosition(position);
	    textComponent.requestFocus();
	}
	
	class GotoLineAction extends AbstractAction
	{

		public GotoLineAction() {	
		}

		public void actionPerformed(ActionEvent e) {
			String inputString = JOptionPane.showInputDialog(null, "Enter line number", "Line number", JOptionPane.QUESTION_MESSAGE);
			if (inputString != null) {
				try {
					int lineNumber = Integer.parseInt(inputString);
					Element root = document.getDefaultRootElement();
					lineNumber = Math.max(lineNumber, 1);
					lineNumber = Math.min(lineNumber, root.getElementCount());
					setCaretPosition( root.getElement( lineNumber - 1 ).getStartOffset() );

				} catch (NumberFormatException e1){
					// do nothing
				}
			}
		}
	}
}
