import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Scanner;


public class FangDenKaese extends JPanel implements MouseMotionListener, KeyListener {
    private int score;
    private int mouseX, mouseY;
    private int cheeseX, cheeseY;
    private int currentlevel = 1;
    private Timer timer;
    private int cheeseStepX, cheeseStepY;
    private int cheeseWidth, cheeseHeight;
    private BufferedImage backgroundImage;
    private BufferedImage cheeseImage;
    private BufferedImage mouseImage;
    private int cheeseSpeed;
    private int highScore;
    private File highScoreFile;
    private int level;
    private boolean isNewLevel;
    private int levelMessageDuration = 2000;
    private long levelMessageStartTime;
    private int enemyX, enemyY;
    private BufferedImage enemyImage;
    private int enemySpeed;
    private boolean isEnemyActive;
    private boolean gameOver;
    private int catDirectionX;
    private int catDirectionY;
    private boolean isCatActive;

    public FangDenKaese() {
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\lilit\\Downloads\\holzboden-3.jpg"));
            cheeseImage = ImageIO.read(new File("C:\\Users\\lilit\\Downloads\\Käse.png"));
            mouseImage = ImageIO.read(new File("C:\\Users\\lilit\\Downloads\\Maus.png"));
            enemyImage = ImageIO.read(new File("C:\\Users\\lilit\\Downloads\\Katze.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cheeseStepX = 2;
        cheeseStepY = 2;

        mouseX = mouseY = -100;
        cheeseX = 100;
        cheeseY = 100;
        enemyX = 400;
        enemyY = 300;
        score = 0;
        gameOver = false;

        timer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveCheese();
                moveCat();
                repaint();
            }
        });

