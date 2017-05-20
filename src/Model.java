import java.awt.Point;
import java.util.Map;


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
   
   private boolean haveKey;
   private boolean haveAxe;
   private boolean haveDynamite;
   private boolean haveTree;
   
   private Map<Point, Character> world;
   
   public Model() {
      
      this.xLoc = 0;
      this.yLoc = 0;
      this.direction = DOWN;
      
      this.haveKey = false;
      this.haveAxe = false;
      this.haveDynamite = false;
      this.haveTree = false;
      
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
   
   public void update(char view[][]) {
      //We need to rotate the view we're given so that it's the same orientation as our original map.
      int rotationsRequired = 0;
      
      switch(this.direction) {
         //We start facing down (or at least that's the way it goes on the openlearning example and given test)
         case DOWN:
            break;
         case LEFT:
            rotationsRequired = 1;
            break;
         case UP:
            rotationsRequired = 2;
            break;
         case RIGHT:
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
            int currY = yLoc + (2+i);
            
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
            world.put(tile, currTile);
         }
      }
   }
   
   private static char[][] rotateMap(char[][] map){
      int length = map.length;
      int height = map[0].length;
      char[][] rotatedMap = new char[height][length];
      for(int l = 0; l < length; l++) {
         for(int h = 0; h < height; h++) {
            rotatedMap[h][length - l - 1] = map[l][h];
         }
      }
      return rotatedMap;
   }
}
