package src.tabusearch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 用于读取算例
 * Class representing a parser for a JSS instance.
 * 
 * @author Thiebout Dewitte
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class Parser {

	/**
	 * Parse a given test instance into a problem.
	 */
	public static Problem parseInstance(String filename) {
		Problem p = null;
		try {
			// Init streams.
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// Get the number of jobs and machines. 
			String strLine = br.readLine().replaceAll("   ", " ");
			strLine = strLine.replaceAll("  ", " ");

			// Some test instances start with a # line including a description. 开头为#的为注释，忽略
			// This line should not be used.
			if (strLine.contains("#")) {
				strLine = br.readLine().replaceAll("   ", " ");
				strLine = strLine.replaceAll("  ", " ");
			}
			String[] aJobs = fixFirstStringEmpty(strLine.split(" "));

			int aantalJobs = Integer.parseInt(aJobs[0]); // 工件数量
			int aantalMachines = Integer.parseInt(aJobs[1]); // 工序数量
			int optimum = -1;
			try {
				optimum = Integer.parseInt(aJobs[2]);
			} catch (Exception e) {
			}

			// Init lists/sets.
			ArrayList<Operation> V = new ArrayList<Operation>();// 记录工序，按工序顺序

			LinkedList<Operation>[] A = new LinkedList[aantalJobs];// 记录工序，按工件顺序
			LinkedList<Operation>[] E = new LinkedList[aantalMachines];// 记录工序，按机器顺序

			Operation source = new Operation(0, null, null, 0);
			V.add(source);

			// Fill V, A, E with given data.
			int operationIndex = 1; // 工序序号
			for (int i = 0; i < aantalJobs; i++) {
				// 输入文件中，每一行代表一件工件的加工过程
				Job j = new Job(i); // i-th iteration contains i-th job 工件序号
				strLine = br.readLine().replaceAll("   ", " ");
				strLine = strLine.replaceAll("  ", " ");
				String[] strArray = fixFirstStringEmpty(strLine.split(" "));

				int k = 0;
				while (k <= strArray.length - 1) {
					int machine = Integer.parseInt(strArray[k]);
					Machine m = new Machine(machine);

					int duration = Integer.parseInt(strArray[k + 1]);

					Operation o = new Operation(duration, j, m, operationIndex);
					V.add(o);

					if (A[i] == null) {
						A[i] = new LinkedList<Operation>();
					}
					if (E[machine] == null) {
						E[machine] = new LinkedList<Operation>();
					}

					A[i].add(o);
					E[machine].add(o);

					k += 2;
					operationIndex++;
				}
			}
			in.close();

			// Add overal completed operation
			Operation sink = new Operation(0, null, null, operationIndex);
			V.add(sink);

			p = new Problem(V, A, E, optimum);

		} catch (Exception e) {
			System.err.println("Invalid input file error: " + e.getMessage());
		}
		return p;
	}

	/**
	 * Check if the first string of an array of strings is empty. If so, remove
	 * the empty first string and move the other strings one step forward.
	 */
	private static String[] fixFirstStringEmpty(String[] s) {
		if (!s[0].equals("") && !s[0].equals(" ")) {
			return s;
		}
		String[] newString = new String[s.length - 1];
		for (int i = 0; i < newString.length; i++) {
			newString[i] = s[i + 1];
		}
		return newString;
	}
}
