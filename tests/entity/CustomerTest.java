package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Dependency-free Customer regression tests. Table, Order and Menu test doubles
 * supply the behaviour that is still a skeleton in the production classes.
 *
 * <p>From the repository root:</p>
 * <pre>
 * javac --release 17 -d out/customer-tests src/entity/*.java tests/entity/CustomerTest.java
 * java -cp out/customer-tests entity.CustomerTest
 * </pre>
 */
public class CustomerTest {
    private static int checks;

    public static void main(String[] args) {
        constructionAndInvalidActions();
        seededFoodChoices();
        seatingAndOrderingPreconditions();
        pastaLifecycle();
        abandonment();
        staffMayUpdateAssociationsFirst();
        dependencyFailuresCanBeRetried();
        reassignedTableIsProtected();
        System.out.println("Customer tests passed (" + checks + " checks).");
    }

    private static void constructionAndInvalidActions() {
        expect(IllegalArgumentException.class, () -> new Customer(0, 1));
        expect(IllegalArgumentException.class, () -> new Customer(-1, 1));
        expect(IllegalArgumentException.class, () -> new Customer(1, 0));
        expect(IllegalArgumentException.class, () -> new Customer(1, -1));
        Customer customer = new Customer(1, 1);
        check(customer.getCustomerId() == 1, "The constructor retains the customer ID.");
        check(customer.getSatisfaction() == 100, "Arrivals start with satisfaction 100.");
        check(customer.getStatus() == CustomerStatus.WAITING, "Arrivals start waiting.");
        check(customer.getTable() == null && customer.getOrder() == null, "Arrivals have no table or order.");
        check(customer.isWaitingForService(), "A queued customer is waiting for service.");
        unchanged(customer, IllegalArgumentException.class, () -> customer.seatAt(null));
        unchanged(customer, IllegalArgumentException.class, () -> customer.chooseItem(null));
        unchanged(customer, IllegalStateException.class, () -> customer.chooseItem(new TestMenu(new MenuItem("Salad", 900, 1))));
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(1));
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        unchanged(customer, IllegalStateException.class, customer::leave);
        unchanged(customer, IllegalArgumentException.class, () -> customer.finishEating(0));
        customer.finishEating(1);
        check(customer.getStatus() == CustomerStatus.WAITING, "An unserved customer cannot finish eating.");

