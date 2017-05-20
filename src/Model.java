
public class Model {
   
   //Definitions for the state
   
   final static int UP = 0;
   final static int RIGHT = 1;
   final static int DOWN = 2;
   final static int LEFT = 3;
   
   
   
   private boolean haveKey;
   private boolean haveAxe;
   private boolean haveDynamite;
   private boolean haveTree;
   
   public Model() {
      haveKey = false;
      haveAxe = false;
      haveDynamite = false;
      haveTree = false;
   }
   
}
