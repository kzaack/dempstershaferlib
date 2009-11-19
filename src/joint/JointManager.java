package joint;

import java.util.ArrayList;

import core.Element;
import core.FocalElement;
import core.FrameOfDiscernment;
import core.JointMassDistribution;
import core.MassDistribution;
import exception.JointNotPossibleException;
import exception.MassDistributionNotValidException;

/**
 * THe class supplies a static method for each different joint operator.
 * 
 * @author Elisa Costante
 * 
 */
public class JointManager {

	/**
	 * Applies Dempster's operator to the list of {@link MassDistribution}.
	 * 
	 * @param masses
	 * @return the result of Dempster's operator to the {@link MassDistribution}
	 *         list
	 * @throws JointNotPossibleException
	 * @throws MassDistributionNotValidException
	 */
	public static JointMassDistribution dempsterJoint(
			ArrayList<MassDistribution> masses)
			throws JointNotPossibleException, MassDistributionNotValidException {
		if (masses.size() > 1)
			return applyOperator(masses, JointOperatorEnum.DEMPSTER.getValue());
		else
			throw new JointNotPossibleException(
					"It's not possible do a joint with just one MassDistribution");
	}

	/**
	 * Applies Yager's operator to the list of {@link MassDistribution}.
	 * 
	 * @param masses
	 * @return the result of Yager's operator to the {@link MassDistribution}
	 *         list
	 * @throws JointNotPossibleException
	 * @throws MassDistributionNotValidException
	 */
	public static JointMassDistribution yagerJoint(
			ArrayList<MassDistribution> masses)
			throws JointNotPossibleException, MassDistributionNotValidException {
		if (masses.size() > 1)
			return applyOperator(masses, JointOperatorEnum.YAGER.getValue());
		else
			throw new JointNotPossibleException(
					"It's not possible do a joint with just one MassDistribution");
	}

	/**
	 * Applies the Average operator to the list of {@link MassDistribution}.
	 * 
	 * @param masses
	 * @return the result of Average operator to the {@link MassDistribution}
	 *         list
	 * @throws JointNotPossibleException
	 * @throws MassDistributionNotValidException
	 */
	public static JointMassDistribution averageJoint(
			ArrayList<MassDistribution> masses)
			throws JointNotPossibleException, MassDistributionNotValidException {
		if (masses.size() > 1)

			return applyOperator(masses, JointOperatorEnum.AVERAGE.getValue());
		else
			throw new JointNotPossibleException(
					"It's not possible do a joint with just one MassDistribution");

	}

	/**
	 * Applies the Distance Evidence operator to the list of
	 * {@link MassDistribution}.
	 * 
	 * @param masses
	 * @return the result of Distance Evidence operator to the
	 *         {@link MassDistribution} list
	 * @throws JointNotPossibleException
	 * @throws MassDistributionNotValidException
	 */
	public static JointMassDistribution distanceEvidenceJoint(
			ArrayList<MassDistribution> masses)
			throws JointNotPossibleException, MassDistributionNotValidException {
		if (masses.size() > 1)

			return applyOperator(masses, JointOperatorEnum.DISTANCE_EVIDENCE
					.getValue());

		else
			throw new JointNotPossibleException(
					"It's not possible do a joint with just one MassDistribution");

	}

