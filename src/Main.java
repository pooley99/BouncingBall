import com.BouncingBall.OOrientated.BallWorld;
import javax.swing.*;

/**
 * Main
 * runs the gui in its own thread to allow the EDT (Event Dispatcher Thread) to handlle the GUI.
 *
 */
public class Main {

    public static void main(String[] args){

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("A World of Balls");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new BallWorld(640, 480));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
