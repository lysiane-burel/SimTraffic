import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.event.*;
import javax.swing.JLabel;

public class UIModel {
	private UserInterface ui = null;
	private SelectionArea area = null;
	/*
	private static double startLatitude = 48.901616; //22 Avenue du Pr�sident Wilson, 75018 Saint-Denis, France 48.901616 2.359531
	private static double startLongitude = 2.257692; // 6 place de la porte de saint-cloud 75016 Paris 48.8383129 2.257692
	*/
	private static double startLatitude = 48.891;//48.901261;
	private static double startLongitude = 2.255010;
	
	private static double endLatitude = 48.8164643; //27 Avenue de la Porte d'Italie, 75013 Paris 13e Arrondissement, France 48.8164643 2.3608403
	private static double endLongitude = 2.4159027; //CGT, 265 Avenue Beno�t Frachon, 75020 Paris, France 2.4159027 48.8539923 
	private static double deltaLat = 48.901616 - endLatitude;
	private static double deltaLong = endLongitude - startLongitude;
	
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>() ;
	private float zoom = 1;
	private float posx = 0;
	private float posy = 0;
	private float oldPosx = 0;
	private float oldPosy = 0;
	private float oldMousex = 0;
	private float oldMousey = 0;
	private boolean mode = true; //false pour d�filer, true pour zoom
	
	private float selecx;
	private float selecy;
	private float selech;
	private float selecw;
	
	public double[] getSelec() {
		double[] coord = {selecx, selecy, selech, selecw};
		return coord;
	}
	public void setUI(UserInterface ui) {
		this.ui = ui;
	}
	
	public final boolean getMode() {
		return mode;
	}
	
	public final void setMode(boolean b) {
		mode = b;
	}
	
	public final SelectionArea getArea() {
		return area;
	}
	public final void setArea(SelectionArea area) {
		this.area = area;
	}

	public void addObserver(ChangeListener listener) {
		listeners.add(listener) ;
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float mult,int mousex, int mousey,boolean changeScrollBar) {
		float m = (float)Math.pow(0.9,mult);
		zoom = zoom*m;
		posx -= mousex*(1-m)/zoom;
		posy -= mousey*(1-m)/zoom;
		area.updateBG(posx,posy,zoom);
		
		if (changeScrollBar) {
			ui.getZoomBar().setValue((int)(20*(zoom-1)));
		}
	}
	
	public void setSelection(int x, int y) {
		area.setSelectionPos(posx + (float)x/zoom,posy + (float)y/zoom);
		selecx = x;
		selecy = y;
		
	}
	
	public void updateSelection(int x, int y) {
		area.setSelectionSize(posx + (float)x/zoom,posy + (float)y/zoom);
		
		selech = x;
		selecw = y;
	}
	
	public float getX() {
		return posx;
	}
	
	public float getY() {
		return posy;
	}
	
	public double[] getCoordonneesGeo(){
		int[] backgroundHandW = area.getBackgroundHandW();
		double startLong = startLongitude+((double)(area.getSelectionX())/backgroundHandW[1])*deltaLong;
		double startLat = startLatitude -((double)(area.getSelectionY())/backgroundHandW[0])*deltaLat;
        double endLat = startLatitude-((double)(area.getSelectionY() + area.getSelectionH())/backgroundHandW[0])*deltaLat;
        double endLong = startLongitude+((double)(area.getSelectionX() + area.getSelectionW())/backgroundHandW[1])*deltaLong;
        double width = endLong - startLong;
        double height = startLat - endLat;
        double centerX = startLong + width/2;
        double centerY = endLat + height/2;
        double earthRadius = 6378.137; 
        double dLat = startLat * Math.PI / 180 - endLat * Math.PI / 180; //delta latitude in radian
        double dLong = endLong * Math.PI / 180 - startLong * Math.PI / 180; // delta long in radian
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(startLat * Math.PI / 180) * Math.cos(endLat * Math.PI / 180) * Math.sin(dLong/2) * Math.sin(dLong/2);
        double diagonalLength = 2*earthRadius*Math.asin(Math.sqrt(a))*1000; //length of rectangle's diagonal in meters
        double[] coord = {startLong, endLat, endLong, startLat, centerY, centerX, diagonalLength/2};
        return coord;
	}
	
	public void setOldPos(int mousex,int mousey) {
		oldPosx = posx;
		oldPosy = posy;
		oldMousex = mousex;
		oldMousey = mousey;
		stateChanges();
	}
	
	public void updatePos(int mousex, int mousey) {
		posx = oldPosx - (float)(mousex - oldMousex)/zoom;
		posy = oldPosy - (float)(mousey - oldMousey)/zoom;
		area.updateBG(posx,posy,zoom);
		stateChanges();
	}
	
	public void stateChanges() {
        ChangeEvent evt = new ChangeEvent(this) ;
        for (ChangeListener listener : listeners)
        	listener.stateChanged(evt);
	}
	
	
}