	private static JointMassDistribution applyOperator(
			ArrayList<MassDistribution> masses, int operator)
			throws MassDistributionNotValidException {

		JointMassDistribution jointDistribution = null;
		int i = 0;

		MassDistribution m1 = masses.get(i);
		i++;
		MassDistribution m2 = masses.get(i);
		i++;

		switch (operator) {
		case 1:
			jointDistribution = average(m1, m2, masses);
			jointDistribution.setOperator(JointOperatorEnum.AVERAGE.getName());
			break;
		case 2:
			jointDistribution = dempster(m1, m2);
			for (int j = i; j < masses.size(); j++) {
				jointDistribution = dempster(jointDistribution, masses.get(j));
			}
			jointDistribution.setOperator(JointOperatorEnum.DEMPSTER.getName());
			break;
		case 3:
			Double conflict = getConflict(m1.getFocalElements(), m2
					.getFocalElements());
			jointDistribution = yager(m1, m2, false, conflict);
			for (int j = i; j < masses.size(); j++) {
				conflict = conflict
						+ getConflict(jointDistribution.getFocalElements(),
								masses.get(j).getFocalElements());
				if (j == (masses.size() - 1))
					jointDistribution = yager(jointDistribution, masses.get(j),
							true, conflict);
				else
					jointDistribution = yager(jointDistribution, masses.get(j),
							false, conflict);
			}
			jointDistribution.setOperator(JointOperatorEnum.YAGER.getName());
			break;
		case 4:
			jointDistribution = distance(masses);

			JointMassDistribution dempsterDistribution = dempster(
					jointDistribution, jointDistribution);

			for (int j = 0; j < masses.size() - 2; j++) {
				jointDistribution = dempster(dempsterDistribution,
						jointDistribution);
			}
			jointDistribution.setOperator(JointOperatorEnum.DISTANCE_EVIDENCE
					.getName());
			break;

		default:
			break;
		}

		if (jointDistribution.isValid())
			return jointDistribution;
		else
			throw new MassDistributionNotValidException("MassDistribution"
					+ jointDistribution.toString() + " is not valid!");
	}

	/**
	 * Applies the Chen-Shy distance evidence aggregation to the mass
	 * distributions.</br>
	 * 
	 * The algorithm of computation is the following: </br> 1. Computation of
	 * SImilarity Matrix <br>
	 * 2. Computation of the Support degree of each piece of evidence mi<br>
	 * 3. Computation of the Credibility degree of each piece of evidence mi <br>
	 * 4. Computation of the modified average mass <br>
	 * 5. If there are N different sources THe DempsterRule will be apllied N-1
	 * times
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 * @throws MassDistributionNotValidException
	 */
	private static JointMassDistribution distance(
			ArrayList<MassDistribution> masses)
			throws MassDistributionNotValidException {
		JointMassDistribution jointMassDistribution = null;

		if (masses.size() > 1) {
			double[][] similarityMatrix = getSimilarityMatrix(masses);

			double[] supportDegree = getSupportDegree(similarityMatrix);

			double[] credibility = getCredibility(supportDegree);

			ArrayList<FocalElement> jointElements = FocalElement
					.getMassUnionElement(masses);

			for (int i = 0; i < jointElements.size(); i++) {

				FocalElement jointElement = jointElements.get(i);

				double newBpa = 0;
				for (int j = 0; j < masses.size(); j++) {
					MassDistribution m = masses.get(j);
					double cred = credibility[j];
					double oldBpa = 0;

					FocalElement focalElement = FocalElement.findElement(m
							.getFocalElements(), jointElement.getElement());

					if (focalElement != null)
						oldBpa = focalElement.getBpa();
					newBpa = newBpa + (cred * oldBpa);
				}
				jointElement.setBpa(newBpa);

			}

			jointMassDistribution = new JointMassDistribution(jointElements);
		}
		if (jointMassDistribution.isValid())
			return jointMassDistribution;
		else
			throw new MassDistributionNotValidException("MassDistribution"
					+ jointMassDistribution.toString() + " is not valid!");
	}

	/**
	 * Computes the scalar product between two mass distributions
	 * 
	 * @param m1
	 *            : {@link MassDistribution}
	 * @param m2
	 *            : {@link MassDistribution}
	 * @return the scalarProduct.
	 */
	private static double getScalarProduct(MassDistribution m1,
			MassDistribution m2) {

		double scalarProduct = 0;

		ArrayList<FocalElement> m1Elements = m1.getFocalElements();
		ArrayList<FocalElement> m2Elements = m2.getFocalElements();

		for (int i = 0; i < m1Elements.size(); i++) {
			FocalElement el1 = m1Elements.get(i);

			for (int j = 0; j < m2Elements.size(); j++) {

				FocalElement el2 = m2Elements.get(j);
				Element intersection = Element.getIntersection(
						el1.getElement(), el2.getElement());
				Element union = Element.getUnion(el1.getElement(), el2
						.getElement());

				int unionSize = 0;
				int intersectionSize = 0;

				if (intersection != null)
					intersectionSize = intersection.size();
				if (union != null)
					unionSize = union.size();

				// scalarProduct= Summation [el1*el2] . |intersect(el1,el2)| /
				// |union(el1,el2)|
				scalarProduct = scalarProduct
						+ ((el1.getBpa() * el2.getBpa()) * (intersectionSize / unionSize));
			}
		}

		return scalarProduct;

	}

