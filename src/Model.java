import java.awt.Point;
import java.util.*;

public class Model {
   
   
//Given constants to do with the map size/view
   final static int WINDOW_SIZE = 5;
   final static int MAXIMUM_X = 80;
   final static int MAXIMUM_Y = 80;
   
   
 //Definitions for the state
   final static int UP = 0;
   final static int RIGHT = 1;
   final static int DOWN = 2;
   final static int LEFT = 3;
   
   final static char DIRECTION_UP = '^';
   final static char DIRECTION_RIGHT = '>';
   final static char DIRECTION_DOWN = 'v';
   final static char DIRECTION_LEFT = '<';
   
   final static char PLAIN = ' ';
   final static char TREE = 'T';
   final static char DOOR = '-';
   final static char WALL = '*';
   final static char WATER = '~';
   
   final static char AXE = 'a';
   final static char KEY = 'k';
   final static char DYNAMITE = 'd';
   final static char TREASURE = '$';
   
   final static char UNEXPLORED = '?';
   
   final static char TURN_LEFT = 'L';
   final static char TURN_RIGHT = 'R';
   final static char MOVE_FORWARD = 'F';
   final static char CHOP_TREE = 'C';
   final static char USE_DYNAMITE = 'B';
   final static char UNLOCK_DOOR = 'U';
   
   private int xLoc;
   private int yLoc;
   private int direction;
   
   private boolean treasureVisible;
   
   private boolean haveKey;
   private boolean haveAxe;
   private boolean haveRaft;
   private boolean haveTreasure;
   private int numDynamites;
   
   
   private Map<Point, Character> world;
   
   private Point treasureLoc;
   private LinkedList<Point> axes;
   private LinkedList<Point> dynamites;
   private LinkedList<Point> keys;
   private LinkedList<Point> trees;
   
   private LinkedList<Point> doors;
   
   public Model() {
      
      this.xLoc = 0;
      this.yLoc = 0;
      this.direction = DOWN;
      
      this.treasureVisible = false;
      
      this.haveKey = false;
      this.haveAxe = false;
      this.haveRaft = false;
      this.haveTreasure = false;
      this.numDynamites = 0;  
      
      this.world = new HashMap<>();
      
      this.axes = new LinkedList<Point>();
      this.dynamites = new LinkedList<Point>();
      this.keys = new LinkedList<Point>();
      this.trees = new LinkedList<Point>();
      
      this.doors = new LinkedList<Point>();
      //We might start at the bottom which means that we can go MAXIMUM_Y Upwards...
      //But we might also start at the top which means we can go MAXIMUM_Y Downwards...
      //So we should just have MAXIMUM_Y in both directions. And the same for the x axis.
      for(int x = -MAXIMUM_X; x <= MAXIMUM_X; x++) {
         for(int y = -MAXIMUM_Y; y <= MAXIMUM_Y; y++) {
            //Pre-fill the world with UNEXPLORED;
            this.world.put(new Point(x,y), UNEXPLORED);
         }
      }
      //We start looking downwards. I think?
      this.world.put(new Point(0,0), DIRECTION_DOWN);
   }
   
   public boolean haveAxe() {
      return haveAxe;
   }
   public boolean haveKey() {
      return haveKey;
   }
   public boolean haveRaft() {
      return haveRaft;
   }
   public int numDynamites() {
      return numDynamites;
   }
   public boolean haveTreasure() {
      return haveTreasure;
   }
   public Map<Point, Character> getWorld() {
      return world;
   }
   public Point getLoc() {
      return new Point(xLoc, yLoc);
   }
   public int getDirection() {
      return this.direction;
   }
   public Point getTreasureLoc() {
      return this.treasureLoc;
   }
   public LinkedList<Point> getAxeLocs(){
      return this.axes;
   }
   public LinkedList<Point> getKeyLocs(){
      return this.keys;
   }
   public LinkedList<Point> getTreeLocs(){
      return this.trees;
   }
   public LinkedList<Point> getDynamiteLocs(){
      return this.dynamites;
   }
   
