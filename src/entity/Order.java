package entity;

/**
 * Stage One order skeleton.
 */
public class Order {
    private int orderNo;
    private Customer customer;
    private MenuItem item;
    private int unitsDone = 0;
    private OrderStatus status;

    public Order(int orderNo, Customer customer, MenuItem item) {
        // insert implementation here
    }

    public void advanceOneUnit() {
        // insert implementation here
    }

    public int getRemainingUnits() {
        // insert implementation here
        return 0;
    }

    public boolean isReady() {
        // insert implementation here
        return false;
    }

    public void markServed() {
        // insert implementation here
    }

    public void markPaid() {
        // insert implementation here
    }

    public void cancel() {
        // insert implementation here
    }

    public int getOrderNo() {
        // insert implementation here
        return 0;
    }

    public Customer getCustomer() {
        // insert implementation here
        return null;
    }

    public MenuItem getItem() {
        // insert implementation here
        return null;
    }

    public OrderStatus getStatus() {
        // insert implementation here
        return null;
    }
}
