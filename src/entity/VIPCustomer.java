package entity;

/** VIP customer: seeded food choice, five-point waiting loss, ten percent off. */
public class VIPCustomer extends Customer {
    public VIPCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    protected int getSatisfactionLoss() {
        return 5;
    }

    @Override
    public String getTypeLabel() {
        return "VIP";
    }

    @Override
    protected int getDiscountPercent() {
        return 10;
    }
}
