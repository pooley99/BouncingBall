import com.BouncingBall.OOrientated.BallWorld;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * MainFullScreenOnly
 * Detects if there is full screen and sets this or sets a window to the maximum resolution
 * adds keylistener for escape and exits program
 */

public class MainFullScreenOnly extends JFrame{

    public MainFullScreenOnly() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (device.isFullScreenSupported()) {
            this.setUndecorated(true);
            this.setResizable(false);
            //this.setIgnoreRepaint(true); //Ignore OS re-paint request
            device.setFullScreenWindow(this);

        } else {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(dim.width, dim.height - 40); //minus task bar
            this.setResizable(true);
        }
        //Allocate the game panel to fill the current screen
        BallWorld ballWorld = new BallWorld(this.getWidth(), this.getHeight());
        this.setContentPane(ballWorld); //set as content pane for this JFrame

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
            }
        });

        this.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("A World of Balls");
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                new MainFullScreenOnly();
            }
        });
    }

}

