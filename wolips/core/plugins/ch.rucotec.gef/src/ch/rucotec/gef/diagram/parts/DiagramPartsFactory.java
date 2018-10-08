package ch.rucotec.gef.diagram.parts;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import ch.rucotec.gef.diagram.model.DiagramConnection;
import ch.rucotec.gef.diagram.model.DiagramNode;
import ch.rucotec.gef.diagram.model.SimpleDiagram;
import javafx.scene.Node;

/**
 * The {@link DiagramPartsFactory} creates a Part for the mind map models, based
 * on the type of the model instance.
 *
 */
public class DiagramPartsFactory implements IContentPartFactory {

    @Inject
    private Injector injector;

    @Override
    public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        if (content instanceof SimpleDiagram) {
            return injector.getInstance(SimpleDiagramPart.class);
        } else if (content instanceof DiagramNode) {
            return injector.getInstance(DiagramNodePart.class);
        } else if (content instanceof DiagramConnection) {
            return injector.getInstance(DiagramConnectionPart.class);
        } else {
            throw new IllegalArgumentException("Unknown content type <" + content.getClass().getName() + ">");
        }
    }
}