	/**
	 * Compute the distance between two mass distributions.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	private static double getDistance(MassDistribution m1, MassDistribution m2) {
		double normM1 = getScalarProduct(m1, m1); // ||m1||^2
		double normM2 = getScalarProduct(m2, m2);// ||m2||^2
		double scalarProduct = getScalarProduct(m1, m2);// <m1,m2>
		double distance = Math
				.sqrt((normM1 + normM2 - 2 * (scalarProduct)) / 2);
		return distance;

	}

	/**
	 * Computes the similarity between two mass distributions.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	private static double getSimilarity(MassDistribution m1, MassDistribution m2) {

		double distance = getDistance(m1, m2);
		double sim = (Math.cos(distance * Math.PI) + 1) / 2;
		return sim;

	}

	private static double[] getSupportDegree(double[][] similarityMatrix) {
		double[] supportDegree = new double[similarityMatrix.length];

		for (int i = 0; i < similarityMatrix.length; i++) {
			supportDegree[i] = 0;
			for (int j = 0; j < similarityMatrix.length; j++) {
				if (i != j)
					supportDegree[i] = supportDegree[i]
							+ similarityMatrix[i][j];
			}
		}

		return supportDegree;
	}

	private static double[] getCredibility(double[] supportDegree) {
		double[] credibility = new double[supportDegree.length];
		double summation = 0;

		for (int i = 0; i < supportDegree.length; i++) {

			summation = summation + supportDegree[i];

		}

		for (int i = 0; i < supportDegree.length; i++) {

			// credibility(m1)= Sup(mi) / Summation(Sup(mi)) for each mass i
			credibility[i] = supportDegree[i] / summation;

		}

		return credibility;
	}

	/**
	 * Compute the Similarity MAtrix of mass distributions.
	 * 
	 * @param masses
	 * @return the similarity MAtrix or null if there are no mass.
	 */
	private static double[][] getSimilarityMatrix(
			ArrayList<MassDistribution> masses) {
		if (masses != null && masses.size() > 0) {
			int n = masses.size();
			double[][] similarityMatrix = new double[n][n];

			for (int i = 0; i < similarityMatrix.length; i++) {
				for (int j = i; j < similarityMatrix.length; j++) {
					if (i == j) {
						similarityMatrix[i][j] = 1;
					} else {
						similarityMatrix[i][j] = similarityMatrix[j][i] = getSimilarity(
								masses.get(i), masses.get(j));
					}
				}
			}
			return similarityMatrix;
		}
		return null;
	}

	private static JointMassDistribution yager(MassDistribution m1,
			MassDistribution m2, boolean last, Double conflictTransfer)
			throws MassDistributionNotValidException {
		ArrayList<FocalElement> m1Elements = m1.getFocalElements();
		ArrayList<FocalElement> m2Elements = m2.getFocalElements();

		// double conflict = getConflict(m1Elements, m2Elements);
		// conflictTransfer = new Double(conflictTransfer.doubleValue() +
		// conflict);

		ArrayList<FocalElement> jointElements = FocalElement
				.getMassUnionElement(m1Elements, m2Elements);

		for (int i = 0; i < jointElements.size(); i++) {
			FocalElement jointElement = jointElements.get(i);
			double bpa = 0;
			for (int k = 0; k < m1Elements.size(); k++) {
				FocalElement el1 = m1Elements.get(k);

				for (int j = 0; j < m2Elements.size(); j++) {
					FocalElement el2 = m2Elements.get(j);

					Element intersection = Element.getIntersection(el1
							.getElement(), el2.getElement());

					if (intersection != null
							&& intersection.equals(jointElement.getElement())) {
						bpa = bpa + (el1.getBpa() * el2.getBpa());
					}
				}

			}
			jointElement.setBpa(bpa);
		}

		if (last) {
			Element universalSet = FrameOfDiscernment.getUniversalSet();

			jointElements.add(new FocalElement(universalSet, conflictTransfer));

		}
		JointMassDistribution jointMass = new JointMassDistribution(
				jointElements);
		return jointMass;
		// if (jointMass.isValid()) {
		// return jointMass;
		// } else
		// throw new MassDistributionNotValidException("MassDistribution"
		// + jointMass.toString() + " is not valid!");

	}

