/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 *
 */
public class FileTools {
	
	private static Logger logger = Logger.getLogger(FileTools.class);

	public static boolean saveStringToFile(Component parent, String dialogTitle, String extension, String content) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(dialogTitle);

		fileChooser.resetChoosableFileFilters();
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		fileChooser.setFileFilter(new ExtensionFileFilter(new String[] { extension }));

		Preferences prefs = Preferences.userNodeForPackage(FileTools.class);
		String curDir = prefs
				.get("currentDir", System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(new File(curDir));

		boolean tryAgain = true;
		while (tryAgain) {
			tryAgain = false;
			int returnVal = fileChooser.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				prefs.put("currentDir", fileChooser.getCurrentDirectory()
						.toString());
				File file = fileChooser.getSelectedFile();
				if (!file.getName().contains(".")) {
					String newName = file.getName() + extension;
					file = new File(file.getParentFile(), newName);
				}

				// TODO: Open in separate thread to avoid hanging UI
				try {
					if (file.exists()) {
						logger.info("File already exists: " + file);
						String msg = "Are you sure you want to overwrite existing file "
								+ file + "?";
						int ret = JOptionPane.showConfirmDialog(
								parent, msg, "File already exists",
								JOptionPane.YES_NO_CANCEL_OPTION);
						if (ret == JOptionPane.YES_OPTION) {
							
						} else if (ret == JOptionPane.NO_OPTION) {
							tryAgain = true;
							continue;
						} else {
							logger.info("Aborted overwrite of " + file);
							return false;
						}
					}
					BufferedWriter out = new BufferedWriter(new FileWriter(file));
			        out.write(content);
			        out.close();
					logger.info("Saved content by overwriting " + file);
					return true;
				} catch (IOException ex) {
					logger.warn("Could not save content to " + file, ex);
					JOptionPane.showMessageDialog(parent,
							"Could not save to " + file + ": \n\n"
									+ ex.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
		}
		return false;
	}
	
	public static String readStringFromFile(Component parent) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		Preferences prefs = Preferences.userNodeForPackage(FileTools.class);
		String curDir = prefs
				.get("currentDir", System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(new File(curDir));

		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						selectedFile));

				String line;
				StringBuffer buffer = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
					buffer.append("\n");
				}
				reader.close();

				return buffer.toString();

			} catch (FileNotFoundException ffe) {
				JOptionPane.showMessageDialog(parent, "File '"
						+ selectedFile.getName() + "' not found",
						"File not found", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(parent, "Can not read file '"
						+ selectedFile.getName() + "'", "Can not read file",
						JOptionPane.ERROR_MESSAGE);
			}

		}
		return null;
	}
}
