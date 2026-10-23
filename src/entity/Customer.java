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
        // A customer finishes eating if it's at least one turn *after* they were served.
        if (this.status == CustomerStatus.EATING && currentTurn > this.servedTurn) {
            this.status = CustomerStatus.READY_TO_PAY;
        }
    }

    public void leave() {
        // insert implementation here
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void markServed(int currentTurn) {
        this.servedTurn = currentTurn;
        this.status = CustomerStatus.EATING;
    }

    public Table getTable() {
        return table;
    }

    public void seatAt(Table table) {
        this.table = table;
        this.status = CustomerStatus.SEATED;
    }

    public Order getOrder() {
        return order;
    }

    public void placeOrder(Order order) {
        this.order = order;
        this.status = CustomerStatus.ORDERED;
    }

    public void markPaid() {
        this.status = CustomerStatus.PAID;
    }
}
