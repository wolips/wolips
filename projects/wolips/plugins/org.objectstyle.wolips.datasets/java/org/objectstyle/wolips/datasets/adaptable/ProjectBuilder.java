/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.datasets.adaptable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.project.IBuilderAccessor;
/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectBuilder extends ProjectFiles {
	/**
	 * Comment for <code>BuilderNotFound</code>
	 */
	public static final int BuilderNotFound = -1;
	protected static final String TARGET_BUILDER_ID = "org.objectstyle.wolips.targetbuilder.targetbuilder";
	protected static final String INCREMENTAL_BUILDER_ID = "org.objectstyle.wolips.incrementalbuilder";
	private static final String ANT_BUILDER_ID = "org.objectstyle.wolips.antbuilder";
	/**
	 * @param project
	 */
	protected ProjectBuilder(IProject project) {
		super(project);
	}
	/**
	 * Installs the target builder.
	 * 
	 * @param position
	 * @throws CoreException
	 */
	public void installTargetBuilder(int position) throws CoreException {
		if (!this.isTargetBuilderInstalled())
			this.installBuilderAtPosition(ProjectBuilder.TARGET_BUILDER_ID,
					position, null);
	}
	/**
	 * Removes the target builder.
	 * 
	 * @return postion of TargetBuilder if not found
	 *         IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public int removeTargetBuilder() throws CoreException {
		if (!this.isTargetBuilderInstalled())
			return IBuilderAccessor.BuilderNotFound;
		int returnValue = this
				.positionForBuilder(ProjectBuilder.TARGET_BUILDER_ID);
		this.removeBuilder(ProjectBuilder.TARGET_BUILDER_ID);
		return returnValue;
	}
	/**
	 * Installs the ant builder.
	 * 
	 * @throws CoreException
	 */
	public void installAntBuilder() throws CoreException {
		if (!this.isAntBuilderInstalled())
			this.installBuilder(ProjectBuilder.ANT_BUILDER_ID);
	}
	/**
	 * Removes the ant builder.
	 * 
	 * @throws CoreException
	 */
	public void removeAntBuilder() throws CoreException {
		if (this.isAntBuilderInstalled())
			this.removeBuilder(ProjectBuilder.ANT_BUILDER_ID);
	}
	/**
	 * Installs the incremetal builder.
	 * 
	 * @throws CoreException
	 */
	public void installIncrementalBuilder() throws CoreException {
		if (!this.isIncrementalBuilderInstalled())
			this.installBuilder(ProjectBuilder.INCREMENTAL_BUILDER_ID);
	}
	/**
	 * Removes the incremental builder.
	 * 
	 * @throws CoreException
	 */
	public void removeIncrementalBuilder() throws CoreException {
		if (this.isIncrementalBuilderInstalled())
			this.removeBuilder(ProjectBuilder.INCREMENTAL_BUILDER_ID);
	}
	/**
	 * Installs the java builder.
	 * 
	 * @throws CoreException
	 */
	public void installJavaBuilder() throws CoreException {
		if (!this.isJavaBuilderInstalled())
			this.installBuilder(JavaCore.BUILDER_ID);
	}
	/**
	 * Installs the java builder.
	 * 
	 * @param position
	 * @throws CoreException
	 */
	public void installJavaBuilder(int position) throws CoreException {
		if (!this.isJavaBuilderInstalled())
			this.installBuilderAtPosition(JavaCore.BUILDER_ID, position, null);
	}
	/**
	 * Removes the incremental builder.
	 * 
	 * @return postion of JavaBuilder if not found
	 *         IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public int removeJavaBuilder() throws CoreException {
		if (!this.isJavaBuilderInstalled())
			return IBuilderAccessor.BuilderNotFound;
		int returnValue = this.positionForBuilder(JavaCore.BUILDER_ID);
		this.removeBuilder(JavaCore.BUILDER_ID);
		return returnValue;
	}
	/**
	 * @return Return true if the target builder is installed.
	 */
	public boolean isTargetBuilderInstalled() {
		return this.isBuilderInstalled(ProjectBuilder.TARGET_BUILDER_ID);
	}
	/**
	 * @return Return true if the ant builder is installed.
	 */
	public boolean isAntBuilderInstalled() {
		return this.isBuilderInstalled(ProjectBuilder.ANT_BUILDER_ID);
	}
	/**
	 * @return Return true if the incremental builder is installed.
	 */
	public boolean isIncrementalBuilderInstalled() {
		return this.isBuilderInstalled(ProjectBuilder.INCREMENTAL_BUILDER_ID);
	}
	/**
	 * @return Return true if the java builder is installed.
	 */
	public boolean isJavaBuilderInstalled() {
		return this.isBuilderInstalled(JavaCore.BUILDER_ID);
	}
	/**
	 * @return The builer args.
	 */
	public Map getBuilderArgs() {
		Map result = null;
		try {
			IProjectDescription desc = this.getIProject().getDescription();
			List cmdList = Arrays.asList(desc.getBuildSpec());
			Iterator iter = cmdList.iterator();
			while (iter.hasNext()) {
				ICommand cmd = (ICommand) iter.next();
				if (this.isWOLipsBuilder(cmd.getBuilderName())) {
					result = cmd.getArguments();
					break;
				}
			}
		} catch (Exception up) {
			// if anything went wrong, we simply don't have any args (yet)
			// might wanna log the exception, though
		}
		if (null == result) {
			// this doesn't exist pre-JDK1.3, is that a problem?
			result = Collections.EMPTY_MAP;
			//result = new HashMap();
		}
		return (result);
	}
	/**
	 * @param name
	 *            Name of a build command
	 * @return boolean whether this is one of ours
	 */
	private boolean isWOLipsBuilder(String name) {
		return (name.equals(ProjectBuilder.INCREMENTAL_BUILDER_ID) || name
				.equals(ProjectBuilder.ANT_BUILDER_ID));
	}
	/**
	 * Method removeJavaBuilder.
	 * 
	 * @param aBuilder
	 * @throws CoreException
	 */
	private void removeBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		ArrayList comList = null;
		List tmp = null;
		ICommand[] newCom = null;
		try {
			desc = this.getIProject().getDescription();
			coms = desc.getBuildSpec();
			comList = new ArrayList();
			tmp = Arrays.asList(coms);
			comList.addAll(tmp);
			boolean foundJBuilder = false;
			for (int i = 0; i < comList.size(); i++) {
				if (((ICommand) comList.get(i)).getBuilderName().equals(
						aBuilder)) {
					comList.remove(i);
					foundJBuilder = true;
				}
			}
			if (foundJBuilder) {
				newCom = new ICommand[comList.size()];
				for (int i = 0; i < comList.size(); i++) {
					newCom[i] = (ICommand) comList.get(i);
				}
				desc.setBuildSpec(newCom);
				this.getIProject().setDescription(desc, null);
			}
		} finally {
			desc = null;
			coms = null;
			comList = null;
			tmp = null;
			newCom = null;
		}
	}
	/**
	 * Method installBuilder.
	 * 
	 * @param aBuilder
	 * @throws CoreException
	 */
	private void installBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		ICommand[] newIc = null;
		ICommand command = null;
		try {
			desc = this.getIProject().getDescription();
			coms = desc.getBuildSpec();
			boolean foundJBuilder = false;
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].getBuilderName().equals(aBuilder)) {
					foundJBuilder = true;
				}
			}
			if (!foundJBuilder) {
				newIc = null;
				command = desc.newCommand();
				command.setBuilderName(aBuilder);
				newIc = new ICommand[coms.length + 1];
				System.arraycopy(coms, 0, newIc, 0, coms.length);
				newIc[coms.length] = command;
				desc.setBuildSpec(newIc);
				this.getIProject().setDescription(desc, null);
			}
		} finally {
			desc = null;
			coms = null;
			newIc = null;
			command = null;
		}
	}
	/**
	 * Method isBuilderInstalled.
	 * 
	 * @param anID
	 * @return boolean
	 */
	private boolean isBuilderInstalled(String anID) {
		try {
			ICommand[] nids = this.getIProject().getDescription()
					.getBuildSpec();
			for (int i = 0; i < nids.length; i++) {
				if (nids[i].getBuilderName().equals(anID))
					return true;
			}
		} catch (Exception anException) {
			DataSetsPlugin.log(anException);
			return false;
		}
		return false;
	}
	/**
	 * Method positionForBuilder.
	 * 
	 * @param aBuilder
	 * @return int
	 * @throws CoreException
	 */
	private int positionForBuilder(String aBuilder) throws CoreException {
		IProjectDescription desc = null;
		ICommand[] coms = null;
		try {
			desc = this.getIProject().getDescription();
			coms = desc.getBuildSpec();
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].getBuilderName().equals(aBuilder))
					return i;
			}
		} finally {
			desc = null;
			coms = null;
		}
		return BuilderNotFound;
	}
	/**
	 * Method installBuilderAtPosition.
	 * 
	 * @param aBuilder
	 * @param installPos
	 * @param arguments
	 * @throws CoreException
	 */
	private void installBuilderAtPosition(String aBuilder, int installPos,
			Map arguments) throws CoreException {
		if (installPos == IBuilderAccessor.BuilderNotFound) {
			DataSetsPlugin
					.log("Somebody tries to install builder: "
							+ aBuilder
							+ " at an illegal position. This may happen if the removed builder does not exist.");
			return;
		}
		IProjectDescription desc = this.getIProject().getDescription();
		ICommand[] coms = desc.getBuildSpec();
		if (arguments == null)
			arguments = new HashMap();
		for (int i = 0; i < coms.length; i++) {
			if (coms[i].getBuilderName().equals(aBuilder)
					&& coms[i].getArguments().equals(arguments))
				return;
		}
		ICommand[] newIc = null;
		ICommand command = desc.newCommand();
		command.setBuilderName(aBuilder);
		command.setArguments(arguments);
		newIc = new ICommand[coms.length + 1];
		if (installPos <= 0) {
			System.arraycopy(coms, 0, newIc, 1, coms.length);
			newIc[0] = command;
		} else if (installPos >= coms.length) {
			System.arraycopy(coms, 0, newIc, 0, coms.length);
			newIc[coms.length] = command;
		} else {
			System.arraycopy(coms, 0, newIc, 0, installPos);
			newIc[installPos] = command;
			System.arraycopy(coms, installPos, newIc, installPos + 1,
					coms.length - installPos);
		}
		desc.setBuildSpec(newIc);
		this.getIProject().setDescription(desc, null);
	}
}