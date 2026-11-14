package entity;

/**
 * Host acts specifically to seat at most one queued customer per turn.
 * This runs in addition to the Waiter's task.
 */
public class Host extends Staff {

    /**
     * Constructs a Host. Calls the parent Staff constructor.
     */
    public Host() {
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
            return ActionResult.failure("The Host has already acted this turn.");
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
        return ActionResult.success("Host seated Customer #" + customer.getCustomerId() + " at Table " + table.getTableNo() + ".");
    }
}
