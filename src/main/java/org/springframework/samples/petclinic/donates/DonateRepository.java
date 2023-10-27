package org.springframework.samples.petclinic.donates;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


public interface DonateRepository extends CrudRepository<Donate, Integer>{

	/*
	pouzivane metody save a deleteById jsou vestavene metody CrudRepository a neni treba je tu definovat
	 */

	@Transactional(readOnly = true)
	@Cacheable("donates")
	Collection<Donate> findAll() throws DataAccessException;

	@Transactional(readOnly = true)
	@Cacheable("donates")
	Page<Donate> findAll(Pageable pageable) throws DataAccessException;

	@Query("SELECT COUNT(*) FROM Donate")
	@Transactional(readOnly = true)
	int findNumberOfDonates();

	@Query("SELECT MAX(d.amount) FROM Donate d")
	@Transactional(readOnly = true)
	int findMaxAmount();

	@Query("SELECT SUM(d.amount) FROM Donate d")
	@Transactional(readOnly = true)
	int calculateTotalAmount();


}
