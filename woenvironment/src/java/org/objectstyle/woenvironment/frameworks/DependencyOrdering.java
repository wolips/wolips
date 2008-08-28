/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2007 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *  4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 *  5. Products derived from this software may not be called "ObjectStyle" nor
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */

package org.objectstyle.woenvironment.frameworks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DependencyOrdering<T extends Dependency> {
  // We need to track with frameworks were projects, because they "win" when competing
  // with the same named framework from a /Frameworks folder
  private Set<String> projectFrameworkNames;

  // We need to track the name of the framework that contained each entry so we can
  // look it back up when we're building the final classpath
  private Map<T, String> dependencyFramework;

  // We also need to keep track of the location of the framework, so we only load
  // jars from the first framework we come across 
  private Map<String, String> addedFrameworkPaths;

  // Pending results contains all of the classpath entries, filtered such that we only
  // load the first of a framework from a /Frameworks folder, but we may end up with
  // dupes that are in a project AND a /Frameworks folder -- we'll clean that up later.
  protected List<T> pendingResult;

  protected void initialize() {
    projectFrameworkNames = new HashSet<String>();
    dependencyFramework = new HashMap<T, String>();
    addedFrameworkPaths = new HashMap<String, String>();
    pendingResult = new LinkedList<T>();
  }
  
  public List<T> orderDependencies(List<T> dependencies) {
    initialize();

    for (T dependency : dependencies) {
      String dependencyRawPath = dependency.getRawPath();
      String[] dependencyRawSegments = dependencyRawPath.split("/");

      // rewrite the raw path from its segments to normalize it against
      // other paths we will make (just makes sure they're all consistent)
      dependencyRawPath = joinRawPath(dependencyRawSegments);

      String frameworkName = null;
      int frameworkSegment = frameworkSegmentForPath(dependencyRawSegments);
      boolean addDependency = false;
      if (frameworkSegment == -1) {
        // MS: If ".framework" isn't in the path and we have a project, then
        // put a "fake" entry in the framework list corresponding to the project.  This
        // prevents /Library/Framework versions of the framework from loading later on 
        // in the classpath.
        if (dependency.isProject()) {
          frameworkName = dependency.getFrameworkName();
          addedFrameworkPaths.put(frameworkName, dependencyRawPath);
          projectFrameworkNames.add(frameworkName);
        }
        addDependency = true;
      }
      else {
        // MS: Otherwise, we have a regular framework path.  In this case, we
        // want to skip any jar that is coming from a different path for the 
        // framework than we have previously loaded.
        frameworkName = dependencyRawSegments[frameworkSegment];

        String frameworkPath = joinRawPath(dependencyRawSegments, frameworkSegment + 1);

        String previousFrameworkPath = addedFrameworkPaths.get(frameworkName);
        if (previousFrameworkPath == null) {
          addDependency = true;
          addedFrameworkPaths.put(frameworkName, frameworkPath);
        }
        else if (previousFrameworkPath.equals(frameworkPath)) {
          addDependency = true;
        }
      }

      // MS: ... all the stars have aligned, and this is a valid entry.  Lets add it.
      if (addDependency) {
        if (frameworkName != null) {
          dependencyFramework.put(dependency, frameworkName);
        }
        // MS: We need to get the build/BuiltFramework.framework folder from
        // a project and add that instead of the bin folder ...
        if (dependency.isWOProject()) {
          addWOProject(dependency);
        }
        else {
          pendingResult.add(dependency);
        }
      }
    }

    // sort classpath: project in front, then frameworks, then apple frameworks, then the rest
    List<T> otherJars = new ArrayList<T>();
    List<T> noAppleJars = new ArrayList<T>();
    List<T> appleJars = new ArrayList<T>();
    List<T> projects = new ArrayList<T>();
    List<T> woa = new ArrayList<T>();
    for (T dependency : pendingResult) {
      String frameworkName = dependencyFramework.get(dependency);
      if (dependency.isProject()) {
        projects.add(dependency);
      }
      // If the framework was added as a project, don't add it as a /Frameworks
      // folder framework.  This is cleaning up from the case where we got, for
      // instance /Library/Frameworks/WOOgnl.framework AND WOOgnl project.  We
      // want the project to win.
      else if (!projectFrameworkNames.contains(frameworkName)) {
        if (dependency.isAppleProvided()) {
          appleJars.add(dependency);
        }
        else if (dependency.isFrameworkJar()) {
          noAppleJars.add(dependency);
        }
        else if (dependency.isBuildProject()) {
          noAppleJars.add(dependency);
        }
        else if (dependency.isWoa()) {
          woa.add(dependency);
        }
        else {
          otherJars.add(dependency);
        }
      }
//			else {
//				System.out.println("WORuntimeClasspathProvider.resolveClasspath: skipping " + frameworkName + ": " + entry);
//			}
    }

    List<T> sortedDependencies = new ArrayList<T>();
    if (woa.size() > 0) {
      sortedDependencies.addAll(woa);
    }
    if (projects.size() > 0) {
      sortedDependencies.addAll(projects);
    }
    if (noAppleJars.size() > 0) {
      sortedDependencies.addAll(noAppleJars);
    }
    if (appleJars.size() > 0) {
      sortedDependencies.addAll(appleJars);
    }
    if (otherJars.size() > 0) {
      sortedDependencies.addAll(otherJars);
    }
//		for (IRuntimeClasspathEntry entry : sortedEntries) {
//			System.out.println("WORuntimeClasspathProvider.resolveClasspath: final = " + entry);
//		}

    return sortedDependencies;
  }

  protected abstract void addWOProject(T dependency);

  protected String joinRawPath(String[] pathSegments) {
    return joinRawPath(pathSegments, pathSegments.length);
  }

  protected String joinRawPath(String[] pathSegments, int length) {
    StringBuffer path = new StringBuffer();
    for (int i = 0; i < length; i++) {
      path.append(pathSegments[i]);
      if (i < length - 1) {
        path.append("/");
      }
    }
    return path.toString();
  }

  protected int frameworkSegmentForPath(String[] pathSegments) {
    int frameworkSegment = -1;
    for (int segmentNum = 0; frameworkSegment == -1 && segmentNum < pathSegments.length; segmentNum++) {
      String segment = pathSegments[segmentNum];
      if (segment.endsWith(".framework")) {
        frameworkSegment = segmentNum;
      }
    }
    return frameworkSegment;
  }
}
