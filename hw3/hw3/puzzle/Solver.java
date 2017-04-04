package hw3.puzzle;
import edu.princeton.cs.algs4.*;

import java.util.ArrayList;

/**
 * Created by Tong Yin on 4/3/2017.
 */
public class Solver {
    private MinPQ<SearchNode> minpq; //priority queue
    private int steps;
    private SearchNode f;

    private class SearchNode implements Comparable<SearchNode>{
        private WorldState cs;
        private int numMoves;
        private SearchNode ps;
        private int DistanceToG;

        public SearchNode(WorldState cs, int numMoves, SearchNode ps) {
            this.cs = cs;
            this.numMoves = numMoves;
            this.ps = ps;
            DistanceToG = cs.estimatedDistanceToGoal();
        }

        @Override
        public int compareTo(SearchNode o) {
            int a = this.numMoves+this.DistanceToG;
            int b = o.numMoves+o.DistanceToG;
            return a-b;
        }

    }
    /*
    Constructor which solves the puzzle, computing
                 everything necessary for moves() and solution() to
                 not have to solve the problem again. Solves the
                 puzzle using the A* algorithm. Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        SearchNode n = new SearchNode(initial, 0, null);
        minpq = new MinPQ<>();
        minpq.insert(n);
        SearchNode x = minpq.delMin();

        steps = x.numMoves;

        while(!x.cs.isGoal()) {
            for (WorldState a : x.cs.neighbors()) {
                if (x.ps==null) {
                    minpq.insert(new SearchNode(a, x.numMoves + 1, x));
                }else {
                    if (!x.ps.cs.equals(a)) {
                        minpq.insert(new SearchNode(a, x.numMoves + 1, x));
                    }
                }
            }
            x = minpq.delMin();
            steps =x.numMoves;
        }

        f = x;
    }

    /*
    Returns the minimum number of moves to solve the puzzle starting
                 at the initial WorldState.
     */
    public int moves() {
        return steps;
    }

    /*
    Returns a sequence of WorldStates from the initial WorldState
                 to the solution.
     */
    public Iterable<WorldState> solution() {
        ArrayList<WorldState> l = new ArrayList<>();
        while(f!=null) {
            l.add(0,f.cs);
            f=f.ps;
        }
        return l;
    }
}
