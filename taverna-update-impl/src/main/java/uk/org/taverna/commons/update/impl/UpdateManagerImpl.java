/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package uk.org.taverna.commons.update.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.osgi.service.event.EventAdmin;

import uk.org.taverna.commons.download.DownloadException;
import uk.org.taverna.commons.download.DownloadManager;
import uk.org.taverna.commons.profile.xml.jaxb.ApplicationProfile;
import uk.org.taverna.commons.profile.xml.jaxb.BundleInfo;
import uk.org.taverna.commons.profile.xml.jaxb.UpdateSite;
import uk.org.taverna.commons.profile.xml.jaxb.Updates;
import uk.org.taverna.commons.update.UpdateException;
import uk.org.taverna.commons.update.UpdateManager;
import uk.org.taverna.commons.versions.xml.jaxb.Version;
import uk.org.taverna.commons.versions.xml.jaxb.Versions;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * Implementation of the Taverna Update Manager.
 *
 * @author David Withers
 */
public class UpdateManagerImpl implements UpdateManager {

	private static final String DIGEST_ALGORITHM = "MD5";

	private EventAdmin eventAdmin;

	private int checkIntervalSeconds;

	private ApplicationConfiguration applicationConfiguration;

	private DownloadManager downloadManager;

	private long lastCheckTime;
	private boolean updateAvailable;
	private Unmarshaller unmarshaller;

	private Versions applicationVersions;
	private Version latestVersion;

