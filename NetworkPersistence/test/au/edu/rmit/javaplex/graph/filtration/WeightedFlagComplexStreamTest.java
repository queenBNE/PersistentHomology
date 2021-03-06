package au.edu.rmit.javaplex.graph.filtration;


import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;

import au.edu.rmit.javaplex.graph.GraphPersistenceAPI;
import au.edu.rmit.javaplex.homology.filtration.DecreasingMapConverter;
import au.edu.rmit.javaplex.plex4.streams.impl.WeightedFlagComplexStream;
import static org.junit.Assert.assertEquals;
import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.graph.UndirectedWeightedListGraph;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.homology.filtration.FiltrationConverter;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;

public class WeightedFlagComplexStreamTest {

	// Edge list of a graph
	double[][] edgelist = new double[][]{{0,1,2.1},
										 {0,3,2.1},
										 {0,4,  1},
										 {1,2,2.1},
										 {1,3,  1},
										 {2,4,0.7},
										 {2,5,  1},
										 {4,5,  1}};

	
	// Homology dimension
	int d = 2;
	double maxWeight = 5d;
	int n = 6;
	UndirectedWeightedListGraph graph;
	
	@Before
	public void buildGraph(){
		graph = new UndirectedWeightedListGraph(n);
		for(double[] edge : edgelist)
			graph.addEdge((int) edge[0], (int) edge[1], edge[2]);
		
	}
	
	
	@Test 
	/**
	 * Compute the persistent homology of the Vietoris-Rips (clique complex) associated with 
	 * a graph filtration on increasing edge weight.
	 * 
	 */
	public void exampleExistingCodeIncreasing(){
		WeightedFlagComplexStream stream = new WeightedFlagComplexStream(graph, 2);
		stream.finalizeStream();
		final AbstractPersistenceAlgorithm<Simplex> algorithm = Plex4.getDefaultSimplicialAlgorithm(d);
		final BarcodeCollection<Double> intervals = algorithm.computeIntervals(stream);
		System.out.println("JavaPlex: Increasing");
		System.out.println(intervals);
		
		// Check if we obtain the correct answer 
		List<Interval<Double>> intervalsZero = intervals.getIntervalsAtDimension(0);
		int smallestIntervalCount = 0;
		int mediumIntervalCount = 0;
		int largeIntervalCount = 0;
		int infIntervalCount = 0;
		for(Interval<Double> interval : intervalsZero){
			if(interval.isRightInfinite())
				infIntervalCount +=1;
			else if(interval.getEnd() == 0.7)
				smallestIntervalCount += 1;
			else if(interval.getEnd() == 1)
				mediumIntervalCount += 1;
			else if(interval.getEnd() == 2.1)
				largeIntervalCount += 1;
		}
		assertEquals(1, smallestIntervalCount);
		assertEquals(3, mediumIntervalCount);
		assertEquals(1, largeIntervalCount);
		assertEquals(1, infIntervalCount);
	}


	@Test 
	/**
	 * 
	 * Compute the persistent homology of the Vietoris-Rips (clique complex) associated with 
	 * a graph filtration on decreasing edge weight.
	 * 
	 */
	public void exampleExistingCodeDecreasing(){
		Set<Double> weights = graph.getWeights();
		weights.add(maxWeight);
		FiltrationConverter filtrationConverter = new DecreasingMapConverter(weights);
		WeightedFlagComplexStream stream = new WeightedFlagComplexStream(graph, d, filtrationConverter);
		stream.finalizeStream();
		final AbstractPersistenceAlgorithm<Simplex> algorithm = Plex4.getDefaultSimplicialAlgorithm(d);
		BarcodeCollection<Double> intervals = algorithm.computeIntervals(stream);
		//intervals = WeightedGraphFiltrationFunctions.convertToDescendingIntervals(intervals);
		System.out.println("JavaPlex: Decreasing");
		System.out.println(intervals);
		
		// Check if correct 
		List<Interval<Double>> intervalsZero = intervals.getIntervalsAtDimension(0);
		int smallestIntervalCount = 0;
		int mediumIntervalCount = 0;
		int largeIntervalCount = 0;
		int infIntervalCount = 0;
		for(Interval<Double> interval : intervalsZero){
			if(interval.getStart() == 0)
				infIntervalCount +=1;
			else if(interval.getStart() == 2.1)
				smallestIntervalCount += 1;
			else if(interval.getStart() == 1)
				mediumIntervalCount += 1;
			else if(interval.getStart() == 0.7)
				largeIntervalCount += 1;
		}
		assertEquals(3, smallestIntervalCount);
		assertEquals(2, mediumIntervalCount);
		assertEquals(0, largeIntervalCount);
		assertEquals(1, infIntervalCount);
	
		List<Interval<Double>> intervalsOne = intervals.getIntervalsAtDimension(1);
		assertEquals(1, intervalsOne.size());
		double start = intervalsOne.get(0).getStart();
		double end = intervalsOne.get(0).getEnd();
		assertEquals(0.7, start, 0);
		assertEquals(1, end, 0);
	}
	
