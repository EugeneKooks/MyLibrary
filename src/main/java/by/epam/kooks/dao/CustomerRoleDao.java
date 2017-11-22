package by.epam.kooks.dao;

import by.epam.kooks.entity.CustomerRole;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.Customer;

/**
 * Interface, describes additional queries for the customerRole table in the database.
 *
 * @author Eugene Kooks
 */
public interface CustomerRoleDao extends Dao<CustomerRole> {
    /**
     * The method looks for a role with the Customer entity.
     *
     * @param customer - entity
     * @return Returns a specific role.
     */
    CustomerRole findByCustomer(Customer customer) throws DaoException;

    /**
     * Method, looking for a role by the name of the role.
     *
     * @param nameRole - name of the role.
     * @return Returns a specific role.
     */
    CustomerRole findRoleByName(String nameRole) throws DaoException;

}
