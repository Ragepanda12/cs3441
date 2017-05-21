import java.awt.Point;
import java.util.*;
public class ReachabilityTester {
   
   private Point start;
   private Point goal;
   private Map<Point, Character> world;
   public ReachabilityTester(Map<Point, Character> world, Point currPos, Point goal) {
      this.start = start;
      this.goal = goal;
      this.world = world;
   }
   //do a bfs, if the queue empties then no path is seen
   public boolean reachable(boolean haveAxe, boolean haveKey, boolean haveRaft) {
      boolean reachable = false;
      Queue<Point> queue = new LinkedList<Point> ();
      
      while(queue.size() != 0) {
         Point currTile = queue.poll();
         if((currTile.getX() == goal.getX()) && (currTile.getY() == goal.getY())) {
            reachable = true;
            break;
         }
         int x = (int)currTile.getX();
         int y = (int)currTile.getY();
         for (int i = 0; i < 4; i++) {
            switch(i) {
            case Model.UP:
               y += 1;
               break;
            case Model.RIGHT:
               x += 1;
               break;
            case Model.DOWN:
               y -= 1;
               break;
            case Model.LEFT:
               x -= 1;
               break;
            }
            Point nextPoint = new Point(x,y);
            if (Model.canPotentiallyMoveOntoTile(world.get(nextPoint), haveAxe, haveKey, haveRaft )) {
               queue.add(nextPoint);               
            }
         }
      }
      return reachable;
   }
}
