package org.springframework.samples.petclinic.donates;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DonateController {

	private final DonateRepository donateRepository;

	public DonateController(DonateRepository clinicService) {
		this.donateRepository = clinicService;
	}

	@PostMapping("/donates.html")
	public String processCreationForm(@Valid Donate donate, BindingResult result) {
		if (result.hasErrors()) {
			// Zpracování chybného formuláře, např. přesměrování na stránku s chybou
			return "error.html";
		}
		this.donateRepository.save(donate);
		return "redirect:/donates.html";
	}

	/*
	get mapping pro donates.html
	výchozí hodnota page = 1
	Model použijeme pro přidání parametrů do html šablony
	vyvtori se instance tridy Donates Donates
	zavola se metoda donates.getDonateList, tedy arraylist, do ktereho se prida
	 */
	@GetMapping("/donates.html")
	public String showDonateList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Here we are returning an object of type 'Donates' rather than a collection of Donate
		// objects so it is simpler for Object-Xml mapping
		Donates donates = new Donates();
		Page<Donate> paginated = findPaginated(page); // vrati vysledek na jedne strance z db veterinaru
		donates.getDonateList().addAll(paginated.toList()); // vytvori arraylist, do ktereho se prida predchozi vysledek
		return addPaginationModel(page, paginated, model); // zavola se metoda viz nize, ktera prida data do modelu
	}

	private String addPaginationModel(int page, Page<Donate> paginated, Model model) {
		List<Donate> listDonates = paginated.getContent(); // vytvori seznam veterinaru na aktualni strance z objektu paginated
		/*
		ulozeni promennych do promennych pro html sablonu
		 */
		model.addAttribute("donate", new Donate());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listDonates", listDonates);
		return "donates/donateList"; // vraci donateList.html
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
	@GetMapping({ "/donates" })
	public @ResponseBody
	Donates showResourcesDonateList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Donates donates = new Donates();
		donates.getDonateList().addAll(this.donateRepository.findAll());
		return donates;
	}

}
