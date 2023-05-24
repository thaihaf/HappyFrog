/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import View.GameFrame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author hapro
 */
public class GameController {

    //frame
    GameFrame view = new GameFrame();

    //get size of panel
    JPanel p = view.getPnFrog();
    int widthPanel = p.getWidth();
    int heightPanel = p.getHeight();

    JLabel lBird = new JLabel();
    int bWidth = 40;
    int bHeight = 40;
    int yBird = 100;
    int xBird = 80;

    // control frog up
    double yChange;
    double v;
    double a = 0.2;
    double timeFly = 0;

    //set size and location default for pipe
    int dist = 170;
    int widthPiDefault = 40;
    int heightPiDefault = (heightPanel - dist) / 2;
    List<JButton> listPipes = new ArrayList<>();

    //another entity
    private Timer timer;
    int time = 5; // every 15ms

    int count = 199;
    int point = 0;
    KeyController k = new KeyController();

    boolean gameStarted = false;
    boolean isPump = false;

    public GameController() {
        view.setVisible(true);

        v = 0;
        yChange = yBird;

        addBird();
        run();

        lBird.addKeyListener(k);
        view.getBtnPause().addKeyListener(k);
        view.getBtnSave().addKeyListener(k);
        view.getBtnExit().addKeyListener(k);

        view.getBtnPause().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause();
            }
        });
        view.getBtnSave().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
//                ClickSave();
            }
        });
        view.getBtnExit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });
    }

    public void addBird() {
        lBird.setIcon(new ImageIcon(getClass().getResource("/image/frog.png")));
        p.add(lBird);

        lBird.setBounds(xBird, yBird, bWidth, bHeight);
    }

    public void addPipe() {
        Random r = new Random();
        JButton btnPiTop = new JButton();
        JButton btnPiBottom = new JButton();

        btnPiTop.setIcon(new ImageIcon(getClass().getResource("/image/pipeTop.png")));
        btnPiBottom.setIcon(new ImageIcon(getClass().getResource("/image/pipeBottom.png")));

        int a = -40 + r.nextInt(80);
        int heightPiTop = heightPiDefault + a;
        int heightPiBottom = heightPanel - heightPiTop - dist;

        btnPiTop.setBounds(widthPanel, 0, widthPiDefault, heightPiTop);
        btnPiBottom.setBounds(widthPanel, heightPanel - heightPiBottom, widthPiDefault, heightPiBottom);

        listPipes.add(btnPiTop);
        listPipes.add(btnPiBottom);

        p.add(btnPiTop);
        p.add(btnPiBottom);
    }

    public void change() {
        yBird = (int) (yChange - v * timeFly + a * timeFly * timeFly / 2);
        timeFly++;
    }

    public void move() {
        lBird.setBounds(xBird, yBird, bWidth, bHeight);

        if (k.isPress()) {
            yChange = yBird;
            timeFly = 0;
            v = 4;
            change();
        }

        change();

        int changeXPipe = 1;
        for (int i = 0; i < listPipes.size(); i++) {
            int xPipe = listPipes.get(i).getX() - changeXPipe;
            int yPipe = listPipes.get(i).getY();
            listPipes.get(i).setLocation(xPipe, yPipe);

            //delete pipes when pipes through screen
            if (xPipe <= (bWidth * -1)) {
                listPipes.remove(i);
                i--;
            }

            if (lBird.getX() == (xPipe + widthPiDefault)) {
                point++;
            }
        }
    }

    public void run() {
        timer = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted) {
                    move();

                    view.getBtnPause().setEnabled(true);
                    view.getBtnSave().setEnabled(true);
                    view.getLblPoint().setText("Point: " + point / 2);

                    //count = 200 ==> after 120*delay , new 2 pipe will add to panel
                    count++;
                    if (count == 200) {
                        addPipe();
                        count = 0;
                    }

                    if (checkTouch()) {
                        gameStarted = false;
                        view.getBtnPause().setEnabled(false);
                        view.getBtnSave().setEnabled(false);
                        timer.stop();
                        showMess();
                    }
                } else {
                    if (k.isPress()) {
                        gameStarted = true;
                    }

                }

            }
        });

        timer.start();
    }

    //pause button
    boolean checkPause = false;

    public void pause() {
        if (checkPause == false) {
            timer.stop();
            checkPause = true;
        } else {
            timer.restart();
            checkPause = false;
        }
    }

    public boolean checkTouch() {
        boolean check = false;
        int xTemp = lBird.getX();
        int yTemp = lBird.getY();

        if (yTemp <= 1) { // top
            lBird.setLocation(xTemp, 1);
            check = true;
        }

        if (yTemp >= (heightPanel - 1 - bHeight)) { // bottomn
            lBird.setLocation(xTemp, heightPanel - 1 - bHeight);
            check = true;
        }

        // pipes
        Rectangle rBird = new Rectangle(xTemp, yTemp, bWidth, bHeight);

        for (int i = 0; i < listPipes.size(); i++) {
            JButton pipe = listPipes.get(i);
            int xPipe = pipe.getX();
            int yPipe = pipe.getY();

            Rectangle rPipe = new Rectangle(xPipe, yPipe, pipe.getWidth(), pipe.getHeight());
            if (rBird.intersects(rPipe)) {
                if (yPipe == 0) {
                    if (yBird <= pipe.getHeight() && xBird + bWidth > xPipe + 2) {
                        lBird.setLocation(xBird, pipe.getHeight());
                    }
                    return true;
                } else {
                    if (yBird + bHeight >= yPipe && xBird + bWidth > xPipe + 2) {
                        lBird.setLocation(xBird, yPipe - bHeight);
                    }
                    return true;
                }
            }
        }

        return check;
    }

    boolean checkSave = false;

    public void showMess() {
        if (checkSave == false) {
            Object mes[] = {"New Game", "Exit"};
            int option = JOptionPane.showOptionDialog(null, "Do you want to continue?",
                    "Notice!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);
            if (option == 0) {
                view.pnFrog.removeAll();
                view.pnFrog.repaint();
                listPipes.clear();
                point = 0;
                count = 120;
                view.lblPoint.setText("Point: 0");
                yBird = 40;
                view.getPnFrog().add(lBird);
                timer.restart();
            }
            if (option == 1) {
                System.exit(0);
            }
        }
    }

    public void exit() {
        if (!checkTouch()) {
            timer.stop();

            int op = JOptionPane.showConfirmDialog(null, "Do you want to exit", "Notice", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                timer.restart();
            }
        } else {
            System.exit(0);

        }
    }
}
