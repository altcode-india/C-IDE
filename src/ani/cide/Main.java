package ani.cide;

import javax.swing.*;

public class Main {
    public static void main(String args[]){
        SwingUtilities.invokeLater(()-> {
            try {
                UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
            } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
            MainForm.main(args);
        });
    }
}
