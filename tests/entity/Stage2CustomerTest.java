package entity;

import java.util.List;
import java.util.function.BiFunction;

/** Shared customer contracts and real Cashier/Order/Table integration. */
public class Stage2CustomerTest {
    private static int checks;
    private static final List<BiFunction<Integer, Integer, Customer>> TYPES = List.of(
            RegularCustomer::new, VIPCustomer::new, CriticCustomer::new);

    public static void main(String[] args) {
        String previous = System.getProperty("restaurant.rush.seed");
        try {
            System.setProperty("restaurant.rush.seed", "482190");
            sharedLifecycle();
            abandonment();
            choices();
            pricingAndCollection();
        } finally {
            if (previous == null) System.clearProperty("restaurant.rush.seed");
            else System.setProperty("restaurant.rush.seed", previous);
        }
        System.out.println("PASS: " + checks + " Stage Two customer/payment checks.");
    }

    private static void sharedLifecycle() {
        int[] losses = {8, 5, 12};
        String[] labels = {"Regular", "VIP", "Critic"};
        for (int i = 0; i < TYPES.size(); i++) {
            BiFunction<Integer, Integer, Customer> type = TYPES.get(i);
            expect(IllegalArgumentException.class, () -> type.apply(0, 1));
            expect(IllegalArgumentException.class, () -> type.apply(1, 0));
            Customer c = type.apply(1, 1);
            check(c.getTypeLabel().equals(labels[i]), "customer-supplied type label");
            check(c.getSatisfaction() == 100 && c.getStatus() == CustomerStatus.WAITING,
                    "all arrivals start waiting at 100");
            expect(IllegalArgumentException.class, () -> c.chooseItem(null));
            expect(IllegalStateException.class, () -> c.chooseItem(new Menu()));
            expect(IllegalStateException.class, c::markPaid);
            expect(IllegalStateException.class, c::leave);
            c.loseSatisfaction();
            Table table = new Table(1);
            c.seatAt(table);
            c.loseSatisfaction();
            Order order = new Order(1, c, c.chooseItem(new Menu()));
            c.placeOrder(order);
            c.loseSatisfaction();
            int recorded = 100 - 3 * losses[i];
            check(c.getSatisfaction() == recorded, "queued, seated and ordered turns lose the subtype amount");
            expect(IllegalStateException.class, () -> c.chooseItem(new Menu()));
            Cashier cashier = new Cashier();
            check(cashier.collectPayment(c) == 0 && !cashier.hasActed(), "no payment before service");
            while (!order.isReady()) order.advanceOneUnit();
            c.markServed(4);
            c.finishEating(4);
            c.loseSatisfaction();
            check(c.getStatus() == CustomerStatus.EATING && c.getSatisfaction() == recorded,
                    "service turn cannot finish eating and freezes satisfaction");
            check(cashier.collectPayment(c) == 0 && !cashier.hasActed(), "no payment while eating");
            c.finishEating(5);
            c.loseSatisfaction();
            check(c.getStatus() == CustomerStatus.READY_TO_PAY && !table.isFree(),
                    "all types finish after one full turn and retain the table");
            c.markPaid();
            c.loseSatisfaction();
            check(c.getStatus() == CustomerStatus.PAID && order.getStatus() == OrderStatus.PAID
                    && table.isFree() && c.getTable() == null && c.getSatisfaction() == recorded,
                    "payment frees the table and preserves paid satisfaction");
            expect(IllegalStateException.class, c::markPaid);
        }
    }

    private static void abandonment() {
        int[] losses = {8, 5, 12};
        int[] finalLoss = {13, 20, 9}; // VIP's entity test deliberately exceeds the game length.
        for (int i = 0; i < TYPES.size(); i++) {
            for (int phase = 0; phase < 4; phase++) {
                Customer c = TYPES.get(i).apply(1, 1);
                Table table = null;
                Order order = null;
                if (phase > 0) {
                    table = new Table(1);
                    c.seatAt(table);
                }
                if (phase > 1) {
                    order = new Order(1, c, new MenuItem("Pasta", 1800, 2));
                    c.placeOrder(order);
                    if (phase == 3) {
                        order.advanceOneUnit();
                        order.advanceOneUnit();
                    }
                }
                for (int turn = 1; turn < finalLoss[i]; turn++) c.loseSatisfaction();
                check(c.getSatisfaction() == 100 - (finalLoss[i] - 1) * losses[i]
                        && c.isWaitingForService(), "no early abandonment");
                c.loseSatisfaction();
                check(c.getStatus() == CustomerStatus.LEFT && c.getSatisfaction() == 0
                        && c.getTable() == null, "subtype loss reaches zero and abandons");
                if (table != null) check(table.isFree(), "abandonment releases the real table");
                if (order != null) check(order.getStatus() == OrderStatus.CANCELLED, "preparing/ready orders cancel");
                c.leave();
                c.loseSatisfaction();
                check(c.getStatus() == CustomerStatus.LEFT && c.getSatisfaction() == 0, "departure is idempotent");
                expect(IllegalStateException.class, c::markPaid);
            }
        }
    }