	public UpdateManagerImpl() throws UpdateException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(UpdateSite.class, ApplicationProfile.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new UpdateException("Error creating JAXBContext", e);
		}
	}

	@Override
	public boolean checkForUpdates() throws UpdateException {
		ApplicationProfile applicationProfile = applicationConfiguration.getApplicationProfile();
		String version = applicationProfile.getVersion();
		Updates updates = applicationProfile.getUpdates();

		URL updatesURL;
		try {
			URI updateSiteURI = new URI(updates.getUpdateSite());
			updatesURL = updateSiteURI.resolve(updates.getUpdatesFile()).toURL();
		} catch (MalformedURLException e) {
			throw new UpdateException(String.format("Update site URL (%s) is not a valid URL",
					updates.getUpdateSite()), e);
		} catch (URISyntaxException e) {
			throw new UpdateException(String.format("Update site URL (%s) is not a valid URL",
					updates.getUpdateSite()), e);
		}
		File updateDirectory = new File(applicationConfiguration.getApplicationHomeDir(), "updates");
		updateDirectory.mkdirs();
		File updatesFile = new File(updateDirectory, updates.getUpdatesFile());
		try {
			downloadManager.download(updatesURL, updatesFile, DIGEST_ALGORITHM);
		} catch (DownloadException e) {
			throw new UpdateException(String.format("Error downloading %1$s",
					updatesURL), e);
		}

		try {
			UpdateSite updateSite = (UpdateSite) unmarshaller
					.unmarshal(updatesFile);
			applicationVersions = updateSite.getVersions();
			latestVersion = applicationVersions.getLatestVersion();
			updateAvailable = isHigherVersion(latestVersion.getVersion(), version);
		} catch (JAXBException e) {
			throw new UpdateException(String.format("Error reading %s",
					updatesFile.getName()), e);
		}
		lastCheckTime = System.currentTimeMillis();
		return updateAvailable;
	}

	@Override
	public boolean update() throws UpdateException {
		if (updateAvailable) {
			ApplicationProfile applicationProfile = applicationConfiguration.getApplicationProfile();
			Updates updates = applicationProfile.getUpdates();
			URL profileURL;
			try {
				URI updateSiteURI = new URI(updates.getUpdateSite());
				profileURL = updateSiteURI.resolve(latestVersion.getFile()).toURL();
			} catch (MalformedURLException e) {
				throw new UpdateException(String.format(
				"Application profile URL (%s) is not a valid URL",
				latestVersion.getFile()), e);
			} catch (URISyntaxException e) {
				throw new UpdateException(String.format("Update site URL (%s) is not a valid URL",
						updates.getUpdateSite()), e);
			}

			File updateDirectory = new File(applicationConfiguration.getApplicationHomeDir(),
					"updates");
			updateDirectory.mkdirs();
			File latestProfileFile = new File(updateDirectory, "ApplicationProfile-"
					+ latestVersion.getVersion() + ".xml");
			try {
				downloadManager.download(profileURL, latestProfileFile, DIGEST_ALGORITHM);
			} catch (DownloadException e) {
				throw new UpdateException(String.format("Error downloading %1$s",
						profileURL), e);
			}

			ApplicationProfile latestProfile;
			try {
				latestProfile = (ApplicationProfile) unmarshaller.unmarshal(latestProfileFile);
			} catch (JAXBException e) {
				throw new UpdateException(String.format("Error reading %s",
						latestProfileFile.getName()), e);
			}

			Set<BundleInfo> requiredBundles = getRequiredBundles(
					applicationConfiguration.getApplicationProfile(), latestProfile);
			downloadBundles(latestProfile, requiredBundles, new File(applicationConfiguration.getStartupDir(), "lib"));
			File applicationProfileFile = new File(applicationConfiguration.getStartupDir(), "ApplicationProfile.xml");
			try {
				FileUtils.copyFile(latestProfileFile, applicationProfileFile);
			} catch (IOException e) {
				throw new UpdateException(String.format("Error copying %1$s to %2$s",
						latestProfileFile.getName(), applicationProfileFile.getName()), e);
			}
//			eventAdmin.postEvent(new Event("UpdateManagerEvent", new HashMap()));
			updateAvailable = false;
			return true;
		}
		return false;
	}

	/**
	 * @param requiredBundles
	 * @param file
	 * @throws UpdateException
	 */
	private void downloadBundles(ApplicationProfile profile, Set<BundleInfo> requiredBundles, File file) throws UpdateException {
		Updates updates = profile.getUpdates();
		String updateSite = updates.getUpdateSite();
		String libDirectory = updates.getLibDirectory();
		if (!libDirectory.endsWith("/")) {
			libDirectory = libDirectory + "/";
		}

		URI updateLibDirectory;
		try {
			updateLibDirectory = new URI(updateSite).resolve(libDirectory);
		} catch (URISyntaxException e) {
			throw new UpdateException(String.format("Update site URL (%s) is not a valid URL",
					updates.getUpdateSite()), e);
		}
		for (BundleInfo bundle : requiredBundles) {
			URL bundleURL;
			URI bundleURI = updateLibDirectory.resolve(bundle.getFileName());
			try {
				bundleURL = bundleURI.toURL();
			} catch (MalformedURLException e) {
				throw new UpdateException(String.format("Bundle URL (%s) is not a valid URL",
						bundleURI), e);
			}
			File bundleDestination = new File(file, bundle.getFileName());
			try {
				downloadManager.download(bundleURL, new File(file, bundle.getFileName()), DIGEST_ALGORITHM);
			} catch (DownloadException e) {
				throw new UpdateException(String.format("Error downloading %1$s to %2$s",
						bundleURL, bundleDestination), e);
			}
		}
	}

	/**
	 * Returns the new bundles required for the new application profile.
	 *
	 * @param currentProfile
	 * @param newProfile
	 * @return the new bundles required for the new application profile
	 */
	private Set<BundleInfo> getRequiredBundles(ApplicationProfile currentProfile,
			ApplicationProfile newProfile) {
		Set<BundleInfo> requiredBundles = new HashSet<BundleInfo>();
		Map<String, BundleInfo> currentBundles = new HashMap<String, BundleInfo>();
		for (BundleInfo bundle : currentProfile.getBundle()) {
			currentBundles.put(bundle.getSymbolicName(), bundle);
		}
		for (BundleInfo bundle : newProfile.getBundle()) {
			if (currentBundles.containsKey(bundle.getSymbolicName())) {
				BundleInfo currentBundle = currentBundles.get(bundle.getSymbolicName());
				if (!bundle.getVersion().equals(currentBundle.getVersion())) {
					requiredBundles.add(bundle);
				}
			} else {
				requiredBundles.add(bundle);
			}
		}
		return requiredBundles;
	}

	private boolean isHigherVersion(String version1, String version2) {
		org.osgi.framework.Version semanticVersion1 = org.osgi.framework.Version.parseVersion(version1);
		org.osgi.framework.Version semanticVersion2 = org.osgi.framework.Version.parseVersion(version2);
		return semanticVersion1.compareTo(semanticVersion2) > 0;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void setCheckIntervalSeconds(int checkIntervalSeconds) {
		this.checkIntervalSeconds = checkIntervalSeconds;
	}

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	public void setDownloadManager(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}

}
