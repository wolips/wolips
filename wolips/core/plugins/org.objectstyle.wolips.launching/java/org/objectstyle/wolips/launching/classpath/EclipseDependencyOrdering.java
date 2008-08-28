package org.objectstyle.wolips.launching.classpath;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.objectstyle.woenvironment.frameworks.DependencyOrdering;
import org.objectstyle.wolips.jdt.classpath.model.EclipseDependency;

public class EclipseDependencyOrdering extends DependencyOrdering<EclipseDependency> {
	private Set<IPath> allProjectArchiveEntries;

	protected void initialize() {
		super.initialize();
		allProjectArchiveEntries = new HashSet<IPath>();
	}

	protected void addWOProject(EclipseDependency dependency) {
		IPath projectArchive = dependency.getWOJavaArchive();
		if (!allProjectArchiveEntries.contains(projectArchive)) {
			pendingResult.add(dependency);
			IRuntimeClasspathEntry resolvedEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(projectArchive);
			pendingResult.add(new EclipseDependency(resolvedEntry));
			allProjectArchiveEntries.add(projectArchive);
		}
	}
}
