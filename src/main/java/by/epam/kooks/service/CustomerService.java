package by.epam.kooks.service;

import by.epam.kooks.dao.CustomerDao;
import by.epam.kooks.dao.CustomerRoleDao;
import by.epam.kooks.dao.PersonDao;
import by.epam.kooks.dao.impl.MySqlCustomerDao;
import by.epam.kooks.dao.impl.MySqlCustomerRoleDao;
import by.epam.kooks.dao.impl.MySqlPersonDao;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.CustomerRole;
import by.epam.kooks.entity.Management;
import by.epam.kooks.entity.Person;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.util.SqlDate;

import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;

/**
 * Class - service, performs all the manipulations and transactions associated with the customer.
 *
 * @author Eugene Kooks
 */
public class CustomerService {
    private static final Logger log = LogManager.getLogger(CustomerService.class);
    /**
     * Default role for customers who will register
     */
    private static final String USER_ROLE = "user";
    /**
     * Method, user registration
     *
     * @param customer - entity, with user data
     */
    public void registerCustomer(Customer customer) throws ServiceException {
            try {
                log.debug("Start register a customer where customer id equals :{}", customer.getId());
                PersonDao personDao = new MySqlPersonDao();
                CustomerDao customerDao = new MySqlCustomerDao();
                CustomerRoleDao customerRoleDao = new MySqlCustomerRoleDao();
                CustomerRole customerRole = customerRoleDao.findRoleByName(USER_ROLE);
                personDao.insert(customer.getPerson());
                customer.setCustomerRole(customerRole);
                customer.setRegisterDate(SqlDate.currentDateAndTime());
                customerDao.insert(customer);
                log.info("Register a customer where customer id equals :{}", customer.getId());
            } catch (DaoException e) {
                log.warn("Can't register customer where customer id equals: {} ", customer.getId(), e);
                throw new ServiceException("can't register customer", e);
            }
        }
    /**
     * Method, searches customer by id
     *
     * @param id - customer id
     * @return - specific customer
     */
    public Customer findCustomerById(int id) throws ServiceException {
            Customer customer;
            try {
                log.debug("Finding customer by id where id equals: {} ", id);
                CustomerDao customerDao = new MySqlCustomerDao();
                customer = customerDao.findById(id);
                if(customer!=null){
                    fillCustomer(customer);
                }else {
                    throw new ServiceException("can't find by customer id customer");
                }
                return customer;
            } catch (DaoException e) {
                log.warn("Can't find customer by id where id equals: {} ", id, e);
                throw new ServiceException("can't find by customer id customer", e);
            }
        }
    /**
     * Method, searches customer by login
     *
     * @param login - customer login
     * @return - specific customer
     */
    private Customer findCustomerByLogin(String login) throws ServiceException {

            Customer customer;
            try {
                log.debug("Start find customer by login where login equals {}", login);
                CustomerDao customerDao = new MySqlCustomerDao();
                customer = customerDao.getCustomer(login);
                fillCustomer(customer);
                log.info("Find customer by login where login equals {}", login);
                return customer;
            } catch (DaoException e) {
                log.warn("Can't find customer by login where login equals: {} ", login, e);
                throw new ServiceException("can't find by login customer", e);
            }
        }
    /**
     * Method, search customer by login and password
     *
     * @param login    - customer login
     * @param password - customer password
     * @return - specific customer
     */
    public Customer findCustomerByLoginAndPassword(String login, String password) throws ServiceException {
            Customer customer;
            try {
                CustomerDao customerDao = new MySqlCustomerDao();
                customer = customerDao.getCustomer(login, password);
                fillCustomer(customer);

                log.info("Find customer by login and password where login/password equals: {} ****", login);
                return customer;
            } catch (DaoException e) {
                log.warn("Can't find customer by login and password where login/password equals: {} ****", login, e);
                throw new ServiceException("can't find by login and password customer", e);
            }
        }
    /**
     * Method, update customer data (Which were acquired as a result of registrations)
     *
     * @param customer - customer id
     */
    public void updateCustomer(Customer customer) throws ServiceException {
            try {
                PersonDao personDao = new MySqlPersonDao();
                CustomerDao customerDao = new MySqlCustomerDao();
                Person person = personDao.findByCustomer(customer);
                customer.setPerson(person);
                customerDao.update(customer);
                log.debug("Update customer where customer id equals: {}", customer.getId());
            } catch (DaoException e) {
                log.warn("Can't update customer where customer id equals: {}", customer.getId(), e);
                throw new ServiceException("can't update customer ", e);
            }
        }
    /**
     * Method,update customer data (Passport data)
     *
     * @param customer - should contain person entity with data
     */
    public void updatePerson(Customer customer) throws ServiceException {
            try {
                PersonDao personDao = new MySqlPersonDao();
                personDao.update(customer.getPerson());
                log.debug("Update person where person id equals: {}", customer.getPerson().getId());
            } catch (DaoException e) {
                log.warn("Can't update person where person id equals: {}", customer.getPerson().getId(), e);
                throw new ServiceException("can't update person", e);
            }
        }
        public void deleteCustomer(Customer customer) throws ServiceException {

            try {
                PersonDao personDao = new MySqlPersonDao();
                CustomerDao customerDao = new MySqlCustomerDao();
                Person person = personDao.findByCustomer(customer);
                customerDao.delete(customer);
                personDao.delete(person);
                log.info("Delete customer where customer id equals: {}", customer.getId());
            } catch (DaoException e) {
                log.warn("Can't rollback transaction", e);
                throw new ServiceException("can't rollback transaction", e);
            }
            log.warn("Can't delete customer where customer id equals: {}", customer.getId());
        }
    /**
     * Method, provides the full list of city
     *
     * @return Return a list of cities
     */
    public int getCustomerCount() throws ServiceException {
        try {
            CustomerDao customerDao = new MySqlCustomerDao();
            return customerDao.getCustomerCount();
        } catch (DaoException e) {
            log.warn("Can't get count customer from CustomerService", e);
            throw new ServiceException("can't get count customer", e);
        }
    }

