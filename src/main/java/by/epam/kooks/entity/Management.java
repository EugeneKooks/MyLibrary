package by.epam.kooks.entity;

import java.sql.Timestamp;

/**
 * @author Eugene Kooks
 */
public class Management extends BaseEntity {
    private Transaction transaction;
    private Timestamp returnDate;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

}
