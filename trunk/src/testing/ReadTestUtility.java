package testing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import core.Element;
import core.FocalElement;
import core.FrameOfDiscernment;
import core.Hypothesis;
import core.JointMassDistribution;
import core.MassDistribution;

public class ReadTestUtility {

	public static void readInput(String filename,
			ArrayList<MassDistribution> masses) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			while (!readLine.startsWith("$Input")) {
				readLine = br.readLine();
			}
			StringTokenizer inputTokenizer = new StringTokenizer(readLine);
			inputTokenizer.nextToken("-");

			int n = Integer.parseInt(inputTokenizer.nextToken("-"));

			for (int i = 0; i < n; i++) {
				readLine = br.readLine();
				masses.add(parseMassDistribution(readLine));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static MassDistribution parseMassDistribution(String readLine) {
		readLine = readLine.replaceAll("\\{", "");
		readLine = readLine.replaceAll("\\}", "");

		MassDistribution results = null;

		ArrayList<FocalElement> elementList = new ArrayList<FocalElement>();

		StringTokenizer elementTokenizer = new StringTokenizer(readLine);

		// readLine=A,B-0.5;C=0.4
		while (elementTokenizer.hasMoreTokens()) {
			String elementString = elementTokenizer.nextToken(";");

			// elementString=A,B-0.5
			FocalElement el = parseElement(elementString);

			elementList.add(el);
		}
		if (elementList.size() > 0)
			results = new MassDistribution(elementList);

		return results;
	}

	private static FocalElement parseElement(String elementString) {
		// elementString=A,B-0.5
		ArrayList<Hypothesis> hypothesiesList = new ArrayList<Hypothesis>();
		StringTokenizer elementTokenizer = new StringTokenizer(elementString);
		String hypothesiesString = elementTokenizer.nextToken("-");
		hypothesiesList = parseHypothesies(hypothesiesString);
		Double bpa = Double.parseDouble(elementTokenizer.nextToken("-"));

		FocalElement el = new FocalElement(new Element(hypothesiesList), bpa);
		return el;
	}

	private static ArrayList<Hypothesis> parseHypothesies(
			String hypothesiesString) {
		// hypothesiesString=A,B
		ArrayList<Hypothesis> hypothesiesList = new ArrayList<Hypothesis>();
		StringTokenizer hypTokenizer = new StringTokenizer(hypothesiesString);
		while (hypTokenizer.hasMoreTokens()) {

			Hypothesis hypothesis = new Hypothesis(hypTokenizer.nextToken(","));

			hypothesiesList.add(hypothesis);
		}
		return hypothesiesList;
	}

	public static JointMassDistribution readDempsterResult(String filename) {
		JointMassDistribution dempsterResult = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			while (!readLine.startsWith("$Output DEMPSTER")) {
				readLine = br.readLine();
			}

			readLine = br.readLine();
			dempsterResult = new JointMassDistribution(parseMassDistribution(
					readLine).getFocalElements());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dempsterResult;
	}

	public static JointMassDistribution readYagerResult(String filename) {
		JointMassDistribution yagerResult = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			while (!readLine.startsWith("$Output YAGER")) {
				readLine = br.readLine();
			}

			readLine = br.readLine();
			yagerResult = new JointMassDistribution(parseMassDistribution(
					readLine).getFocalElements());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return yagerResult;
	}

	public static JointMassDistribution readAverageResult(String filename) {
		JointMassDistribution averageResult = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			while (!readLine.startsWith("$Output AVERAGE")) {
				readLine = br.readLine();
			}

			readLine = br.readLine();
			averageResult = new JointMassDistribution(parseMassDistribution(
					readLine).getFocalElements());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return averageResult;
	}

	public static JointMassDistribution readDistanceResult(String filename) {
		JointMassDistribution distanceResult = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			while (!readLine.startsWith("$Output DISTANCE")) {
				readLine = br.readLine();
			}

			readLine = br.readLine();
			distanceResult = new JointMassDistribution(parseMassDistribution(
					readLine).getFocalElements());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return distanceResult;
	}

	public static FrameOfDiscernment readFrameOfDiscernment(String filename) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String readLine = br.readLine();

			// Frame Od Discernment: {A;B;C}
			while (!readLine.startsWith("$Frame of Discernment")) {
				readLine = br.readLine();
			}

			readLine = br.readLine();
			readLine = readLine.replaceAll("\\{", "");
			ArrayList<Hypothesis> allHypothesies = parseHypothesies(readLine);
			return new FrameOfDiscernment(allHypothesies);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