	@Test 
	/**
	 * Compute the persistent homology of the Vietoris-Rips (clique complex) associated with 
	 * a graph filtration on increasing edge weight using the GraphPersistenceAPI
	 */
	public void exampleGraphPersistenceAPIIncreasing(){
		final BarcodeCollection<Double> intervals = GraphPersistenceAPI.getWeightedGraphPersistence(graph, 2, true);
		System.out.println("JavaPlex: Increasing");
		System.out.println(intervals);
		
		// Check if we obtain the correct answer 
		List<Interval<Double>> intervalsZero = intervals.getIntervalsAtDimension(0);
		int smallestIntervalCount = 0;
		int mediumIntervalCount = 0;
		int largeIntervalCount = 0;
		int infIntervalCount = 0;
		for(Interval<Double> interval : intervalsZero){
			if(interval.isRightInfinite())
				infIntervalCount +=1;
			else if(interval.getEnd() == 0.7)
				smallestIntervalCount += 1;
			else if(interval.getEnd() == 1)
				mediumIntervalCount += 1;
			else if(interval.getEnd() == 2.1)
				largeIntervalCount += 1;
		}
		assertEquals(1, smallestIntervalCount);
		assertEquals(3, mediumIntervalCount);
		assertEquals(1, largeIntervalCount);
		assertEquals(1, infIntervalCount);
	}


	@Test 
	/**
	 * 
	 * Compute the persistent homology of the Vietoris-Rips (clique complex) associated with 
	 * a graph filtration on decreasing edge weight using the GraphPersistenceAPI
	 * 
	 */
	public void exampleGraphPersistenceAPIDecreasing(){
		BarcodeCollection<Double> intervals = GraphPersistenceAPI.getWeightedGraphPersistence(graph, 2, false);
		//intervals = WeightedGraphFiltrationFunctions.convertToDescendingIntervals(intervals);
		System.out.println("JavaPlex: Decreasing");
		System.out.println(intervals);
		
		// Check if correct 
		List<Interval<Double>> intervalsZero = intervals.getIntervalsAtDimension(0);
		int smallestIntervalCount = 0;
		int mediumIntervalCount = 0;
		int largeIntervalCount = 0;
		int infIntervalCount = 0;
		for(Interval<Double> interval : intervalsZero){
			if(interval.getStart() == 0)
				infIntervalCount +=1;
			else if(interval.getStart() == 2.1)
				smallestIntervalCount += 1;
			else if(interval.getStart() == 1)
				mediumIntervalCount += 1;
			else if(interval.getStart() == 0.7)
				largeIntervalCount += 1;
		}
		assertEquals(3, smallestIntervalCount);
		assertEquals(2, mediumIntervalCount);
		assertEquals(0, largeIntervalCount);
		assertEquals(1, infIntervalCount);
	
		List<Interval<Double>> intervalsOne = intervals.getIntervalsAtDimension(1);
		assertEquals(1, intervalsOne.size());
		double start = intervalsOne.get(0).getStart();
		double end = intervalsOne.get(0).getEnd();
		assertEquals(0.7, start, 0);
		assertEquals(1, end, 0);
	}
}
