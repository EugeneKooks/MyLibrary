package by.epam.kooks.dao;

import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Person;
import by.epam.kooks.dao.exception.DaoException;

/**
 * Interface, describes additional queries for the person table in the database.
 *
 * @author Eugene Kooks
 */
public interface PersonDao extends Dao<Person> {

    /**
     * Method, searches for the Person entity with the Book entity taken into account.
     *
     * @param customer - entity
     * @return Returns a specific entity.
     */
    Person findByCustomer(Customer customer) throws DaoException;
}
