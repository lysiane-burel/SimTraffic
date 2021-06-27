
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;  

public class UserInterface extends JFrame implements ChangeListener{
	
	private SelectionArea area;
	private final UIModel uIModel = new UIModel();
	private final JToggleButton selectButton = new JToggleButton("Sélection");
	private final JToggleButton moveButton = new JToggleButton("Déplacer");
	private final JScrollBar zoomBar = new JScrollBar(JScrollBar.HORIZONTAL);
	
	public UserInterface() {
		super("Importer une zone");
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		uIModel.addObserver(this);
		uIModel.setUI(this);
		ImageIcon image;
		try {
			BufferedImage originalImage = ImageIO.read(new File("../data/carte_paris.png"));
			Image resizeImage = originalImage.getScaledInstance((int)(originalImage.getWidth()*0.7), (int)(originalImage.getHeight()*0.7), Image.SCALE_SMOOTH);
			image = new ImageIcon(resizeImage);
		}
		catch(Exception e) {
			e.printStackTrace();
			image = new ImageIcon("../data/carte_paris.png");
		}
		
		area = new SelectionArea(image,  this);
		
		uIModel.setArea(area);
		
		//Carte
        add(area, BorderLayout.CENTER);
        
        final UserInterface ui = this;
        
        //Panel du bas
        JPanel bottomPanel = new JPanel();
        add(bottomPanel,BorderLayout.SOUTH);
        bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.PAGE_AXIS));
        
        bottomPanel.add(Box.createRigidArea(new Dimension(0,10)));
        
        //Panel pour les boutons de mode et le zoom
        JPanel modeZoomPanel = new JPanel();
        bottomPanel.add(modeZoomPanel);
        modeZoomPanel.setLayout(new BoxLayout(modeZoomPanel,BoxLayout.LINE_AXIS));
        modeZoomPanel.add(Box.createRigidArea(new Dimension(10,0)));
        
        //Panel pour les boutons pour changer de mode (sÃ©lection ou dÃ©placement)
        JPanel modeButtonsPanel = new JPanel();
        modeZoomPanel.add(modeButtonsPanel);
        modeButtonsPanel.setLayout(new BoxLayout(modeButtonsPanel,BoxLayout.PAGE_AXIS));
        modeButtonsPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel text = new JLabel("Mode");
        text.setAlignmentX(CENTER_ALIGNMENT);
        modeButtonsPanel.add(text);
        
        modeButtonsPanel.add(Box.createRigidArea(new Dimension(0,5)));
        
        ActionListener actionselect = new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		if (ui.moveButton.isSelected()) {
        		 ui.getUIModel().setMode(true);
        		 ui.selectButton.setSelected(true);
        		 ui.moveButton.setSelected(false);
        		} else {
        			ui.selectButton.setSelected(true);
        		}
            }  
        };
        selectButton.addActionListener(actionselect);
        modeButtonsPanel.add(selectButton);
        selectButton.setSelected(true);
        selectButton.setAlignmentX(CENTER_ALIGNMENT);
        
        modeButtonsPanel.add(Box.createRigidArea(new Dimension(0,3)));
        
        ActionListener actionmove = new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){
        		if (ui.selectButton.isSelected()) {
           		 ui.getUIModel().setMode(false);
           		 ui.selectButton.setSelected(false);
           		 ui.moveButton.setSelected(true);
           		} else {
           			ui.moveButton.setSelected(true);
           		}
            }  
        };
        moveButton.addActionListener(actionmove);
        modeButtonsPanel.add(moveButton);
        moveButton.setAlignmentX(CENTER_ALIGNMENT);
        
        //Panel du zoom
        modeZoomPanel.add(Box.createRigidArea(new Dimension(10,0)));
        
      	JPanel rightPanel = new JPanel();
      	modeZoomPanel.add(rightPanel);
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
        
        text = new JLabel("Zoom");
        text.setAlignmentX(CENTER_ALIGNMENT);
        rightPanel.add(text);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0,5)));
              
      	AdjustmentListener actionzoom = new AdjustmentListener(){	
      		public void adjustmentValueChanged(AdjustmentEvent e){
      			ui.uIModel.setZoom((float)(Math.log((1 + (float)e.getValue()/20)/ui.getUIModel().getZoom())/Math.log(0.9)),823/2,659/2,false);
        	}
        };
        
        zoomBar.addAdjustmentListener(actionzoom);
        rightPanel.add(zoomBar);
              
        //Panel pour les boutons appliquer et annuler
        JPanel windowButtonsPanel = new JPanel();
        bottomPanel.add(windowButtonsPanel,BorderLayout.SOUTH);
        windowButtonsPanel.setLayout(new FlowLayout());
        
        JButton showButton = new JButton("Charger la zone");
        showButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){
        		JFrame frameBegin = new JFrame();
        		frameBegin.setTitle("Calcul en cours...");
        		JOptionPane.showMessageDialog(frameBegin, "Calculs en cours, veuillez patienter...");
        		frameBegin.pack();
        		
        		//LoadingWindowInterface loadWindow = new LoadingWindowInterface();
        		
        		double[] coord = getUIModel().getCoordonneesGeo();
        		
        		String[] pathAndArgs = new String[9];
        		pathAndArgs[0] = "python";
        		pathAndArgs[1] = "ProgrammePrincipal.py";
        		for (int i = 2; i<9; i++) {
        			pathAndArgs[i] = Double.toString(coord[i-2]);
        			System.out.println(Double.toString(coord[i-2]));
        		} 
        		ScriptPython scriptPython = new ScriptPython();
                scriptPython.runScript(pathAndArgs);
                
                MainGenetique.mainGenetique();
                
                String[] pathAndArgs2 = new String[6];
        		pathAndArgs2[0] = "python";
        		pathAndArgs2[1] = "AffichageResultat.py";
        		for (int i = 2; i<6; i++) {
        			pathAndArgs2[i] = Double.toString(coord[i-2]);
        			System.out.println(Double.toString(coord[i-2]));
        		} 
        		ScriptPython scriptPython2 = new ScriptPython();
                scriptPython2.runScript(pathAndArgs2);
                
                frameBegin.dispose();
                
                JFrame frameEnd = new JFrame();
        		frameEnd.setTitle("Calcul terminé !");
        		JOptionPane.showMessageDialog(frameEnd, "Le calcul est terminé.");
        		frameEnd.pack();
        	}
        });
        
        windowButtonsPanel.add(showButton);
        
        JButton cancelButton = new JButton("Annuler");
        ActionListener actioncancel = new ActionListener(){
        	
        	public void actionPerformed(ActionEvent e){  
        		 ui.dispose();
            }  
        };
        cancelButton.addActionListener(actioncancel);
        windowButtonsPanel.add(cancelButton);
        
        modeZoomPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
        pack();
	}
	
	public UIModel getUIModel() {
		return uIModel;
	}
	
	public void stateChanged(ChangeEvent evt) {
		area.notifyForUpdate();
	}
	
	public JScrollBar getZoomBar() {
		return zoomBar;
	}
}
