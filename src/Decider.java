import java.awt.Point;
import java.util.*;

public class Decider {
   private Queue<Character> moveQueue;
   private Model model;
   private boolean needKey = false;
   private boolean needAxe = false;
   private boolean needRaft = false;
   private int numDynamitesNeeded;
   private int numDynamitesTo;
   private int numDynamitesFrom;

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
         //search ground extensively, get dynamite, axe, key
         //if gold found, get gold and return land
      //if can reach tree, search water extensively
      //if gold found, find route back to start
         //check for walls, doors and water
            //if walls, check numDynamite = walls
            //if doors, check haveKey
            //if water, check route from gold to tree;
      //Priority 1: Have Gold, go back to base position (0,0)
         //But we might not be able to be cause we don't have a raft anymore
         if(model.haveTreasure()) {
            System.out.println("Prio 1");
            if(createPathTo(model.getLoc(), new Point(0,0))) {
               break;
            }
         }

         //Priority 2: Can see gold, go to pick it up
         //I suppose theoretically if we need to use a raft to get there then there must be a tree there
         if(this.model.treasureVisible()) {
            System.out.println("Curr = " + model.getLoc());
            checkPathTo(model.getLoc(), model.getTreasureLoc());
            numDynamitesTo = numDynamitesNeeded;
            checkPathTo(model.getTreasureLoc(), new Point(0,0));
            numDynamitesFrom = numDynamitesNeeded;
            System.out.println("needKey = " + needKey);
            System.out.println("needAxe = " + needAxe);
            System.out.println("needRaft = " + needRaft);
            System.out.println("numDynaTo = " + numDynamitesTo + "numDynaFrom = " + numDynamitesFrom);
            if(!needKey && !needAxe && !needRaft && model.numDynamites() >= numDynamitesNeeded){
               if(createPathTo(model.getLoc(), model.getTreasureLoc())) {
                  break;
               }
            } else if(needKey) {
               if(((!model.haveKey()) && (!model.getKeyLocs().isEmpty() )
                     && !model.getKeySeenLocs().isEmpty())) {
                  if(createPathTo(model.getLoc(), model.getKeySeenLocs().peek())) {
                     model.getKeySeenLocs().poll();
                     break;
                  }
               }
            } else if(needAxe) {
               if(((!model.haveAxe()) && (!model.getAxeLocs().isEmpty() )
                     && !model.getAxeSeenLocs().isEmpty())) {
                  if(createPathTo(model.getLoc(), model.getAxeSeenLocs().peek())) {
                     model.getAxeSeenLocs().poll();
                     break;
                  }
               }
            } else if(needRaft) {
               if(((model.haveAxe()) && (!model.getTreeLocs().isEmpty() )
                     && !model.getTreeSeenLocs().isEmpty())) {
                  if(createPathTo(model.getLoc(), model.getTreeSeenLocs().peek())) {
                     model.getTreeSeenLocs().poll();
                     break;
                  }
               }
            } else if(model.numDynamites() <= numDynamitesNeeded) {
               if((!model.getDynamiteLocs().isEmpty())&& !model.getDynamiteSeenLocs().isEmpty()) {
                  if(createPathTo(model.getLoc(), model.getDynamiteSeenLocs().peek())) {
                     model.getDynamiteSeenLocs().poll();
                     break;
                  }
               }
            }
            /*if(createPathTo(model.getLoc(), model.getTreasureLoc())) {
               break;
            }*/
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
            System.out.println("Have Raft");
            System.out.println("Prio 10");
            toExplore = model.nearestReachableRevealingWaterTile(model.getLoc());
            if(toExplore != null) {
               if(createPathTo(model.getLoc(), toExplore)) {
                  break;
               }
            }
         }

         //This one should probably be lower priority because we might have to cut a tree to move forward into an area
         if(((!model.haveRaft()) && (!model.getTreeLocs().isEmpty())
               && (model.getCurrentTerrain() != Model.WATER))) {
            if(createPathTo(model.getLoc(), model.getTreeLocs().peek())) {
               System.out.println("Found Tree");
               model.getTreeLocs().poll();
               moveQueue.add(Model.CHOP_TREE);
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

         //Priority 5: Blow up something with dynamite to open    a new path

         //Holds logic for using dynamite if the blown wall would lead to important item
         if(model.numDynamites() > 0 && model.frontTileIsWall(model.getLoc())) {
            Point frontTile = model.frontTile(model.getLoc());
            if(model.treasureVisible()) {
               if(createPathTo(frontTile, model.getTreasureLoc())){
                  moveQueue.add(Model.USE_DYNAMITE);
                  break;
               }
            }
         if(!model.getDynamiteLocs().isEmpty()) {
            if(createPathTo(frontTile, model.getDynamiteLocs().peek())){
               model.getDynamiteLocs().poll();
               moveQueue.add(Model.USE_DYNAMITE);
               break;
            }
         } else if(!model.getAxeLocs().isEmpty()) {
            if(createPathTo(frontTile, model.getAxeLocs().peek())){
               model.getAxeLocs().poll();
               moveQueue.add(Model.USE_DYNAMITE);
               break;
            }
         } else if(!model.getKeyLocs().isEmpty()) {
            if (createPathTo(frontTile, model.getKeyLocs().peek())){
               model.getKeyLocs().poll();
               moveQueue.add(Model.USE_DYNAMITE);
               break;
            }
         }
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
            else if(model.getWorld().get(path.peek()) == Model.TREE
                  && model.getWorld().get(curr) != Model.WATER) {
               this.moveQueue.add(Model.CHOP_TREE);
            }
            else if(model.getWorld().get(path.peek()) == Model.WALL
                  && model.numDynamites() > 0) {
               this.moveQueue.add(Model.USE_DYNAMITE);
            }
            this.moveQueue.add(Model.MOVE_FORWARD);
         }
         success = true;
      }
      return success;
   }
   
   private void checkPathTo(Point from, Point to) {
      AStarSearch a = new AStarSearch(model.getWorld(), from, to);
      a.aStar(true, true, true);   //haveAxe, haveKey, haveRaft all set to true
      needKey = false;
      needAxe = false;
      needRaft = false;
      numDynamitesNeeded = 0;
      if(a.reachable()) {
         LinkedList<Point> pathCheck = a.reconstructPath();
         pathCheck.addFirst(from);
         int currDirection = model.getDirection();
         while(pathCheck.size() > 1) {
            System.out.println("Route = " + pathCheck);
            Point curr = pathCheck.poll();
            int nextDirection = whatDirection(curr, pathCheck.peek());
            currDirection = nextDirection;
            if(model.getWorld().get(pathCheck.peek()) == Model.WALL) {
               numDynamitesNeeded++;
            }
            else if(model.getWorld().get(pathCheck.peek()) == Model.DOOR){
               if(!model.haveKey()){
                  needKey = true;
               }
            }
            else if(model.getWorld().get(pathCheck.peek()) == Model.TREE) {
               if(!model.haveAxe()){
                  needAxe = true;
               }
            }
            else if(model.getWorld().get(pathCheck.peek()) == Model.WATER) {
               if(!model.haveRaft() || model.getWorld().get(curr) == Model.WATER){
                  needRaft = true;
               }
            }
         }
      }
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
