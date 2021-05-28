import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**
 * Tic-Tac-Toe with simple GUI.
 * 
 * @author Oluwaseyi Sehinde-Ibini
 * @version CS50 PROJECT
*/

public class TicTacToeGUI implements ActionListener
{
   //Class constants
   private static final int WINDOW_WIDTH = 300;
   private static final int WINDOW_HEIGHT = 300;
   private static final int TEXT_WIDTH = 30;
    
   private static final String PLAYER_X = "X"; // player using "X"
   private static final String PLAYER_O = "O"; // player using "O"
   private static final String EMPTY = "";  // empty cell
   private static final String TIE = "T"; // game ended in a tie
   
   ImageIcon icon = new ImageIcon("Player.PNG");
   
   private int xTotalWin;
   
   private int oTotalWin;
   
   private int tTotalTie;
  
   private String player;   // current player (PLAYER_X or PLAYER_O)

   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

   private int numFreeSquares; // number of squares still free
   
   private JMenuItem resetItem; // reset board
    
   private JMenuItem quitItem; // quit
   
   private JMenuItem resetScore;
   
   private String oImgPath = "playero.png";
   
   private String xImgPath = "playerx.png"; 
   
   private String soundPath = "tada.mp3";

   private JMenuItem playerXStart; //player x starts
   
   private JMenuItem playerOStart; //player o starts
   
   private JLabel gameText; // current message
   
   private JLabel gameStatistics;
      
   private JButton board[][]; // 3x3 array of JButtons
    
   private JFrame window = new JFrame("TIC-TAC-TOE"); 

   /**
   * Constructs a new Tic-Tac-Toe GUI board
   */
    
   public TicTacToeGUI()
   {
       setUpGUI(); // set up GUI
       setFields(); // set up other fields
       player = PLAYER_X;
   }

   /**
   * Set up the non-GUI fields
   *
   */
   private void setFields() 
   {
       winner = EMPTY;
       numFreeSquares = 9;
   }
   
   private void winSound()
   {
       try
       {
           File sound = new File(soundPath).getAbsoluteFile();
           Clip clip = AudioSystem.getClip();
           clip.open(AudioSystem.getAudioInputStream(sound));
           clip.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    
   }
   
   /**
   * reset the game so we can start again.
   *
   */
   private void resetGame() 
   {
       // reset board
       for (int i = 0; i < 3; i++)
       {
           for (int j = 0; j < 3; j++)
           {             
               board[i][j].setText(EMPTY);
               board[i][j].setEnabled(true);
               board[i][j].setIcon(null);
            }
       }
       gameText.setText(gameProgress());
       updateGameStatistics();
       // reset other fields
       setFields();      
    }
    
    private String gameProgress()
    {
        return (player.equals(PLAYER_X)) ? "Game in Progress: X's turn" : "Game in Progress: O's turn";
    }
    
    public void resetScore()
    {
        xTotalWin = 0;
        oTotalWin = 0;
        tTotalTie = 0;
        resetGame();
    }
    
   
   /**
   * Action Performed (from actionListener Interface).
   * (This method is executed when a button is selected.)
   *
   * @param the action event
   */

   public void actionPerformed(ActionEvent e)
   {
       // see if it's a menu item
       if(e.getSource() instanceof JMenuItem) 
       {
           JMenuItem select = (JMenuItem) e.getSource();
           if (select == resetScore)
           {
               resetScore();
               return;
           }
           
           if (select==playerOStart)
           {
               player = PLAYER_O; 
               resetGame();
               return;
           }
           
           if (select==playerXStart)
           {               
               player = PLAYER_X;
               resetGame();
               return;
           }       
           
           if (select==resetItem)
           { 
               resetGame();// reset
               return;
           }
           System.exit(0);   // must be quit
       }
        
       // it must be a button
       JButton chose = (JButton) e.getSource(); // set chose to the button clicked
       chose.setText(player);// set its text to the player's mark
       String usePath = player.equals(PLAYER_X) ? xImgPath : oImgPath;
       chose.setIcon(new javax.swing.ImageIcon(this.getClass().getResource(usePath)));
       chose.setDisabledIcon(new javax.swing.ImageIcon(this.getClass().getResource(usePath)));
       chose.setEnabled(false);   // disable button (can't choose it now)
       numFreeSquares--;
       
       //see if game is over 
       if(haveWinner(chose))
       {
           winner = player; // must be the player who just went
          if (winner.equals(PLAYER_X))
          {
              xTotalWin++;
          }
          else
          {
              oTotalWin++;
          }
          
          winSound();
          
       }
       else if(numFreeSquares==0)
       {
           winner = TIE; // board is full so it's a tie
           tTotalTie++;
       }
       
       // if have winner stop the game
       if (winner!=EMPTY) 
       {
           disableAll(); // disable all buttons
           // print winner
           String s = "Game over: ";
           if (winner == PLAYER_X) 
           {
               s += "X wins";
           } 
           else if (winner == PLAYER_O) 
           {
               s += "O wins";
           } 
           else if (winner == TIE) 
           {
               s += "Tied game";
           }   
           gameText.setText(s);
           
           updateGameStatistics();
       } 
       else
       {
           // change to other player (game continues)
           if (player==PLAYER_X)
           {
               player=PLAYER_O;
               gameText.setText("Game in progress: O's turn");
           } 
           else
           {
               player=PLAYER_X;
               gameText.setText("Game in progress: X's turn");
           }
           
           updateGameStatistics();
       }
   }
    
   private void updateGameStatistics()
   {
       String msg = "Wins  -  Player X : ";
       msg += Integer.toString(xTotalWin);
       msg += "  |  Player O :  ";
       msg += Integer.toString(oTotalWin);
       msg += "  |  Ties : ";
       msg += Integer.toString(tTotalTie);
       gameStatistics.setText(msg);
   }
   
   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param Square just filled
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(JButton c) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).
       if(numFreeSquares>4) 
       {
           return false;
       }
       
