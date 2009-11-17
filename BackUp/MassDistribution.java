package core;

import java.util.ArrayList;
import java.util.Iterator;

import utilities.DoubleWrapper;

public class MassDistribution {

	protected ArrayList<Element> elements;

	public MassDistribution(ArrayList<Element> mass) {
		super();
		this.elements = mass;
	}

	public ArrayList<Element> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Element> mass) {
		this.elements = mass;
	}

	public void addElement(Element element) {
		if (elements == null)
			elements = new ArrayList<Element>();
		elements.add(element);
	}

	/**
	 * Verify if the elements distribution is valid, hat means the sum of all
	 * elements it's equal to one.
	 * 
	 * @return true if the elements distribution is valid, false otherwise.
	 */
	public boolean isValid() {
		DoubleWrapper sum = new DoubleWrapper(0);
		for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			sum.setDoubleValue(sum.getDoubleValue() + element.getBpa());

		}
		if (sum.getDoubleValue() == new DoubleWrapper(1).getDoubleValue())
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MassDistribution [elements=" + elements + "]";
	}

}