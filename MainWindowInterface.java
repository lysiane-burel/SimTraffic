import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.*;


public class MainWindowInterface extends JFrame{
    
	public MainWindowInterface() {
		super("SimTraffic");
		setVisible(true);
		setSize(500,500);
		
		MenuBar menuBar = new MenuBar();
		setMenuBar(menuBar);
		
		final MainWindowInterface window = this;
		
		//Fichier
		Menu fichier = new Menu("Fichier");
		menuBar.add(fichier);
		
		MenuItem newMap = new MenuItem("Nouvelle carte");
        newMap.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		 //CrÃ©er une nouvelle carte
            }  
        });
        fichier.add(newMap);
        
        
		MenuItem open = new MenuItem("Ouvrir");
		open.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		 //Ouvrir une carte
        		FileDialog fileDialog = new FileDialog(window,"Choisir un fichier",FileDialog.LOAD);
        		fileDialog.setVisible(true);
        	}  
        });
        fichier.add(open);
        
        
        MenuItem save = new MenuItem("Enregistrer");
		save.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		 //Enregistrer une carte
        	}  
        });
        fichier.add(save);
        
        
		MenuItem savePrompt = new MenuItem("Enregistrer sous");
		savePrompt.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		 //Enregistrer une carte
        		FileDialog fileDialog = new FileDialog(window,"Choisir une destination",FileDialog.SAVE);
        		fileDialog.setVisible(true);
        	}
        });
		fichier.add(savePrompt);
		
		
        MenuItem quit = new MenuItem("Quitter");
        quit.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		//Quitter le logiciel
        		System.exit(NORMAL);
        	}  
        });
        fichier.add(quit);
		
        //Edition
		Menu edition = new Menu("Edition");
		menuBar.add(edition);
		
        MenuItem undo = new MenuItem("Annuler");
        undo.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Annuler la derniÃ¨re action
        	}  
        });
        edition.add(undo);
        
        
        MenuItem redo = new MenuItem("R�tablir");
        redo.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//RÃ©tablir la derniÃ¨re action
        	}  
        });
        edition.add(redo);
        
		
        MenuItem loadMap = new MenuItem("Charger depuis OpenStreetMap");
        loadMap.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Ouvre la page pour choisir la zone
        		UserInterface ui = new UserInterface();
        	}  
        });
        edition.add(loadMap);
        
        
        MenuItem showProblems = new MenuItem("Afficher les points de congestion");
        showProblems.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Ouvre la page avec la liste des pbs Ã  corriger
        	}  
        });
        edition.add(showProblems);
        
		
		//Affichage
		Menu affichage = new Menu("Affichage");
		menuBar.add(affichage);
		
		
        MenuItem defaultZoom = new MenuItem("R�tablir le zoom par d�faut");
        defaultZoom.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//RÃ©tablir le zoom par dÃ©faut
        	}  
        });
        affichage.add(defaultZoom);
        
        
        MenuItem centerView = new MenuItem("Recentrer la vue");
        centerView.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Recentre la vue sur le problÃ¨me choisi
        	}  
        });
        affichage.add(centerView);
		
		//Outils
		Menu outils = new Menu("Outils");
		menuBar.add(outils);
		
		
        MenuItem showToolsBox = new MenuItem("Afficher la bo�te � outils");
        showToolsBox.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Ouvre la boite Ã  outils
        	}  
        });
        outils.add(showToolsBox);
        
        
        MenuItem compute = new MenuItem("Lancer les calculs");
        compute.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Lance SUMO
        	}  
        });
        outils.add(compute);
        
        
        MenuItem settings = new MenuItem("Param�tres de calcul");
        settings.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Ouvre la page avec les paramÃ¨tres
        	}  
        });
        outils.add(settings);
		
		//Aide
		Menu aide = new Menu("Aide");
		menuBar.add(aide);
		
        MenuItem SimTrafficHelp = new MenuItem("Aide de SimTraffic");
        SimTrafficHelp.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Aide
        	}  
        });
        aide.add(SimTrafficHelp);
        
        
        MenuItem SUMOHelp = new MenuItem("Aide de SUMO");
        SUMOHelp.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Aide
        		try {
        	        Desktop.getDesktop().browse(new URL("https://sumo.app/help").toURI());
        	    } catch (Exception ex) {
        	        ex.printStackTrace();
        	    }
        	}  
        });
        aide.add(SUMOHelp);
        
        
        MenuItem OSMHelp = new MenuItem("Aide d'OpenStreetMap");
        OSMHelp.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		//Aide
        		try {
        	        Desktop.getDesktop().browse(new URL("https://www.openstreetmap.org/help").toURI());
        	    } catch (Exception ex) {
        	        ex.printStackTrace();
        	    }
        	}  
        });
        aide.add(OSMHelp);
        
        
	}
}
