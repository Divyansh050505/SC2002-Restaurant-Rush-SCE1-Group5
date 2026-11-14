package entity;


public class Waiter extends Staff {

    
    public Waiter() {
        super();
    }

    
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

    
    public ActionResult takeOrder(Customer customer, int orderNo, Menu menu, HappyHour happyHour) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("Customer #" + customer.getCustomerId() + " is not ready to order.");
        }

        MenuItem chosenItem = customer.chooseItem(menu);
        int billCents = customer.calculateBillCents(chosenItem.getPriceCents());
        boolean happyHourPrice = happyHour.isActive();
        if (happyHourPrice) {
            billCents = happyHour.applyDiscount(billCents);
        }
        Order newOrder = new Order(orderNo, customer, chosenItem, billCents, happyHourPrice);
        customer.placeOrder(newOrder);

        markActed();
        String message = "Waiter took order for " + chosenItem.getName() + " from Customer #" + customer.getCustomerId() + ".";
        if (happyHourPrice) {
            message += " Happy Hour price locked.";
        }
        return ActionResult.success(message);
    }

    
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
