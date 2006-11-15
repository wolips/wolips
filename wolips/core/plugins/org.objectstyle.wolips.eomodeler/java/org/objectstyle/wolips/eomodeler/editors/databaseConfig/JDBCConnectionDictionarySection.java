/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eomodeler.editors.databaseConfig;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;

public class JDBCConnectionDictionarySection extends Composite implements IConnectionDictionarySection {
	private EODatabaseConfig _databaseConfig;

	private Text _usernameText;

	private Text _passwordText;

	private Text _urlText;

	private Text _driverText;

	private Text _pluginText;

	private DataBindingContext _bindingContext;

	public JDBCConnectionDictionarySection(Composite parent, int style, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		setBackground(parent.getBackground());
		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.USERNAME), SWT.NONE);
		_usernameText = new Text(this, SWT.BORDER);
		_usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.PASSWORD), SWT.NONE);
		_passwordText = new Text(this, SWT.BORDER);
		_passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.URL), SWT.NONE);
		_urlText = new Text(this, SWT.BORDER);
		_urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.DRIVER), SWT.NONE);
		_driverText = new Text(this, SWT.BORDER);
		_driverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		widgetFactory.createCLabel(this, Messages.getString("EOModel." + EODatabaseConfig.PLUGIN), SWT.NONE);
		_pluginText = new Text(this, SWT.BORDER);
		_pluginText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(EODatabaseConfig databaseContext) {
		disposeBindings();

		_databaseConfig = databaseContext;

		if (_databaseConfig != null) {
			_bindingContext = BindingFactory.createContext();
			_bindingContext.bind(_usernameText, new Property(_databaseConfig, EODatabaseConfig.USERNAME), null);
			_bindingContext.bind(_passwordText, new Property(_databaseConfig, EODatabaseConfig.PASSWORD), null);
			_bindingContext.bind(_urlText, new Property(_databaseConfig, EODatabaseConfig.URL), null);
			_bindingContext.bind(_driverText, new Property(_databaseConfig, EODatabaseConfig.DRIVER), null);
			_bindingContext.bind(_pluginText, new Property(_databaseConfig, EODatabaseConfig.PLUGIN), null);
		}
	}

	public void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
	}

	public void dispose() {
		disposeBindings();
		super.dispose();
	}
}
