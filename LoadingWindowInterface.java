import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.*;

public class LoadingWindowInterface  extends JFrame{
	private JProgressBar progressBar = new JProgressBar();
	private JLabel label = new JLabel();
	
	public LoadingWindowInterface() {
		super("Calcul en cours...");
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		//La barre de progression
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		//Le petit texte
		label.setText(String.format("Veuillez patienter, %d générations ont été calculées.", 0));
		label.setAlignmentX(CENTER_ALIGNMENT);
		
		//Mise en page
		container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		container.add(Box.createRigidArea(new Dimension(10,0)));
		
		container.add(panel, BorderLayout.CENTER);
		panel.add(Box.createRigidArea(new Dimension(0,10)));
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(0,5)));
		panel.add(progressBar);
		panel.add(Box.createRigidArea(new Dimension(0,10)));
		
		container.add(Box.createRigidArea(new Dimension(10,0)));
		
		//Curseur de chargement
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		pack();
	}
	
	//Met Ã  jour l'avancement
	public void update(int percent, int numberGenerationsDone) {
		progressBar.setValue(percent);
		label.setText(String.format("Veuillez patienter, %d générations ont été calculées.", numberGenerationsDone));
	}
	
	//Quand c'est fini, Ã§a ferme la fenÃªtre et ouvre un pop-up
	public void done() {
		dispose();
		JFrame frame = new JFrame();
		frame.setTitle("Calcul terminé !");
		JOptionPane.showMessageDialog(frame, "Le calcul est terminé.");
		frame.pack();
	}
	
}
