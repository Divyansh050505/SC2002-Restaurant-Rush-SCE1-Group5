package entity;

/**
 * Host behaves exactly like a Waiter but operates as a separate staff member
 * with their own independent action state per turn.
 */
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


    public ActionResult takeOrder(Customer customer, int orderNo, Menu menu) {
        if (hasActed()) {
            return ActionResult.failure("The Host has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("Customer #" + customer.getCustomerId() + " is not ready to order.");
        }

        MenuItem chosenItem = customer.chooseItem(menu);
        Order newOrder = new Order(orderNo, customer, chosenItem);
        customer.placeOrder(newOrder); // Links the order to the customer

        markActed();
        return ActionResult.success("Host took order for " + chosenItem.getName() + " from Customer #" + customer.getCustomerId() + ".");
    }


    public ActionResult serve(Order order, int currentTurn) {
        if (hasActed()) {
            return ActionResult.failure("The Host has already acted this turn.");
        }
        if (order.getStatus() != OrderStatus.READY) {
            return ActionResult.failure("Order #" + order.getOrderNo() + " is not ready to be served.");
        }

        order.markServed();
        order.getCustomer().markServed(currentTurn);

        markActed();
        return ActionResult.success("Host served " + order.getItem().getName() + " to Customer #" + order.getCustomer().getCustomerId() + ".");
    }
}
