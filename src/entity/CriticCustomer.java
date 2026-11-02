package entity;

/** Critic customer: twelve-point waiting loss, full price, and Pasta preference. */
public class CriticCustomer extends Customer {
    public CriticCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    protected int getSatisfactionLoss() {
        return 12;
    }

    @Override
    public String getTypeLabel() {
        return "Critic";
    }

    @Override
    protected MenuItem selectItem(Menu menu) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != null && "Pasta".equals(item.getName())) {
                return item;
            }
        }
        // Preserve the shared contract for custom menus without Pasta.
        return super.selectItem(menu);
    }
}