    /**
     * Method,provides a list of customer in a specific range
     *
     * @param start - the row from which you must begin
     * @param end   - the row from which you must finish
     * @return list of customer
     */
    public List<Customer> getListCustomers(int start, int end) throws ServiceException {
            try {
                CustomerDao customerDao = new MySqlCustomerDao();
                List<Customer> list = customerDao.getLimitCustomers(start, end);
                for (Customer customer : list) {
                    fillCustomer(customer);
                }
                log.debug("Get customer list by range {} to {}", start, end);
                return list;
            } catch (DaoException e) {
                log.warn("can't get list of customer ", e);
                throw new ServiceException("can't get list of customer ", e);
            }
        }
    /**
     * Method, checks for login availability
     *
     * @param login - customer login
     * @return Return accessibility or inaccessibility of the book
     */
    public boolean isCustomerLoginAvailable(String login) throws ServiceException {
        return findCustomerByLogin(login) == null;
    }
    /**
     * Searches for customer  by entity management
     *
     * @param management - entity
     * @return - specific customer
     */
    public Customer findCustomerByManagement(Management management) throws ServiceException {

            Customer customer;
            try {
                CustomerDao customerDao = new MySqlCustomerDao();
                customer = customerDao.findByManagement(management);
                fillCustomer(customer);
                return customer;
            } catch (DaoException e) {
                log.warn("Can't find customer by management id where id equals {} ", management.getId(), e);
                throw new ServiceException("Can't find customer by management id", e);
            }
        }
    /**
     * Method, fill the customer entity of the data associated with customer
     *
     * @param customer - entity
     */
    private void fillCustomer(Customer customer) throws ServiceException {
        try {
            if (customer != null) {
                log.debug("Fill customer with information");
                    PersonDao personDao = new MySqlPersonDao();
                    CustomerRoleDao customerRoleDao = new MySqlCustomerRoleDao();
                    Person person = personDao.findByCustomer(customer);
                    customer.setPerson(person);
                    customer.setCustomerRole(customerRoleDao.findByCustomer(customer));
                }
        } catch (DaoException e) {
            log.warn("Can't fill customer with information ", e);
            throw new ServiceException("Can't fill customer ", e);
        }
    }
}