   public LinkedList<Point> getDoorLocs(){
      return this.doors;
   }

   public boolean treasureVisible() {
      return this.treasureVisible;
   }
   
   public void update(char view[][]) {
      //We need to rotate the view we're given so that it's the same orientation as our original map.
      int rotationsRequired = 0;
      
      switch(this.direction) {
         case UP:
            break;
         case RIGHT:
            rotationsRequired = 1;
            break;
         case DOWN:
            rotationsRequired = 2;
            break;
         case LEFT:
            rotationsRequired = 3;
            break;

      }
      for(int i = 0; i < rotationsRequired; i++) {
         view = rotateMap(view);
      }
      for(int i = 0; i < WINDOW_SIZE; i++) {
         for(int j = 0; j < WINDOW_SIZE; j++) {
            char currTile = view[i][j];
            int currX = xLoc + (j-2);
            int currY = yLoc + (2-i);
            
            if(i == 2 && j == 2) {
               switch(direction) {
                  case UP:
                     currTile = DIRECTION_UP;
                     break;
                  case RIGHT:
                     currTile = DIRECTION_RIGHT;
                     break;
                  case DOWN:
                     currTile = DIRECTION_DOWN;
                     break;
                  case LEFT:
                     currTile = DIRECTION_LEFT;
                     break;
               }
            }
            Point tile = new Point(currX, currY);
            
            switch(currTile) {
               case AXE:
                  if(!this.axes.contains(tile)) {
                     this.axes.add(tile);
                  }
                  break;
               case DYNAMITE:
                  if(!this.dynamites.contains(tile)) {
                     this.dynamites.add(tile);
                  }
                  break;
               case TREASURE:
                  this.treasureVisible = true;
                  this.treasureLoc = tile;
                  break;
               case KEY:
                  if(!this.keys.contains(tile)) {
                     this.keys.add(tile);
                  }
                  break;
               case TREE:
                  if(!this.trees.contains(tile)) {
                     this.trees.add(tile);
                  }
                  break;
               case DOOR:
                  if(!this.doors.contains(tile)) {
                     this.doors.add(tile);
                  }
                  break;
            }
            this.world.put(tile, currTile);
         }
      }
      //showMap();
   }
   
