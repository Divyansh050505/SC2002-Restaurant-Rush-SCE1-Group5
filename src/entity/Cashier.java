package entity;


public class Cashier extends Staff {

    
    public Cashier() {
        super();
    }

   
    public int collectPayment(Customer customer) {
        if (hasActed() || customer.getStatus() != CustomerStatus.READY_TO_PAY) {
            return 0; // Cannot collect payment if cashier already acted or customer is not ready
        }

        Order order = customer.getOrder();
        if (order == null) {
            return 0; 
        }

        
        int amountPaid = order.getBillCents();

        
        customer.markPaid();

        markActed(); 

        return amountPaid;
    }
}
