package org.purl.wf4ever.provtaverna.cmdline;

import java.security.Permission;

/**
 * A security manager that disallows System.exit().
 * <p>
 * Methods calling {@link System#exit(int)} will instead fail with a
 * {@link ExitNotAllowed} exception.
 * <p>
 * To install this security manager, use {@link AvoidExit#install()} - and then
 * call {@link AvoidExit#uninstall()} on the returned instance, which will
 * restore the original security manager.
 * <p>
 * The installation and removal of security managers is by its nature NOT thread
 * safe. It is therefore not advisable to use {@link AvoidExit#install()} or
 * {@link AvoidExit#uninstall()} from more than one thread - as otherwise the
 * wrong 'old' security manager could be set.
 * <p>
 * In case this happens, this class will always revert to allowing System.exit
 * after {@link #uninstall()} has been called, in order for
 * {@link RuntimeException}s to cause normal exit of the JVM.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public final class AvoidExit extends SecurityManager {

	boolean exitAllowed = false;

	public static class ExitNotAllowed extends SecurityException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int status;

		public ExitNotAllowed(int status) {
			super("Exit not allowed! Status: " + status);
			this.status = status;
		}

		public int getStatus() {
			return status;
		}
	}

	protected SecurityManager oldSecurityManager = null;

	protected AvoidExit() {
		oldSecurityManager = System.getSecurityManager();
		System.setSecurityManager(this);
	}

	@Override
	public void checkExit(int status) {
		if (!exitAllowed) {
			throw new ExitNotAllowed(status);
		}
	}

	@Override
	public void checkPermission(Permission perm) {
		if (perm instanceof RuntimePermission
				&& perm.getName().equals("setSecurityManager")) {
			// Needed to uninstall ourselves - and also it was obviously allowed
			// before as we we got installed
			return;
		}
		if (oldSecurityManager != null) {
			oldSecurityManager.checkPermission(perm);
		}
		// Otherwise, allow anything
	}

	/**
	 * Uninstall this security manager
	 * <p>
	 * If the current security manager is this instance, then the security
	 * manager that was present before this instance was installed will be
	 * reinstated as the current security manager.
	 * <p>
	 * If the current security manager is another instance, then an error
	 * message is printed on System.err and the current security manager
	 * is not modified.
	 * <p>
	 * After calling uninstall, {@link System#exit(int)} will always be
	 * allowed by this instance.
	 * 
	 */
	public void uninstall() {
		// Fallback in case we are not removed properly, then allow exit again
		exitAllowed = true;
		if (System.getSecurityManager() != this) {
			// Note: AVOID exceptions here - as they might force an exit
			System.err
					.println("Can't uninstall AvoidExit; no longer current security manager");
			return;
		}
		System.setSecurityManager(oldSecurityManager);
	}

	/**
	 * Install an AvoidExit as a security manager
	 * 
	 */
	public static AvoidExit install() {
		return new AvoidExit();
	}

}