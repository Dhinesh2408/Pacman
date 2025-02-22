import javax.swing.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man");
        PacMan pacmanGame = new PacMan();
        
        frame.add(pacmanGame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
