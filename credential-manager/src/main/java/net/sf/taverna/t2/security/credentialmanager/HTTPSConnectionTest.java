package net.sf.taverna.t2.security.credentialmanager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;



public class HTTPSConnectionTest {

	// Log4J Logger
	private static Logger logger = Logger.getLogger(HTTPSConnectionTest.class);

	public static void main(String[] args){
		
		CredentialManager credManager;
		try {
			credManager = CredentialManager.getInstance();
			HttpsURLConnection.setDefaultSSLSocketFactory(credManager.createTavernaSSLSocketFactory());
			URL url = new URL ("https://rpc103.cs.man.ac.uk:8443/wsrf/services/cagrid/SecureHelloWorld?wsdl");
			HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
			// user should be asked automatically if they want to trust the connection
			httpsConnection.connect();
			
		} catch (CMException e) {
			logger.error("", e);
		} catch (MalformedURLException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
		catch(Exception ex){ // anything we did not expect
			logger.error("", ex);
		}
		
	}
}
