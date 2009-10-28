package net.sf.taverna.t2.security.credentialmanager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



public class HTTPSConnectionTest {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception ex){ // anything we did not expect
			ex.printStackTrace();
		}
		
	}
}
