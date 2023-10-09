package snake;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

public class Snake extends JFrame{
    static final int MAX = 500;
    
    Snake() {
        super("Змейка");
        setSize(MAX + 20, MAX + 70);
        Container c = getContentPane();
        CounterPanel cp = new CounterPanel();
        c.add(cp, BorderLayout.NORTH);
        Gameplay gp = new Gameplay();
        c.add(gp,BorderLayout.CENTER);
        gp.setCounterPanel(cp);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
    }

    public static void main(String[] args) {
        new Snake();
    }
    
}
