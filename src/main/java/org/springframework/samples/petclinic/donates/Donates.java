package org.springframework.samples.petclinic.donates;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.samples.petclinic.vet.Vet;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Donates {

	private List<Donate> donates;

	/*
	Tato anotace rika, ze ma dany vystup zaradit do XML
	 */
	@XmlElement
	public List<Donate> getDonateList() {
		if (donates == null) {
			donates = new ArrayList<>();
		}
		return donates;
	}

}
