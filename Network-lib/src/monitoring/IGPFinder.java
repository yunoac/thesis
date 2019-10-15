package monitoring;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import utils.ArrayExt;
import utils.MyAssert;

/**
 * Class to compute IGP weights.
 * 
 * @author f.aubry@uclouvain.be
 */

public class IGPFinder {
	
	static Random rnd = new Random();

	public static WeightFunction logPrimeCompleteIGP(Graph g) {
		// get the prime numbers (starting from 31)
		int[] primes = getKPrimesFrom(3, g.E());
		// compute their logarithms
		double[] w = new double[primes.length];
		for(int i = 0; i < primes.length; i++) {
			w[i] = Math.log(primes[i]);
		}
		double diam = Math.ceil(Graphs.diameter(g, w));
		for(int i = 0; i < primes.length; i++) {
			w[i] += diam;
		}
		MyAssert.assertTrue(Graphs.isComplete(g, w));
		return new WeightFunction(w);
	}

	public static Pair<WeightFunction, Integer> randomIGP(Graph g, int M) {
		double[] w = randomIGPAddmax(g, M);
		int iter = 1;
		System.out.println("iter: " + iter);
		while(!Graphs.isECMPFreeComplete(g, w)) {	
			w = randomIGPAddmax(g, M);
			iter++;
			System.out.println("iter: " + iter);
		}
		WeightFunction igp = new WeightFunction();
		for(Edge e : g.getEdgesByIndex()) {
			igp.setWeight(e, w[e.getIndex()]);
		}
		return new Pair<WeightFunction, Integer>(igp, iter);
	}

	
	private static double[] randomIGPAddmax(Graph g, int maxVal) {
		double[] w = new double[g.E()];
		for(int i = 0; i < w.length; i++) {
			w[i] = rnd.nextInt(maxVal) + 1;
		}
		double max = ArrayExt.max(w);
		for(Edge e : g.getEdgesByIndex()) {
			w[e.getIndex()] += max;
		}
		return w;
	}


	/*
	 * Compute the IGP weights with the prime number based algorithm.
	 * 
	 * 1. Choose one prime pe number for each edge e.
	 * 2. Set the weight of e as floor(10^s pe) + diameter with increasing s
	 * 3. Stop increasing s when ECMP is broken or maxAllowedValue is reached
	 */
	public static Pair<WeightFunction, Integer> primeECMPFreeIGP(Graph g) {
		// get the prime numbers (starting from 31)
		int[] primes = getKPrimesFrom(2, g.E());
		// compute their logarithms
		double[] log = new double[primes.length];
		for(int i = 0; i < primes.length; i++) {
			log[i] = Math.log(primes[i]);
		}
		int s = 1;
		double pow = 1;
		// initialize the IGP
		double[] w;
		while(true) {
			w = new double[g.E()];
			int max = (int)(pow * log[primes.length - 1]);
			for(Edge e : g.getEdgesByIndex()) {
				w[e.getIndex()] = (int)(pow * log[e.getIndex()]) + max;
			}
			if(Graphs.isECMPFreeComplete(g, w)) {
				break;
			}
			pow *= 10;
			s++;
		}
		WeightFunction igp = new WeightFunction();
		for(Edge e : g.getEdgesByIndex()) {
			igp.setWeight(e, w[e.getIndex()]);
		}
		return new Pair<WeightFunction, Integer>(igp, s);
	}


	/*
	 * Get the first k primes larger than or equal to s.
	 */
	private static int[] getKPrimesFrom(int s, int k) {
		int[] P = new int[k];
		int p = k;
		for(int i = 0; i < k; i++) {
			while(!BigInteger.valueOf(p).isProbablePrime(80)) {
				p++;
			}
			P[i] = p++;
		}
		return P;
	}

}
