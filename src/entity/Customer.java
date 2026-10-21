package entity;

/**
 * Stage One regular customer skeleton.
 */
public class Customer {
    private static final int SATISFACTION_LOSS = 8;
    private int customerId;
    private int arrivalTurn;
    private int satisfaction = 100;
    private CustomerStatus status;
    private int servedTurn;
    private Table table;
    private Order order;

    public Customer(int customerId, int arrivalTurn) {
        // insert implementation here
    }

    public MenuItem chooseItem(Menu menu) {
        // insert implementation here
        return null;
    }

    public void loseSatisfaction() {
        // insert implementation here
    }

    public boolean isWaitingForService() {
        // insert implementation here
        return false;
    }

    public void finishEating(int currentTurn) {
        // insert implementation here
    }

    public void leave() {
        // insert implementation here
    }

    public int getCustomerId() {
        // insert implementation here
        return 0;
    }

    public int getSatisfaction() {
        // insert implementation here
        return 0;
    }

    public CustomerStatus getStatus() {
        // insert implementation here
        return null;
    }

    public void markServed(int currentTurn) {
        // insert implementation here
    }

    public Table getTable() {
        // insert implementation here
        return null;
    }

    public void seatAt(Table table) {
        // insert implementation here
    }

    public Order getOrder() {
        // insert implementation here
        return null;
    }

    public void placeOrder(Order order) {
        // insert implementation here
    }

    public void markPaid() {
        // insert implementation here
    }
}
