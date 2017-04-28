/*CSCI 1101 – Assignment 5 – Minesweeper.java
This program was originally an extension of the TickleMeElmo program.
It also has text fields from the TemperatureConverter program,
and the audioClips from the audioClip demo program.
This program codes for a minesweeper game. 
<Jeremy Peters> <B00707976>  <Apr 9, 2017> */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.geometry.Pos;
import javafx.geometry.Insets;

//For mouse clicks.
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;


//To format the popup box.
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//This is for the left pane.
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

//For sound effects.
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//For playing a move.
import java.util.ArrayList;

public class Minesweeper extends Application
{
   //Displays the text at top
   private Text topText;
   //Displays the title and text at the right.
   private Text rightTitle;
   private Text rightText;
   //Displays parameters at the left.
   private Label rows;
   private Label columns;
   private Label totalBombs;
   private TextField inputRows;
   private TextField inputColumns;
   private TextField inputBombs;
   //Stores information at each square.
   private Square[][] grid;
   private Button[][] buttons;
   //Allows user to reset.
   private Button reset;
   //Stores the panes used for the box.
   private GridPane pane;
   private HBox bottom;
   private HBox top;
   private VBox right;
   private VBox left;
   //Combines all above panes.
   private BorderPane border;   
    
   //Sound effects for the game.
   private String marvinURL, bombURL, applauseURL;
   private AudioClip marvin, bomb, applause;
   
   //This will determine the dimensions of the grid.
   private int dimX = 30;
   private int dimY = 16;
   //"bombCount" bombCounts the number of bombs left
   private int initialBombs = 99;
   private int bombCount = initialBombs;
   //"numTurns" bombCounts the number of left clicks (turns) taken.
   //This is used to set the bombs up after the first turn.
   private int numTurns = 0;
      
   @Override
   public void start(Stage primaryStage) 
   {
      //This will introduce the game.
      marvinURL = "http://www.barbneal.com/wp-content/uploads/marvin03.mp3";
      marvin = new AudioClip(marvinURL);
      //Will provide sound effects for a the game ending.
      bombURL = "https://www.soundjay.com/mechanical/explosion-01.wav";
      bomb = new AudioClip(bombURL);
      //Provides sound effects for a applause if the user wins.
      applauseURL = "https://www.soundjay.com/human/applause-01.wav";
      applause = new AudioClip(applauseURL);
                
      //This will appear at the top of the GUI message box.  
      top = new HBox();
      top.setPadding(new Insets(10,10,10,10));
      //This topText will show if the wrong square has been hit.
      topText = new Text("GAME ON!");
      Font font1 = Font.font("Arial", FontWeight.BOLD,50);
      topText.setFont(font1);
      topText.setVisible(true);
            
      //Adds the topText button to the top box.
      top.getChildren().addAll(topText);
      top.setAlignment(Pos.CENTER);
      top.setStyle("-fx-background-color:BLUE");
            
      //The reset button will appear at the bottom of the GUI box.
      bottom = new HBox();
      bottom.setPadding(new Insets(10,10,10,10));
      bottom.setSpacing(10);
      //This topText allows the user to reset the game.
      reset = new Button("\t\tRESET\t\t");
      //Activates the reset button.
      reset.setOnMouseClicked(this::processButtonPress);
      //Adds the reset button to the bottom box.
      bottom.getChildren().addAll(reset);
      bottom.setAlignment(Pos.CENTER);
      bottom.setStyle("-fx-background-color:BLUE");
      
      //Formats the right of the GUI box.
      VBox right = new VBox();
      right.setPadding(new Insets(10));
      right.setSpacing(10);
      //This will show the number of bombs unmarked.
      rightTitle = new Text("Bombs");
      Font font2 = Font.font("Arial", FontWeight.BOLD, 20);
      rightTitle.setFont(font2);
      rightText = new Text("" + bombCount);
      rightText.setFont(font2);
      right.setAlignment(Pos.TOP_CENTER);
      right.setStyle("-fx-background-color:GREEN");
      right.getChildren().addAll(rightTitle, rightText);
      
      //Formats the left of the GUI box.
      VBox left = new VBox();
      left.setPadding(new Insets(10));
      left.setSpacing(10);
      //This area gives users control over the game settings.
      //Text controls = new Text("Controls");
      rows = new Label("Rows");
      columns = new Label("Columns");
      totalBombs = new Label("Total Bombs"); 
      //Creates the labels and text fields.
      rows.setFont(font2);
      columns.setFont(font2);
      totalBombs.setFont(font2);
      //Puts default parameters in text field.
      inputRows = new TextField("" + dimY);
      inputColumns = new TextField("" + dimX);
      inputBombs = new TextField("" + bombCount);
      //Activates the textfields.
      inputRows.setOnAction(this::processReturn);
      inputColumns.setOnAction(this::processReturn);
      inputBombs.setOnAction(this::processReturn);      
      //This adds the Text and TextField to the left pane.
      left.getChildren().add(rows);
      left.getChildren().add(inputRows);
      left.getChildren().add(columns);
      left.getChildren().add(inputColumns);
      left.getChildren().add(totalBombs);
      left.getChildren().add(inputBombs);
      left.setAlignment(Pos.TOP_LEFT);
      left.setStyle("-fx-background-color:GREEN");    
                 
      //Formats the gridPane for the middle of the GUI box.
      //The tickle-me-elmo template used a flow pane, but a grid pane is used,
      //so that the buttons used are all arranged in a grid.
      pane = new GridPane();
      pane.setAlignment(Pos.CENTER);
      pane.setStyle("-fx-background-color:WHITE");
      pane.setPadding(new Insets(10,10,10,10));
      pane.setHgap(2);
      pane.setVgap(2);
      //Adds the grid buttons to the pane at the specified row and column number.           
      
      //This will take care of adding the grid coordinates and the buttons.  
      setGame();
                                   
      //This will add all panes to the BorderPane.          
      border = new BorderPane();
      border.setBottom(bottom);
      border.setTop(top);
      border.setRight(right);
      border.setLeft(left);
      border.setCenter(pane);
      
      //This sets the scene for the game.
      Scene scene = new Scene(border, 1100,700);
      primaryStage.setTitle("Minesweeper");
      primaryStage.setScene(scene);
      primaryStage.show();
      primaryStage.setResizable(true);
      
      //This starts the game.
      marvin.play();                   
   }    
   
