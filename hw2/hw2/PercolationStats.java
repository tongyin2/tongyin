package hw2;                       
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.introcs.StdRandom;

public class PercolationStats {
    private Percolation[] pers;
    private double[] fracPerc;
    private double ti;

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N or T cannot be smaller than 0");
        }
        pers = new Percolation[T];
        fracPerc = new double[T];
        ti = (double) T;
        for (int i=0; i < T; i++) {
            pers[i] = new Percolation(N);

            while (!pers[i].percolates()) {
                int r, c;
                do {
                    r = StdRandom.uniform(N);
                    c = StdRandom.uniform(N);
                }while(pers[i].isOpen(r,c));
                pers[i].open(r,c);
            }
            fracPerc[i] = ((double)pers[i].numberOfOpenSites())/((double) (N*N));
        }

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(fracPerc);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(fracPerc);
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean()-1.96*stddev()/(Math.sqrt(ti));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean()+1.96*stddev()/(Math.sqrt(ti));
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(20, 20);
        System.out.println("Mean of fracPerc: "+ps.mean());
    }
}                       
