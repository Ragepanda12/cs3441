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
      return 'r';
   }
}
