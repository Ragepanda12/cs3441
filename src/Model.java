
public class Model {
   
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
   
   public Model() {
      
      this.xLoc = 0;
      this.yLoc = 0;
      this.direction = UP;
      
      this.haveKey = false;
      this.haveAxe = false;
      this.haveDynamite = false;
      this.haveTree = false;
   }
   
}