        timer.start();
        cheeseSpeed = 2;
        highScoreFile = new File("highscore.txt");
        highScore = loadHighScore();
        level = 1;
        initLevel(level);


    }

    private void moveCheese() {
        int dx = mouseX - cheeseX;
        int dy = mouseY - cheeseY;

        if (isEnemyActive) {
            int enemyDirectionX = mouseX - enemyX;
            int enemyDirectionY = mouseY - enemyY;
            double enemyLength = Math.sqrt(enemyDirectionX * enemyDirectionX + enemyDirectionY * enemyDirectionY);
            double enemyDirectionXNormalized = enemyDirectionX / enemyLength;
            double enemyDirectionYNormalized = enemyDirectionY / enemyLength;


        }
        double length = Math.sqrt(dx * dx + dy * dy);
        double directionX = dx / length;
        double directionY = dy / length;
        double speed = Math.min(15, Math.max(1, length / 10));


        directionX = -directionX;
        directionY = -directionY;


        cheeseX += (int) (directionX * speed);
        cheeseY += (int) (directionY * speed);
        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore);
        }
        if (score % 2 == 0 && score >= (level * 2)) {
            level++;
            initLevel(level);
        } else if (score == (level * 2)) {
            level++;
            initLevel(level);
        }


        if (cheeseX + cheeseWidth >= getWidth() || cheeseX <= 0) {
            cheeseX = Math.max(0, Math.min(cheeseX, getWidth() - cheeseWidth));
        }
        if (cheeseY + cheeseHeight >= getHeight() || cheeseY <= 0) {
            cheeseY = Math.max(0, Math.min(cheeseY, getHeight() - cheeseHeight));
        }

        int distanceX = Math.abs(mouseX - cheeseX);
        int distanceY = Math.abs(mouseY - cheeseY);
        int distanceThreshold = 100;

        if (distanceX < distanceThreshold && distanceY < distanceThreshold) {
            cheeseSpeed = 1;
            score++;
            if (score > highScore) {
                highScore = score;
            }
        } else {
            cheeseSpeed = 1;
        }

        cheeseX += cheeseStepX * cheeseSpeed;
        cheeseY += cheeseStepY * cheeseSpeed;


        if (cheeseX + cheeseWidth >= getWidth() || cheeseX <= 0) {
            cheeseStepX = -cheeseStepX;
        }
        if (cheeseY + cheeseHeight >= getHeight() || cheeseY <= 0) {
            cheeseStepY = -cheeseStepY;
        
        }
        if (isEnemyActive) {
            int enemyDirectionX = mouseX - enemyX;
            int enemyDirectionY = mouseY - enemyY;
            double enemyLength = Math.sqrt(enemyDirectionX * enemyDirectionX + enemyDirectionY * enemyDirectionY);
            double enemyDirectionXNormalized = enemyDirectionX / enemyLength;
            double enemyDirectionYNormalized = enemyDirectionY / enemyLength;

            enemyX += (int) (enemyDirectionXNormalized * enemySpeed);
            enemyY += (int) (enemyDirectionYNormalized * enemySpeed);
        }
        int enemyDistanceX = Math.abs(mouseX - enemyX);
        int enemyDistanceY = Math.abs(mouseY - enemyY);

        int enemyDistanceThreshold = 50;

        if (enemyDistanceX < enemyDistanceThreshold && enemyDistanceY < enemyDistanceThreshold) {
            gameOver = true;

        }
        if (isEnemyActive) {
            int enemyDirectionX;
            int enemyDirectionY;

            if (currentlevel >= 3) {
                enemyDirectionX = mouseX - enemyX;
                enemyDirectionY = mouseY - enemyY;
            } else {
                enemyDirectionX = cheeseX - enemyX;
                enemyDirectionY = cheeseY - enemyY;
            }

            double enemyLength = Math.sqrt(enemyDirectionX * enemyDirectionX + enemyDirectionY * enemyDirectionY);
            double enemyDirectionXNormalized = enemyDirectionX / enemyLength;
            double enemyDirectionYNormalized = enemyDirectionY / enemyLength;

            enemyX += (int) (enemyDirectionXNormalized * enemySpeed);
            enemyY += (int) (enemyDirectionYNormalized * enemySpeed);

            enemyDistanceX = Math.abs(mouseX - enemyX);
            enemyDistanceY = Math.abs(mouseY - enemyY);

            if (enemyDistanceX < enemyDistanceThreshold && enemyDistanceY < enemyDistanceThreshold) {
                gameOver = true;
                //
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {


        }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        int mouseWidth = 100;
        int mouseHeight = 90;
        if (mouseX + mouseWidth > getWidth()) {
            mouseX = getWidth() - mouseWidth;
        } else if (mouseX < 0) {
            mouseX = 0;
        }
        if (mouseY + mouseHeight > getHeight()) {
            mouseY = getHeight() - mouseHeight;
        } else if (mouseY < 80) {
            mouseY = 80;
        }
        g2d.drawImage(mouseImage, mouseX, mouseY, mouseWidth, mouseHeight, null);


        cheeseWidth = 100;
        cheeseHeight = 90;
        int[] xPoints = {cheeseX, cheeseX + 30, cheeseX + cheeseWidth};
        int[] yPoints = {cheeseY + cheeseHeight, cheeseY, cheeseY + cheeseHeight};

        if (mouseX + mouseWidth >= cheeseX && mouseX <= cheeseX + cheeseWidth &&
                mouseY + mouseHeight >= cheeseY && mouseY <= cheeseY + cheeseHeight) {
            score++;
            cheeseX = (int) (Math.random() * (getWidth() - cheeseWidth));
            cheeseY = (int) (Math.random() * (getHeight() - cheeseHeight - 80)) + 100;
        }
        g2d.drawImage(cheeseImage, cheeseX, cheeseY, cheeseWidth, cheeseHeight, null);


        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String levelText = "Level: " + level;
        int levelTextWidth = g2d.getFontMetrics().stringWidth(levelText);
        int levelTextHeight = g2d.getFontMetrics().getHeight();
        int levelTextX = 10;
        int levelTextY = levelTextHeight + 20;
        g2d.drawString(levelText, levelTextX, levelTextY);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Highscore: " + highScore, 10, 100);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 10, 70);


        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String text = "Los Maus, schnapp dir den Käse!";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();
        int textX = (getWidth() - textWidth);
        g2d.drawString(text, textX, 70);

        if (isNewLevel) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            String levelMessage = "Level " + level;
            currentlevel = +1;
            int messageWidth = g2d.getFontMetrics().stringWidth(levelMessage);
            int messageHeight = g2d.getFontMetrics().getHeight();
            int messageX = (getWidth() - messageWidth) / 2;
            int messageY = (getHeight() - messageHeight) / 2;
            g2d.drawString(levelMessage, messageX, messageY);



        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - levelMessageStartTime >= levelMessageDuration) {
            isNewLevel = false; // Levelanzeige deaktivieren

            if (enemyImage != null && isEnemyActive) {
                int enemyWIDTH = 250;
                int enemyHEIGHT = 220;
                g2d.drawImage(enemyImage, enemyX, enemyY, enemyWIDTH, enemyHEIGHT, null);
            }
        }

        if (isNewLevel) {

        }

            if (gameOver) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 50));

                String gameOverText = "Game Over";
                int gameOverTextWidth = g2d.getFontMetrics().stringWidth(gameOverText);
                int gameOverTextHeight = g2d.getFontMetrics().getHeight();
                int gameOverTextX = (getWidth() - gameOverTextWidth) / 2;
                int gameOverTextY = (getHeight() - gameOverTextHeight) / 2;
                g2d.drawString(gameOverText, gameOverTextX, gameOverTextY);

                Timer restartTimer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        score = 0;
                        mouseX = mouseY = -100;
                        cheeseX = 100;
                        cheeseY = 100;
                        enemyX = 400;
                        enemyY = 300;
                        gameOver = false;
                        level = 1;
                        initLevel(level);

                        timer.start();
                        repaint();
                    }
                });
                restartTimer.setRepeats(false);
                restartTimer.start();
            }
        }


        public static void main(String[] args) {
        FangDenKaese game = new FangDenKaese();
        JFrame frame = new JFrame();
        frame.setTitle("Fang den Käse");
        frame.setSize(750, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.add(game);
        frame.setVisible(true);
        game.requestFocus();
        game.setVisible(true);
    }

    private void moveCat() {
        if (isCatActive) {
            int catSpeed = 5;


            catDirectionX = mouseX - enemyX;
            catDirectionY = mouseY - enemyY;
            double catLength = Math.sqrt(catDirectionX * catDirectionX + catDirectionY * catDirectionY);
            double catDirectionXNormalized = catDirectionX / catLength;
            double catDirectionYNormalized = catDirectionY / catLength;

            enemyX += (int) (catDirectionXNormalized * catSpeed);
            enemyY += (int) (catDirectionYNormalized * catSpeed);
        } else {
            int catSpeed = 2;

            int randomDirectionX = (int) (Math.random() * 3) - 1;
            int randomDirectionY = (int) (Math.random() * 3) - 1;

            enemyX += randomDirectionX * catSpeed;
            enemyY += randomDirectionY * catSpeed;
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {

        moveCat();
        int keyCode = e.getKeyCode();
        int mouseStep = 20;

        if (keyCode == KeyEvent.VK_LEFT) {
            mouseX -= mouseStep;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            mouseX += mouseStep;
        } else if (keyCode == KeyEvent.VK_UP) {
            mouseY -= mouseStep;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            mouseY += mouseStep;
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private int loadHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(highScoreFile));
            String highScoreStr = reader.readLine();
            reader.close();
            return Integer.parseInt(highScoreStr);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Fehler beim Laden des Highscores: " + e.getMessage());
        }
        return 0;
    }
    private void saveHighScore(int highScore) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
            writer.write(Integer.toString(highScore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
    private void initLevel(int level) {
        try {
            switch (level) {
                case 1:
                    cheeseSpeed = 2;
                    break;
                case 2:
                    cheeseSpeed = 6;
                    cheeseStepX = 3;
                    cheeseStepY = 3;
                    break;
                case 3:
                    cheeseSpeed = 6;
                    cheeseStepX = -3;
                    cheeseStepY = -3;
                    enemyX = 400;
                    enemyY = 350;
                    enemyImage = ImageIO.read(new File("C:\\Users\\lilit\\Downloads\\Katze.png"));

                  isEnemyActive = true ;
                    isCatActive = true ;
            }

            isNewLevel = true;
            isEnemyActive = true;

            levelMessageStartTime = System.currentTimeMillis();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }