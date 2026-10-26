package entity;

import java.util.Random;

/**
 * A Stage One regular customer and their service lifecycle.
 *
 * <p>Invalid arguments and actions are rejected with descriptive
 * {@link IllegalArgumentException}s or {@link IllegalStateException}s, leaving
 * customer fields unchanged on failure. Staff can report these through {@link ActionResult};
 * this entity does not print messages or consume a staff task.</p>
 *
 * <p>State transitions establish the private table and order references.
 * Later actions rely on those references but recheck table occupancy and order
 * status because other objects can change them.</p>
 */
public class Customer {
    private static final int SATISFACTION_LOSS = 8;
    private final int customerId;
    private final int arrivalTurn;
    private int satisfaction = 100;
    private CustomerStatus status;
    private int servedTurn = -1;
    private Table table;
    private Order order;

    /** Creates an unserved, waiting customer with satisfaction 100. */
    public Customer(int customerId, int arrivalTurn) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        if (arrivalTurn <= 0) {
            throw new IllegalArgumentException("Arrival turn must be positive.");
        }
        this.customerId = customerId;
        this.arrivalTurn = arrivalTurn;
        this.status = CustomerStatus.WAITING;
    }

    /**
     * Chooses from the menu only while seated and without an order.
     * The seed is 2002 + 31 * customerId + arrivalTurn. For an unchanged menu,
     * retries and repeated games give the same customer the same choice,
     * independently of the order in which customers are asked.
     */
    public MenuItem chooseItem(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("A menu is required to choose food.");
        }
        if (status != CustomerStatus.SEATED) {
            throw new IllegalStateException("Only a seated customer without an order can choose food.");
        }
        int itemCount = menu.getItemCount();
        if (itemCount <= 0) {
            throw new IllegalArgumentException("Cannot choose food from an empty menu.");
        }

        final long seed = 2002L + 31L * customerId + arrivalTurn;
        int index = new Random(seed).nextInt(itemCount);
        MenuItem item;
        try {
            item = menu.getItem(index);
        } catch (IndexOutOfBoundsException exception) {
            throw new IllegalStateException("The menu does not contain the selected item.", exception);
        }
        if (item == null) {
            throw new IllegalStateException("The selected menu item is missing.");
        }
        return item;
    }

    /**
     * Applies one end-of-turn loss to an unserved customer, with a floor of zero.
     * Reaching zero also completes abandonment. Served and terminal customers
     * are unchanged, so their satisfaction remains available for reporting.
     */
    public void loseSatisfaction() {
        if (!isWaitingForService()) {
            return;
        }
        int previousSatisfaction = satisfaction;
        satisfaction = Math.max(0, satisfaction - SATISFACTION_LOSS);
        if (satisfaction == 0) {
            try {
                leave();
            } catch (RuntimeException exception) {
                // Preserve the customer's state if cancellation or table release fails.
                satisfaction = previousSatisfaction;
                throw exception;
            }
        }
    }

    public boolean isWaitingForService() {
        return status == CustomerStatus.WAITING
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED;
    }

    /**
     * Finishes eating only on a turn after service; other states are unchanged.
     * The controller processes payment before this step so newly ready customers
     * cannot pay until the following turn.
     */
    public void finishEating(int currentTurn) {
        if (currentTurn < arrivalTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the customer's arrival.");
        }
        if (status != CustomerStatus.EATING) {
            return;
        }
        if (currentTurn < servedTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the served turn.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException("A customer can only eat their own served order.");
        }
        if (currentTurn > servedTurn) {
            status = CustomerStatus.READY_TO_PAY;
        }
    }

    /** Completes abandonment at zero satisfaction; repeated departure is harmless. */
    public void leave() {
        if (status == CustomerStatus.LEFT) {
            return;
        }
        if (!isWaitingForService() || satisfaction != 0) {
            throw new IllegalStateException("Only an unserved customer with zero satisfaction can leave.");
        }
        if (status != CustomerStatus.WAITING && table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order != null) {
            OrderStatus orderStatus = order.getStatus();
            if (orderStatus != OrderStatus.PREPARING
                    && orderStatus != OrderStatus.READY
                    && orderStatus != OrderStatus.CANCELLED) {
                throw new IllegalStateException("A served or paid order cannot be abandoned.");
            }
            if (orderStatus != OrderStatus.CANCELLED) {
                order.cancel();
            }
        }
        if (table != null) {
            table.release();
        }
        table = null;
        status = CustomerStatus.LEFT;
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

    /**
     * Serves a READY order and starts eating. An order already marked SERVED by
     * the Waiter is accepted too; the customer must still be awaiting service.
     */
    public void markServed(int currentTurn) {
        if (currentTurn < arrivalTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the customer's arrival.");
        }
        if (status != CustomerStatus.ORDERED) {
            throw new IllegalStateException("Only a customer awaiting their order can be served.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.READY && orderStatus != OrderStatus.SERVED) {
            throw new IllegalStateException("The customer's order must be ready before it can be served.");
        }

        if (orderStatus == OrderStatus.READY) {
            order.markServed();
        }
        servedTurn = currentTurn;
        status = CustomerStatus.EATING;
    }

    public Table getTable() {
        return table;
    }

    /** Seats a waiting customer and keeps both sides of the table association in sync. */
    public void seatAt(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("A table is required to seat a customer.");
        }
        if (status != CustomerStatus.WAITING) {
            throw new IllegalStateException("Only a waiting customer without a table or order can be seated.");
        }
        Customer occupant = table.getCustomer();
        if (occupant != this && (occupant != null || !table.isFree())) {
            throw new IllegalStateException("The table is already occupied.");
        }

        // The Waiter may already have assigned this customer to the table.
        if (occupant != this) {
            table.assign(this);
        }
        this.table = table;
        status = CustomerStatus.SEATED;
    }

    public Order getOrder() {
        return order;
    }

    /** Attaches exactly one new order belonging to this seated customer. */
    public void placeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("An order is required.");
        }
        if (status != CustomerStatus.SEATED) {
            throw new IllegalStateException("Only a seated customer without an order can place one.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order.getCustomer() != this) {
            throw new IllegalArgumentException("The order belongs to a different customer.");
        }
        if (order.getItem() == null || order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("A new order must contain a menu item and be PREPARING.");
        }

        this.order = order;
        status = CustomerStatus.ORDERED;
    }

    /**
     * Marks the customer and order paid and releases the table. The order and
     * satisfaction are retained for revenue and satisfaction reporting.
     * Cashier/controller code is responsible for the payment turn and revenue.
     */
    public void markPaid() {
        if (status != CustomerStatus.READY_TO_PAY) {
            throw new IllegalStateException("Only a customer who has finished eating can pay.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.SERVED && orderStatus != OrderStatus.PAID) {
            throw new IllegalStateException("Only a served order can be paid for.");
        }

        // The Cashier may already have marked the order paid.
        if (orderStatus == OrderStatus.SERVED) {
            order.markPaid();
        }
        table.release();
        table = null;
        status = CustomerStatus.PAID;
    }
}
