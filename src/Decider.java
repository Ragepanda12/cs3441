import java.awt.Point;
import java.util.*;

public class Decider {
   private Queue<Character> moveQueue;
   private Model model;

   private boolean searchingWater;
   
   public Decider() {
      this.moveQueue = new LinkedList<Character>();
      this.model = new Model();
   }
   
   public char make_decision( char view[][] ) {
      this.model.update(view);
      char move = 'r';
      
      while(moveQueue.isEmpty()) {
      //what do we do?  
    	 /*
    	 //Priority 0: Win condition
         if(model.haveTreasure() && model.getLoc() == new Point(0,0)){
            System.out.println("Mission completed. Game Over");
            System.exit(0);
         }*/
    	  
    	 //Priority 1: Have Gold, go back to base position (0,0)
         //But we might not be able to be cause we don't have a raft anymore
         if(model.haveTreasure()) {
            System.out.println("Prio 1");
            if(createPathTo(model.getLoc(), new Point(0,0))) {
               break;
            }
         }
         //If we are on water and aren't going back to the start with the gold, we should exhaustively search water before
         //Trying to make a move to anything else to reveal all information
         //Since rafts are limited resource
         if(model.getCurrentTerrain() == Model.WATER) {
            System.out.println("Prio 3");
            Point toExplore = model.nearestReachableRevealingWaterTile(model.getLoc());
            System.out.println("Coming from: " + model.getLoc());
            System.out.println("Going to: " + toExplore);
            if(toExplore != null){
               if(createPathTo(model.getLoc(),toExplore)) {
                  break;
               }
            }            
         }
         //Priority 2: Can see gold, go to pick it up
         //I suppose theoretically if we need to use a raft to get there then there must be a tree there
         if(this.model.treasureVisible()) {
            System.out.println("Prio 2");
            if(createPathTo(model.getLoc(), model.getTreasureLoc())) {
               break;
            }
         }
         //Priority 2.5: Unlock doors
         if((model.haveKey()) && (!model.getDoorLocs().isEmpty())) {
            System.out.println("Prio 4");
            if(createPathTo(model.getLoc(), model.getDoorLocs().peek())) {
               model.getDoorLocs().poll();
               break;
            }
         }
         //Priority 3: Pick up any tools we can see
         if(((!model.haveAxe()) && (!model.getAxeLocs().isEmpty()))) {
            System.out.println("Prio 5");
            if(createPathTo(model.getLoc(), model.getAxeLocs().peek())) {
               model.getAxeLocs().poll();
               break;
            }
         }
         if(((!model.haveKey()) && (!model.getKeyLocs().isEmpty()))) {
            System.out.println("Prio 6");
            if(createPathTo(model.getLoc(), model.getKeyLocs().peek())) {
               model.getKeyLocs().poll();
               break;
            }
         }
         if(!model.getDynamiteLocs().isEmpty()) {
            System.out.println("Prio 7");
            if(createPathTo(model.getLoc(), model.getDynamiteLocs().peek())) {
               model.getDynamiteLocs().poll();
               break;
            }
         }
         //Priority 4: Explore any unexplored locations
         //Go to the nearest ?
         //If null is returned then there is no new info we can find
         Point toExplore = model.nearestReachableRevealingTile(model.getLoc());
         if(toExplore != null){
            System.out.println("Prio 8");
            if(createPathTo(model.getLoc(),toExplore)) {
               break;
            } 
         }
         
         //This one should probably be lower priority because we might have to cut a tree to move forward into an area
         if(((!model.haveRaft()) && (!model.getTreeLocs().isEmpty()))) {
            System.out.println("Prio 9");
            if(createPathTo(model.getLoc(), model.getTreeLocs().peek())) {
               System.out.println("Found Tree");
               model.getTreeLocs().poll();
               moveQueue.add(Model.CHOP_TREE);
               break;
            }
         }
         //Priority 4.5 go onto water
         if(model.haveRaft()) {
            System.out.println("Prio 10");
            toExplore = model.nearestReachableRevealingWaterTile(model.getLoc());
            if(toExplore != null) {
               if(createPathTo(model.getLoc(), toExplore)) {
                  break;
               }
            }
         }

         //Priority 5: Blow up something with dynamite to open    a new path
         if(((!model.haveAxe()) && (!model.getAxeLocs().isEmpty())
               && !model.getAxeSeenLocs().isEmpty())) {
            System.out.println("Prio 11");
            if(createPathTo(model.getLoc(), model.getAxeSeenLocs().peek())) {
               model.getAxeSeenLocs().poll();
               break;
            }
         }
         //Look for important items behind wall
         //Check wall if blowable
         //Blow up wall
         //============FIX LOGIC=====================//
         if(model.numDynamites() > 0 && model.frontTileIsWall(model.getLoc())) {
            System.out.println("Prio 12");
            System.out.println("Front is a wall");
            moveQueue.add(Model.USE_DYNAMITE);
            break;
         }
         
      }
      move = moveQueue.poll();
      this.model.updateMove(move);
      System.out.println(move);
      //model.showMap();
      return move;
   }

   
   private boolean createPathTo(Point from, Point to) {
      AStarSearch a = new AStarSearch(model.getWorld(), from, to);
      a.aStar(model.haveAxe(), model.haveKey(), model.haveRaft());
      boolean success = false;
      if(a.reachable()) {
         LinkedList<Point> path = a.reconstructPath();
         path.addFirst(from);
         int currDirection = model.getDirection();
         while(path.size() > 1) {
            Point curr = path.poll();
            int nextDirection = whatDirection(curr, path.peek());
            this.moveQueue.addAll(getTurnMoves(currDirection, nextDirection));
            currDirection = nextDirection;
            if(model.getWorld().get(path.peek()) == Model.DOOR){
               this.moveQueue.add(Model.UNLOCK_DOOR);
            }
            else if(model.getWorld().get(path.peek()) == Model.TREE) {
               this.moveQueue.add(Model.CHOP_TREE);
            }
            this.moveQueue.add(Model.MOVE_FORWARD);
         }
         success = true;
      }
      return success;
   }
   private LinkedList<Character> getTurnMoves(int currDirection, int nextDirection){
      LinkedList<Character> turns = new LinkedList<Character>();
      if(currDirection == nextDirection) {
         return turns;
      }
      int leftTurns = 0;
      int rightTurns = 0;
      //Up = 0, Right = 1, Down = 2, Left = 3.
      //Addition = clockwise rotation, Subtraction = anti-clockwise rotation

      if(nextDirection > currDirection) {
         //If we need to turn clockwise 3 times, just turn counter-clockwise once
         if(nextDirection - currDirection == 3) {
            leftTurns = 1;
         }
         //Otherwise just turn clockwise 1 or 2 as required
         else {
            rightTurns = nextDirection - currDirection;
         }
      }
      else {
         if(currDirection - nextDirection == 3) {
            rightTurns = 1;
         }
         else {
            leftTurns = currDirection - nextDirection;
         }
      }
      if(leftTurns == 0) {
         for(int i = 0; i < rightTurns; i++) {
            turns.add(Model.TURN_RIGHT);
         }
      }
      else {
         for(int i = 0; i < leftTurns; i++) {
            turns.add(Model.TURN_LEFT);
         }
      }
      return turns;
   }
   
   private int whatDirection(Point curr, Point next) {
      int x = (int) (next.getX() - curr.getX());
      int y = (int) (next.getY() - curr.getY());
      int direction = 0;
      if(x != 0) {
         if(x > 0) {
            direction = Model.RIGHT;
         }
         else {
            direction = Model.LEFT;
         }
      }
      else {
         if(y > 0) {
            direction = Model.UP;
         }
         else {
            direction = Model.DOWN;
         }
      }
      return direction;
   }
}
