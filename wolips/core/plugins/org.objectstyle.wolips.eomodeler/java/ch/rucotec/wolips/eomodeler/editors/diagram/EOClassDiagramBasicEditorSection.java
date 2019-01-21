package ch.rucotec.wolips.eomodeler.editors.diagram;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.UglyFocusHackWorkaroundListener;

import ch.rucotec.wolips.eomodeler.DiagramTab;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOClassDiagram;

/**
 * This class describes how the PropetiesView in Eclipse.
 * If a {@link EOClassDiagram} is selected in the Outline then the PropetiesView will show this.
 * the settings are made in the {@code plugin.xml}
 * 
 * @author celik
 *
 */
public class EOClassDiagramBasicEditorSection extends AbstractPropertySection {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	
	private EOClassDiagram myDiagram;
	private EOModel myCurrentModel;
	
	private Text myNameText;
	
	private DataBindingContext myBindingContext;
	
	private Composite parent;
	
	private ArrayList<Button> checkBoxes = new ArrayList<Button>();
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------
	
	/**
	 * This creates the form in the Properties Tab
	 */
	@Override
	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		parent = _parent;
		_parent.setLayout(new GridLayout(3, false));

		getWidgetFactory().createCLabel(_parent, "Diagram Name", SWT.NONE);
		myNameText = new Text(_parent, SWT.BORDER);
		myNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 0));
		UglyFocusHackWorkaroundListener.addListener(myNameText);
	}
	
	/**
	 * Creates checkboxes for every entity in the EOModel.
	 */
	private void createEntityCheckBoxes() {
		if (myCurrentModel == null) {
			myCurrentModel = myDiagram._getModelParent().getModel();
		} else if (myCurrentModel != myDiagram._getModelParent().getModel()) {
			// die alten Checkboxen werden geloescht.
			for (Button b : checkBoxes) {
				b.dispose();
			}
			checkBoxes.clear();
			myCurrentModel = myDiagram._getModelParent().getModel();
		}
		
		// fuer jedes entity im EOModel wird ein Checkbox mit einem listener hinzugefuegt.
		Iterator<EOEntity> entityIterator = myCurrentModel.getEntities().iterator();
		while (entityIterator.hasNext()) {
			final EOEntity entity = entityIterator.next();
			
			// mit dieser abfrage werden die join tables nicht als checkboxen angezeigt.
			if (!entity.getClassNameWithoutPackage().equals("EOGenericRecord")) {
				Button entityCheckBox = null;
				for (Button checkbox : checkBoxes) {
					if (entity.getName().equals(checkbox.getText())) {
						entityCheckBox = checkbox;
						break;
					}
				}
				
				if (entityCheckBox == null) {
					entityCheckBox = new Button(parent, SWT.CHECK);
					entityCheckBox.setText(entity.getName());
					checkBoxes.add(entityCheckBox);
				}
				
				if (myDiagram.getEntities().contains(entity)) {
					entityCheckBox.setSelection(true);
				} else {
					entityCheckBox.setSelection(false);
				}
				entityCheckBox.addSelectionListener(new SelectionAdapter() {
	
			        @Override
			        public void widgetSelected(SelectionEvent event) {
			            Button btn = (Button) event.getSource();
			        	if (btn.getSelection()) {
			        		myDiagram.addEntityToDiagram(entity);
			        	} else if (!btn.getSelection()) {
			        		myDiagram.removeEntityFromDiagram(entity);
			        	}
			        	DiagramTab.getInstance().setSelectedDiagram(myDiagram);
			        	myDiagram._getModelParent().setModelDirty(true);
			        }
			    });
			}
		}
	}
	
	/**
	 * This sets the Selection and binds the data with the to a Node (Text, Label, Button, ComboViewer etc.), 
	 * through selection we get the data which needs to be filled
	 * in our form (which is in the Properties Tab).
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();
		
		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		myDiagram = (EOClassDiagram) selectedObject;
		if (myDiagram != null) {
			myBindingContext = new DataBindingContext();
			myBindingContext.bindValue(SWTObservables.observeText(myNameText, SWT.Modify), BeansObservables.observeValue(myDiagram, AbstractDiagram.NAME), null, null);
			createEntityCheckBoxes();
		}
	}
	
	/**
	 * Disposes the bindings which were created in the setInput() method
	 */
	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}