   private static char[][] rotateMap(char[][] map){
      int x = map.length;
      int y = map[0].length;
      char[][] rotatedMap = new char[y][x];
      for(int l = 0; l < x; l++) {
         for(int h = 0; h < y; h++) {
            rotatedMap[h][x - 1 - l] = map[l][h];
         }
      }
      return rotatedMap;
   }
   //Update after the user has input a move...update inventory/change map rep
   //E.g cut a tree, has raft, or stepped off raft, doesn't have raft anymore.
   public void updateMove(char move) {
      
      switch(move) {
      //Right turn
         case 'R':
            switch (this.direction) {
               case UP:
                  direction = RIGHT;
                  break;
               case RIGHT:
                  direction = DOWN;
                  break;
               case DOWN:
                  direction = LEFT;
                  break;
               case LEFT:
                  direction = UP;
                  break;
            }
            break;
         //Left Turn
         case 'L':
            switch (this.direction) {
               case UP:
                  direction = LEFT;
                  break;
               case RIGHT:
                  direction = UP;
                  break;
               case DOWN:
                  direction = RIGHT;
                  break;
               case LEFT:
                  direction = DOWN;
                  break;
            }
            break;
         case 'F':
           char frontTile = world.get(frontTile(new Point(this.xLoc, this.yLoc)));
           if((frontTile == WALL) || (frontTile == DOOR) || (frontTile == TREE)) {
              break;
           }
           if(((world.get(new Point(this.xLoc, this.yLoc))) == WATER) && (canMoveOntoTile(frontTile))){
              this.haveRaft = false;
           }
           if (frontTile == AXE) {
              haveAxe = true;
           }
           else if (frontTile == KEY) {
              haveKey = true;
           }
           else if (frontTile == DYNAMITE) {
              numDynamites += 1;
           }
           else if (frontTile == TREASURE) {
              haveTreasure = true;
           }
           switch(this.direction) {
              case UP:
                 yLoc += 1;
                 break;
              case RIGHT:
                 xLoc += 1;
                 break;
              case DOWN:
                 yLoc -= 1;
                 break;
              case LEFT:
                 xLoc -= 1;
                 break;
           }
           //For these ones, we'll get the updated 'unlocked' or 'cut' or 'blown up' things in the next view anyway
         case 'C':
            break;
         case 'U':
            this.doors.remove(frontTile(new Point(xLoc, yLoc)));
            break;
         case 'B':
            numDynamites -= 1;
            break;
      }
   }
   public Point frontTile(Point tile) {
      int x = (int) tile.getX();
      int y = (int) tile.getY();
      
      switch(this.direction) {
         case UP:
            y += 1;
            break;
         case RIGHT:
            x += 1;
            break;
         case DOWN:
            y -= 1;
            break;
         case LEFT:
            x -= 1;
            break;
      }
      return new Point(x,y);
   }
   public static boolean canMoveOntoTile(char tile) {
      return((tile == PLAIN) ||
             (tile == AXE) ||
             (tile == KEY) ||
             (tile == DYNAMITE) ||
             (tile == DIRECTION_UP) ||
             (tile == DIRECTION_LEFT) ||
             (tile == DIRECTION_RIGHT) ||
             (tile == DIRECTION_DOWN)
            );
   }
   public static boolean canPotentiallyMoveOntoTile(char tile, boolean haveAxe, boolean haveKey, boolean haveRaft) {
      return((tile == PLAIN) ||
             (tile == AXE) ||
             (tile == KEY) ||
             (tile == DYNAMITE) ||
             (tile == DIRECTION_UP) ||
             (tile == DIRECTION_LEFT) ||
             (tile == DIRECTION_RIGHT) ||
             (tile == DIRECTION_DOWN) ||
             (tile == TREE && haveAxe) ||
             (tile == WATER && haveRaft) ||
             (tile == DOOR && haveKey) /*||
             (tile == WALL && haveDynamite) still thinking about when to use dynamite*/
            );
   }
   //Returns the nearest reachable point that can reveal any ?. Returns (0,0) (which is always known) if there are no ?'s
   public Point nearestReachableRevealingTile(Point curr) {
      //Search outwards in squares
      int x = (int) curr.getX();
      int y = (int) curr.getY();
      for(int i = 1; i < MAXIMUM_X/2; i++) {
         for(int x1 = -i; x1 < i; x1++) {
            for(int y1 = -i; y1 < i; y1++) {
               Point currPoint = new Point(x+x1, y+y1);
               if(world.containsKey(currPoint)) {
                  if(canSeeUnknowns(currPoint)) {
                     AStarSearch a = new AStarSearch(this.world, curr, currPoint);
                     a.aStar(this.haveAxe, this.haveKey, this.haveRaft);
                     if(a.reachable()) {
                        return currPoint;
                     }
                  }
               }
            }
         }
      }
      return null;
   }   
   
   //Returns whether the front tile is a wall
   public boolean frontTileIsWall(Point curr) {
	   char frontTile = world.get(frontTile(curr));
	   if(frontTile == WALL){
		   return true;
	   }
	   return false;
   }
   
   private boolean canSeeUnknowns(Point curr) {
      boolean canSee = false;
      for(int i = -2; i <= 2; i++) {
         for(int j = -2; j <= 2; j++) {
            if(world.get(new Point((int)(curr.getX()+i), (int)(curr.getY()+j))) == UNEXPLORED) {
               canSee = true;
               break;
            }
         }
         if(canSee == true) {
            break;
         }
      }
      
      return canSee;
   }
   
   public void showMap() {
      System.out.println(xLoc);
      System.out.println(yLoc);
      for(int y = 12; y >= -12; y--) {
         for(int x = -12; x <= 12; x++) {
            char tile = world.get(new Point(x,y));
            System.out.print(tile);
         }
         System.out.println();
      }
   }
}