   //This will process any input into the text fields
   public void processReturn(ActionEvent event)
   {                  
      //Any input into the text field will reset with new parameters.
      setGame();
         
   }//End method 
   
   //Processes the user mouse clicks.
   public void processButtonPress(MouseEvent event)
   {
      //Stops all audio clips.
      if(marvin.isPlaying())
         marvin.stop();
      if(bomb.isPlaying())
         bomb.stop();
      if(applause.isPlaying())
         applause.stop();
      if(marvin.isPlaying())
         marvin.stop();
         
      MouseButton button = event.getButton();
      if(event.getButton() == MouseButton.PRIMARY)
      {   
         if(event.getSource()==reset)
            setGame();          
                 
         else
         {
            //Loops through the grid array to find the source of the mouse click.
            for(int i=0; i<dimX; i++)
               for(int j=0; j<dimY; j++)
                  //The button at grid[i][j] is pushed.
                  if(event.getSource() == buttons[i][j])
                     if(buttons[i][j].getText().equals("  "))
                     {
                        //One more turn has been taken.
                        numTurns++;
                        //User has only taken one turn, and now the bombs are populated.
                        if(numTurns == 1)
                           grid[i][j].populateGrid(grid, bombCount);
                        
                        grid[i][j].step();                        
                        //the clicked square is a bomb.
                        if(grid[i][j].hasBomb())
                           userLoses();
                        else if(!grid[i][j].isClear())
                           buttons[i][j].setText("" + grid[i][j].getClue());                                        
                        //the clicked square is not hit.
                        else
                        {
                           //This will obtain all cleared squares to display 
                           //when the user has played on a square nowhere near a bomb. 
                           ArrayList<Square> clearing = grid[i][j].getClearing(grid);
                           //This method will update the GUI buttons accordingly.
                           update(clearing);                                                                                                                    
                        }                             
                     }//end if(button is pushed).
         }//end else
      }//end if(primary mousebutton)
      
      //This will handle right mouse clicks.
      if(event.getButton() == MouseButton.SECONDARY)
      {
         for(int i=0; i<dimX; i++)
            for(int j=0; j<dimY; j++)
               if(event.getSource() == buttons[i][j])
               {
                  //A right mouse click flags a square if the square is not already flagged.
                  if(buttons[i][j].getText().equals("  "))
                  {
                     buttons[i][j].setText("F");
                     buttons[i][j].setStyle("-fx-color:ORANGE");
                     grid[i][j].flag();
                     bombCount--;
                     rightText.setText("" + bombCount);
                  }   
                  //A right mouse click on a flagged square will unflag the square.
                  else if(buttons[i][j].getText().equals("F"))
                  {
                     buttons[i][j].setText("?");
                     buttons[i][j].setStyle("-fx-color:LIGHTPINK");
                     grid[i][j].unFlag();                     
                     rightText.setText("" + bombCount);
                  }
                  else if(buttons[i][j].getText().equals("?"))
                  {
                     buttons[i][j].setText("  ");
                     buttons[i][j].setStyle("-fx-color:LIGHTGRAY");
                     bombCount++;
                     grid[i][j].unFlag();
                     rightText.setText("" + bombCount);
                  }                           
               }                        
      }//end if(secondary mousebutton)  
      
      //The middle button was pushed.
      if(event.getButton() == MouseButton.MIDDLE)
      {
         int flagCount = 0;
         for(int i=0; i<dimX; i++)
            for(int j=0; j<dimY; j++)
               //Finds source of event.
               if(event.getSource()==buttons[i][j])                  
               {
                  String buttonText = buttons[i][j].getText();
                  //May or may not be necessary
                  if(buttonText.equals("" + grid[i][j].getClue()))
                  {
                     //Stores the result to update.
                     ArrayList<Square> result = new ArrayList<Square>();
                     //Adds neighbours of this square.
                     ArrayList<Square> neighbours = new ArrayList<Square>();                    
                     grid[i][j].appendNeighbours(neighbours, grid);                     
                     //Loops through the neighbours to get clearing to add to result.
                     for(int k = neighbours.size()-1; k>=0; k--)
                     {
                        //This is a neighbour.
                        Square s1 = neighbours.get(k);
                        //Counts flags of neighbour.
                        if(s1.isFlagged())
                           flagCount++;
                        //This is the clearing of the square s1.
                        ArrayList<Square> clearing = s1.getClearing(grid);                         
                        //Adds clearing to the result.
                        for(int l = clearing.size()-1; l>=0; l--)
                        {
                           Square s2 = clearing.get(l);
                           //result must not have this square and this square must not be flagged.
                           if(!result.contains(s2) && !s2.isFlagged() && !s2.isPlayed())
                              result.add(s2);
                        }                                                     
                     }
                     //System.out.println(clearing);         
                     if(flagCount==grid[i][j].getClue())
                        update(result);
                  }                 
               }
         
      }//end if(middle mousebutton)
      
      determineWinner();
   }//end method                     
   
