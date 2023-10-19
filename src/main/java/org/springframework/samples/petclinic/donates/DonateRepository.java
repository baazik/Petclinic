package org.springframework.samples.petclinic.donates;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;



public interface DonateRepository extends Repository<Donate, Integer>  {

	/*
	ukladani donatu do db
	 */
	void save(Donate donate);

	@Transactional(readOnly = true)
	@Cacheable("donates")
	Collection<Donate> findAll() throws DataAccessException;

	@Transactional(readOnly = true)
	@Cacheable("donates")
	Page<Donate> findAll(Pageable pageable) throws DataAccessException;



}
