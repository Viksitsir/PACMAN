import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;


public class PacMan extends JPanel implements ActionListener, KeyListener{

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            startX = x;
            startY = y;
        }

        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += velocityX;
            this.y += velocityY;

            for(Block wall:walls){
                if(collision(this, wall)){
                    this.x -= velocityX;
                    this.y -= velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if(direction == 'U'){
                velocityX = 0;
                velocityY = -tileSize/4;
            }
            else if(direction == 'D'){
                velocityX = 0;
                velocityY = tileSize/4;
            }
            else if(direction == 'L'){
                velocityX = -tileSize/4;
                velocityY = 0;
            }
            else if(direction == 'R'){
                velocityX = tileSize/4;
                velocityY = 0;
            }
        }

        void reset(){
            x = startX;
            y = startY;
        }           
    }


    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private String[] tileMap2={
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    private String[] tileMap1={
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "OOOXXXXXXXXXXXXXOOO",
        "XXXXXXXXXrXXXXXXXXX",
        "OXXXXXXXbpoXXXXXXXO",
        "XXXXXXXXXXXXXXXXXXX",
        "OOOXXXXXXXXXXXXXOOO",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXX     P     XXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    private String[] tileMap = tileMap1;

    HashSet<Block> walls;
    HashSet<Block> ghosts;
    HashSet<Block> foods;
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;


    PacMan(){   
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();


        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();

        for(Block ghost: ghosts){
            ghost.direction = directions[random.nextInt(4)];
            ghost.updateVelocity();
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();

    }  

    public void loadMap(){
        walls = new HashSet<>();
        ghosts = new HashSet<>();
        foods = new HashSet<>();

        for(int r=0;r<rowCount;r++ ){
            for(int c=0; c<columnCount;c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;  
                
                if(tileMapChar == 'X'){
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar == 'b'){
                    Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(blueGhost);
                }
                else if(tileMapChar == 'r'){
                    Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(redGhost);
                }
                else if(tileMapChar == 'o'){
                    Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(orangeGhost);
                }
                else if(tileMapChar == 'p'){
                    Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(pinkGhost);
                }
                else if(tileMapChar == 'P'){
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if(tileMapChar == ' '){
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
                else if(tileMapChar == 'O'){
                    // Do nothing
                }

            }

        }

        

    }



    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost: ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall: walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for(Block food: foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.YELLOW);
        if(gameOver){
            g.setColor(Color.RED);
            g.drawString("Game Over", boardWidth/2 - 50, boardHeight/2);
            g.drawString("Score: " + score, boardWidth/2 - 50, boardHeight/2 + 20);
            
        }
        else{
            g.drawString("Lives "+"x" + String.valueOf(lives), 10, 20);
            g.drawString("Score: " + score, 15*32, 20);
        }
        if(tileMap == tileMap2){
            g.drawString("Level 2", 10, 40);
        }
        else{
            g.drawString("Level 1", 10, 40);
        }
            
    }



    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        if(pacman.x < -32){
            pacman.x = (19)*32;
        }
        else if(pacman.x > (19)*32){
            pacman.x = -32;
        }


        for(Block wall:walls){
            if(collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for(Block ghost:ghosts){

            if(ghost.x < -32){
                ghost.x = (19)*32;
            }
            else if(ghost.x > (19)*32){
                ghost.x = -32;
            }

            if(ghost.y == tileSize*9 && ghost.direction != 'D' && ghost.direction != 'U'){
                if(ghost.x == tileSize*4 || ghost.x == tileSize*14){
                    ghost.direction = 'U';
                    ghost.updateVelocity();
                }
                
            }

            if(collision(pacman, ghost)){
                pacman.reset();
                lives--;
                pacman.velocityX = 0;
                pacman.velocityY = 0;
                ghost.direction = directions[random.nextInt(4)];
                pacman.image = pacmanRightImage;
                for(Block g: ghosts){
                    g.reset();
                }
                if(lives == 0){
                    gameOver = true;
                    gameLoop.stop();
                }
            }

            if (random.nextInt(20) == 0) { // Adjust the probability as needed
                ghost.direction = directions[random.nextInt(4)];
                ghost.updateVelocity();
            }
            

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.direction = directions[random.nextInt(4)];
                    ghost.updateVelocity();
                }
            }
        }

        Block foodEaten;
        for(Block food:foods){
            if(collision(pacman, food)){
                foodEaten = food;
                score += 10;
                foods.remove(foodEaten);
                break;
            }
        }

    }

    public boolean collision(Block a, Block b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(foods.isEmpty()){
            tileMap = tileMap2;
            loadMap();
            pacman.reset();
            for(Block ghost: ghosts){
                ghost.reset();
                ghost.direction = directions[random.nextInt(4)];
                ghost.updateVelocity();
            }
            
            repaint();
            return;
        }
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    @Override
    public void keyPressed(KeyEvent e) {
    }


    @Override
    public void keyReleased(KeyEvent e) {

        if(gameOver){
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                gameOver = false;
                score = 0;
                lives = 3;
                tileMap = tileMap1;
                loadMap();
                for(Block ghost: ghosts){
                    ghost.direction = directions[random.nextInt(4)];
                    ghost.updateVelocity();
                }
                gameLoop.start();
            }
            return;
        }

        if(e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        } 
        
        if(pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }

    }

}
