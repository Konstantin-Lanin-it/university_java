package snake;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class CounterPanel extends JPanel {
    static private int DELTA = 5;
    private int counter;
    private JLabel counterLabel = new JLabel("0");
    
    CounterPanel () {
        //something for beauty
        JLabel textLabel = new JLabel("Очки ");
        add(textLabel);
        add(counterLabel);
    }
    public void clean() {
        counterLabel.setText(""+ (counter = 0));
    }
    public void update() {
        counterLabel.setText(""+ (counter += DELTA));
    }
}
