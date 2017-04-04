package hw3.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState{
    private final int[][] board;
    private final int[][] goal;

    public Board(int[][] tiles) {
        board = new int[tiles.length][tiles.length];
        goal = new int[tiles.length][tiles.length];

        for (int i=0; i<size(); i++) {
            for (int j=0; j<size(); j++) {
                board[i][j] = tiles[i][j];
                if (i==size()-1 && j==size()-1) {
                    goal[i][j] = 0;
                }else {
                    goal[i][j] = i*size()+j+1;
                }
            }
        }
    }

    public int tileAt(int i, int j) {
        if ( i<0 || i>(size()-1) || j<0 || j>(size()-1)) {
            throw new IndexOutOfBoundsException();
        }
        return board[i][j];
    }

    public int size() {
        return board.length;
    }


    public int hamming() {
        int v=0;
        for (int i=0; i<size(); i++) {
            for (int j=0; j<size(); j++) {
                if(tileAt(i,j)!=goal[i][j] && tileAt(i,j)!=0) {
                    v = v+1;
                }
            }
        }
        return v;
    }

    public int manhattan() {
        int v=0;
        for (int i=0; i<size(); i++) {
            for (int j=0; j<size(); j++) {
                int tile = tileAt(i,j);
                if(tile!=goal[i][j]) {
                    int y=0;
                    int x=0;
                    if (tile!=0) {
                        y = Math.abs((tile - 1) % size() - j);
                        x = Math.abs((tile - 1) / size() - i);
                    }
                    v = v+x+y;
                }
            }
        }
        return v;
    }

    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (y == null || getClass() != y.getClass()) {
            return false;
        }

        Board board1 = (Board) y;

        if (board1.size()!=size()) {
            return false;
        }

        for (int i=0; i<size(); i++) {
            for (int j=0; j<size(); j++) {
                if (board1.board[i][j]!=this.board[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }


    /*
    method neighbors is implemented by using http://joshh.ug/neighbors.html
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == 0) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = 0;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = 0;
                }
            }
        }
        return neighbors;
    }


    /** Returns the string representation of the board. 
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i,j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
