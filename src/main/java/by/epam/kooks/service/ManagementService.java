package by.epam.kooks.service;

import by.epam.kooks.dao.ManagementDao;
import by.epam.kooks.dao.TransactionDao;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.dao.impl.MySqlBookInfoDao;
import by.epam.kooks.dao.impl.MySqlManagementDao;
import by.epam.kooks.dao.impl.MySqlTransactionDao;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Management;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.exception.ServiceException;
import by.epam.kooks.dao.BookInfoDao;
import by.epam.kooks.entity.BookInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class - service, perform all manipulations and transactions associated with the management.
 *
 * @author Eugene Kooks
 */
public class ManagementService {
    private static final Logger log = LogManager.getLogger(ManagementService.class);
    /**
     * Field - count of book which can increase for one return book transaction
     */
    private static final int ONE_BOOK = 1;

    /**
     * Method, intended to provide return book.
     *
     * @param management - entity with contain transaction id
     */
    public void adminReturnBook(Management management) throws ServiceException {
            try {
                log.debug("Start execute transaction for Return book where management id equals {} ", management.getId());
                BookService bookService = new BookService();
                TransactionDao transactionDao = new MySqlTransactionDao();
                ManagementDao managementDao = new MySqlManagementDao();
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                Transaction transaction = transactionDao.findByManagement(management);
                BookInfo bookInfo = bookInfoDao.findByTransaction(transaction);
                management = managementDao.findById(management.getId());
                bookInfo.setAmount(bookInfo.getAmount() + ONE_BOOK);
                transaction.setBookInfo(bookInfo);
                management.setTransaction(transaction);
                if (management.getReturnDate() == null) {
                    log.debug("Management entity passed checking for execute management(return book) where management id equals {} ", management.getId());
                    management.setReturnDate(Timestamp.valueOf(LocalDateTime.now()));
                    bookService.updateBookInfo(bookInfo);
                    managementDao.update(management);
                }
                log.info("Execute transaction for Return book where management id equals {} ", management.getId());
            } catch (DaoException e) {
                log.warn("Can't return book  where management id equals {}", management.getId(), e);
                throw new ServiceException("Can't insert transaction ", e);
            }
        }
    /**
     * Method, provides a list of active and inactive managements in the range of rows in the table
     * (Active managements - the admin has not yet returned the book to the bookInfo)
     *
     * @param start    - the row from which you must begin
     * @param end      - the row from which you must finish
     * @param isActive - active/inactive management state
     */
    public List<Management> getListManagement(int start, int end, boolean isActive) throws ServiceException {
            try {
                log.debug("Get list management by active/inactive  range {} to {} ", start, end);
                ManagementDao managementDao = new MySqlManagementDao();
                List<Management> list = managementDao.getListManagement(start, end, isActive);
                for (Management management : list) {
                    fillManagement(management);
                }
                return list;
            } catch (DaoException e) {
                log.warn("Can't get list management by active/inactive  range {} to {} ", start, end, e);
                throw new ServiceException("Can't get list management", e);
            }
        }
    /**
     * Method, provides the number of  active/inactive managements
     *
     * @param isActive - active/inactive managements
     * @return Return count of active/inactive managements
     */
    public int getManagementCount(boolean isActive) throws ServiceException {
            try {
                ManagementDao managementDao = new MySqlManagementDao();
                return managementDao.getManagementCount(isActive);
            } catch (DaoException e) {
                log.warn("Can't get count management by activity where activity equals {}", isActive, e);
                throw new ServiceException("can't get count book", e);
            }
        }
    /**
     * Method, fill the transaction entity of the data associated with transaction
     *
     * @param management - entity
     */
    private void fillManagement(Management management) throws ServiceException {
        TransactionService transactionService = new TransactionService();
        CustomerService customerService = new CustomerService();
        Customer customer;
        if (management != null) {
                try {
                    log.debug("Fill book with information");

                    TransactionDao transactionDao = new MySqlTransactionDao();
                    customer = customerService.findCustomerByManagement(management);
                    Transaction transaction = transactionDao.findByManagement(management);
                    transactionService.fillTransaction(transaction);
                    transaction.setCustomer(customer);
                    management.setTransaction(transaction);
                } catch (DaoException e) {
                    log.warn("Can't fill book with information ", e);
                }
            }
        }}