    private static void choices() {
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        Menu reordered = new TestMenu(pasta, burger, salad);
        for (int id = 1; id <= 6; id++) {
            Customer critic = seated(CriticCustomer::new, id);
            check(critic.chooseItem(reordered) == pasta && critic.chooseItem(reordered) == pasta,
                    "Critic chooses the actual Pasta entry regardless of ID or menu order");
            check(critic.getOrder() == null && critic.getStatus() == CustomerStatus.SEATED,
                    "choice does not place an order");
        }
        Customer critic = seated(CriticCustomer::new, 1);
        check(critic.chooseItem(new TestMenu(salad)) == salad, "Critic supports menus without Pasta");
        expect(IllegalArgumentException.class, () -> critic.chooseItem(new TestMenu()));
        expect(IllegalStateException.class, () -> critic.chooseItem(new TestMenu((MenuItem) null)));

        // The same default food slots apply to Regular/VIP, irrespective of
        // which menu is queried first or whether Critics choose before them.
        Menu regularMenu = new Menu();
        Menu vipMenu = new Menu();
        check(seated(CriticCustomer::new, 1).chooseItem(vipMenu).getName().equals("Pasta"),
                "a Critic may choose before the default food plan is initialized");
        for (int id = 6; id >= 1; id--) {
            MenuItem regular = seated(RegularCustomer::new, id).chooseItem(regularMenu);
            MenuItem vip = seated(VIPCustomer::new, id).chooseItem(vipMenu);
            check(regular.getName().equals(vip.getName()), "VIP inherits the existing default food rule");
        }
    }

    private static void pricingAndCollection() {
        int[] prices = {900, 1500, 1800};
        int[][] bills = {{900, 1500, 1800}, {810, 1350, 1620}, {900, 1500, 1800}};
        for (int i = 0; i < TYPES.size(); i++) {
            Customer c = TYPES.get(i).apply(1, 1);
            expect(IllegalArgumentException.class, () -> c.calculateBillCents(-1));
            check(c.calculateBillCents(0) == 0 && c.getStatus() == CustomerStatus.WAITING,
                    "pricing is pure and zero remains zero");
            for (int j = 0; j < prices.length; j++) {
                check(c.calculateBillCents(prices[j]) == bills[i][j], "menu price/discount in cents");
                Customer paying = ready(TYPES.get(i), prices[j]);
                Table table = paying.getTable();
                Cashier cashier = new Cashier();
                check(cashier.collectPayment(paying) == bills[i][j], "Cashier collects subtype bill");
                check(cashier.hasActed() && paying.getStatus() == CustomerStatus.PAID
                        && paying.getOrder().getStatus() == OrderStatus.PAID && table.isFree(),
                        "real transaction completes all state changes");
                Customer next = ready(VIPCustomer::new, 1500);
                check(cashier.collectPayment(next) == 0 && next.getStatus() == CustomerStatus.READY_TO_PAY,
                        "Cashier can only collect once per turn");
                cashier.resetTurn();
                check(cashier.collectPayment(paying) == 0 && !cashier.hasActed(), "paid orders cannot pay twice");
                check(cashier.collectPayment(next) == 1350, "Cashier can collect next customer after reset");
            }
        }
        Customer vip = new VIPCustomer(1, 1);
        check(vip.calculateBillCents(5) == 5 && vip.calculateBillCents(15) == 14,
                "half cents round up");
        check(vip.calculateBillCents(19) == 17 && vip.calculateBillCents(Integer.MAX_VALUE) == 1932735282,
                "rounding down and multiplication without int overflow");
    }

    private static Customer seated(BiFunction<Integer, Integer, Customer> type, int id) {
        Customer c = type.apply(id, 1);
        c.seatAt(new Table(id));
        return c;
    }

    private static Customer ready(BiFunction<Integer, Integer, Customer> type, int price) {
        Customer c = seated(type, 1);
        Order o = new Order(1, c, new MenuItem("Test dish", price, 1));
        c.placeOrder(o);
        o.advanceOneUnit();
        c.markServed(3);
        c.finishEating(4);
        return c;
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable e) {
            check(type.isInstance(e) && e.getMessage() != null && !e.getMessage().isBlank(), "descriptive " + type.getSimpleName());
            return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static class TestMenu extends Menu {
        private final MenuItem[] entries;
        TestMenu(MenuItem... entries) { this.entries = entries; }
        @Override public int getItemCount() { return entries.length; }
        @Override public MenuItem getItem(int index) { return entries[index]; }
    }
}