	private static JointMassDistribution dempster(MassDistribution m1,
			MassDistribution m2) throws MassDistributionNotValidException {

		ArrayList<FocalElement> m1Elements = m1.getFocalElements();
		ArrayList<FocalElement> m2Elements = m2.getFocalElements();

		double conflict = getConflict(m1Elements, m2Elements);

		ArrayList<FocalElement> jointElements = FocalElement
				.getMassUnionElement(m1Elements, m2Elements);

		for (int i = 0; i < jointElements.size(); i++) {
			FocalElement jointElement = jointElements.get(i);
			double bpa = 0;
			for (int k = 0; k < m1Elements.size(); k++) {
				FocalElement el1 = m1Elements.get(k);

				for (int j = 0; j < m2Elements.size(); j++) {
					FocalElement el2 = m2Elements.get(j);

					Element intersection = Element.getIntersection(el1
							.getElement(), el2.getElement());

					if (intersection != null
							&& intersection.equals(jointElement.getElement())) {
						bpa = bpa + (el1.getBpa() * el2.getBpa());
					}
				}

			}
			bpa = bpa / (1 - conflict);
			jointElement.setBpa(bpa);
		}

		JointMassDistribution jointMass = new JointMassDistribution(
				jointElements);
		if (jointMass.isValid()) {
			return jointMass;
		} else
			throw new MassDistributionNotValidException("MassDistribution"
					+ jointMass.toString() + " is not valid!");
	}

	private static double getConflict(ArrayList<FocalElement> m1Elements,
			ArrayList<FocalElement> m2Elements) {
		double conflict = 0;
		for (int i = 0; i < m1Elements.size(); i++) {
			FocalElement el1 = m1Elements.get(i);

			for (int j = 0; j < m2Elements.size(); j++) {
				FocalElement el2 = m2Elements.get(j);

				if (Element.getIntersection(el1.getElement(), el2.getElement()) == null) {
					conflict = conflict + (el1.getBpa() * el2.getBpa());
				}
			}

		}
		return conflict;
	}

	private static JointMassDistribution average(MassDistribution m1,
			MassDistribution m2, ArrayList<MassDistribution> masses)
			throws MassDistributionNotValidException {

		ArrayList<FocalElement> m1Elements = m1.getFocalElements();
		ArrayList<FocalElement> m2Elements = m2.getFocalElements();

		ArrayList<FocalElement> jointElements = FocalElement
				.getMassUnionElement(m1Elements, m2Elements);
		for (int i = 2; i < masses.size(); i++) {

			MassDistribution m3 = masses.get(i);
			ArrayList<FocalElement> el3 = m3.getFocalElements();
			jointElements = FocalElement
					.getMassUnionElement(jointElements, el3);
		}

		for (int i = 0; i < jointElements.size(); i++) {

			double bpa = 0;
			FocalElement jointElement = jointElements.get(i);

			ArrayList<FocalElement> sameElements = new ArrayList<FocalElement>();

			for (int j = 0; j < masses.size(); j++) {
				FocalElement same = FocalElement.findElement(masses.get(j)
						.getFocalElements(), jointElement.getElement());
				if (same != null) {
					sameElements.add(same);
				}
			}

			for (FocalElement element : sameElements) {
				bpa = bpa + element.getBpa();
			}

			jointElement.setBpa(new Double(bpa / masses.size()));

		}

		JointMassDistribution jointMass = new JointMassDistribution(
				jointElements);
		if (jointMass.isValid()) {
			return jointMass;
		} else
			throw new MassDistributionNotValidException("MassDistribution"
					+ jointMass.toString() + " is not valid!");
	}
}
