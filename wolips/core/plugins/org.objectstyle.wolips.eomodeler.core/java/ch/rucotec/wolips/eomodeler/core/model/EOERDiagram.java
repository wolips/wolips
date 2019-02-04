package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;

/**
 * The {@code EOERDiagram} represents a class diagram and extends {@link AbstractDiagram}
 * most methods in this class are just Overrides from its extended classes.
 * 
 * @author celik
 * @see AbstractDiagram
 */
public class EOERDiagram extends AbstractDiagram<EOERDiagramCollection>{
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * This constructor calls the constructor of its super and gives the name as parameter.
	 * 
	 * @param name
	 */
	public EOERDiagram(String name) {
		super(name);
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	/**
	 * Finds the {@link EOEntityERDiagram} for the given entity.
	 * If there is none than one is created and is handed over to the 
	 * {@code addEntityToDiagram(AbstractEOEntityDiagram entityDiagram)}
	 * 
	 */
	@Override
	public void addEntityToDiagram(EOEntity entity) {
		EOEntityERDiagram entityERDiagram = (EOEntityERDiagram) getEntityDiagramWithEntity(entity);
		if (entityERDiagram == null) {
			entityERDiagram = new EOEntityERDiagram(entity, _getModelParent());
		}
		EOEntityDiagramDimension dimension = new EOEntityDiagramDimension(100, 100, 100, 100);
		entityERDiagram.getDiagramDimensions().put(getName(), dimension);
		super.addEntityToDiagram(entityERDiagram);
	}
	
	/**
	 * Creates a new object of {@code EOERDiagram} and returns it.
	 */
	@Override
	protected AbstractDiagram createDiagram(String name) {
		return new EOERDiagram(name);
	}

	public void setName(String _name) throws DuplicateNameException {
		setName(_name, true);
	}
	
	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		String name = _name;
		if (name == null || name.isEmpty()) {
			return;
//			name = _getModelParent().findUnusedERDiagramName(Messages.getString("AbstractDiagram.noBlankDiagramNames"));
		}
		if (_getModelParent() != null) {
			_getModelParent().checkForDuplicateERDiagramName(this, name, null);
		}
		super.setName(name, _fireEvents);
	}
	
	@Override
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	@Override
	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (_getModelParent() != null) {
			_getModelParent().erdiagramChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return ((_getModelParent() == null) ? "?" : _getModelParent().getFullyQualifiedName()) + "/erdiagram: " + getName();
	}

	@Override
	public EOERDiagram _cloneModelObject() {
		EOERDiagram erdiagram = (EOERDiagram)cloneDiagram();
		return erdiagram;
	}

	@Override
	public Class<EOERDiagramCollection> _getModelParentType() {
		return EOERDiagramCollection.class;
	}