       // find the square that was selected
       int row=0, col=0;
       
       outerloop: // a label to allow us to break out of both loops
       for (int i = 0; i < 3; i++) 
       {
           for (int j = 0; j < 3; j++)
           {
              if (c==board[i][j])
              { 
                  // object identity
                  row = i;
                  col = j;  //  row, col represent the chosen square
                  break outerloop; // break out of both loops
              }    
           }
       }
       
       // check row "row"
       if( board[row][0].getText().equals(board[row][1].getText()) && board[row][0].getText().equals(board[row][2].getText()) )
       {
           return true;
       }
       
       // check column "col"
       if (board[0][col].getText().equals(board[1][col].getText()) &&board[0][col].getText().equals(board[2][col].getText()) ) 
       {
           return true;
       }

       // if row=col check one diagonal
       if (row == col)
       {
          if( board[0][0].getText().equals(board[1][1].getText()) && board[0][0].getText().equals(board[2][2].getText()) ) 
          {
              return true;
          }
       }

       // if row=2-col check other diagonal
       if (row == 2-col)
       {
          if( board[0][2].getText().equals(board[1][1].getText()) && board[0][2].getText().equals(board[2][0].getText()) )
          {
              return true;
          }
       }

       // no winner yet
       return false;
   }
   
   
   /**
   * Disables all buttons (game over)
   */
   private void disableAll()
   {
       if (numFreeSquares==0) 
       {
           return; // nothing to do
       }
       
       int i, j;
       for (i = 0; i < 3; i++) {
           for (j = 0; j < 3; j++) {
              board[i][j].setEnabled(false);
           }
       }
   }

   /**
   * Set up the GUI
   *
   */
   private void setUpGUI()
   {

        // for control keys
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); 

        window.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set up the menu bar and menu
        JMenuBar menubar = new JMenuBar();
        window.setJMenuBar(menubar); // add menu bar to our frame

        JMenu fileMenu = new JMenu("Game"); // create a menu called "Game"
        menubar.add(fileMenu); // and add to our menu bar
       
        resetItem = new JMenuItem("New"); // create a menu item called "Reset"
        fileMenu.add(resetItem); // and add to our menu 
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));//(can also use ctrl-N:)
        resetItem.addActionListener(this);
        
        resetScore = new JMenuItem("Reset Score");
        fileMenu.add(resetScore);
        resetScore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
        resetScore.addActionListener(this);

        quitItem = new JMenuItem("Quit"); // create a menu item called "Quit"
        fileMenu.add(quitItem); // and add to our menu 
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));//(can also use ctrl-Q:)
        quitItem.addActionListener(this);
        
        JMenu startingPlayer = new JMenu("Starting Player");
        menubar.add(startingPlayer);
        
        playerXStart = new JMenuItem("Player X");
        startingPlayer.add(playerXStart);// and add to our menu 
        playerXStart.setMnemonic(KeyEvent.VK_X); //add underline under the X for alt-X
        playerXStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));//(can also use ctrl-X:)
        playerXStart.addActionListener(this);
        
        playerOStart = new JMenuItem("Player O");
        startingPlayer.add(playerOStart);// and add to our menu 
        playerOStart.setMnemonic(KeyEvent.VK_O); // add underline under the O for alt-O
        playerOStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));//(can also use ctrl-O:)
        playerOStart.addActionListener(this);
                
        window.getContentPane().setLayout(new BorderLayout()); // default so not required
        
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));
        window.getContentPane().add(gamePanel, BorderLayout.CENTER);
        
        gameText = new JLabel("Game in Progress: X's turn");
        gameStatistics = new JLabel("Wins - Player X : 0  |  Player O : 0  |  Ties : 0");
        window.getContentPane().add(gameText, BorderLayout.SOUTH);
        window.getContentPane().add(gameStatistics, BorderLayout.NORTH);
       
        
        // create JButtons, add to window, and action listener
        board = new JButton[3][3];
        Font font = new Font("Dialog", Font.PLAIN, 0);
        for (int i = 0; i < 3; i++)
        {
           for (int j = 0; j < 3; j++)
           {
              board[i][j] = new JButton(EMPTY);
              board[i][j].setFont(font);
              gamePanel.add(board[i][j]);
              board[i][j].addActionListener(this);
           }
        }
     
        window.setVisible(true);
   }

}