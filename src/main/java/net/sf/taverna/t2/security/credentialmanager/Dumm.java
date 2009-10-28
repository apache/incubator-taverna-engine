package net.sf.taverna.t2.security.credentialmanager;

public class Dumm {

	public interface CredMan{

		void addCredentialProvider(CredentialProvider credentialProvider);}
	
	
	public interface CredentialProvider {
		public Response getResponseFor(Request req);
	}
	
	public interface Response {}
	public interface Request {
		public String getType();
		
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		CredMan x;
		
		/*x.addCredentialProvider(new CredentialProvider(){

			public Response getResponseFor(MasterRequest req) {
				return new MasterResponse(args[0]);
			}});
		
		
		x.addCredentialProvider(new CredentialProvider(){

			public Response getResponseFor(Request req) {
				if (req instanceof MasterRequest) {
					String pw = Swing.popupDialogue("What's that password");
					return new MasterResponse(pw);
				}
				return null;
			}});
		
		*/
		
	}

}