	/**
	 * Removes this from its parent.
	 */
	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		_getModelParent().removeERDiagram(this);
	}

	/**
	 * Adds this to the given modelParent.
	 */
	@Override
	public void _addToModelParent(EOERDiagramCollection modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedDiagramName(getName()));
		}
		modelParent.addERDiagram(this);
	}
	
	// Drawings
	@Override
	public SimpleDiagram drawDiagram() {
	SimpleDiagram myERD = new SimpleDiagram();
	HashMap<String, DiagramNode> entityNodeMap = new HashMap<String, DiagramNode>();
	Set<String> horizontalParents = new HashSet<String>();
	Set<String> neededParents = new HashSet<String>();
	
	for (AbstractEOEntityDiagram entityERD : getDiagramEntities()) {
		DiagramNode entityNode = entityERD.draw(getName());
		EOEntity entity = entityERD.getEntity();
		entityNodeMap.put(entity.getName(), entityNode);
	}
	
	for (DiagramNode node : entityNodeMap.values()) {
		EOEntity nodeEntity = node.getEntityDiagram().getEntity();
		
		// Inheritance
		if (nodeEntity.isHorizontalInheritance()) {
			if (!neededParents.contains(nodeEntity.getParent().getName())) {
//				horizontalParents.add(nodeEntity.getParent().getName()); 
			}
			node.removeParentAttributes(nodeEntity.getParent().getPrimaryKeyAttributes());
			node.removeRelationshipToParent();
		} else if (nodeEntity.isVerticalInheritance()) {
			if (entityNodeMap.get(nodeEntity.getParent().getName()) != null) {
				int sourceToTargetCardinality = 0;
				int targetToSourceCardinality = 0;
				
				sourceToTargetCardinality = DiagramConnection.TOONE;
				targetToSourceCardinality = DiagramConnection.TOONE + DiagramConnection.OPTIONAL;
				neededParents.add(nodeEntity.getParent().getName());
				
				DiagramConnection conn = new DiagramConnection(DiagramType.ERDIAGRAM);
				conn.connect(node, entityNodeMap.get(nodeEntity.getParent().getName()));
				conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);
		
				myERD.addChildElement(conn);
			}
		} else if (nodeEntity.isSingleTableInheritance()) {
			DiagramNode parentNode = entityNodeMap.get(nodeEntity.getParent().getName());
			if (parentNode != null) {
				parentNode.addChildrenAttributes(nodeEntity.getAttributes());
				parentNode.addChildrenRelationships(nodeEntity.getRelationships());
			}
			/* if the parentNode is already registered in myERD we delete it and
			 * reference node to parentNode, thus the relationship loop below will be executed
			 */
			if (parentNode != null && myERD.getChildElements().contains(parentNode)) {
				myERD.getChildElements().remove(parentNode);
				node = parentNode;
			} else {
				continue;
			}
		}
		
		for (EORelationship relationship : node.getRelationshipsList()) {
			boolean manyToManyConnection = false;
			
			if (relationship.isToMany() && getEntities().contains(relationship.getDestination())) {
				EOEntity sourceEntity = relationship.getEntity();
				EOEntity destinationEntity = relationship.getDestination();
				Iterator<EORelationship> destinationRelationshipIterator = destinationEntity.getRelationships().iterator();

				int sourceToTargetCardinality = 0;
				int targetToSourceCardinality = 0;
				
				if (destinationEntity.isVerticalInheritance() && destinationEntity.getParent() == sourceEntity) {
					continue;
				}

				// löst das Many to Many Problem und fügt die notwendigen Kardinalitäten ein.
				while (destinationRelationshipIterator.hasNext()) {
					EORelationship destinationRelationship = destinationRelationshipIterator.next();

					if (destinationRelationship.getDestination() == sourceEntity) {
						// Hier werden die Kardinalitäten erstellt..
						if (destinationRelationship.isToMany()) {
							manyToManyConnection = true;
						} else {
							manyToManyConnection = false;
							sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
							targetToSourceCardinality = DiagramConnection.TOONE + (destinationRelationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
						}

						if (!manyToManyConnection) {
							// TODO rekursive beziehungen werden hier einfach uebersprungen, hier sollte das irgendwie gehandelt werden.
							DiagramNode destinationNode = entityNodeMap.get(relationship.getDestination().getName());
							if (destinationNode.getEntityDiagram().getEntity().isSingleTableInheritance()) {
								destinationNode = entityNodeMap.get(destinationNode.getEntityDiagram().getEntity().getParent().getName());
							}
							if (node != destinationNode && !node.getTitle().equals(destinationNode.getTitle())) {
								DiagramConnection conn = new DiagramConnection(DiagramType.ERDIAGRAM);
								conn.connect(node, destinationNode);
								conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);
	
								myERD.addChildElement(conn);
								neededParents.add(nodeEntity.getName());
							}
						}
					}
				}

			}

		}
		// add node to mindMap
		myERD.addChildElement(node);
	}
	
	/* Delete the ParentNodes of horizontal inheritance if it's 
	 * not needed from other Nodes.
	 */
	for (String horizontalParent : horizontalParents) {
		DiagramNode parentNode = entityNodeMap.get(horizontalParent);
		if (parentNode != null && !neededParents.contains(horizontalParent)) {
			myERD.removeChildElement(parentNode);
		}
	}
	return myERD;
}

}
