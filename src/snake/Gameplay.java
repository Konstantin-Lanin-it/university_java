package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

class Gameplay extends JPanel implements Runnable {
    static final int DELAY = 100, A = 500, N = 15, DELTA = A / N,
                     STRAIGHT = 0, LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4;
    private Gameplay thisGame = this;
    private boolean started = false, turned = false, crashed = false;
    private String foulder =  "Images\\";//"\\src\\Images\\";//"C:\\studyjava\\Images\\";
    private BufferedImage bodyIm[] = new BufferedImage[2], turnIm[] = new BufferedImage[4],
            headIm[] = new BufferedImage[4], tailIm[] = new BufferedImage[4], appleIm;
    private ArrayDeque snake = null;
    private int snakeLength, turn = STRAIGHT;
    private Random appleRandomizer = new Random();
    private Square apple = new Square(-1, -1);
    private CounterPanel points = null;
    private MySound snakeEatingMusic = null, snakeBackMusic = null;
    
    Gameplay() {
        try {
            appleIm = readImageFromFile(foulder + "Apple.png");
            for (int i = 0; i < 2; i++) {
                bodyIm[i] = readImageFromFile(foulder + "Body" + (i + 1) + ".png");
            }
            for (int i = 0; i < 4; i++) {
                turnIm[i] = readImageFromFile(foulder + "Turn" + (i + 1) + ".png");
                headIm[i] = readImageFromFile(foulder + "Head" + (i + 1) + ".png");
                tailIm[i] = readImageFromFile(foulder + "Tail" + (i + 1) + ".png");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        addKeyListener(new MyKeyListener());
        setFocusable(true);
    }
    
    @Override
    public void paint(Graphics g) {
        //for debug
        if (!started) {
            return;
        }
        if (crashed) {
            g.setFont(new Font("Courier New", Font.ITALIC, 40));
            char[] s = {'G', 'a', 'm', 'e', ' ', 'o', 'v', 'e', 'r'};
            g.drawChars(s, 0, 9, A / 2 - 100, A / 2 - 20);
            return;
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, A, A);
        Square[] snakeCopy = new Square[snakeLength];
        int k = 0;
        for(Iterator it = snake.iterator(); it.hasNext(); k++) {
            snakeCopy[k] = (Square)it.next();
            //Square curSquare = (Square)it.next();
            //g.fillRect(curSquare.getX() * DELTA, curSquare.getY() * DELTA, DELTA, DELTA);
        }
        BufferedImage curTailImage = null;
        int to = findDirection(snakeCopy[0], snakeCopy[1]);
        switch (to) {
            case LEFT : curTailImage = tailIm[0]; break;
            case UP : curTailImage = tailIm[1]; break;
            case RIGHT : curTailImage = tailIm[2]; break;
            case DOWN : curTailImage = tailIm[3]; break;
        }
        g.drawImage(curTailImage, snakeCopy[0].getX() * DELTA, snakeCopy[0].getY() * DELTA, DELTA, DELTA, this);
        BufferedImage curHeadImage = null;
        to = findDirection(snakeCopy[snakeLength - 1], snakeCopy[snakeLength - 2]);
        switch (to) {
            case LEFT : curHeadImage = headIm[0]; break;
            case UP : curHeadImage = headIm[1]; break;
            case RIGHT : curHeadImage = headIm[2]; break;
            case DOWN : curHeadImage = headIm[3]; break;
        }
        g.drawImage(curHeadImage, snakeCopy[snakeLength - 1].getX() * DELTA, snakeCopy[snakeLength - 1].getY() * DELTA, DELTA, DELTA, this);
        for (int i = 1; i < snakeLength - 1; i++) {
            BufferedImage curImage = null;
            int to1 = findDirection(snakeCopy[i], snakeCopy[i - 1]),
                to2 = findDirection(snakeCopy[i], snakeCopy[i + 1]);
            if (to1 == LEFT && to2 == RIGHT || to1 == RIGHT && to2 == LEFT) {
                curImage = bodyIm[0];
            } else if (to1 == DOWN && to2 == UP || to1 == UP && to2 == DOWN) {
                curImage = bodyIm[1];
            } else if (to1 == UP && to2 == LEFT || to1 == LEFT && to2 == UP) {
                curImage = turnIm[2];
            } else if (to1 == UP && to2 == RIGHT || to1 == RIGHT && to2 == UP) {
                curImage = turnIm[3];
            } else if (to1 == RIGHT && to2 == DOWN || to1 == DOWN && to2 == RIGHT) {
                curImage = turnIm[0];
            } else {
                curImage = turnIm[1];
            }
            g.drawImage(curImage, snakeCopy[i].getX() * DELTA, snakeCopy[i].getY() * DELTA, DELTA, DELTA, this);
        }
        if (apple.getX() != -1) {
            //g.setColor(Color.red);
            //g.fillRect(apple.getX() * DELTA, apple.getY() * DELTA, DELTA, DELTA);
            g.drawImage(appleIm, apple.getX() * DELTA, apple.getY() * DELTA, DELTA, DELTA, this);
        } 
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, A, A);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
            sleep(DELAY);
            } catch (InterruptedException ex) {        
                ex.printStackTrace();
            } 
            moveSnake(turn);
            if (!check()) {
                crashed = true;
                repaint();
                break;
            }
            turn = STRAIGHT;
            repaint();
        }
        snakeBackMusic.finish();
    }

    private BufferedImage readImageFromFile(String fileName) throws IOException {
        BufferedImage image = null;
        File f = null;
        f = new File(fileName);
        image = new BufferedImage(DELTA, DELTA, BufferedImage.TYPE_INT_ARGB);
        image = ImageIO.read(f);
        return image;    
    }
    
    private ArrayDeque makeStartSnake() {
        ArrayDeque startSnake = new ArrayDeque();
        startSnake.addLast(new Square(1, 4));
        startSnake.addLast(new Square(2, 4));
        startSnake.addLast(new Square(3, 4));
        //---
        //startSnake.addLast(new Square(4, 4));
        //startSnake.addLast(new Square(5, 5));
        return startSnake;
    }

    private void moveSnake(int moveType) {
        Square nextSquare = new Square();
        Square head = (Square)snake.getLast(), neck = findNeck();
        int from;
        if (head.getX() < neck.getX()) {
            from = RIGHT;
        } else if (head.getY() < neck.getY()) {
            from = DOWN;
        } else if (head.getX() > neck.getX()) {
            from = LEFT;
        } else {
            from = UP;
        }
        if (moveType == STRAIGHT || moveType == from) {
            switch (from) {
                case LEFT : moveType = RIGHT; break;
                case UP : moveType = DOWN; break;
                case RIGHT : moveType = LEFT; break;
                case DOWN : moveType = UP; break;
            }
        } 
        switch (moveType) {
            case LEFT : nextSquare.setX(head.getX() - 1);
                        nextSquare.setY(head.getY()); break;
            case UP : nextSquare.setX(head.getX());
                      nextSquare.setY(head.getY() - 1); break;
            case RIGHT : nextSquare.setX(head.getX() + 1);
                         nextSquare.setY(head.getY()); break;
            case DOWN : nextSquare.setX(head.getX());
                        nextSquare.setY(head.getY() + 1); break;
        }
        if (!(apple.getX() == nextSquare.getX() && 
              apple.getY() == nextSquare.getY())) {
            snake.removeFirst();
        } else {
            snakeLength++;
            apple.setX(-1);
            points.update();
            if (snakeEatingMusic != null) {
                snakeEatingMusic.finish();
            }
            snakeEatingMusic = new MySound("Music\\EatingAnApple.wav");
            snakeEatingMusic.start();
            apple = randomizeApple();
        }
        snake.addLast(nextSquare);
    }

    private Square findNeck() {
        Iterator it = snake.iterator();
        for(int i = 0; i < snakeLength - 2; i++) {
           it.next();
        }
        return (Square)it.next();
    }

    private boolean check() {
        Square head = (Square)snake.getLast();
        if (Math.max(head.getX(), head.getY()) == N ||
            Math.min(head.getX(), head.getY()) == -1) {
            return false;
        }
        Iterator it = snake.iterator();
        for(int i = 0; i < snakeLength - 1; i++) {
           Square curSquare = (Square)it.next();
           if (curSquare.getX() == head.getX() &&
               curSquare.getY() == head.getY()) {
               return false;
           }
        }
        return true;
    }
    
     private Square randomizeApple() {
        boolean[][] isEmpty = new boolean[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    isEmpty[i][j] = true;
                }
            }
            for(Iterator it = snake.iterator(); it.hasNext(); ) {
                Square cur = (Square)it.next();
                isEmpty[cur.getX()][cur.getY()] = false;
            }
            int randPos = Math.abs(appleRandomizer.nextInt()) % (N * N - snakeLength);
            int sum = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (isEmpty[i][j]) {
                        if (sum == randPos) {
                            return new Square(i, j);
                        }
                        sum ++;
                    }
                }
            }
            return null;
    }

    private int findDirection(Square a, Square b) {
        if (a.getX() < b.getX()) {
            return LEFT;
        } else if (a.getY() < b.getY()) {
            return UP;
        } else if (a.getX() > b.getX()) {
            return RIGHT;
        } else {
            return DOWN;
        }
    }

    public void setCounterPanel(CounterPanel cp) {
        points = cp;
    }
    
    class MyKeyListener extends KeyAdapter {      
        public void keyPressed(KeyEvent e) {
            if (!started || crashed == true) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    snake = makeStartSnake();
                    snakeLength = 3; //5;
                    apple = randomizeApple();
                    points.clean();
                    //snakeBackMusic = new MySound("Music\\SnakeTheme.wav");
                    //snakeBackMusic.start();
                    Thread snakeGame = new Thread(thisGame);
                    snakeGame.start();
                    started = true;
                    crashed = false;
                }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A : turn = LEFT; break;
                    case KeyEvent.VK_W : turn = UP; break;
                    case KeyEvent.VK_D : turn = RIGHT; break; 
                    case KeyEvent.VK_S : turn = DOWN; break;
                    default : ;
                }             
            }
        }
    }
    
}
