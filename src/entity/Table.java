package entity;

/**
 * Stage One table skeleton.
 */
public class Table {
    private int tableNo;
    private Customer customer;

    public Table(int tableNo) {
    
        this.tableNo=tableNo;

        this.customer=null;
    }

    public boolean isFree() {
        

        if (this.customer==null) {

            return true;
        }
        else {

            return false;
        }
    }

    public void assign(Customer customer) {

        this.customer=customer;
    }

    public void release() {

        this.customer=null;

    }

    public int getTableNo() {

        return this.tableNo;
    }

    public Customer getCustomer() {

        return this.customer;
    }
}
