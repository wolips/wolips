/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.commons.logging;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.commons.CommonsPlugin;
/**
 * @author uli
 * @author mnolte
 */
public class PluginLogger {
	private String pluginID = null;
	private boolean debug;
	/**
	 * @param pluginID
	 * @param debug
	 */
	public PluginLogger(String pluginID, boolean debug) {
		this.pluginID = pluginID;
		this.debug = debug;
	}
	/**
	 * Prints an IStatus.
	 * 
	 * @param status
	 */
	public void log(IStatus status) {
		CommonsPlugin.getDefault().getLog().log(status);
	}
	/**
	 * Prints a message.
	 * 
	 * @param message
	 */
	public void log(String message) {
		this.log(new Status(IStatus.ERROR, pluginID, IStatus.ERROR, message,
				null));
	}
	/**
	 * Prints a Throwable.
	 * 
	 * @param e
	 */
	public void log(Throwable e) {
		this.log(new Status(IStatus.ERROR, pluginID, IStatus.ERROR,
				"Internal Error", e)); //$NON-NLS-1$
	}
	/**
	 * Prints a Throwable.
	 * @param message
	 * @param e
	 */
	public void log(String message, Throwable e) {
		this.log(new Status(IStatus.ERROR, pluginID, IStatus.ERROR,
				message, e)); //$NON-NLS-1$
	}
	/**
	 * If debug is true this method prints a String to the log.
	 * 
	 * @param aString
	 */
	public void debug(String aString) {
		if (debug)
			this.log(aString);
	}
	/**
	 * If debug is true this method prints an Exception to the log.
	 * 
	 * @param aThrowable
	 */
	public void debug(Throwable aThrowable) {
		if (debug)
			this.log(aThrowable);
	}
	/**
	 * @param message
	 * @param t
	 */
	public void debug(Object message, Throwable t) {
		if (debug)
			this.log(new Status(IStatus.WARNING, pluginID, IStatus.WARNING,
					message.toString(), t)); //$NON-NLS-1$
	}
	/**
	 * @param message
	 */
	public void debug(Object message) {
		if (debug)
			this.log(new Status(IStatus.WARNING, pluginID, IStatus.WARNING,
					message.toString(), null)); //$NON-NLS-1$
	}
}