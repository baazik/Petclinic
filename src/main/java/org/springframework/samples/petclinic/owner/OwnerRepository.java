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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.report.Report;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
// repository znamena, ze bude spravovat komunikaci s DB
// rozsiruje rozhrani repository, coz je soucast Spring Data JPA
// anotace definije typy entit a prim. klice s nimiz rozhrani bude pracovat
public interface OwnerRepository extends Repository<Owner, Integer> {

	/**
	 * Retrieve all {@link PetType}s from the data store.
	 * @return a Collection of {@link PetType}s.
	 */
	/*
	Vrati vsechny tpy zvirat jako seznam
	 */
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none
	 * found)
	 */
	/*
	Vrati vyhledane owner dle prijmeni
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join  owner.pets WHERE owner.lastName LIKE :lastName% ")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	/*
	Vyhledava ownery dle jejich ID
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);

	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 * @param owner the {@link Owner} to save
	 */
	/*
	uklada ownera do db
	funguje to diky tomu, ze trida Owner je jako entita a ma definovano, co se ma ukladat kam
	 */
	void save(Owner owner);

	/**
	 * Returns all the owners from data store
	 **/
	/*
	vrati vsechny ownery v DB
	 */
	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	Page<Owner> findAll(Pageable pageable);

	/*
	vrati pocet Owneru
	*/
	@Query("SELECT COUNT(*) FROM Owner")
	@Transactional(readOnly = true)
	int findNumberOfOwners();

	/*
	vrati pocet Pets
	*/
	@Query("SELECT COUNT(*) FROM Pet")
	@Transactional(readOnly = true)
	int findNumberOfPets();


	@Query(value = "SELECT p.name AS petName, t.name AS petTypeName, v.visit_date AS visitDate " +
		"FROM pets p " +
		"LEFT JOIN visits v ON p.id = v.pet_id " +
		"LEFT JOIN types t ON p.type_id = t.id " +
		"ORDER BY v.visit_date DESC",
		nativeQuery = true)
	List<Object[]> findPetSummaries();

}
