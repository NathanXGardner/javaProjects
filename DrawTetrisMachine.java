package project.pkg3;

// This program was written by Nathan Gardner

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import javax.swing.Timer;

public class DrawTetrisMachine extends Frame  {
    public static void main(String[] args) {
       new DrawTetrisMachine();
    }

    DrawTetrisMachine() {
        super("Tetris Screen");        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        setSize(3000, 1800);
        TetrisMachine t = new TetrisMachine();
        add("Center", t);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setVisible(true);
    }
}

class TetrisMachine extends Canvas {
        
    public final static boolean DEBUG = false;
    
    boolean T = false, I = false, O = false;
    boolean J = false, L = false, S = false, Z = false;
    boolean gameStart = false;
    boolean gamePause = false;
    boolean stop = false;
    boolean firstPrint = true;
    
    State thisState;
    State nextState;
    
    int fallDelay = 500;//in miliseconds
    int pauseDelay = 50;//in miliseconds
    
    int shiftToBoard = 7;
    
    int fontSize = 10;
    int bigFontSize = 15;
    
    float scale = 0, scaleX = 0, scaleY = 0;
    float logicalHeight = 350, logicalWidth = 550;
    float padX = 0, padY = 0;
    float xOffset = 0, yOffset = 0;
    
    float pauseW = 50, pauseH = 15;
    
    int c = 10;//cell size
    float boardX = 10*c, boardY = 20*c;
    
    float quitW = 2*c, quitH = 3*((float)c)/2;
    float quitX = 15*c, quitY = 15*c;    
    
    float nextW = 6*c, nextH = 5*c;
    float nextX = 12*c, nextY = 0;
        
    float pickW = 6*c, pickH = 5*c;
    float pickX = 20*c, pickY = 0;
    
    int colorKey;
    
    int [][]board = new int[][] {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
    };    
     
    void initializeSizes() {
        Dimension d = getSize();
        
        //Width is greater than the height therefore the drawn object needs to be scaled to height
        if(d.width > d.height) {
            scale = d.height / logicalHeight;        //number of physical units (px) per logical unit of smaller dimension
            scaleX = scaleY = scale;                 //NOTE equivalent scaling based on smaller dimension
            padX = d.width - (logicalWidth * scaleX);//the total padding is the total physical space minus the occupying space
            xOffset = padX / 2;                      //half the padding is used to horizontally center the object
        }
        
        //Height is greater than the width therefore the drawn object needs to be scaled to width
        else {//d.width <= d.height 
            scale = d.width / logicalWidth;          //number of physical units (px) per logical unit of smaller dimension
            scaleX = scaleY = scale;                 //NOTE equivalent scaling based on smaller dimension
            padY = d.height - (logicalHeight * scaleY);//the total padding is the total physical space minus the occupying space
            yOffset = padY / 2;                      //half the padding is used to horizontally center the object
        }
    }     
    
    public int getRealX(float x) {
        return (int)(scaleX * x + xOffset);
    }
    public int getRealY(float y) {
        return (int)(scaleY * y + yOffset);
    }    
    public float getLogicalX(int x) {
        return (float)(x - xOffset)/scaleX;
    }
    public float getLogicalY(int y) {
        return (float)(y - yOffset)/scaleY;
    } 
    public int getRealWidth(float w) {
        return (int)(scaleX * w);
    }
    public int getRealHeight(float h) {
        return (int)(scaleY * h);
    }
    
