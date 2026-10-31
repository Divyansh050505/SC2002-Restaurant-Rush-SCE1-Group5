package entity;

/**
 * Collects the bill supplied by the customer's pricing behaviour.
 */
public class Cashier extends Staff {

    /**
     * Constructs a Cashier. Calls the parent Staff constructor.
     */
    public Cashier() {
        super();
    }

    /**
     * Collects payment from a customer who is ready to pay.
     * This method handles the transaction itself. The controller is responsible
     * for finding the right customer and updating the total revenue.
     *
     * @param customer The customer who is paying.
     * @return The amount paid in cents, or 0 if payment could not be collected.
     */
    public int collectPayment(Customer customer) {
        if (hasActed() || customer.getStatus() != CustomerStatus.READY_TO_PAY) {
            return 0; // Cannot collect payment if cashier already acted or customer is not ready
        }

        Order order = customer.getOrder();
        if (order == null) {
            return 0; // Should not happen if customer is READY_TO_PAY
        }

        int amountPaid = customer.calculateBillCents(order.getItem().getPriceCents());

        // Marks the customer and order PAID and frees the table
        customer.markPaid();

        markActed(); // The cashier has now performed their action for the turn

        return amountPaid;
    }
}
