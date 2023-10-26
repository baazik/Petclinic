package org.springframework.samples.petclinic.donates;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
public class DonateController {

	private final DonateRepository donateRepository;
	private final CacheManager cacheManager;
	private static final Logger logger = LoggerFactory.getLogger(DonateController.class);

	public DonateController(DonateRepository clinicService, CacheManager cacheManager) {
		this.donateRepository = clinicService;
		this.cacheManager = cacheManager;
	}

	@PostMapping("/donates.html")
	public String processCreationForm(@Valid Donate donate, BindingResult result) {
		if (result.hasErrors()) {
			// Zpracování chybného formuláře, např. přesměrování na stránku s chybou
			logger.error("Error with processing the form.");
			return "error.html";
		}
		this.donateRepository.save(donate);
		logger.info("New donate added: donator={}, date={}, amount={}, message={}", donate.getDonatorName(), donate.getDate(), donate.getAmount(), donate.getMessage());

		// vycisteni cache po ulozeni novych dat do db, aby byly znovu nacteny do cache
		Cache cache = cacheManager.getCache("donates");
		if (cache != null) {
			cache.clear();
			logger.info("Cleaning cache - donates.");
		}

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
		Page<Donate> paginated = findPaginated(page); // vrati vysledek na jedne strance z db donates
		donates.getDonateList().addAll(paginated.toList()); // vytvori arraylist, do ktereho se prida predchozi vysledek
		return addPaginationModel(page, paginated, model); // zavola se metoda viz nize, ktera prida data do modelu
	}

	private String addPaginationModel(int page, Page<Donate> paginated, Model model) {
		List<Donate> listDonates = paginated.getContent(); // vytvori seznam donatu na aktualni strance z objektu paginated
		/*
		ulozeni promennych do promennych pro html sablonu
		 */
		model.addAttribute("donate", new Donate());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listDonates", listDonates);
		logger.info("Adding data to model for view: donate, currentPage, totalPages, totalItems, listDonate");
		return "donates/donateList"; // vraci donateList.html

	}

	/*
	vraci stranku donates na zaklade aktualni stranky
	 */
	private Page<Donate> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		logger.info("Finding donates in database.");
		return donateRepository.findAll(pageable);
	}

	/*
	Mazani donatu z DB
	 */
	@PostMapping("/delete/{donateId}")
	public String deleteDonate(@PathVariable Integer donateId) {
		donateRepository.deleteById(donateId);
		logger.info("Deleting donate with id {} from database.",donateId);

		Cache cache = cacheManager.getCache("donates");
		if (cache != null) {
			cache.clear();
			logger.info("Cleaning cache - donates.");
		}

		return "redirect:/donates.html";
	}

	/*
	nevraci html stranku, nybrz data ve formatu json
	 */
	@GetMapping({ "/donates" })
	public @ResponseBody
	Donates showResourcesDonateList() {
		// Here we are returning an object of type 'Donates' rather than a collection of Donate
		// objects so it is simpler for JSon/Object mapping
		Donates donates = new Donates();
		donates.getDonateList().addAll(this.donateRepository.findAll());
		return donates;
	}

}
