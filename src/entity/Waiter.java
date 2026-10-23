package entity;

/**
 * Stage One waiter skeleton.
 */
public class Waiter extends Staff {

    /**
     * Constructs a Waiter. Calls the parent Staff constructor.
     */
    public Waiter() {
        super();
    }

    /**
     * Seats a waiting customer at a free table.
     *
     * @param customer The customer to be seated.
     * @param table    The table to assign to the customer.
     * @return An ActionResult indicating success or failure.
     */
    public ActionResult seat(Customer customer, Table table) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.WAITING) {
            return ActionResult.failure("Customer #" + customer.getCustomerId() + " is not waiting to be seated.");
        }
        if (table == null || !table.isFree()) {
            return ActionResult.failure("There are no free tables available.");
        }

        table.assign(customer);
        customer.seatAt(table);
        markActed();
        return ActionResult.success("Waiter seated Customer #" + customer.getCustomerId() + " at Table " + table.getTableNo() + ".");
    }

    /**
     * Takes an order from a seated customer. The customer chooses an item from the menu.
     *
     * @param customer The customer who is ordering.
     * @param orderNo  The new order number for this order.
     * @param menu     The menu from which the customer will choose.
     * @return An ActionResult indicating success or failure.
     */
    public ActionResult takeOrder(Customer customer, int orderNo, Menu menu) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("Customer #" + customer.getCustomerId() + " is not ready to order.");
        }

        MenuItem chosenItem = customer.chooseItem(menu);
        Order newOrder = new Order(orderNo, customer, chosenItem);
        customer.placeOrder(newOrder); // Links the order to the customer

        markActed();
        return ActionResult.success("Waiter took order for " + chosenItem.getName() + " from Customer #" + customer.getCustomerId() + ".");
    }

    /**
     * Serves a prepared order to the correct customer.
     *
     * @param order      The order to be served.
     * @param currentTurn The current turn number, to record when the customer starts eating.
     * @return An ActionResult indicating success or failure.
     */
    public ActionResult serve(Order order, int currentTurn) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (order.getStatus() != OrderStatus.READY) {
            return ActionResult.failure("Order #" + order.getOrderNo() + " is not ready to be served.");
        }

        order.markServed();
        order.getCustomer().markServed(currentTurn);

        markActed();
        return ActionResult.success("Waiter served " + order.getItem().getName() + " to Customer #" + order.getCustomer().getCustomerId() + ".");
    }
}
