import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y, width, height, startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;
        Image image;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            updatePacmanImage();  // Update Pac-Man image on direction change
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                    updatePacmanImage();  // Restore the correct image if blocked
                }
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> { velocityX = 0; velocityY = -tileSize / 4; }
                case 'D' -> { velocityX = 0; velocityY = tileSize / 4; }
                case 'L' -> { velocityX = -tileSize / 4; velocityY = 0; }
                case 'R' -> { velocityX = tileSize / 4; velocityY = 0; }
            }
        }

        void updatePacmanImage() {
            if (this == pacman) {  // Only update Pac-Man's image
                switch (this.direction) {
                    case 'U' -> this.image = pacmanUpImage;
                    case 'D' -> this.image = pacmanDownImage;
                    case 'L' -> this.image = pacmanLeftImage;
                    case 'R' -> this.image = pacmanRightImage;
                }
            }
        }
        

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21, columnCount = 19, tileSize = 32;
    private final int boardWidth = columnCount * tileSize, boardHeight = rowCount * tileSize;
    private final Random random = new Random();
    private final Timer gameLoop;
    private final char[] directions = {'U', 'D', 'L', 'R'};
    
    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private final HashSet<Block> walls = new HashSet<>();
    private final HashSet<Block> foods = new HashSet<>();
    private final HashSet<Block> ghosts = new HashSet<>();
    
    private Block pacman;
    private int score = 0, lives = 3;
    private boolean gameOver = false;

    private final String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX", 
        "X        X        X", 
        "X XX XXX X XXX XX X",
        "X X               X", 
        "X XX X XXXXX X XX X", 
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX", 
        "XOOX X       X XOOX", 
        "XXXX X X   X X XXXX",
        "X   r    b    o   X", 
        "XXXX X XXXXX X XXXX", 
        "XOOX X       X XOOX",
        "XXXX X XXXXX X XXXX", 
        "X X             X X", 
        "X XX XXX X XXX XX X",
        "X  X     P     X  X", 
        "XX X X XXXXX X X XX", 
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X", 
        "X                 X", 
        "XXXXXXXXXXXXXXXXXXX"
    };    
        

    public PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        loadImages();
        loadMap();
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }
    //private Image backgroundImage;
    private void loadImages() {
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        // Load the background image
       // backgroundImage = new ImageIcon(getClass().getResource("./background.png")).getImage();
    }

    private void loadMap() {
        walls.clear();
        foods.clear();
        ghosts.clear();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                int x = c * tileSize, y = r * tileSize;
                char tile = tileMap[r].charAt(c);
                switch (tile) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", boardWidth / 3, boardHeight / 2);
            
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("Press 'R' to Restart", boardWidth / 3 + 10, boardHeight / 2 + 40);
            
            gameLoop.stop(); // Stop the game loop
            return;
        }
    
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    
        g.setColor(Color.WHITE);
        for (Iterator<Block> it = foods.iterator(); it.hasNext();) {
            Block food = it.next();
            if (collision(pacman, food)) {
                it.remove();
                score += 10;
            } else {
                g.fillRect(food.x, food.y, food.width, food.height);
            }
        }
    
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("x" + lives + " Score: " + score, tileSize / 2, tileSize / 2);
    }
    
    

    /*@Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }*/
    @Override
    public void actionPerformed(ActionEvent e) {
    
        // Check collision with walls for Pac-Man
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }
    
        // Move ghosts
        for (Block ghost : ghosts) {
            int prevX = ghost.x;
            int prevY = ghost.y;
    
            // Move in the current direction
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
    
            // Check collision with walls for ghosts
            boolean hitWall = false;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    hitWall = true;
                    break;
                }
            }
    
            // If a wall is hit, revert and change direction
            if (hitWall) {
                ghost.x = prevX;
                ghost.y = prevY;
                ghost.updateDirection(directions[new Random().nextInt(4)]);
            }
        }
    
        // Check collision with ghosts
        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    pacman.reset(); // Reset Pac-Man if lives remain
                }
            }
        }
    
        repaint();
    }
    

    @Override
public void keyPressed(KeyEvent e) {
    if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
        restartGame(); // Call restart method
            } else {
                pacman.updateDirection(switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> 'U';
                    case KeyEvent.VK_DOWN -> 'D';
                    case KeyEvent.VK_LEFT -> 'L';
                    case KeyEvent.VK_RIGHT -> 'R';
                    default -> pacman.direction;
                });
            }
        }
        
        
        private void restartGame() {
            gameOver = false;
            lives = 3;
            score = 0;
            loadMap();  // Reload the entire map
            gameLoop.start(); // Restart the game loop
            repaint();
        }        
        
            @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
