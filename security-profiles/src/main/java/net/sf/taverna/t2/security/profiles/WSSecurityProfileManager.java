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
package net.sf.taverna.t2.security.profiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;

import org.apache.log4j.Logger;


/**
 * Manager for system (pre-defined) and user-defined WS-Security profiles.
 * 
 * @author Alexandra Nenadic
 *
 */
public class WSSecurityProfileManager {
	
	private static Logger logger = Logger
			.getLogger(WSSecurityProfileManager.class);
	
    /**
     * File with system-defined profiles.
     */
    File profilesFile_SystemDefined;
    
    /**
     * File with user-defined profiles.
     */
    File profilesFile_UserDefined;
    
	/**
	 * List of pre-defined WS Security profiles read from a file.
	 */
	private Vector<WSSecurityProfile> wsSecurityProfiles_SystemDefined;
	 
	/**
	 * List of pre-defined WS Security profiles' names.
	 */
	private Vector<String> wsSecurityProfileNames_SystemDefined;	
	 
	/**
	 * List of pre-defined WS Security profiles' descriptions.
	 */
	private Vector<String> wsSecurityProfileDescriptions_SystemDefined;
	
	/**
	 * List of user-defined WS Security profiles read from a file.
	 */
	private Vector<WSSecurityProfile> wsSecurityProfiles_UserDefined;
	 
	/**
	 * List of user-defined WS Security profiles' names.
	 */
	private Vector<String> wsSecurityProfileNames_UserDefined;	
	 
	/**
	 * List of user-defined WS Security profiles' descriptions.
	 */
	private Vector<String> wsSecurityProfileDescriptions_UserDefined;
	
	/** 
	 * WSSecurityProfileManager singelton
	 */
	private static WSSecurityProfileManager INSTANCE;
	
