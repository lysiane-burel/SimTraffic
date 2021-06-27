
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.SwingUtilities;


public class SelectionListener extends MouseInputAdapter {
	
	private UserInterface ui;
	
	public SelectionListener(UserInterface ui) {
		super();
		this.ui = ui;
	}
	
    public void mousePressed(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		if (ui.getUIModel().getMode()) {
    			ui.getUIModel().setSelection(e.getX(),e.getY());
    		} else {
    			ui.getUIModel().setOldPos(e.getX(),e.getY());
    		}
    	}
    	
    	if (SwingUtilities.isMiddleMouseButton(e)) {
    		ui.getUIModel().setOldPos(e.getX(),e.getY());
    	}
    }

    public void mouseDragged(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		if (ui.getUIModel().getMode()) {
    			ui.getUIModel().updateSelection(e.getX(), e.getY());
    		} else {
    			ui.getUIModel().updatePos(e.getX(), e.getY());
    		}
    	}
    	
    	if (SwingUtilities.isMiddleMouseButton(e)) {
    		ui.getUIModel().updatePos(e.getX(), e.getY());
    	}
    }

    public void mouseReleased(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		if (ui.getUIModel().getMode()) {
    			ui.getUIModel().updateSelection(e.getX(), e.getY());
    		} else {
    			ui.getUIModel().updatePos(e.getX(), e.getY());
    		}
    	}
    	
    	if (SwingUtilities.isMiddleMouseButton(e)) {
    		ui.getUIModel().updatePos(e.getX(), e.getY());
    		
        	//On met à jour la taille de la fenêtre
        	ui.pack();
    	}
    }
    
    //Zoom
    public void mouseWheelMoved(MouseWheelEvent e) {
    	ui.getUIModel().setZoom(e.getWheelRotation(),e.getX(),e.getY(),true);
    }
}