        Customer laterArrival = new Customer(2, 4);
        unchanged(laterArrival, IllegalArgumentException.class, () -> laterArrival.markServed(3));
        unchanged(laterArrival, IllegalArgumentException.class, () -> laterArrival.finishEating(3));
    }

    private static void seededFoodChoices() {
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        TestMenu menu = new TestMenu(salad, burger, pasta);
        MenuItem[] firstRun = new MenuItem[6];
        Set<MenuItem> choices = new HashSet<>();
        for (int i = 0; i < firstRun.length; i++) {
            Customer customer = seated(i + 1, 1 + 3 * i);
            firstRun[i] = customer.chooseItem(menu);
            choices.add(firstRun[i]);
            check(firstRun[i] == salad || firstRun[i] == burger || firstRun[i] == pasta,
                    "A choice must be an existing menu item.");
            check(customer.chooseItem(menu) == firstRun[i], "Retries must not reroll the food choice.");
            check(customer.getOrder() == null && customer.getStatus() == CustomerStatus.SEATED,
                    "Choosing food does not place an order.");
        }
        for (int i = firstRun.length - 1; i >= 0; i--) {
            Customer replay = seated(i + 1, 1 + 3 * i);
            check(replay.chooseItem(menu) == firstRun[i], "Replays are independent of customer selection order.");
        }
        check(choices.size() > 1, "The six scheduled customers do not all choose the same item.");

        Customer customer = seated(1, 1);
        unchanged(customer, IllegalArgumentException.class, () -> customer.chooseItem(null));
        unchanged(customer, IllegalArgumentException.class, () -> customer.chooseItem(new TestMenu()));
        unchanged(customer, IllegalStateException.class, () -> customer.chooseItem(new TestMenu((MenuItem) null)));
        TestMenu broken = new TestMenu(salad) {
            @Override
            public MenuItem getItem(int index) {
                throw new IndexOutOfBoundsException("Missing item.");
            }
        };
        Throwable error = expect(IllegalStateException.class, () -> customer.chooseItem(broken));
        check(error.getCause() instanceof IndexOutOfBoundsException, "Menu lookup failures retain their cause.");
        check(customer.chooseItem(menu) == firstRun[0], "Failed choices do not change the seed.");
        check(customer.chooseItem(new TestMenu(pasta)) == pasta, "A one-item menu is supported.");

        String previousSeed = System.getProperty("restaurant.rush.seed");
        try {
            System.setProperty("restaurant.rush.seed", "48219");
            TestMenu firstGame = new TestMenu(salad, burger, pasta);
            TestMenu replay = new TestMenu(salad, burger, pasta);
            MenuItem[] recorded = new MenuItem[6];
            for (int i = 0; i < recorded.length; i++) {
                recorded[i] = seated(i + 1, 1 + 3 * i).chooseItem(firstGame);
            }
            for (int i = recorded.length - 1; i >= 0; i--) {
                check(seated(i + 1, 1 + 3 * i).chooseItem(replay) == recorded[i],
                        "The same seed reproduces every choice in a fresh game, independently of ordering priority.");
            }

            Set<java.util.List<MenuItem>> seededPlans = new HashSet<>();
            for (int seed = 1; seed <= 20; seed++) {
                System.setProperty("restaurant.rush.seed", Integer.toString(seed));
                TestMenu seededMenu = new TestMenu(salad, burger, pasta);
                java.util.ArrayList<MenuItem> plan = new java.util.ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    plan.add(seated(i + 1, 1 + 3 * i).chooseItem(seededMenu));
                }
                check(new HashSet<>(plan).size() == 3, "Seeded games still include all three menu items.");
                seededPlans.add(plan);
            }
            check(seededPlans.size() > 1, "Different seeds allow different food plans.");
        } finally {
            if (previousSeed == null) {
                System.clearProperty("restaurant.rush.seed");
            } else {
                System.setProperty("restaurant.rush.seed", previousSeed);
            }
        }
    }

    private static void seatingAndOrderingPreconditions() {
        Customer customer = new Customer(1, 1);
        TestTable occupied = new TestTable();
        Customer other = new Customer(2, 4);
        other.seatAt(occupied);
        unchanged(customer, IllegalStateException.class, () -> customer.seatAt(occupied));
        check(occupied.getCustomer() == other, "Rejected seating preserves the existing occupant.");

        TestOrder order = new TestOrder(customer, new MenuItem("Salad", 900, 1), 1);
        unchanged(customer, IllegalStateException.class, () -> customer.placeOrder(order));
        TestTable table = new TestTable();
        customer.seatAt(table);
        check(customer.getTable() == table && table.getCustomer() == customer, "Seating updates both table links.");
        check(customer.getStatus() == CustomerStatus.SEATED, "Seating changes customer status.");
        check(customer.isWaitingForService(), "A seated customer still waits for service.");
        TestTable secondTable = new TestTable();
        unchanged(customer, IllegalStateException.class, () -> customer.seatAt(secondTable));
        check(secondTable.isFree(), "Rejected reseating does not occupy another table.");
        unchanged(customer, IllegalArgumentException.class, () -> customer.placeOrder(null));
        unchanged(customer, IllegalArgumentException.class,
                () -> customer.placeOrder(new TestOrder(other, order.getItem(), 1)));
        unchanged(customer, IllegalArgumentException.class,
                () -> customer.placeOrder(new TestOrder(customer, null, 1)));
        TestOrder readyOrder = new TestOrder(customer, order.getItem(), 1);
        readyOrder.advanceOneUnit();
        unchanged(customer, IllegalArgumentException.class, () -> customer.placeOrder(readyOrder));

        customer.placeOrder(order);
        check(customer.getOrder() == order && customer.getStatus() == CustomerStatus.ORDERED,
                "Placing an order updates the order link and customer status together.");
        check(customer.isWaitingForService(), "A customer with an unserved order still waits.");
        unchanged(customer, IllegalStateException.class, () -> customer.placeOrder(order));
        unchanged(customer, IllegalStateException.class, () -> customer.chooseItem(new TestMenu(order.getItem())));
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(2));
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        unchanged(customer, IllegalStateException.class, customer::leave);
        check(order.getStatus() == OrderStatus.PREPARING && !table.isFree(),
                "Rejected actions preserve the order and occupied table.");
    }

    private static void pastaLifecycle() {
        Customer customer = seated(1, 1);
        TestTable table = (TestTable) customer.getTable();
        customer.loseSatisfaction(); // Turn 1: seated.
        check(customer.getSatisfaction() == 92, "Turn 1 satisfaction matches the assignment example.");
        TestOrder order = new TestOrder(customer, new MenuItem("Pasta", 1800, 2), 2);
        customer.placeOrder(order);
        order.advanceOneUnit();
        customer.loseSatisfaction(); // Turn 2: first preparation unit.
        check(customer.getSatisfaction() == 84, "Turn 2 satisfaction matches the assignment example.");
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(3));
        order.advanceOneUnit();
        customer.loseSatisfaction(); // Turn 3: order becomes READY after the Waiter acted.
        check(customer.getSatisfaction() == 76, "Turn 3 satisfaction matches the assignment example.");
        customer.markServed(4);
        check(order.getStatus() == OrderStatus.SERVED && customer.getStatus() == CustomerStatus.EATING,
                "Service updates both order and customer status.");
        check(!customer.isWaitingForService(), "Eating customers no longer wait for service.");
        customer.finishEating(4);
        check(customer.getStatus() == CustomerStatus.EATING, "Eating cannot finish on the service turn.");
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(4));
        unchanged(customer, IllegalArgumentException.class, () -> customer.finishEating(3));
        customer.loseSatisfaction();
        check(customer.getSatisfaction() == 76, "Eating causes no satisfaction loss.");

        customer.finishEating(5);
        check(customer.getStatus() == CustomerStatus.READY_TO_PAY, "A full later turn finishes eating.");
        check(!table.isFree(), "The table remains occupied while ready to pay.");
        customer.loseSatisfaction();
        check(customer.getSatisfaction() == 76, "Ready-to-pay customers lose no satisfaction.");
        customer.markPaid(); // Turn 6: the controller runs payment before eating each turn.
        check(customer.getStatus() == CustomerStatus.PAID && order.getStatus() == OrderStatus.PAID,
                "Payment updates both customer and order.");
        check(customer.getTable() == null && table.isFree(), "Payment frees both table links.");
        check(customer.getOrder() == order, "The paid order is retained for reporting.");
        customer.loseSatisfaction();
        customer.finishEating(6);
        check(customer.getSatisfaction() == 76 && customer.getStatus() == CustomerStatus.PAID,
                "Paid satisfaction and terminal status remain unchanged.");
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        unchanged(customer, IllegalStateException.class, customer::leave);
        unchanged(customer, IllegalStateException.class, () -> customer.seatAt(new TestTable()));
        check(table.releaseCount == 1, "Payment cannot release the table twice.");
    }

    private static void abandonment() {
        for (int phase = 0; phase < 5; phase++) {
            Customer customer = new Customer(1, 1);
            TestTable table = null;
            TestOrder order = null;
            if (phase >= 1) {
                table = new TestTable();
                customer.seatAt(table);
            }
            if (phase >= 2) {
                order = new TestOrder(customer, new MenuItem("Salad", 900, 1), 1);
                customer.placeOrder(order);
                if (phase == 3) {
                    order.advanceOneUnit();
                } else if (phase == 4) {
                    order.cancel();
                }
            }
            for (int turn = 1; turn <= 12; turn++) {
                customer.loseSatisfaction();
            }
            check(customer.getSatisfaction() == 4, "An unserved customer has four points after 12 losses.");
            customer.loseSatisfaction();
            check(customer.getSatisfaction() == 0 && customer.getStatus() == CustomerStatus.LEFT,
                    "The thirteenth loss clamps to zero and abandons service.");
            check(!customer.isWaitingForService() && customer.getTable() == null,
                    "A departed customer stops waiting and no longer holds a table.");
            if (table != null) {
                check(table.isFree() && table.releaseCount == 1, "Abandonment frees the occupied table once.");
            }
            if (order != null) {
                check(order.getStatus() == OrderStatus.CANCELLED && customer.getOrder() == order,
                        "The unfinished order is cancelled and retained for reporting.");
                check(order.cancelCount == 1, "Already cancelled orders are not cancelled twice.");
            }
            customer.leave();
            customer.loseSatisfaction();
            customer.finishEating(18);
            check(customer.getStatus() == CustomerStatus.LEFT && customer.getSatisfaction() == 0,
                    "Departed customers remain departed without further loss.");
            unchanged(customer, IllegalStateException.class, customer::markPaid);
            unchanged(customer, IllegalStateException.class, () -> customer.markServed(18));
            unchanged(customer, IllegalStateException.class, () -> customer.seatAt(new TestTable()));
        }
    }

    private static void staffMayUpdateAssociationsFirst() {
        Customer customer = new Customer(1, 1);
        TestTable table = new TestTable();
        table.assign(customer);
        customer.seatAt(table);
        check(table.assignCount == 1, "A table already assigned by the Waiter is not assigned twice.");
        TestOrder order = new TestOrder(customer, new MenuItem("Salad", 900, 1), 1);
        customer.placeOrder(order);
        order.advanceOneUnit();
        order.markServed();
        customer.markServed(3);
        check(order.serveCount == 1, "An order marked served by the Waiter is not served twice.");
        customer.finishEating(4);
        order.markPaid();
        customer.markPaid();
        check(order.payCount == 1, "An order marked paid by the Cashier is not paid twice.");
        check(table.isFree() && customer.getStatus() == CustomerStatus.PAID, "Staff-first updates finish consistently.");
    }

    private static void dependencyFailuresCanBeRetried() {
        Customer customer = new Customer(1, 1);
        TestTable table = new TestTable();
        table.failAssign = true;
        unchanged(customer, IllegalStateException.class, () -> customer.seatAt(table));
        table.failAssign = false;
        customer.seatAt(table);
        TestOrder order = new TestOrder(customer, new MenuItem("Salad", 900, 1), 1);
        customer.placeOrder(order);
        order.advanceOneUnit();
        order.failServe = true;
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(3));
        order.failServe = false;
        customer.markServed(3);
        customer.finishEating(4);
        order.failPay = true;
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        order.failPay = false;
        table.failRelease = true;
        unchanged(customer, IllegalStateException.class, customer::markPaid);
        table.failRelease = false;
        customer.markPaid();
        check(customer.getStatus() == CustomerStatus.PAID && table.isFree(), "A failed payment can be retried.");
        check(order.payCount == 1, "Retrying a table release does not pay the order twice.");

        Customer abandoning = seated(2, 4);
        TestTable abandonedTable = (TestTable) abandoning.getTable();
        TestOrder cancelled = new TestOrder(abandoning, new MenuItem("Pasta", 1800, 2), 2);
        abandoning.placeOrder(cancelled);
        for (int i = 0; i < 12; i++) {
            abandoning.loseSatisfaction();
        }
        cancelled.failCancel = true;
        unchanged(abandoning, IllegalStateException.class, abandoning::loseSatisfaction);
        cancelled.failCancel = false;
        abandonedTable.failRelease = true;
        unchanged(abandoning, IllegalStateException.class, abandoning::loseSatisfaction);
        abandonedTable.failRelease = false;
        abandoning.loseSatisfaction();
        check(abandoning.getSatisfaction() == 0 && abandoning.getStatus() == CustomerStatus.LEFT,
                "Retrying abandonment completes all customer fields together.");
        check(cancelled.cancelCount == 1 && abandonedTable.isFree(), "Retrying does not cancel an order twice.");
    }

    private static void reassignedTableIsProtected() {
        Customer customer = seated(1, 1);
        TestTable table = (TestTable) customer.getTable();
        TestOrder order = new TestOrder(customer, new MenuItem("Salad", 900, 1), 1);
        customer.placeOrder(order);
        order.advanceOneUnit();

        // Table is mutable and exposed to staff, so its occupant can change independently.
        table.release();
        Customer other = new Customer(2, 4);
        other.seatAt(table);
        unchanged(customer, IllegalStateException.class, () -> customer.markServed(4));
        check(order.getStatus() == OrderStatus.READY, "Invalid seating must prevent service.");
        for (int i = 0; i < 12; i++) {
            customer.loseSatisfaction();
        }
        unchanged(customer, IllegalStateException.class, customer::loseSatisfaction);
        check(table.getCustomer() == other && other.getTable() == table,
                "A customer's departure must not free another customer's table.");
        check(order.getStatus() == OrderStatus.READY,
                "An invalid table association must be rejected before cancelling an order.");
    }

    private static Customer seated(int id, int arrivalTurn) {
        Customer customer = new Customer(id, arrivalTurn);
        customer.seatAt(new TestTable());
        return customer;
    }

    private static void unchanged(Customer customer, Class<? extends Throwable> type, Runnable action) {
        CustomerStatus status = customer.getStatus();
        int satisfaction = customer.getSatisfaction();
        Table table = customer.getTable();
        Order order = customer.getOrder();
        expect(type, action);
        check(customer.getStatus() == status && customer.getSatisfaction() == satisfaction
                        && customer.getTable() == table && customer.getOrder() == order,
                "A rejected action must preserve customer fields.");
    }

    private static Throwable expect(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable exception) {
            check(type.isInstance(exception), "Expected " + type.getSimpleName() + ", got " + exception);
            check(exception.getMessage() != null && !exception.getMessage().isBlank(),
                    "Validation failures must explain the rejection.");
            return exception;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static class TestMenu extends Menu {
        private final MenuItem[] entries;

        TestMenu(MenuItem... entries) {
            this.entries = entries;
        }

        @Override
        public int getItemCount() {
            return entries.length;
        }

        @Override
        public MenuItem getItem(int index) {
            return entries[index];
        }
    }

    private static class TestTable extends Table {
        private Customer occupant;
        private int assignCount;
        private int releaseCount;
        private boolean failAssign;
        private boolean failRelease;

        TestTable() {
            super(1);
        }

        @Override
        public Customer getCustomer() {
            return occupant;
        }

        @Override
        public boolean isFree() {
            return occupant == null;
        }

        @Override
        public void assign(Customer customer) {
            if (failAssign) {
                throw new IllegalStateException("Table assignment failed.");
            }
            check(isFree(), "The table must be free before assignment.");
            occupant = customer;
            assignCount++;
        }

        @Override
        public void release() {
            if (failRelease) {
                throw new IllegalStateException("Table release failed.");
            }
            check(occupant != null, "Only occupied tables can be released.");
            occupant = null;
            releaseCount++;
        }
    }

    private static class TestOrder extends Order {
        private final Customer owner;
        private final MenuItem item;
        private int remaining;
        private OrderStatus state = OrderStatus.PREPARING;
        private int serveCount;
        private int payCount;
        private int cancelCount;
        private boolean failServe;
        private boolean failPay;
        private boolean failCancel;

        TestOrder(Customer owner, MenuItem item, int units) {
            super(1, owner, item);
            this.owner = owner;
            this.item = item;
            remaining = units;
        }

        @Override
        public Customer getCustomer() {
            return owner;
        }

        @Override
        public MenuItem getItem() {
            return item;
        }

        @Override
        public OrderStatus getStatus() {
            return state;
        }

        @Override
        public void advanceOneUnit() {
            check(state == OrderStatus.PREPARING, "Only preparing orders can advance.");
            if (--remaining == 0) {
                state = OrderStatus.READY;
            }
        }

        @Override
        public void markServed() {
            if (failServe) {
                throw new IllegalStateException("Order service failed.");
            }
            check(state == OrderStatus.READY, "Only ready orders can be served.");
            state = OrderStatus.SERVED;
            serveCount++;
        }

        @Override
        public void markPaid() {
            if (failPay) {
                throw new IllegalStateException("Order payment failed.");
            }
            check(state == OrderStatus.SERVED, "Only served orders can be paid.");
            state = OrderStatus.PAID;
            payCount++;
        }

        @Override
        public void cancel() {
            if (failCancel) {
                throw new IllegalStateException("Order cancellation failed.");
            }
            check(state == OrderStatus.PREPARING || state == OrderStatus.READY,
                    "Only unserved orders can be cancelled.");
            state = OrderStatus.CANCELLED;
            cancelCount++;
        }
    }
}
