package entity;

public class Host extends Staff {

    public Host() {
        super();
    }

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
