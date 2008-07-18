/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.variables;

import java.util.Dictionary;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IStartup;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.wolips.baseforplugins.AbstractBaseActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The main plugin class to be used in the desktop.
 */
public class VariablesPlugin extends AbstractBaseActivator implements IStartup {
	// The shared instance.
	private static VariablesPlugin plugin;

	private WOEnvironment woEnvironment;

	/**
	 * The constructor.
	 */
	public VariablesPlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return The path to user settings.
	 */
	public static IPath UserHomeLibrayWOLipsPath() {
		IPath path = new Path(System.getProperty("user.home"));
		path = path.append("Library");
		path = path.append("WOLips");
		return path;
	}

	/**
	 * @return the shared instance
	 */
	public static VariablesPlugin getDefault() {
		return plugin;
	}

	private IPath fixMissingSeparatorAfterDevice(String string) {
		IPath path;
		if (string != null && string.length() > 1 && string.charAt(1) == ':') {
			path = new Path(string.substring(2)).setDevice(string.substring(0, 2));
		}
		else if (string != null) {
			path = new Path(string);
		}
		else {
			path = null;
		}
		return path;
	}

	/**
	 * @return the path to the local root
	 */
	public IPath getLocalRoot() {
		return this.fixMissingSeparatorAfterDevice(this.getWOEnvironment().getWOVariables().localRoot());
	}

	/**
	 * @return the path to the system root
	 */
	public IPath getSystemRoot() {
		return this.fixMissingSeparatorAfterDevice(this.getWOEnvironment().getWOVariables().systemRoot());
	}

	/**
	 * @return the path to the network root
	 */
	public IPath getNetworkRoot() {
		return this.fixMissingSeparatorAfterDevice(this.getWOEnvironment().getWOVariables().networkRoot());
	}

	/**
	 * @return the path to the user home
	 */
	public IPath getUserRoot() {
		return this.fixMissingSeparatorAfterDevice(this.getWOVariables().userRoot());
	}

	/**
	 * @return the path to the reference api
	 */
	public IPath getReferenceApi() {
		String referenceApi = this.getWOVariables().referenceApi();
		if (referenceApi == null) {
			return null;
		}
		return this.fixMissingSeparatorAfterDevice(referenceApi);
	}

	/**
	 * @return the path to the reference api
	 */
	public String getReferenceApiAsJavaDocCompatibleString() {
		IPath referenceApi = this.getReferenceApi();
		if (referenceApi == null) {
			return null;
		}
		String referenceApiString = referenceApi.toOSString();
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			referenceApiString = referenceApiString.replace('\\', '/');
		}
		referenceApiString = "file://" + referenceApiString;
		return referenceApiString;
	}

	/**
	 * @return the path to external build root
	 */
	public IPath getExternalBuildRoot() {
		String root = this.getWOVariables().externalBuildRoot();
		if (root != null) {
			IPath result = this.fixMissingSeparatorAfterDevice(root);
			return result;
		}
		return null;
	}

	private WOVariables getWOVariables() {
		return this.getWOEnvironment().getWOVariables();
	}

	private WOEnvironment getWOEnvironment() {
		if (this.woEnvironment == null) {
			this.woEnvironment = new WOEnvironment(null);
		}
		return this.woEnvironment;
	}

	/**
	 * @return the plugin id
	 */
	public static String getPluginId() {
		if (plugin != null) {
			Dictionary dictionary = plugin.getBundle().getHeaders();
			String pluginID = (String) dictionary.get(Constants.BUNDLE_NAME);
			return pluginID;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public String getProperty(String key) {
		return getWOVariables().getProperty(key);
	}
	
	public void earlyStartup() {
		getWOVariables(); // initialize WOVariables
	}
}