    TetrisMachine() {       
        ActionListener updatePosition = (ActionEvent e) -> {
            if(gameStart) {
                nextState = new State();
                moveDown();
                repaint();
            }
        };
        
        new Timer(fallDelay,updatePosition).start();
        
        addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
                                        if(gameStart && !gamePause) {
                                            nextState = new State();
                                            moveDown();
                                        }
                                        break;
				case KeyEvent.VK_LEFT:
                                        if(gameStart && !gamePause) {
                                            nextState = new State();
                                            moveLeft();
                                        }
					break;
				case KeyEvent.VK_RIGHT:
                                        if(gameStart && !gamePause) {
                                            nextState = new State();
                                            moveRight();
                                        }
					break;
				case KeyEvent.VK_O:
                                        if(gameStart && !gamePause) {
                                            nextState = new State();
                                            rotateCW();
                                        }
					break;
				case KeyEvent.VK_P:
                                        if(gameStart && !gamePause) {
                                            nextState = new State();
                                            rotateCCW();
                                        }
					break;                                        
				}
                                repaint();
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});       
                
                
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(!(x >= getRealX(0) && x < getRealX(boardX) 
                && y >= getRealY(0) && y < getRealY(boardY))) {

                    nextState = new State();
                    if(e.getWheelRotation() < 0) {
                        rotateCCW();
                    }
                    else {
                        rotateCW();
                    }
                    repaint();
                }
            }
        });    
        
        
        addMouseListener(new MouseAdapter() {   
        @Override
        public void mouseClicked(MouseEvent e) {
                //specifying if the coordinate of the mouse overlaps with 
                //those of the defined button, then clicking the mouse 
                //in this region should terminate the program
                if(getLogicalX(e.getX()) >= quitX && getLogicalX(e.getX()) <= (quitX + quitW) 
                && getLogicalY(e.getY()) >= quitY && getLogicalY(e.getY()) <= (quitY + quitH)) {
                    System.exit(0);
                } 
                else if(getLogicalX(e.getX()) >= 20*c && getLogicalX(e.getX()) <= (20*c + pickW) 
                && getLogicalY(e.getY()) >= 7*c && getLogicalY(e.getY()) <= (7*c + pickH)) {
                    //O button selected
                    if(!gameStart) {
                        thisState = new State(1,0,4,0,5,1,4,1,5,true);
                        board[0][4] = 1;
                        board[0][5] = 1;
                        board[1][4] = 1;
                        board[1][5] = 1;   
                        repaint();
                        O = true;
                        repaint(); 
                    }                
                }
                else if(getLogicalX(e.getX()) >= 28*c && getLogicalX(e.getX()) <= (28*c + pickW) 
                && getLogicalY(e.getY()) >= 7*c && getLogicalY(e.getY()) <= (7*c + pickH)) {
                    //L button selected
                    if(!gameStart) {
                        thisState = new State(4,0,3,0,4,0,5,1,3); 
                        board[0][3] = 4;
                        board[0][4] = 4;
                        board[0][5] = 4;
                        board[1][3] = 4;   
                        repaint();
                        L = true;
                        repaint(); 
                    }                
                }
                else if(getLogicalX(e.getX()) >= 28*c && getLogicalX(e.getX()) <= (28*c + pickW) 
                && getLogicalY(e.getY()) >= 14*c && getLogicalY(e.getY()) <= (14*c + pickH)) {
                    //J button selected
                    if(!gameStart) {                     
                        thisState = new State(2,0,5,0,4,0,3,1,5);
                        board[0][3] = 2;
                        board[0][4] = 2;
                        board[0][5] = 2;
                        board[1][5] = 2;                           
                        repaint();
                        J = true;
                        repaint(); 
                    }              
                }
                else if(getLogicalX(e.getX()) >= 20*c && getLogicalX(e.getX()) <= (20*c + pickW) 
                && getLogicalY(e.getY()) >= 14*c && getLogicalY(e.getY()) <= (14*c + pickH)) {
                    //S button selected
                    if(!gameStart) {
                        thisState = new State(6,1,4,1,3,0,5,0,4);
                        board[0][4] = 6;
                        board[0][5] = 6;
                        board[1][3] = 6;
                        board[1][4] = 6;   
                        repaint();
                        S = true;
                        repaint(); 
                    }                        
                }
                else if(getLogicalX(e.getX()) >= 28*c && getLogicalX(e.getX()) <= (28*c + pickW) 
                && getLogicalY(e.getY()) >= 0 && getLogicalY(e.getY()) <= (pickH)) {                
                    //Z button selected
                     if(!gameStart) {
                        thisState = new State(5,1,5,0,4,0,5,1,6);
                        board[0][4] = 5;
                        board[0][5] = 5;
                        board[1][5] = 5;
                        board[1][6] = 5;  
                        repaint();                        
                        Z = true;
                        repaint(); 
                    }               
                }
                else if(getLogicalX(e.getX()) >= 36*c && getLogicalX(e.getX()) <= (36*c + pickW) 
                && getLogicalY(e.getY()) >= 0 && getLogicalY(e.getY()) <= (pickH)) {                 
                    //I button selected
                    if(!gameStart) {
                        thisState = new State(3,0,3,0,4,0,5,0,6);
                        board[0][3] = 3;
                        board[0][4] = 3;
                        board[0][5] = 3;
                        board[0][6] = 3;
                        repaint();                        
                        I = true;
                        repaint(); 
                    }          
                }
                else if(getLogicalX(e.getX()) >= 20*c && getLogicalX(e.getX()) <= (20*c + pickW) 
                && getLogicalY(e.getY()) >= 0 && getLogicalY(e.getY()) <= (pickH)) {                  
                    //T button selected 
                    if(!gameStart) {
                        thisState = new State(7,0,4,0,5,0,3,1,4); 
                        board[0][4] = 7;
                        board[0][5] = 7;
                        board[0][3] = 7;
                        board[1][4] = 7;
                        repaint();
                        T = true;
                        repaint(); 
                    }          
                }
                if(T||S||Z||J||O||I||L) {
                    gameStart = true;
                }
            }
        });        
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(x >= getRealX(0) && x <= getRealX(boardX) 
                && y >= getRealY(0) && y <= getRealY(boardY)) {
                    if(gamePause) {
                        gamePause = false;
                        gameStart = true;//pause has been entered, pause game
                        repaint();
                    }
                    else {
                        gamePause = true;
                        gameStart = false;//pause has been exited, resume game
                        repaint();  
                    }
                }
            }
        });      
    }
    
    @Override
    public void paint(Graphics g) {
        initializeSizes();
        
        //draw the tetris board
        g.drawRect(getRealX(0),getRealY(0),getRealWidth(boardX),getRealHeight(boardY));

        //draw the quit box with text
        g.drawRect(getRealX(quitX),getRealY(quitY),getRealWidth(quitW),getRealHeight(quitH));
        g.setFont(new Font("Calibri", Font.BOLD, getRealWidth(fontSize)));
        g.drawString("Quit", getRealX(quitX), getRealY(quitY+quitY/16));
        
        //draw the next piece box to the right of the board
        g.drawRect(getRealX(nextX),getRealY(nextY),getRealWidth(nextW),getRealHeight(nextH));
        
        //draw the pick piece boxes
        g.setColor(Color.BLACK);
        //Draw T 
        g.drawRect(getRealX(20*c),getRealY(0),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*22),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*23),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*24),getRealY(c),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*23),getRealY(c*2),getRealWidth(c),getRealHeight(c));
                
        //draw I 
        g.drawRect(getRealX(36*c),getRealY(0),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*37),getRealY(c*2),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*38),getRealY(c*2),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*39),getRealY(c*2),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*40),getRealY(c*2),getRealWidth(c),getRealHeight(c));         
        
        //draw O
        g.drawRect(getRealX(20*c),getRealY(7*c),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*22),getRealY(c*8),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*22),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*23),getRealY(c*8),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*23),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        
        //draw S
        g.drawRect(getRealX(20*c),getRealY(14*c),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*22),getRealY(c*16),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*23),getRealY(c*15),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*23),getRealY(c*16),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*24),getRealY(c*15),getRealWidth(c),getRealHeight(c));           
        
        //draw Z
        g.drawRect(getRealX(28*c),getRealY(0*c),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*29),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*30),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*30),getRealY(c*2),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*31),getRealY(c*2),getRealWidth(c),getRealHeight(c));          
        
        //draw L
        g.drawRect(getRealX(28*c),getRealY(7*c),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*30),getRealY(c*8),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*30),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*30),getRealY(c*10),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*31),getRealY(c*10),getRealWidth(c),getRealHeight(c));          
        
        //draw J
        g.drawRect(getRealX(28*c),getRealY(14*c),getRealWidth(pickW),getRealHeight(pickH));
        
        g.drawRect(getRealX(c*30),getRealY(c*17),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*31),getRealY(c*15),getRealWidth(c),getRealHeight(c));
        g.drawRect(getRealX(c*31),getRealY(c*16),getRealWidth(c),getRealHeight(c));      
        g.drawRect(getRealX(c*31),getRealY(c*17),getRealWidth(c),getRealHeight(c));          
        
        //Fill all of the selectable buttons
        //Draw T 
        g.setColor(new Color(200,0,255));
        g.fillRect(getRealX(c*22),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*23),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*24),getRealY(c),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*23),getRealY(c*2),getRealWidth(c),getRealHeight(c));       
                
        //draw I 
        g.setColor(new Color(0,191,255));
        g.fillRect(getRealX(c*37),getRealY(c*2),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*38),getRealY(c*2),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*39),getRealY(c*2),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*40),getRealY(c*2),getRealWidth(c),getRealHeight(c));         
        
        //draw O
        g.setColor(new Color(255,255,0));        
        g.fillRect(getRealX(c*22),getRealY(c*8),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*22),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*23),getRealY(c*8),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*23),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        
        //draw S
        g.setColor(new Color(255,0,0));        
        g.fillRect(getRealX(c*22),getRealY(c*16),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*23),getRealY(c*15),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*23),getRealY(c*16),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*24),getRealY(c*15),getRealWidth(c),getRealHeight(c));           
        
        //draw Z
        g.setColor(new Color(0,250,140));        
        g.fillRect(getRealX(c*29),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*30),getRealY(c),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*30),getRealY(c*2),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*31),getRealY(c*2),getRealWidth(c),getRealHeight(c));          
        
        //draw L
        g.setColor(new Color(255,130,10));        
        g.fillRect(getRealX(c*30),getRealY(c*8),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*30),getRealY(c*9),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*30),getRealY(c*10),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*31),getRealY(c*10),getRealWidth(c),getRealHeight(c));          
        
        //draw J
        g.setColor(new Color(0,0,230));
        g.fillRect(getRealX(c*30),getRealY(c*17),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*31),getRealY(c*15),getRealWidth(c),getRealHeight(c));
        g.fillRect(getRealX(c*31),getRealY(c*16),getRealWidth(c),getRealHeight(c));      
        g.fillRect(getRealX(c*31),getRealY(c*17),getRealWidth(c),getRealHeight(c));        
        
        g.setColor(Color.BLACK);

        for(int i=0; i < 20; i++) {
            for(int j=0; j < 10; j++) {
                colorKey = board[i][j];
                if(colorKey != 0) {
                    switch (colorKey) {
                    case 8: 
                    case 1: g.setColor(new Color(255,255,0));//O
                            break;
                    case 9: 
                    case 2: g.setColor(new Color(0,0,230));//J
                            break;
                    case 10: 
                    case 3 :g.setColor(new Color(0,191,255));//I
                            break;
                    case 11: 
                    case 4 :g.setColor(new Color(255,130,10));//L
                            break;
                    case 12: 
                    case 5 :g.setColor(new Color(0,250,140));//Z
                            break;
                    case 13: 
                    case 6 :g.setColor(new Color(255,0,0));//S
                            break;
                    case 14: 
                    case 7 :g.setColor(new Color(200,0,255));//T
                            break;   
                    }
                    g.fillRect(getRealX(c*j),getRealY(c*i),getRealWidth(c),getRealHeight(c));
                    g.setColor(Color.BLACK);
                    g.drawRect(getRealX(c*j),getRealY(c*i),getRealWidth(c),getRealHeight(c));
                }
            }
            g.setColor(Color.BLACK);
        }
        
        if(gamePause) { // anytime paint is called, ready should toggle on and off 
            g.setColor(new Color(255,205,0));
            g.setFont(new Font("Calibri", Font.BOLD,getRealWidth(bigFontSize)));
            g.drawString("Pause",getRealX(boardX*3/8),getRealY(boardY/2));
            g.drawRect(getRealX(boardX/4),getRealY(boardY*7/16),getRealWidth(pauseW),getRealHeight(pauseH));
        }
    }

    //returns true of the piece successfully rotated CCW     
    public boolean rotateCCW() {
        if(thisState.isSquare) {return false;}
        nextState.aX = thisState.pivotX + thisState.pivotY - thisState.aY;
        nextState.bX = thisState.pivotX + thisState.pivotY - thisState.bY;
        nextState.cX = thisState.pivotX + thisState.pivotY - thisState.cY;
                        
        nextState.aY = thisState.pivotY - thisState.pivotX + thisState.aX;
        nextState.bY = thisState.pivotY - thisState.pivotX + thisState.bX;
        nextState.cY = thisState.pivotY - thisState.pivotX + thisState.cX;
        
        nextState.pivotX = thisState.pivotX;
        nextState.pivotY = thisState.pivotY;
        
        nextState.color = thisState.color;
        
        return updateState();
    }
    
    //returns true of the piece successfully rotated CW  
    public boolean rotateCW() {
        if(thisState.isSquare) {return false;}
        nextState.aX = thisState.pivotX - thisState.pivotY + thisState.aY;
        nextState.bX = thisState.pivotX - thisState.pivotY + thisState.bY;
        nextState.cX = thisState.pivotX - thisState.pivotY + thisState.cY;
                        
        nextState.aY = thisState.pivotY + thisState.pivotX - thisState.aX;
        nextState.bY = thisState.pivotY + thisState.pivotX - thisState.bX;
        nextState.cY = thisState.pivotY + thisState.pivotX - thisState.cX;
        
        nextState.pivotX = thisState.pivotX;
        nextState.pivotY = thisState.pivotY;
        
        nextState.color = thisState.color;
        
        return updateState();
    }
    
    //returns true if the piece successfully moved down    
    public boolean moveDown() {
        nextState.aY = thisState.aY +1;
        nextState.bY = thisState.bY +1;
        nextState.cY = thisState.cY +1;
        nextState.pivotY = thisState.pivotY + 1; 
        
        nextState.aX = thisState.aX;
        nextState.bX = thisState.bX;
        nextState.cX = thisState.cX;
        nextState.pivotX = thisState.pivotX;
        
        nextState.color = thisState.color;
        stop = true;
        
        return updateState();
    }
    
    //returns true of the piece successfully moved left
    public boolean moveLeft() {
        nextState.aX = thisState.aX -1;
        nextState.bX = thisState.bX -1;
        nextState.cX = thisState.cX -1;
        nextState.pivotX = thisState.pivotX - 1; 
        
        nextState.aY = thisState.aY;
        nextState.bY = thisState.bY;
        nextState.cY = thisState.cY;
        nextState.pivotY = thisState.pivotY;
        
        nextState.color = thisState.color;
        
        return updateState();
    }
        
    //returns true of the piece successfully moved right
    public boolean moveRight() {
        nextState.aX = thisState.aX +1;
        nextState.bX = thisState.bX +1;
        nextState.cX = thisState.cX +1;
        nextState.pivotX = thisState.pivotX + 1; 
        
        nextState.aY = thisState.aY;
        nextState.bY = thisState.bY;
        nextState.cY = thisState.cY;
        nextState.pivotY = thisState.pivotY;        
        
        nextState.color = thisState.color;
        
        return updateState();
    }
    
    //returns true if the state gets updated (piece successfully moves to new location
    //The variable stop prevents pieces from sticking due to horizontal or rotational collision
    public boolean updateState() {
        
        if(!collides()) {
            board[thisState.aY][thisState.aX] = 0;
            board[thisState.bY][thisState.bX] = 0;
            board[thisState.cY][thisState.cX] = 0;
            board[thisState.pivotY][thisState.pivotX] = 0;
            thisState = nextState;
            board[thisState.aY][thisState.aX] = thisState.color;
            board[thisState.bY][thisState.bX] = thisState.color;
            board[thisState.cY][thisState.cX] = thisState.color;
            board[thisState.pivotY][thisState.pivotX] = thisState.color;
            stop = false;//reset stop
            return true;
        }
        if(stop) {
            board[thisState.aY][thisState.aX] = thisState.color + shiftToBoard;
            board[thisState.bY][thisState.bX] = thisState.color + shiftToBoard;
            board[thisState.cY][thisState.cX] = thisState.color + shiftToBoard;
            board[thisState.pivotY][thisState.pivotX] = thisState.color + shiftToBoard;   

            thisState = new State();
            stop = false;//reset stop
            gameStart = false;//reset the game 
        }
        return false;
    }  
    
    //the first four if statements check for collision with a wall of the game board
    //the fourth if statement checks to see if the piece is going to collide with a board piece.
    public boolean collides() {
        if(nextState.aY < 0 || nextState.bY < 0 || nextState.cY < 0 || nextState.pivotY < 0)
            return true;
        if(nextState.aX < 0 || nextState.bX < 0 || nextState.cX < 0 || nextState.pivotX < 0)
            return true;    
        if(nextState.aX > 9 || nextState.bX > 9 || nextState.cX > 9 || nextState.pivotX > 9)
            return true;   
        if(nextState.aY > 19 || nextState.bY > 19 || nextState.cY > 19 || nextState.pivotY > 19)
            return true;
        return (board[nextState.aY][nextState.aX] > 7 || board[nextState.bY][nextState.bX] > 7 ||
               board[nextState.cY][nextState.cX] > 7 || board[nextState.pivotY][nextState.pivotX] > 7); 
    }
    
	public void newPiece() {
//            State[] states = new State[7];
//            state[0] = ;
//            state[1] = ;
//            state[2] = ;
//            state[3] = ;
//            state[4] = ;
//            state[5] = ;
//            state[6] = ;
            
	}    
    
    public void printBoard() {
        for(int j=0;j<20;j++) {
            for (int i=0;i<10;i++) {
                if(board[j][i] < 10) {
                    System.out.print("[" + board[j][i] + " ]");
                }
                else {
                    System.out.print("[" + board[j][i] + "]");
                }
            }
            System.out.println();
        } 
        System.out.println("\n");
    }
}