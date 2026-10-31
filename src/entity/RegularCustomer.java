package entity;

/** Regular customer: seeded food choice, eight-point waiting loss, full price. */
public class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    protected int getSatisfactionLoss() {
        return 8;
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
