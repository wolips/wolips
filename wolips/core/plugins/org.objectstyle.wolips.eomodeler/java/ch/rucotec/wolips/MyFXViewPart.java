package ch.rucotec.wolips;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

import com.sun.javafx.stage.EmbeddedWindow;

import ch.rucotec.gef.diagram.SimpleDiagramApplication;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class MyFXViewPart extends EditorPart {
	private FXCanvas canvas;
	private SimpleDiagramApplication gefApplication;
	private EOModel myModel;

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent, SWT.NONE);
		canvas.setScene(createFxScene());

		try {
			Class canvasClass = Class.forName("javafx.embed.swt.FXCanvas");
			Field stage = canvasClass.getDeclaredField("stage");
			stage.setAccessible(true);
			EmbeddedWindow ss = (EmbeddedWindow) stage.get(canvas);
			gefApplication = new SimpleDiagramApplication();
			gefApplication.start(ss);

		} catch (Exception e) {
			System.err.println("Error in MyFXViewPart: " + e.getCause());
		}

	}

	protected abstract Scene createFxScene();

	@Override
	public void setFocus() {
		canvas.setFocus();
		setFxFocus();
	}

	public SimpleDiagramApplication getGefApplication() {
		return gefApplication;
	}

	public Stage getStage() {
		return (Stage) canvas.getScene().getWindow();
	}

	public void setModel(EOModel _model) {
		myModel = _model;
		gefApplication.generateErd(myModel);
	}
	
	public EOModel getModel() {
		return myModel;
	}

	protected abstract void setFxFocus();
}