   //This will reset the game with new dimensions, and bomb counts.
   public void setGame()
   {
      //Resets dimensions and bomb count to whatever was in TextFields.
      int tempX, tempY, tempB;
      try
      {
         tempX = Integer.parseInt(inputColumns.getText());
         tempY = Integer.parseInt(inputRows.getText());
         tempB = Integer.parseInt(inputBombs.getText());
      }catch(Exception inputMisMatchException)
      {
         tempX = dimX;
         tempY = dimY;
         tempB = initialBombs;
      }
            
      dimX = tempX;
      dimY = tempY;
      initialBombs = tempB;
      
      if(initialBombs<0)
         initialBombs = 0;
      if(initialBombs>(dimX*dimY-9))
         initialBombs = dimX*dimY-9;
               
      //Stops all audio clips.
      if(marvin.isPlaying())
         marvin.stop();
      if(bomb.isPlaying())
         bomb.stop();
      if(applause.isPlaying())
         applause.stop();
         
      //This clears any previous buttons from the pane,
      //if applicable.
      if(!pane.getChildren().isEmpty())
         pane.getChildren().clear();
      
      //Resets grid and buttons to new dimensions.
      grid = new Square[dimX][dimY];
      buttons = new Button[dimX][dimY];
      for(int i=0; i<dimX; i++)
         for(int j=0; j<dimY; j++)
         {
            //Makes a new Squaren object.
            grid[i][j] = new Square(i,j);
            //Makes a new button object.
            buttons[i][j] = new Button();
            buttons[i][j].setText("  ");
            buttons[i][j].setStyle("-fx-background-Color:LIGHTGRAY");
            buttons[i][j].setOnMouseClicked(this::processButtonPress);
            //Fixes the button object.
            buttons[i][j].setLayoutX(i);
            buttons[i][j].setLayoutY(j);
            //Adds this button to pane.
            pane.add(buttons[i][j], i, j);         
         }
      
      //This will continue resetting the game as normal.   
      reset();                                   
   }      
   
