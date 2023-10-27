package org.springframework.samples.petclinic.report;

import org.springframework.samples.petclinic.donates.DonateRepository;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReportController {

	private final DonateRepository donateRepository;
	private final OwnerRepository ownerRepository;

	public ReportController(DonateRepository donateRepository, OwnerRepository ownerRepository) {
		this.donateRepository = donateRepository;
		this.ownerRepository = ownerRepository;
	}

	@GetMapping("/reports.html")
	public String showReports(Model model) {
		Report report = new Report();
		report.setNumberOfDonates(String.valueOf(donateRepository.findNumberOfDonates()));
		report.setBiggestDonate(String.valueOf(donateRepository.findMaxAmount()));
		report.setSumOfDonates(String.valueOf(donateRepository.calculateTotalAmount()));
		report.setNumberOfOwners(String.valueOf(ownerRepository.findNumberOfOwners()));
		List<Object[]> petSummaries = ownerRepository.findPetSummaries();
		model.addAttribute("petSummaries", petSummaries);
		model.addAttribute("report", report);
		return "reports/reports";
	}

}
