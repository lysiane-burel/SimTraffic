import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class SelectionArea extends JLabel{
	
	private float posx = 0;
	private float posy = 0;
	private float w = 0;
	private float h = 0;
    private final UserInterface ui;
    private ImageIcon background; //Pour éviter d'avoir à le recharger à chaque zoom
    
	public SelectionArea(ImageIcon image, UserInterface ui) {
		super(image);
		background = image;
		setOpaque(true);
		this.ui = ui;
		SelectionListener myListener = new SelectionListener(ui);
        addMouseListener(myListener);
        addMouseWheelListener(myListener);
        addMouseMotionListener(myListener);
	}
	
	public float getSelectionX() {
		return posx;
	}
	
	public float getSelectionY() {
		return posy;
	}
	
	public float getSelectionW() {
		return w;
	}
	
	public float getSelectionH() {
		return h;
	}
	
	public int[] getBackgroundHandW() {
		int[] hAndW = new int[2];
		hAndW[0] = background.getIconHeight();
		hAndW[1] = background.getIconWidth();
		return hAndW;
	}

	public final void setSelectionPos(float x, float y) {
		posx = x;
		posy = y;
		w = 0;
		h = 0;
	}
	
	public final void setSelectionSize(float x, float y) {
		w = x - posx;
		h = y - posy;
	}
	
	public final void updateBG(float posx, float posy, float zoom) {
		//Zoom de l'image
		Image resizeImage = (background.getImage()).getScaledInstance((int)(background.getIconWidth()*zoom), (int)(background.getIconHeight()*zoom), Image.SCALE_SMOOTH);
		
		//Déplacement de l'image
		Image movedImage = createImage(new FilteredImageSource(resizeImage.getSource(),new CropImageFilter((int)(posx*zoom), (int)(posy*zoom),823,659)));
		
		ImageIcon icon = new ImageIcon(movedImage);
		setIcon(icon);
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(16711680));
        
        Rectangle rectToDraw = new Rectangle((int)((posx - ui.getUIModel().getX()) * ui.getUIModel().getZoom()),(int)((posy - ui.getUIModel().getY()) * ui.getUIModel().getZoom()),(int)(w * ui.getUIModel().getZoom()),(int)(h * ui.getUIModel().getZoom()));
        
        int x = Math.min(rectToDraw.x,rectToDraw.x + rectToDraw.width);
        int y = Math.min(rectToDraw.y,rectToDraw.y + rectToDraw.height);
        
        //g.drawRect(x, y, Math.abs(rectToDraw.width)-1, Math.abs(rectToDraw.height)-1);
        g.drawRect(x-1, y-1, Math.abs(rectToDraw.width), Math.abs(rectToDraw.height));
        //g.drawRect(x-1, y-1, Math.abs(rectToDraw.width)-1, Math.abs(rectToDraw.height)-1);
        ui.getUIModel().stateChanges();;
    }
	
	public void notifyForUpdate() {
		repaint();
	}
}
