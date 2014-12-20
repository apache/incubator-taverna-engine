package net.sf.taverna.t2.lang.uibuilder;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sf.taverna.t2.lang.uibuilder.UIBuilder;

/**
 * Torture test for the UIBuilder, run this as an application
 * 
 * @author Tom Oinn
 * 
 */
public class Application {

	private static final String NUMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		try {
			UIManager.setLookAndFeel(NUMBUS);
		} catch (ClassNotFoundException ex) {
			// ignore
		}
		Object bean = new TopLevelBean();
		JFrame win = new JFrame();
		JPanel contents = UIBuilder.buildEditor(bean, new String[] {
				"boundbean", "boundbean.string:type=textarea", "boundbean.url",
				"boundbean.uri",
				"boundbean.string:type=textarea", "boundbean.url",
				"enumeratedfield", "nest", "nest.list", "nest.list.list1",
				"nest.list.list2", "nest.list.list2.string",
				"nest.list.list2.url" });
		contents.setBackground(new Color(240, 230, 200));
		win.setContentPane(new JScrollPane(contents));
		win.setTitle("Bean test");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.pack();
		win.setVisible(true);
	}

}
