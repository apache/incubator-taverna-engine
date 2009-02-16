/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.security.profiles.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sf.taverna.t2.security.profiles.WSSecurityProfile;

/**
 * 
 * Test launcher for WSSecurityProfileChooser (so it does not have to be
 * launched from Taverna).
 * 
 * @author Alexandra Nenadic
 *
 */
public class WSSecurityProfileChooserLauncher extends JFrame {
	
	private static final long serialVersionUID = 2079805060170251148L;
	
	public WSSecurityProfileChooserLauncher(){
		
		JPanel jpLaunch = new JPanel();
		jpLaunch.setPreferredSize(new Dimension (300, 120));
		
		JLabel jlLaunch = new JLabel("T2: WSS Profile Chooser");
		
		JButton jbLaunch = new JButton("WSS Profile Chooser");
		jbLaunch.setToolTipText("Launches WSS Profile Chooser");
		jbLaunch.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				WSSecurityProfileChooser wssPC = new WSSecurityProfileChooser(null);
				if (wssPC.isInitialised()) {
					wssPC.setVisible(true);
				}

				WSSecurityProfile wssProfile = wssPC.getWSSecurityProfile();
				if (wssProfile != null) // user did not cancel
					System.err.println(wssProfile.getWSSecurityProfileString());
			}
		});
		
		jpLaunch.add(jlLaunch);
		jpLaunch.add(jbLaunch);

		getContentPane().add(jpLaunch,BorderLayout.CENTER);
		
        // Handle application close
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
		pack();

        // Centre the frame in the centre of the desktop
        setLocationRelativeTo(null);
        
        // Set the frame's title
        setTitle("WS Security Profile Launcher");
        
        setVisible(true);
		
	}
	
	
    /**
     * Runnable to create and show the Credential Manager Launcher's GUI.
     */
    private static class CreateAndShowGui
        implements Runnable
    {

        /**
         * Create and show the launcher GUI.
         */
        public void run()
        {
            new WSSecurityProfileChooserLauncher();
        }
    }
    
    
    /**
     * Launcher for the Credential Manager GUI.
     */
    public static void main(String[] args)
    {
        // Create and show GUI on the event handler thread
        SwingUtilities.invokeLater(new CreateAndShowGui());
    }

}
