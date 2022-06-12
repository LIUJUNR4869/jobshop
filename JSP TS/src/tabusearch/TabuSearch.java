package src.tabusearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Class for the tabu search algorithm.
 * 
 * @author Thiebout Dewitte
 * @version 1.0
 */

public class TabuSearch {

	/************************************
	 * TABU SEARCH ALGORITHM
	 ************************************/

	/**
	 * Tabu search algorithm, which is used to find an optimal solution of a JSS
	 * problem.
	 * 
	 * @param p
	 *            The given JSS instance
	 * @return The optimal solution of the tabu search
	 * 
	 * @note algorithm TS in the paper
	 */
	public static Solution tabuSearch(Problem p) {
		// Get the initial solution and initialize variables.
		Solution s = getInitialSolution(p);

		float bestCost = s.getCost();
		Solution bestSol = s;
		TabuList t = new TabuList(p);

		// Try to improve the solution.
		// K is the number of the iteration (the number of moves already
		// executed) at the point where a move is gonna be executed.
		int numberOfIterationsOfNoImprovement = 0;
		int K = 0;
		while (checkStoppingRule(K, numberOfIterationsOfNoImprovement)
				&& K < getSafetyStop() && bestCost != p.getOptimalCost()) {
			Solution s_bar = s;
			float costS_bar = Integer.MAX_VALUE;
			Move appliedMove = null; // no move

			// Check all possible inversions.
			for (Move m : s.getPossibleInversionsN1()) {
				Neighbor1 n = new Neighbor1(m, s);
				float costNeighbor = n.getNewSolution().getCost();

				// Check if the neighbor improves the solution and is allowed
				// following to the tabu list.
				// Make an exception to tabu list (aspiration criterion) if the
				// cost of the neighbor
				// solution is lower than the solution found upon now.
				if (costNeighbor < costS_bar
						&& (costNeighbor < bestCost || t.isAllowed(m, K))) {
					s_bar = n.getNewSolution();
					costS_bar = costNeighbor;
					appliedMove = m;
				}
			}

			// Randomization. If all possible moves belong to tabu list and none
			// satisfies aspiration criterion, choose a random move from all
			// possible ones.
			if (appliedMove == null) {
				HashSet<Move> inversions = s.getPossibleInversionsN1();
				Move m = chooseRandomMoveFromSet(inversions);
				Neighbor1 n = new Neighbor1(m, s);
				s_bar = n.getNewSolution();
				appliedMove = m;
			}

			Phase phase = Phase.WORSEN;

			if (s_bar.getCost() < s.getCost()) {
				phase = Phase.IMPROVING;
			}

			// If best solution upon now has been improved,
			// update the best solution found so far.
			// Also, if there has been no improvement during the last \Delta
			// iterations, restart process (current solution = best solution).
			if ((s_bar.getCost() < bestCost)
					|| (numberOfIterationsOfNoImprovement == getDelta())) {
				bestSol = s_bar;
				bestCost = s_bar.getCost();
				numberOfIterationsOfNoImprovement = 0;
				phase = Phase.EUREKA;
			} else {
				numberOfIterationsOfNoImprovement++;
			}

			t.update(appliedMove, K, phase); // add applied move to tabu list

			s = s_bar;

			K++;
		}
		return bestSol;
	}

	/**
	 * 检验是否被禁忌
	 * @param k
	 * @param numberOfIterationsOfNoImprovement
	 * @return
	 */
	private static boolean checkStoppingRule(int k,int numberOfIterationsOfNoImprovement) {
		if (numberOfIterationsOfNoImprovement >= getDelta() && k > getMaxiter()) {
			return false;
		}
		return true;
	}

	/************************************
	 * UTILITY METHODS
	 ************************************/

