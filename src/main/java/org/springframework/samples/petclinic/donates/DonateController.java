package org.springframework.samples.petclinic.donates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public class DonateController {

	private final DonateRepository donateRepository;

	public DonateController(DonateRepository clinicService) {
		this.donateRepository = clinicService;
	}

	/*
	get mapping pro vets.html
	výchozí hodnota page = 1
	Model použijeme pro přidání parametrů do html šablony
	vyvtori se instance tridy Vets vets
	zavola se metoda vets.getVetlist, tedy arraylist, do ktereho se prida
	 */
	@GetMapping("/donates.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Donates donates = new Donates();
		Page<Donate> paginated = findPaginated(page); // vrati vysledek na jedne strance z db veterinaru
		donates.getDonateList().addAll(paginated.toList()); // vytvori arraylist, do ktereho se prida predchozi vysledek
		return addPaginationModel(page, paginated, model); // zavola se metoda viz nize, ktera prida data do modelu
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent(); // vytvori seznam veterinaru na aktualni strance z objektu paginated
		/*
		ulozeni promennych do promennych pro html sablonu
		 */
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		return "vets/vetList"; // vraci vetList.html
	}

	/*
	vraci stranku veterinaru na zaklade aktualni stranky
	 */
	private Page<Donate> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return donateRepository.findAll(pageable);
	}

	/*
	nevraci html stranku, nybrz data ve formatu json
	 */
	@GetMapping({ "/vets" })
	public @ResponseBody
	Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}

}