	/**
	 * Returns a WSSecurityProfileManager singleton.
	 * 
	 * @throws WSSecurityProfileManagerException 
	 */
	public static WSSecurityProfileManager getInstance() throws WSSecurityProfileManagerException{
		synchronized(WSSecurityProfileManager.class) {
	        if (INSTANCE == null)
	        	INSTANCE = new WSSecurityProfileManager();
	      }
	     return INSTANCE;
	 }
	
	
	/**
	 * Overrides the Object’s clone method to prevent the singleton object to be cloned.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); 
	}
	
	private File getConfigurationDirectory() {
		File home = ApplicationRuntime.getInstance().getApplicationHomeDir();
		File configDirectory = new File(home,"conf");
		if (!configDirectory.exists()) {
			configDirectory.mkdir();
		}
		File secConfigDirectory = new File(configDirectory,"security");
		if (!secConfigDirectory.exists()) {
			secConfigDirectory.mkdir();
		}
		logger.info("Using config directory:"+secConfigDirectory.getAbsolutePath());
		return secConfigDirectory;
	}
	
    /**
     * Credential WSSecurityProfileManager constructor.
     * Loads pre-defined system and saved user-defined WS-Security profiles.
     * 
	 * @throws WSSecurityProfileManagerException 
     */
	// Private constructor to suppress unauthorized calls to it
	private WSSecurityProfileManager () throws WSSecurityProfileManagerException {
    	 
    	// Quick 'n' dirty - expects the files containing profiles to be in the right format.
    	
    	BufferedReader profilesFileReader = null;
        String line;
        
        wsSecurityProfiles_SystemDefined= new Vector<WSSecurityProfile>();
        wsSecurityProfileNames_SystemDefined = new Vector<String>();
        wsSecurityProfileDescriptions_SystemDefined = new Vector<String>();

        // Read the profiles from the file
        try{
            
            profilesFileReader = new BufferedReader(new InputStreamReader(WSSecurityProfileManager.class.getResourceAsStream("/profiles/WSSecurity.profiles")));
            
			while ((line = profilesFileReader.readLine()) != null) {
				// skip empty lines
				while ((line != null) && (line.trim().length() == 0)) {
					line = profilesFileReader.readLine();
				}

				if ((line != null)
						&& line.startsWith("-----BEGIN PROFILE-----")) {

					// Start of the new profile
					WSSecurityProfile wsSecurityProfile = new WSSecurityProfile();
					while ((line != null)
							&& (!line.equals("-----END PROFILE-----"))) {

						// Skip empty lines
						while ((line != null) && (line.trim().length() == 0)) {
							line = profilesFileReader.readLine();
						}
						// Remove white space from the begining and end of the
						// line
						line = line.trim();
						if (line.startsWith("Name=")) { // must be single line
							wsSecurityProfile.setWSSecurityProfileName(line
									.substring(5));
						} else if (line.startsWith("Description=")) { // must be single line
							wsSecurityProfile
									.setWSSecurityProfileDescription(line
											.substring(12));
						} else if (line.startsWith("Profile=")) { // first line of profile text
							wsSecurityProfile.setWSSecurityProfileString(line
									.substring(8)
									+ "\n");
						} else { // continuation of profile text
							wsSecurityProfile
									.setWSSecurityProfileString(wsSecurityProfile
											.getWSSecurityProfileString()
											+ line + "\n");
						}
						line = profilesFileReader.readLine();
					}
					wsSecurityProfiles_SystemDefined.add(wsSecurityProfile);
					wsSecurityProfileNames_SystemDefined.add(wsSecurityProfile
							.getWSSecurityProfileName());
					wsSecurityProfileDescriptions_SystemDefined
							.add(wsSecurityProfile
									.getWSSecurityProfileDescription());
				}
			}
			logger.info("Loaded system-defined WS-Security profiles.");
        }
        /*
		 * catch(FileNotFoundException ex){ // should not happen - we have
		 * already checked if resource existed String exMessage =
		 * "WSSecurityProfileManager: missing pre-defined WS Security profiles
		 * file."; throw new WSSecurityProfileManagerException(exMessage); }
		 */
        catch(Exception ex){
        	String exMessage = "WSSecurityProfileManager failed to read the file with pre-defined WS Security profiles.";
        	logger.error(exMessage, ex);
        	throw new WSSecurityProfileManagerException(exMessage);
        }
        finally {
        	if (profilesFileReader != null)
        	{
        		try {
        			profilesFileReader.close();
 	        		}
 	             catch (IOException e) { 
 	                	//ignore
 	             }
 	        }
 	    } 
        
        // File with user-defined profiles
    	profilesFile_UserDefined = new File (getConfigurationDirectory(),"UserWSSecurity.profiles");

        wsSecurityProfiles_UserDefined= new Vector<WSSecurityProfile>();
        wsSecurityProfileNames_UserDefined = new Vector<String>();
        wsSecurityProfileDescriptions_UserDefined = new Vector<String>();
        
    	// Check if the file exists - it may not exist yet if user has not previously saved any profiles
    	if (profilesFile_UserDefined.exists()){
    		
            wsSecurityProfiles_UserDefined= new Vector<WSSecurityProfile>();
            wsSecurityProfileNames_UserDefined = new Vector<String>();
            wsSecurityProfileDescriptions_UserDefined = new Vector<String>();
            
            BufferedReader userProfilesFileReader = null;
    	      try{
    	    	  // Open the file for reading
    	    	  userProfilesFileReader = new BufferedReader(new FileReader(profilesFile_UserDefined));
 
    	    	  // Read the user-defined profiles
    	          while ((line=userProfilesFileReader.readLine())!=null) {
    	          	//skip empty lines
    	          	while ((line!=null) && (line.trim().length() == 0)){
    	          		line=userProfilesFileReader.readLine();
    	          	}
    	          	
    	            if ((line!=null) && line.startsWith("-----BEGIN PROFILE-----")) {

    	          	  // Start of the new profile
    	          	  WSSecurityProfile wsSecurityProfile = new WSSecurityProfile();
    	          	  while ((line!=null) && (!line.equals("-----END PROFILE-----"))){

    	                	// Skip empty lines
    	                	while ((line!=null) && (line.trim().length() == 0)){
    	                		line=userProfilesFileReader.readLine();
    	                	}
    	                	// Remove white space from the begining and end of the line
    	                	line = line.trim();
    	                	if (line.startsWith("Name=")){ // must be single line
    	                		wsSecurityProfile.setWSSecurityProfileName(line.substring(5));            		
    	                	}
    	                	else if (line.startsWith("Description=")){ // must be single line
    	                		wsSecurityProfile.setWSSecurityProfileDescription(line.substring(12));
    	                	}
    	                	else if (line.startsWith("Profile=")){ // first line of profile text
    	                		wsSecurityProfile.setWSSecurityProfileString(line.substring(8) +"\n");             			
    	                	}
    	                	else{ // continuation of profile text
    	                		wsSecurityProfile.setWSSecurityProfileString(wsSecurityProfile.getWSSecurityProfileString() + line + "\n");
    	                	}
    	                	line=userProfilesFileReader.readLine();
    	          	  }
    	          	wsSecurityProfiles_UserDefined.add(wsSecurityProfile);
    	          	wsSecurityProfileNames_UserDefined.add(wsSecurityProfile.getWSSecurityProfileName());
    	          	wsSecurityProfileDescriptions_UserDefined.add(wsSecurityProfile.getWSSecurityProfileDescription());
    	            }
    	          }
    	  		logger.info("Loaded user-defined WS-Security profiles.");
    	      }
    	      /*
    	      catch(FileNotFoundException ex){
    	      	 // Should not happen as we've already checked for the existence of the file
    	      }
    	      */
    	      catch(IOException ex){
    	    	  String exMessage = "WSSecurityProfileManager failed to read the file with user-defined WS Security profiles.";
    	    	  logger.error(exMessage, ex);
    	    	  throw new WSSecurityProfileManagerException(exMessage);
    	       }
    	       finally {
    	        	if (userProfilesFileReader != null)
    	        	{
    	        		try {
    	        			userProfilesFileReader.close();
    	        		}
    	        		catch (IOException e) { 
    	                	//ignore
    	        		}
    	        	}
    	       } 
    	}
    }
	
	
	/**
	 * Saves a profile to the file with user-defined profiles.
	 * 
	 * @throws WSSecurityProfileManagerException 
	 */
	public void saveProfile(WSSecurityProfile profile) throws WSSecurityProfileManagerException{
    	
		synchronized(profilesFile_UserDefined){
			   // If the file does not exist yet
		      if (!profilesFile_UserDefined.exists()){
		       	//Create a new file
		     	 try {
		     		profilesFile_UserDefined.createNewFile();
		     	 }
		     	 catch(IOException ex)
		     	 {
		     		 String exMessage = "WSSecurityProfileManager failed to create a file for user-defined profiles.";
		     		 logger.error(exMessage, ex);
		     		 throw new WSSecurityProfileManagerException(exMessage);
		     	 }
		      }

		      BufferedWriter userProfilesFileWriter = null;
		      try{
		       	// Open the file for writing (i.e. appending)
		    	  userProfilesFileWriter = new BufferedWriter((new FileWriter(profilesFile_UserDefined, true)));
		    	  // Add a new profile entry
		          String profileEntry ;
		          profileEntry = "-----BEGIN PROFILE-----\n";
		    	  // Profile name
		          profileEntry = profileEntry +"Name="+ profile.getWSSecurityProfileName() +"\n";
		    	  // Profile description
		          profileEntry = profileEntry +"Description=" + profile.getWSSecurityProfileDescription() +"\n";
		    	  // Profile itself
		          profileEntry = profileEntry +"Profile=" + profile.getWSSecurityProfileString();
		          profileEntry = profileEntry + "-----END PROFILE-----\n";
		         
		    	  userProfilesFileWriter.append(profileEntry);
		    	  userProfilesFileWriter.newLine();
		    	  
		    	  // Also add to the list with user defined profiles 
		    	  wsSecurityProfiles_UserDefined.add(profile);
		    	  wsSecurityProfileNames_UserDefined.add(profile.getWSSecurityProfileName());
		    	  wsSecurityProfileDescriptions_UserDefined.add(profile.getWSSecurityProfileDescription());
		    	  
		       }
		       catch(FileNotFoundException ex){
		      	 // Should not happen
		       }
		       catch(IOException ex){
		    	   String exMessage = "WSSecurityProfileManager failed to save the new user-defined profile.";
		    	   logger.error(exMessage, ex);
		    	   throw new WSSecurityProfileManagerException(exMessage);
		       }
		       finally {
		        	if (userProfilesFileWriter != null)
		        	{
		        		try {
		        			userProfilesFileWriter.close();
		        		}
		        		catch (IOException e) { 
		                	//ignore
		        		}
		        	}
		       } 
		}
	}
	
