package hw2;                       

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] sites;
    private int NumOfOpen;
    private int dimension;
    private WeightedQuickUnionUF wu;
    private WeightedQuickUnionUF au;

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        if (N <= 0 ) {
            throw new IllegalArgumentException("Percolation cannot be initialized when N<0");
        }
        dimension = N;
        sites = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sites[i][j] = false;
            }
        }
        NumOfOpen = 0;
        wu = new WeightedQuickUnionUF(N*N+2);
        au = new WeightedQuickUnionUF(N*N+1);
        for (int i=0; i < N; i++) {
            wu.union(i,N*N);
            wu.union((N-1)*N+i, N*N+1);
            au.union(i, N*N);
        }
    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 0 || row > dimension-1
                || col < 0 || col > dimension-1) {
            throw new IndexOutOfBoundsException("Cannot open site");
        }
        if (!isOpen(row, col)) {
            sites[row][col] = true;
            NumOfOpen = NumOfOpen + 1;
            ConnectOpen(row, col);

        }
    }

    //connect opened sites
    private void ConnectOpen(int row, int col) {
        int site = xyTo1D(row, col);
        //connect site below if it's open
        if (row < dimension-1) {
            if (isOpen(row+1, col)) {
                wu.union(site, xyTo1D(row+1, col));
                au.union(site, xyTo1D(row+1, col));
            }
        }
        //connect site above if it's open
        if (row > 0) {
            if (isOpen(row-1, col)) {
                wu.union(site, xyTo1D(row-1, col));
                au.union(site, xyTo1D(row-1, col));
            }
        }
        //connect site right if it's open
        if (col < dimension -1) {
            if (isOpen(row, col+1)) {
                wu.union(site, xyTo1D(row, col+1));
                au.union(site, xyTo1D(row, col+1));
            }
        }
        //connect site left if it's open
        if (col > 0) {
            if (isOpen(row, col-1)) {
                wu.union(site, xyTo1D(row, col-1));
                au.union(site, xyTo1D(row, col-1));
            }
        }

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 0 || row > dimension-1
                || col < 0 || col > dimension-1) {
            throw new IndexOutOfBoundsException("Cannot access site");
        }

        return sites[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) {
            return false;
        }

        int site = xyTo1D(row, col);

        if (au.connected(site, dimension*dimension)) {
            return true;
        }else {
            return false;
        }

    }

    // number of open sites
    public int numberOfOpenSites() {
        return NumOfOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        if (wu.connected(dimension*dimension, dimension*dimension+1)) {
            return true;
        }else {
            return false;
        }
    }

    //convert row col site into site number
    public int xyTo1D(int row, int col) {
        return row*dimension+col;
    }


    // unit testing (not required)
    public static void main(String[] args) {

    }
}                       
