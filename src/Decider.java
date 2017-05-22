import java.awt.Point;
import java.util.*;

public class Decider {
   private Queue<Character> moveQueue;
   private Model model;
   
   public Decider() {
      this.moveQueue = new LinkedList<Character>();
      this.model = new Model();
   }
   
   public char make_decision( char view[][] ) {
      this.model.update(view);
      char move = 'r';
      while(moveQueue.isEmpty()) {
      //what do we do?  
         //Priority 1: Have Gold, go back to base position (0,0)
         //But we might not be able to be cause we don't have a raft anymore
         if(model.haveTreasure()) {
            if(pathTo(model.getLoc(), new Point(0,0))) {
               break;
            }
            else {
               //do something when we can't actually path back to the start
            }
         }
         //Priority 2: Can see gold, go to pick it up
         //I suppose theoretically if we need to use a raft to get there then there must be a tree there
         else if(this.model.treasureVisible()) {
            if(pathTo(model.getLoc(), model.getTreasureLoc())) {
               break;
            }
            else {
               //do something when we can't actually path to the gold like get more tools
            }
         }
         //Priority 3: Pick up any tools we can see
         else if(((!model.haveAxe()) && (!model.getAxeLocs().isEmpty()))) {
            
         }
         else if(((!model.haveKey()) && (!model.getKeyLocs().isEmpty()))) {
            
         }
         else if(((!model.haveRaft()) && (!model.getTreeLocs().isEmpty()))) {
            
         }
         else if(!model.getDynamiteLocs().isEmpty()) {
            
         }
         //Priority 4: Explore any unexplored locations
         //Priority 5: Blow up something with dynamite to open a new path
      }
      this.model.updateMove(move); 
      return move;
   }
   private boolean pathTo(Point from, Point to) {
      AStarSearch a = new AStarSearch(model.getWorld(), from, to);
      a.aStar(model.haveAxe(), model.haveKey(), model.haveRaft());
      boolean success = false;
      if(a.reachable()) {
         LinkedList<Point> path = a.reconstructPath();
         for(Point step : path) {
            this.moveQueue.add(model.getWorld().get(step));
         }
         success = true;
      }
      return success;
   }
}