	/**
	 * Returns true if the profile is user-defined, false otherwise.
	 * 
	 * @throws WSSecurityProfileManagerException 
	 */
	public boolean isUserDefinedProfile(String name) {
		
		if (wsSecurityProfileNames_UserDefined.contains(name))
			return true;
		else
			return false;
	}
	
	/**
	 * Deletes a profile from the file with user-defined profiles.
	 * 
	 * @throws WSSecurityProfileManagerException 
	 */
	public void deleteProfile(String nameToDelete) throws WSSecurityProfileManagerException{
    	
		synchronized(profilesFile_UserDefined){
			assert profilesFile_UserDefined != null; // the file must have already been created and must contain at least one profile


		    BufferedWriter userProfilesFileWriter = null;
		    BufferedReader userProfilesFileReader = null;
		    
            try{

            	userProfilesFileReader = new  BufferedReader(new FileReader(profilesFile_UserDefined));
           	 	StringBuffer sb = new StringBuffer();;
           	 	String profile ; // current profile
           	  	String line;
           	  	
            	String currentProfileName = null; // currenlty read profile
           	  	
           	  	// Read the user-defined profiles file
    	        while ((line=userProfilesFileReader.readLine())!=null) {
    	          	//skip empty lines
    	          	while ((line!=null) && (line.trim().length() == 0)){
    	          		line=userProfilesFileReader.readLine();
    	          	}
    	          	
    	            if ((line!=null) && line.startsWith("-----BEGIN PROFILE-----")) {
    	            	profile = line;
    	            	profile = profile + System.getProperty("line.separator");
	                	line=userProfilesFileReader.readLine();

    	          	  // Start of the new profile
    	            	while ((line!=null) && (!line.equals("-----END PROFILE-----"))){

    	                	// Remove white space from the begining and end of the line
    	                	line = line.trim();
    	                	if (line.startsWith("Name=")){ 
    	                		// Get the name of the current profile
    	                		currentProfileName = line.substring(5);
    	                	}
	                		profile = profile + line;	
	    	            	profile = profile + System.getProperty("line.separator");
    	                	line=userProfilesFileReader.readLine();
    	          	  }
    	            	
    	          	  if (!nameToDelete.equals(currentProfileName)){ // If the profile is not for deletion
    	          		profile = profile + line; // appand the last read line, i.e. "-----END PROFILE-----" line
    	            	profile = profile + System.getProperty("line.separator");
    	          		// copy the profile
    	          		sb.append(profile);
    	          	  }
    	          	  else{ // Remove the profile from the lists
    	          		int indexToDelete = wsSecurityProfileNames_UserDefined.indexOf(nameToDelete);
    	          		wsSecurityProfiles_UserDefined.remove(indexToDelete);
    	          		wsSecurityProfileNames_UserDefined.remove(indexToDelete);
    	          		wsSecurityProfileDescriptions_UserDefined.remove(indexToDelete);
    	          	  }
    	            }
    	          }
  	           	 	
              	 //userProfilesFileReader.close(); // Will be closed in the 'finally' block
          		 
              	 // Delete the profilesFile_UserDefined
              	profilesFile_UserDefined.delete();
          		 
          		 if (sb.length()!=0){ // is there anything left to write to the file?
                  	 // Write the new profilesFile_UserDefined from the buffer
          			userProfilesFileWriter = new  BufferedWriter(new FileWriter(profilesFile_UserDefined));
          			userProfilesFileWriter.write(sb.toString());
          			//userProfilesFileWriter.close(); // Will be closed in the 'finally' block
          		 }    
            }
            catch(Exception ex){
        		 String exMessage = "WSSecurityProfileManager failed to delete the user-defined profile.";
             		logger.error(exMessage, ex);
        		 throw (new WSSecurityProfileManagerException(exMessage));                	 
             }
            finally {
              	if (userProfilesFileReader != null)
              	{
              		try {
              			userProfilesFileReader.close();
                      }
                      catch (IOException e) { 
                      	//ignore
                      }
              	}
             	if (userProfilesFileWriter != null)
              	{
              		try {
              			userProfilesFileWriter.close();
              		}
              		catch (IOException e) { 
                      	//ignore
              		}
              	}
             }   
		}
	}
	
	/**
	 * Returns a combined list of system and user-defined WS-Security profiles.
	 * 
	 */
	public Vector<WSSecurityProfile> getWSSecurityProfiles(){
		Vector<WSSecurityProfile> profiles = new Vector<WSSecurityProfile>();
		profiles.addAll(wsSecurityProfiles_SystemDefined);
		profiles.addAll(wsSecurityProfiles_UserDefined);
		return profiles;
	}
	
	/**
	 * Returns a combined list of system and user-defined WS-Security profile names.
	 * 
	 */
	public Vector<String> getWSSecurityProfileNames(){
		Vector<String> names = new Vector<String>();
		names.addAll(wsSecurityProfileNames_SystemDefined);
		names.addAll(wsSecurityProfileNames_UserDefined);
		return names;
	}
	
	/**
	 * Returns a combined list of system and user-defined WS-Security profile descriptions.
	 */
	public Vector<String> getWSSecurityProfileDescriptions(){
		Vector<String> descs = new Vector<String>();
		descs.addAll(wsSecurityProfileDescriptions_SystemDefined);
		descs.addAll(wsSecurityProfileDescriptions_UserDefined);
		return descs;
	}
}