	/**
	 * Choose a random move from a given HashSet.
	 * 
	 * @return
	 * @throws Exception
	 *             If
	 */
	private static Move chooseRandomMoveFromSet(HashSet<Move> inversions) {
		int size = inversions.size();
		int item = new Random().nextInt(size);
		int i = 0;
		for (Move m : inversions) {
			if (i == item)
				return m;
			i = i + 1;
		}
		// Should never happen in tabu search.
		try {
			throw new Exception("Random move could not be chosen.");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Get the initial solution of a given JSS problem, using a bidirectional
	 * algorithm.
	 * 
	 * This method makes the important assumption that each machine gets the
	 * same number of operations: this is true for all test instances from the
	 * OR-library. If this would not be satisfied, we must push the right
	 * schedule to the left such that the combined schedule makes a block.
	 * 通过一种双向算法构造初始解。
	 * @note algorithm bi-dir in the paper
	 */
	public static Solution getInitialSolution(Problem p) {
		// initialisation: sets of scheduled operations
		HashSet<Operation> l = new HashSet<Operation>();
		HashSet<Operation> r = new HashSet<Operation>();
		Solution initSol = new Solution(p);

		// add source and sink as scheduled operations 用操作0表示操作结束
		l.add(new Operation(0, null, null, 0));
		r.add(new Operation(0, null, null, p.getNumberOfOperations() - 1));

		// initialisation: sets of schedulable operations
		HashMap<Operation, Integer> s = new HashMap<Operation, Integer>();
		HashMap<Operation, Integer> t = new HashMap<Operation, Integer>();

		// add first and last operations of each job as schedulable operations
		for (LinkedList<Operation> list : p.getA()) {
			s.put(list.getFirst(), 0); // r_i = earliest starting time = 0
			t.put(list.getLast(), 0); // t_i = queue of operation = 0
		}

		// make schedule
		while (l.size() + r.size() < p.getNumberOfOperations()) {
			
			// left schedule
			Entry<Operation, Integer> pair = chooseOperationListSchedulePriorityRule(s);
			Operation o = pair.getKey();
			int min = pair.getValue();

			// put operation o on machine mu_o in the first position free from
			// the beginning
			initSol.scheduleOperationLeft(o);

			// o can't be scheduled anymore and is added to the left schedule
			s.remove(o);
			l.add(o);

			if (t.containsKey(o))
				t.remove(o);

			// if the successor of o has not been scheduled yet, make it
			// schedulable
			if (!r.contains(p.getSJOfOperation(o))) {
				if (p.getSJOfOperation(o) != null) {
					s.put(p.getSJOfOperation(o), min + o.getDuration());
				}
			}

			// update r_i for all schedulable operations
			for (Entry<Operation, Integer> e : s.entrySet()) {
				if (e.getKey().getMachine().equals(o.getMachine())) {
					Integer r_i = e.getValue();
					e.setValue(r_i + o.getDuration());
				}
			}

			// continue scheduling if not all operations have been scheduled
			if (l.size() + r.size() < p.getNumberOfOperations()) {
				
				// right schedule
				Entry<Operation, Integer> pair2 = chooseOperationListSchedulePriorityRule(t);
				Operation o2 = pair2.getKey();
				int min2 = pair2.getValue();

				// put operation o2 on machine mu_o2 in the first position free
				// from the end
				initSol.scheduleOperationRight(o2);

				// o2 can't be scheduled anymore and is added to the right
				// schedule
				t.remove(o2);
				r.add(o2);

				if (s.containsKey(o2))
					s.remove(o2);

				// add newly schedulable operations to t
				if (!l.contains(p.getPJOfOperation(o2))) {
					if (p.getPJOfOperation(o2) != null)
						t.put(p.getPJOfOperation(o2), min2 + o2.getDuration());
				}

				// update t_i for all schedulable operations
				for (Entry<Operation, Integer> e : t.entrySet()) {
					if (e.getKey().getMachine().equals(o2.getMachine())) {
						Integer t_i = e.getValue();
						e.setValue(t_i + o2.getDuration());
					}
				}
			}
		}
		return initSol;
	}

	/**
	 * Get the initial solution of a given JSS problem, using a schedule which
	 * is time increasing.
	 * 
	 * On average this method computes worse initial solutions, than
	 * getInitialSolution.
	 */
	public static Solution getInitialSolutionOnlyLeft(Problem p) {
		// initialisation: sets of scheduled operations
		HashSet<Operation> l = new HashSet<Operation>();
		HashSet<Operation> r = new HashSet<Operation>();
		Solution initSol = new Solution(p);

		// add source and sink as scheduled operations
		l.add(new Operation(0, null, null, 0));
		r.add(new Operation(0, null, null, p.getNumberOfOperations() - 1));

		// initialisation: sets of schedulable operations
		HashMap<Operation, Integer> s = new HashMap<Operation, Integer>();
		HashMap<Operation, Integer> t = new HashMap<Operation, Integer>();

		// add first and last operations of each job as schedulable operations
		for (LinkedList<Operation> list : p.getA()) {
			s.put(list.getFirst(), 0); // r_i = earliest starting time = 0
			t.put(list.getLast(), 0); // t_i = queue of operation = 0
		}

		while (l.size() + r.size() < p.getNumberOfOperations()) {

			// left schedule
			Entry<Operation, Integer> pair = chooseOperationListSchedulePriorityRule(s);
			Operation o = pair.getKey();
			int min = pair.getValue();

			// put operation o on machine mu_o in the first position free from
			// the beginning
			initSol.scheduleOperationLeft(o);

			// o can't be scheduled anymore and is added to the left schedule
			s.remove(o);
			l.add(o);

			if (t.containsKey(o))
				t.remove(o);

			// if the successor of o has not been scheduled yet, make it
			// schedulable
			if (!r.contains(p.getSJOfOperation(o))) {
				if (p.getSJOfOperation(o) != null)
					s.put(p.getSJOfOperation(o), min + o.getDuration());
			}

			// update r_i for all schedulable operations
			for (Entry<Operation, Integer> e : s.entrySet()) {
				if (e.getKey().getMachine().equals(o.getMachine())) {
					Integer r_i = e.getValue();
					e.setValue(r_i + o.getDuration());
				}
			}
		}
		return initSol;
	}

	/**
	 * Choose the operation with the earliest starting time (increasing time) or
	 * with the smallest queue (decreasing time).
	 */
	private static Entry<Operation, Integer> chooseOperationListSchedulePriorityRule(
			HashMap<Operation, Integer> t) {
		Entry<Operation, Integer> pair = null;
		int min2 = Integer.MAX_VALUE;
		for (Entry<Operation, Integer> e : t.entrySet()) {
			if (min2 > e.getValue()) { // choose operation with smallest waiting
										// time/queue
				pair = e;
				min2 = e.getValue();
			}
		}
		return pair;
	}

	/************************************
	 * CONSTANTS
	 ************************************/

	/**
	 * Variable referencing the number of operations considered in our semi
	 * greedy priority rule.
	 */
	private static final int C = 3;

	/**
	 * @return the c
	 */
	public static int getC() {
		return C;
	}

	/**
	 * Variable referencing the global maximum number of iterations.
	 */
	private static final int MAXITER = 1200;

	/**
	 * @return the maxiter
	 */
	public static int getMaxiter() {
		return MAXITER;
	}

	/**
	 * Variable referencing the restarting parameter delta.
	 */
	private static final int DELTA = 800;

	/**
	 * @return the delta
	 */
	public static int getDelta() {
		return DELTA;
	}

	/**
	 * Variable referencing the value used in the safe stopping criterium for
	 * tabu search.
	 */
	private static final int SAFETY_STOP = 5 * getMaxiter();

	/**
	 * @return the safetyStop
	 */
	public static int getSafetyStop() {
		return SAFETY_STOP;
	}

}