   //This will restart the game without affecting the dimensions or the total bomb count.
   public void reset()
   {
      numTurns = 0;
      //Resets the bomb count.
      bombCount = initialBombs;
      rightText.setText("" + bombCount);
      //Replaces the "GAME OVER" topText.
      topText.setText("GAME ON!");
      top.setStyle("-fx-background-color:BLUE");
      
      Square.resetGrid(grid);
      for(int i=0; i<dimX; i++)
         for(int j=0; j<dimY; j++)
         {
            //Resets the topText on each button.
            buttons[i][j].setText("  ");
            grid[i][j].unFlag();   
            //Activates the buttons.
            buttons[i][j].setDisable(false);
         }
   }
        
   //This will update the buttons according to the clearing in a 
   //board resulting from an empty mouse clicked square.
   //This assumes that the squares in the list do not have a bomb on them.
   public void update(ArrayList<Square> list)
   {
      //Loops through neighbours of the clicked square "clearing"
      for(int k=0; k<list.size(); k++)
      {
         //This will store the square, and the x and y coordinates, respectively.
         Square tempSq = list.get(k);
         int x = tempSq.getX();
         int y = tempSq.getY();
         //This will update whether the square has been hit.
         tempSq.step();
         //The Square object has no neighbouring bombs.
         
         if(grid[x][y].isClear())
         {
            buttons[x][y].setText("  ");
            buttons[x][y].setDisable(true);
         }
         //The Square object has neighbouring bombs.
         else if(!grid[x][y].hasBomb())
            buttons[x][y].setText("" + grid[x][y].getClue());            
         else
            userLoses();        
      }           
   }
   
   //This will display all bombs of a given grid of squares.
   public void userLoses()
   {
      //User loses the game.
      top.setStyle("-fx-background-color:RED");
      //Makes the top pane red.
      topText.setText("GAME OVER!");
      
      //Loops through the grid, and shows all bombs.
      for(int i=0; i<dimX; i++)
         for(int j=0; j<dimY; j++)
         {
            //updates each bombed button object.
            if(grid[i][j].hasBomb())
            {
               buttons[i][j].setText("X");
               buttons[i][j].setStyle("-fx-color:RED");
            }   
            //disables all buttons.   
            buttons[i][j].setDisable(true);
         }
      
      //This will create a bomb sound effect.
      bomb.play();         
   }
   
   //This will determine whether the user won the game.
   public void determineWinner()
   {
      boolean userWon = true;
      //userWon will be updated to false if there is a square that
      //has no bomb, and was not stepped on (the button is enabled).
      //Or if there was a bombed square that was stepped on (the button is disabled).
      for(int i=0; i<dimX; i++)
         for(int j=0; j<dimY; j++)
         {
            if(!grid[i][j].hasBomb() && !grid[i][j].isPlayed())
               userWon = false;
            else if(grid[i][j].hasBomb() && grid[i][j].isPlayed())
               userWon = false;
         }         
      //The GUI will show whether the user won the game. 
      if(userWon)
      {
         top.setStyle("-fx-background-color:YELLOW");
         topText.setText("YOU WIN!!");
         applause.play();         
      }      
   }   
           
   public static void main(String[] args)
   {
      //Launches the program.
      Application.launch(args);         
   }
}