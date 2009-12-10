package core;

import interfaces.ISource;

import java.util.ArrayList;

import massDistribution.ClassAttributeMap;
import massDistribution.ClassificationAttribute;
import massDistribution.IMeasure;
import massDistribution.MassDistribution;
import massDistribution.MeasuredAttribute;
import massDistribution.Range;

/**
 * This class represents a source of evidence. Different source must have the
 * same {@link FrameOfDiscernment} to aggregate their {@link MassDistribution}.
 * 
 * @author Elisa Costante
 * 
 */
public abstract class SourceOfEvidence implements ISource {

	private FrameOfDiscernment frameOfDiscernment;

	private String name;

	public SourceOfEvidence(FrameOfDiscernment frameOfDiscernment, String name) {
		this.frameOfDiscernment = frameOfDiscernment;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FrameOfDiscernment getFrameOfDiscernment() {
		return frameOfDiscernment;
	}

	public void setFrameOfDiscernment(FrameOfDiscernment frameOfDiscernment) {
		this.frameOfDiscernment = frameOfDiscernment;
	}

	public MassDistribution getMassDistribution(
			ClassAttributeMap classAttributeMap) {

		ArrayList<FocalElement> focalEvidence = new ArrayList<FocalElement>();

		ArrayList<MeasuredAttribute> measureAttributesList = readMeasureAttribute();

		for (MeasuredAttribute measuredAttribute : measureAttributesList) {

			ClassificationAttribute classAttribute = classAttributeMap
					.getClassificationAttribute(measuredAttribute
							.getIdentifier());
			IMeasure measuredValue = measuredAttribute.getMeasure();

			if (measuredValue.hasMeasuredValue()) {
				Element element = computeElement(classAttribute, measuredValue);

				FocalElement focalElement = new FocalElement(element,
						classAttribute.getWeight());

				focalEvidence.add(focalElement);
			}

		}

		return computeMass(focalEvidence);
	}

	private MassDistribution computeMass(ArrayList<FocalElement> focalEvidence) {
		MassDistribution mass = new MassDistribution();

		ArrayList<FocalElement> bodyOfEvidence = new ArrayList<FocalElement>();

		for (int i = 0; i < focalEvidence.size(); i++) {
			FocalElement focalElement = focalEvidence.get(i);

			double bpa = focalElement.getBpa();

			for (int j = i + 1; j < focalEvidence.size(); j++) {
				FocalElement same = focalEvidence.get(j);
				if (focalElement.getElement().equals(same.getElement())) {
					bpa = bpa + same.getBpa();
				}
			}
			if (FocalElement.findElement(bodyOfEvidence, focalElement
					.getElement()) == null)
				bodyOfEvidence.add(new FocalElement(focalElement.getElement(),
						bpa));
		}

		mass = new MassDistribution(bodyOfEvidence);

		if (!mass.isValid()) {
			FocalElement universalSet = new FocalElement(new Element(
					frameOfDiscernment.getHipothesies()), (double) (1.0 - mass
					.getTotalBpa()));
			mass.addElement(universalSet);
		}
		MassDistribution.setBodyOfEvidence(mass);

		return mass;
	}

	/**
	 * Returns the {@link Element} or null
	 * 
	 * @param classAttribute
	 * @param measuredValue
	 * @return
	 */
	private Element computeElement(ClassificationAttribute classAttribute,
			IMeasure measuredValue) {
		ArrayList<Hypothesis> allHypothesis = frameOfDiscernment
				.getHipothesies();

		Element element = new Element();
		for (Hypothesis hypothesis : allHypothesis) {

			ArrayList<Range> allRange = classAttribute.getRanges(hypothesis);

			if (allRange != null) {
				for (Range range : allRange) {
					if (range.containsMeasure(measuredValue)) {
						element.addHypothesis(hypothesis);
					}
				}
			}
		}

		if (element.getHypothesies() == null)
			System.out.println(classAttribute);
		return element;
	}

	/**
	 * This method must be implemented in order to read the attribute collected
	 * and measured from the source.
	 * 
	 * @return a list of {@link MeasuredAttribute}.
	 */
	public abstract ArrayList<MeasuredAttribute> readMeasureAttribute();

}
