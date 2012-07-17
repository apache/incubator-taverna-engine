package org.purl.wf4ever.provtaverna.cmdline;

import java.security.Permission;
import java.util.Stack;

public final class AvoidExit extends SecurityManager {

	boolean exitAllowed = false;
	
	public static class ExitNotAllowed extends SecurityException {
		public ExitNotAllowed() {
			super("Exit not allowed");
		}
	}

	protected SecurityManager oldSecurityManager = null;

	protected AvoidExit() {
		oldSecurityManager = System.getSecurityManager();
		System.setSecurityManager(this);
	}

	@Override
	public void checkExit(int status) {
		if (! exitAllowed) {
			throw new ExitNotAllowed();
		}
	}

	@Override
	public void checkPermission(Permission perm) {		
		if (perm instanceof RuntimePermission && perm.getName().equals("setSecurityManager")) {			
			// Needed to uninstall ourselves - and also it was obviously allowed
			// before as we we got installed
			return;
		}
		super.checkPermission(perm);
	}

	public void uninstall() {
		// Fallback in case we are not removed properly, then allow exit again
		exitAllowed = true;
		if (System.getSecurityManager() != this) {
			// Note: AVOID exceptions here - as they might force an exit
			System.err.println("Can't uninstall AvoidExit; not current security manager");
			return;
		}
		System.setSecurityManager(oldSecurityManager);		
	}

	public static AvoidExit install() {
		return new AvoidExit();
	}

}