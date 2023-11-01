package org.springframework.samples.petclinic.report;

import java.util.List;

public class Report {

	private String numberOfDonates;
	private String sumOfDonates;
	private String biggestDonate;
	private String numberOfOwners;
	private String numberOfPets;
	private List<Object[]> petSummaries;

	public String getNumberOfDonates() {
		return numberOfDonates;
	}

	public void setNumberOfDonates(String numberOfDonates) {
		this.numberOfDonates = numberOfDonates;
	}

	public String getSumOfDonates() {
		return sumOfDonates;
	}

	public void setSumOfDonates(String sumOfDonates) {
		this.sumOfDonates = sumOfDonates;
	}

	public String getBiggestDonate() {
		return biggestDonate;
	}

	public void setBiggestDonate(String biggestDonate) {
		this.biggestDonate = biggestDonate;
	}

	public String getNumberOfOwners() {
		return numberOfOwners;
	}

	public void setNumberOfOwners(String numberOfOwners) {
		this.numberOfOwners = numberOfOwners;
	}

	public String getNumberOfPets() {
		return numberOfPets;
	}

	public void setNumberOfPets(String numberOfPets) {
		this.numberOfPets = numberOfPets;
	}

	public List<Object[]> getPetSummaries() {
		return petSummaries;
	}

	public void setPetSummaries(List<Object[]> petSummaries) {
		this.petSummaries = petSummaries;
	}

}
