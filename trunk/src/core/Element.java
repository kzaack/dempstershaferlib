package core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * An {@link Element} belong to PowerSet. It is a Singleton if has only one
 * {@link Hypothesis}
 * 
 * @author Elisa Costante
 * 
 */
public class Element implements Comparable, Cloneable {

	protected ArrayList<Hypothesis> hypothesies;

	private Double bpa;
	private Double belief;
	private Double plausability;

	public Element(ArrayList<Hypothesis> hypothesies, Double bpa) {
		super();
		setBpa(bpa);
		this.hypothesies = hypothesies;
	}

	public Element(ArrayList<Hypothesis> hypothesies) {
		super();
		this.hypothesies = hypothesies;
	}

	public Double getBpa() {
		return bpa;
	}

	/**
	 * Set the Value of bpa. It mantains only 5 decimal digit
	 * 
	 * @param bpa
	 */
	public void setBpa(Double bpa) {
		BigDecimal bigDecimal = new BigDecimal(bpa, new MathContext(5));
		bpa = bigDecimal.doubleValue();
		this.bpa = bpa;

	}

	public Double getBelief() {
		return belief;
	}

	public void setBelief(Double belief) {
		BigDecimal bigDecimal = new BigDecimal(belief, new MathContext(5));
		belief = bigDecimal.doubleValue();
		this.belief = belief;
	}

	public Double getPlausability() {
		return plausability;
	}

	public void setPlausability(Double plausability) {
		BigDecimal bigDecimal = new BigDecimal(plausability, new MathContext(5));
		plausability = bigDecimal.doubleValue();
		this.plausability = plausability;
	}

	public ArrayList<Hypothesis> getHypothesies() {
		return hypothesies;
	}

	public void setHypothesies(ArrayList<Hypothesis> hypothesies) {
		this.hypothesies = hypothesies;
	}

	/**
	 * @return true if and only if the element has just one {@link Hypothesis}.
	 */
	public boolean isSingleton() {
		if (hypothesies.size() == 1)
			return true;
		else
			return false;

	}

	/**
	 * Returns the Intersection between <code>element1</code> and
	 * <code>element2</code>.
	 * 
	 * @param element1
	 * @param element2
	 * @return the intersection or null if the intersection is empty.
	 */

	public static Element getIntersection(Element element1, Element element2) {

		ArrayList<Hypothesis> h1Array = element1.getHypothesies();
		ArrayList<Hypothesis> h2Array = element2.getHypothesies();
		ArrayList<Hypothesis> intersectionHypothesies = new ArrayList<Hypothesis>();

		Element intersectionElement = null;

		for (int i = 0; i < h1Array.size(); i++) {
			Hypothesis h1 = h1Array.get(i);
			for (int j = 0; j < h2Array.size(); j++) {
				Hypothesis h2 = h2Array.get(j);
				if (h1.equals(h2))
					intersectionHypothesies.add(h1);
			}

		}
		if (intersectionHypothesies.size() > 0) {
			intersectionElement = new Element(intersectionHypothesies);
		}

		return intersectionElement;
	}

	/**
	 * Returns the Union between <code>element1</code> and <code>element2</code>
	 * . An union of two {@link Element} is an {@link Element} that has all the
	 * hypothesies common to both the elemnts <code>element1</code> and
	 * <code>element2</code>.
	 * 
	 * @param element1
	 * @param element2
	 * @return the Union between the elements or null if the union is empty.
	 */
	public static Element getUnion(Element element1, Element element2) {

		TreeSet<Hypothesis> union = new TreeSet<Hypothesis>(element1
				.getHypothesies());
		union.addAll(element2.getHypothesies());

		if (union.size() > 0)

			return new Element(new ArrayList<Hypothesis>(union));
		else
			return null;
	}

	/**
	 * Return the union of two elements list.
	 * 
	 * @param el1
	 * @param el2
	 * @return the union of two elements list.
	 */
	public static ArrayList<Element> getMassUnionElement(
			ArrayList<Element> elementList1, ArrayList<Element> elementList2) {

		TreeSet<Element> union = new TreeSet<Element>(elementList1);

		union.addAll(new TreeSet<Element>(elementList2));

		return new ArrayList<Element>(union);
	}

	@Override
	public boolean equals(Object obj) {
		Element other = (Element) obj;
		ArrayList<Hypothesis> otherHypothesies = other.getHypothesies();
		if (other.getHypothesies().size() == hypothesies.size()
				&& otherHypothesies.containsAll(hypothesies))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		String elementToString = "{";
		for (int i = 0; i < hypothesies.size(); i++) {
			Hypothesis hypothesis = (Hypothesis) hypothesies.get(i);
			elementToString = elementToString + hypothesis.getName();
			if (i != (hypothesies.size() - 1)) {
				elementToString = elementToString + ",";
			}
		}
		elementToString = elementToString + " - " + bpa.toString() + "}";

		return elementToString;
	}

	@Override
	public int compareTo(Object o) {
		Element element = (Element) o;
		String compare1 = element.getHypothesies().toString();
		String compare2 = hypothesies.toString();

		return compare1.compareTo(compare2);
	}

	/**
	 * @return the number of the hypothesies of the element
	 */
	public int size() {
		if (hypothesies != null)
			return hypothesies.size();
		else
			return 0;
	}

	public static ArrayList<Element> getMassUnionElement(
			ArrayList<MassDistribution> masses) {
		ArrayList<Element> union = null;
		if (masses.size() >= 2) {
			ArrayList<Element> m1Elements = masses.get(0).getElements();
			ArrayList<Element> m2Elements = masses.get(1).getElements();

			union = getMassUnionElement(m1Elements, m2Elements);
			for (int i = 2; i < masses.size(); i++) {
				union = getMassUnionElement(union, masses.get(i).getElements());
			}

		}
		return union;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ArrayList<Hypothesis> hypClone = new ArrayList<Hypothesis>();
		for (int i = 0; i < hypothesies.size(); i++) {
			Hypothesis hyp = (Hypothesis) hypothesies.get(i).clone();
			hypClone.add(hyp);
		}
		Element clone = new Element(hypClone, bpa);
		return clone;
	}
}
