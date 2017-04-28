/*CSCI 1101 – Assignment 5 – Minesweeper.java
This program codes for the squares in the Minesweeper game,
and is responsible for keeping track of bombs and places the user has clicked. 
<Jeremy Peters> <B00707976>  <Apr 9, 2017> */

//Although the arrays of buttons in Minesweeper could be added to this class,
//I decided to keep all of the GUI elements in one class, and only have this
//"Square" class keep track of the game board in a "behind-the-scenes" manner.

import java.util.ArrayList;

//This will occupy a square on the board object.
public class Square
{
   //Attributes.
      
   //Determines whether there is a bomb on this square.
   //and whether the user stepped this square.
   //The boolean flagged determines whether the user has flagged this square.
   private boolean bomb, stepped, flagged;
   //"Clue" determines the nubmer of neighbouring squares with a bomb.
   //This int gives the player a clue about the bombs.
   private int clue;
   //Determines the x and y positions of this square object.
   private int x;
   private int y;
   
   //Constructor.
   public Square(int x,int y)
   {
      bomb = false;
      stepped = false;
      flagged = false;
      clue = 0;
      this.x = x;
      this.y = y;
   }
   
   //Get and set methods.
   public boolean hasBomb()
   {
      return bomb;
   }
   public boolean isPlayed()
   {
      return stepped;
   }
   public boolean isFlagged()
   {
      return flagged;
   }      
   public int getClue()
   {
      return clue;
   }
   public int getX()
   {
      return x;
   }
   public int getY()
   {
      return y;
   }
   //This will update the square
   public void step()
   {
      stepped = true;
   }
   //This will flag a square
   public void flag()
   {
      flagged = true;
   }
   //This will remove a flag from a square
   public void unFlag()
   {
      flagged = false;
   }      
   //This method increments the int "clue" based on neighbouring bombs.
   public void incClue()
   {
      clue++;
   }   
   //Determines if this square has no bomb in or beside it (is "empty").      
   public boolean isClear()
   {
      return bomb == false && clue == 0;
   }               
   //Resets the square at the beginning of the game.
   public void reset(boolean bomb)
   {
      this.bomb = bomb;
      stepped = false;
      flagged = false;
      clue = 0;   
   }
   
   //This will provide the starting grid for the user.
   public static void resetGrid(Square[][] grid)
   {
      for(int i=0; i<grid.length; i++)
         for(int j=0; j<grid[i].length; j++)
            grid[i][j].reset(false);
   }
   
   //This will randomly populate a given grid with
   //the number of bombs indicated by the variable "count"
   public void populateGrid(Square[][] grid, int count)
   {
      //m and n store the dimensions of the grid.
      //i and j randomly assign bombs to squares from
      //0 (inclusive) to m and n (exclusive, respectively).
      int m = grid.length, n = grid[0].length, i, j;
      //int i = (int)(m*Math.random()), j = (int)(n*Math.random());
      //The bomb count is not out of bounds.
      while(0<count && count<=(m*n-9))
      {
         //prevents bombs from landing on the first click.
         //or in the neighbourhood of the first click (if applicable)
         //The test continues if there are 9 safe squares, i and j are next to the clicked square,
         //or i and j are on the clicked square. 
         do
         {
            i = (int)(m*Math.random());
            j = (int)(n*Math.random());
            System.out.println(count);            
         }while(x-1<=i && i<=x+1 && y-1<=j && j<=y+1);
         
         //Detonates a square only if the square doesn't already have a bomb.
         if(!grid[i][j].hasBomb())
         {
            grid[i][j].reset(true);
            count--;
         }               
      }
      
      updateNeighbours(grid);
   }         
             
   //Updates the entire grid based on the positions of the bombs.
   public static void updateNeighbours(Square[][] grid)
   {
      for(int i=0; i<grid.length; i++)
         for(int j=0; j<grid[i].length; j++)
         { 
            ArrayList<Square> list = new ArrayList<Square>();
            //Gets neighbours of this square.
            grid[i][j].appendNeighbours(list, grid);
            for(int k=0; k<list.size(); k++)
               //Increments the clue number if there is a bomb.
               if(grid[i][j].hasBomb())
                  list.get(k).incClue();
         }         
   }
                       
   //This will provide all squares that have no bombs 
   //and are connected through neighbours to this square.
   public ArrayList<Square> getClearing(Square[][] grid)
   {
      //This gets the clearing around this square object 
      ArrayList<Square> clearing = new ArrayList<Square>();
      clearing.add(this);
      //This will get the neighbours of squares who are empty.
      for(int i=0; i<clearing.size(); i++)
      {
         //If this element is not next to a bomb, we want the clearing
         //to add neighbours of this element.
         if(clearing.get(i).isClear())
         {
            clearing.get(i).appendNeighbours(clearing, grid);
         }           
      }
      return clearing;
   }   
   
   //This will provide all squares immediately around this current square,
   //that do not have bombs around them.
   public void appendNeighbours(ArrayList<Square> list, Square[][] grid)
   {
      //This adds all clearing to this square object. 
      //This "if maze" prevents out of bounds exceptions on every square to be added.         
      if(this == grid[x][y])
      {
         if(x>0)
         {
            if(y>0)
               append(list, grid[x-1][y-1]);
            append(list, grid[x-1][y]);
            if(y<grid[0].length-1)
               append(list, grid[x-1][y+1]);
         }
            
         if(y>0)
            append(list, grid[x][y-1]);
         if(y<grid[0].length-1)
            append(list, grid[x][y+1]);
            
         if(x<grid.length-1)
         {
            if(y>0)
               append(list, grid[x+1][y-1]);
            append(list, grid[x+1][y]);
            if(y<grid[0].length-1)
               append(list, grid[x+1][y+1]);
         }
      }           
   }
      
   //The "append" method will add to an arrayList, only if that list doesn't already contain the element.
   public static void append(ArrayList<Square> list, Square square)
   {
      if(!list.contains(square))
         list.add(square);
   }     
   
   //This will count the number of bombs that appear in the grid.
   public static int countBombs(Square[][] grid)
   {
      int count = 0;
      for(int i=0; i<grid.length; i++)
         for(int j=0; j<grid[i].length; j++)
            if(grid[i][j].hasBomb())
               count++;
      return count;
   }
   
   public String toString()
   {
      return "x: " + x + ", y: " + y + ", clue: " + clue;
   }                   
}//end class      
            
      
   