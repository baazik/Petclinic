/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

	/*
	html sablona ulozena do Stringu
	 */
	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	/*
	vytvoření instance třídy OwnerRepository
	 */
	private final OwnerRepository owners;

	/*

	 */
	public OwnerController(OwnerRepository clinicService) {
		this.owners = clinicService;
	}

	/*
	zakazuje data binderu zpracovavat pole s nazvem id = neprejeme si moznost upravovat id ownera
	dataBinder je soucasti Springu - umoznuje vazat data mezi HTTP pozadavkem a objekty javy
	tady konkretne se nastavuje, ze pole id nema byt povoleno pro dataBinding, tedy ze data pro toto pole nebudou zpracovana
	 */
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/*
	metoda poskytuje modelovy atribut s nazvem owner - ten bude dostupny v html sablone
	metoda findOwner vraci instanci tridy Owner na zaklade ownerID, ktere je ziskano z URL
	pokud id neexistuje, vytvori se nova instance Ownera
	 */
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.owners.findById(ownerId);
	}

	/*
	zpracovava get pozadavek na /owners/new
	vytvari novou instanci Ownera a prida ji do modelu, aby byla k dispozici v html sablone
	vraci sablonu pro vytvareni ci upravu ownera
	 */
	@GetMapping("/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	/*
	zpracovava post pozadavek na url /owners/new
	metoda bere objekt Owner a zpracovava ho
	pokud jsou v objektu chyby, validace selze a vrati se zpet stranka pro vytvoreni ci upravu s chybovymi zpravami
	pokud validace probehne v poradku, owner je ulozen do db a uzivatel je presmerovan na detail noveho ownera
	 */
	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		return "redirect:/owners/" + owner.getId();
	}

	/*
	zpracovava get pozadavek na url /owners/find
	zobrazi stranku s formularem pro vyhledani owneru
	 */
	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	/*
	zpracovava get pozadavek na url /owners
	slouzi k vyhledani owneru na zaklade jmena a strankovani vysledku
	vysledky jsou predany modelu k zobrazeni
	 */
	@GetMapping("/owners")
	/*
	int page - aktualni stranka
	Owner owner - objekt tridy owner, ktery obsahuje kriteria pro vyhledavani, zde je to lastName
	BindingResult result - slouzi k zachyceni chyb pri validaci vstupnich dat
	Model model - prostredek ke komunikaci s FE
	 */
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // pokud je lastName null, nastavi se prazdny String, tedy rozsiri se vyhledavani
		}

		// hleda ownery podle lastName
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, owner.getLastName());
		if (ownersResults.isEmpty()) {
			// pokud nebyly vyhledany zadne vysledky
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			// pokud byl nalezen 1 owner
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}

		// pokud bylo nalezeno vice owneru
		return addPaginationModel(page, model, ownersResults);
	}

	/*
	predava do modelu informace o strankovani, jako je aktualni stranka, pocet stranek, pocet polozek a seznam owneru
	 */
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

	/*
	provadi strankovani vlastniku na zaklade prijmeni
	vraci stranku vysledku, kazda stranka obsahuje az 5 zaznamu
	Pageable - rozhrani Spring data, ktere umoznuje konfigurovat parametry pro strankovani
	PageRequest.of - vytvari objekt typu Pageable s informacemi o strankovani, kde page je cislo aktualni stranky (index od 0) a pageSize urcuje pocet polozek na strance
    owners.findByLastName - lastname je kriterium tedy prijmeni, pageable jsou parametry strankovani; vysledkem je objekt Page<Owner> tedy seznam owneru na aktualni strance
	 */
	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastName(lastname, pageable);
	}

	/*
	zpracuje get metodu pro url /owners/{ownerId}/edit
	nacte ownera na zaklade id a prida ho do modelu pro upravu
	vrati stranku pro upravu ownera
	 */
	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	/*
	zpracovava post metodu
	validuje vlastnika a pokud v objeku nejsou chyby, ulozi zmeny do databaze
	nasledne presmeruje na detail ownera
	 */
	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		owner.setId(ownerId);
		this.owners.save(owner);
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	/*
	zpracovava get pozadavek
	vytvori model a zobrazi detail ownera na strance
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails"); // urceni html stranky k zobrazeni
		Owner owner = this.owners.findById(ownerId); // ziska vlastnika na zaklade id
		mav.addObject(owner); // prida vlastnika do modelu, takze objekt bude dostupny v html strance k zobrazeni detailu
		return mav; // vrati instanci ModelAndView, takze bude zobrazena stranka s detaily vlastnika
	}


}
