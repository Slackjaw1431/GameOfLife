import javax.swing.SwingUtilities;

import gui.MainFrame;

public class MainApp {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->new MainFrame("Game of Life"));
	//swing utilitiles creates a loop of events on a swing thread
		//calls the paintcomponent
	}

}
