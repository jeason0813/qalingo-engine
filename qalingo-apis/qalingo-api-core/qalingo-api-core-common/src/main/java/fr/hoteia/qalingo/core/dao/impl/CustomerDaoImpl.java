/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.core.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.hoteia.qalingo.core.dao.CustomerDao;
import fr.hoteia.qalingo.core.domain.Customer;

@Transactional
@Repository("customerDao")
public class CustomerDaoImpl extends AbstractGenericDaoImpl implements CustomerDao {

	public Customer getCustomerById(final Long customerId) {
		return em.find(Customer.class, customerId);
	}
	
	public Customer getCustomerByCode(final String code) {
		Session session = (Session) em.getDelegate();
		String sql = "FROM Customer WHERE upper(code) = upper(:code)";
		Query query = session.createQuery(sql);
		query.setString("code", code);
		Customer customer = (Customer) query.uniqueResult();
		return customer;
	}

	public Customer getCustomerByLoginOrEmail(final String usernameOrEmail) {
		Session session = (Session) em.getDelegate();
		String sql = "FROM Customer WHERE (login = :usernameOrEmail OR email = :usernameOrEmail) AND active = true";
		Query query = session.createQuery(sql);
		query.setString("usernameOrEmail", usernameOrEmail);
		Customer customer = (Customer) query.uniqueResult();
		return customer;
	}
	
	public List<Customer> findCustomers() {
		Session session = (Session) em.getDelegate();
		String sql = "FROM Customer ORDER BY lastname";
		Query query = session.createQuery(sql);
		List<Customer> customers = (List<Customer>) query.list();
		return customers;
	}
	
	public List<Customer> findByExample(final Customer customerExample) {
		return super.findByExample(customerExample);
	}

	public void saveOrUpdateCustomer(final Customer customer) {
		if(customer.getDateCreate() == null){
			customer.setDateCreate(new Date());
			customer.setActive(true);
			customer.setValidated(false);
			if(StringUtils.isEmpty(customer.getCode())){
				customer.setCode(UUID.randomUUID().toString());
			}
		}
		customer.setDateUpdate(new Date());
//		if(customer.getId() == null){
//			em.persist(customer);
//		} else {
//			em.merge(customer);
//		}
		em.merge(customer);
	}

	public void deleteCustomer(final Customer customer) {
		em.remove(customer);
	}

}