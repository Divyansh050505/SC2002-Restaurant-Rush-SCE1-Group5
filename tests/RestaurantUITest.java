import boundary.RestaurantUI;
import control.RestaurantController;
import entity.*;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Standalone UI contract tests; fixtures do not implement or validate game rules. */
public class RestaurantUITest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            money();
            validation();
            eofAndFullLoop();
            actions();
            screens();
        } finally {
            Locale.setDefault(previous);
        }
        System.out.println("PASS: " + checks + " UI checks (controlled fixtures, not full-game integration).");
    }

    private static void money() throws Exception {
        capture("", new Fixture(), (ui, output) -> {
            int[] amounts = {0, 9, 100, 1830, -9, Integer.MIN_VALUE, Integer.MAX_VALUE};
            String[] expected = {"$0.00", "$0.09", "$1.00", "$18.30", "-$0.09",
                    "-$21474836.48", "$21474836.47"};
            for (int i = 0; i < amounts.length; i++) {
                check(expected[i].equals(invoke(ui, "formatMoney", new Class<?>[]{int.class}, amounts[i])),
                        "exact integer money formatting " + amounts[i]);
            }
        });
    }

    private static void validation() throws Exception {
        capture("abc\n\n1.5\n-1\n4\n99999999999999999999999\n 2 \n", new Fixture(), (ui, out) -> {
            check(invoke(ui, "readChoice", new Class<?>[]{int.class, int.class}, 0, 3).equals(2),
                    "valid choice after invalid input");
            check(count(out.toString(StandardCharsets.UTF_8), "Invalid input.") == 6,
                    "all invalid values explain and retry");
        });
    }

    private static void eofAndFullLoop() throws Exception {
        for (String input : List.of("0\n", "", "2\n", "2\n0\n0\n")) {
            Fixture fixture = new Fixture();
            capture(input, fixture, (ui, out) -> {
                ui.start();
                String text = out.toString(StandardCharsets.UTF_8);
                check(fixture.turn == 0 && fixture.ended == 0, "startup exit/back/EOF does not start turn 1");
                check(!text.contains("FINAL RESULT:"), "exiting before play has no game result");
            });
        }
        Fixture replay = new Fixture();
        String previousSeed = System.getProperty("restaurant.rush.seed");
        capture("2\nabc\n-1\n1000000\n48219\n", replay, (ui, out) -> {
            ui.start();
            String text = out.toString(StandardCharsets.UTF_8);
            check(count(text, "Invalid input.") == 3, "invalid replay seeds explain and retry");
            check(text.contains("Game seed: 48219"), "the accepted replay seed is displayed");
            check(replay.turn == 18 && replay.ended == 18, "a valid replay starts and completes the game");
        });
        check(java.util.Objects.equals(previousSeed, System.getProperty("restaurant.rush.seed")),
                "the launch seed configuration is restored after a game");

        // EOF at the Host menu, Host customer selection, Waiter menu, Chef menu and Chef order selection.
        for (String input : List.of("", "1\n", "0\n", "0\n0\n", "0\n0\n1\n")) {
            Fixture fixture = new Fixture();
            capture("1\n" + input, fixture, (ui, out) -> {
                ui.start();
                String text = out.toString(StandardCharsets.UTF_8);
                check(text.matches("(?s).*Game seed: [1-9][0-9]{5}.*"), "new game displays a six-digit seed");
                check(fixture.turn == 18 && fixture.ended == 18, "EOF completes all 18 turns");
                check(count(text, "Input ended.") == 1, "EOF notice appears once");
                check(count(text, "--- End of turn") == 18, "every turn has summary");
                check(text.contains("FINAL RESULT: DEFEAT"), "EOF reaches final result");
            });
        }
    }

    private static void actions() throws Exception {
        Fixture fixture = new Fixture();
        // Host waits, then Waiter: back, rejected seating, retry seating, then Chef action.
        capture("1\n0\n1\n0\n1\n1\n1\n1\n1\n1\n", fixture, (ui, out) -> {
            ui.start();
            check(fixture.calls.equals(List.of("seat:1", "seat:1", "prepare:1")),
                    "back/rejection do not spend task; success advances to Chef");
            check(out.toString(StandardCharsets.UTF_8).contains("Seat rejected for test"),
                    "controller rejection shown");
        });
        Fixture other = new Fixture();
        capture("1\n0\n2\n1\n0\n0\n3\n1\n0\n", other, (ui, out) -> {
            ui.start();
            check(other.calls.equals(List.of("order:1", "serve:1")), "order and serve route by IDs");
        });
        Fixture empty = new Fixture();
        empty.orders.clear();
        empty.customers.clear();
        capture("1\n0\n3\n0\n1\n0\n", empty, (ui, out) -> {
            ui.start();
            check(count(out.toString(StandardCharsets.UTF_8), "There are no orders yet.") == 2,
                    "empty order menus recover");
            check(empty.calls.isEmpty(), "empty lists do not trigger actions");
        });
        Fixture host = new Fixture();
        // Host: back, then seat customer 1; the Waiter still has its own task.
        capture("1\n1\n0\n1\n1\n1\n1\n0\n", host, (ui, out) -> {
            ui.start();
            check(host.calls.equals(List.of("host:1", "seat:1")),
                    "Host seats by ID and does not use up the Waiter's task");
        });
        Fixture happy = new Fixture();
        // Turn 1: Happy Hour, then Host waits. Turn 2: option 2 is gone, so 2 is invalid.
        capture("1\n2\n0\n0\n0\n2\n0\n0\n0\n", happy, (ui, out) -> {
            ui.start();
            String text = out.toString(StandardCharsets.UTF_8);
            check(happy.calls.equals(List.of("happy")), "Happy Hour is switched on from the Host menu");
            check(text.contains("Happy Hour: ON | 2 turns remaining"), "Happy Hour duration is displayed");
            check(count(text, "2. Switch on Happy Hour") == 1, "Happy Hour is only offered until it is used");
            check(count(text, "Invalid input. Enter a whole number from 0 to 1.") == 1,
                    "Host menu rejects the Happy Hour option after it is used");
        });
    }

    private static void screens() throws Exception {
        Fixture fixture = new Fixture();
        fixture.paid = 4;
        fixture.revenue = 4500;
        fixture.average = 60;
        fixture.victory = true;
        capture("1\n", fixture, (ui, out) -> {
            ui.start();
            String text = out.toString(StandardCharsets.UTF_8);
            for (String expected : List.of("Customer #1", "satisfaction 76", "Order #1",
                    "Pasta", "$18.00", "PREPARING", "preparation units remaining: 1",
                    "Table #1: Customer #1", "Table #2: FREE", "Served: 5 | Paid: 4 | Departures: 1",
                    "FINAL RESULT: VICTORY", "PASS Paid customers", "PASS Revenue",
                    "PASS Average paid-customer satisfaction", "60.00 / 60", "Turns used: 18 / 18",
                    "Host task:", "Happy Hour: available", "Bill $18.00")) {
                check(text.contains(expected), "screen includes " + expected);
            }
        });
        fixture.paid = 3;
        fixture.revenue = 4499;
        fixture.average = 59.99;
        fixture.victory = false;
        capture("", fixture, (ui, out) -> {
            invoke(ui, "displayFinalResult", new Class<?>[0]);
            check(count(out.toString(StandardCharsets.UTF_8), "FAIL") == 3, "all below-target values fail");
        });
        fixture.paid = 0;
        capture("", fixture, (ui, out) -> {
            invoke(ui, "displayFinalResult", new Class<?>[0]);
            check(out.toString(StandardCharsets.UTF_8).contains("N/A (no paid customers)"),
                    "no paid customers shows N/A");
        });
    }

    private static Object invoke(RestaurantUI ui, String name, Class<?>[] types, Object... args)
            throws Exception {
        Method method = RestaurantUI.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(ui, args);
    }

    private interface TestBody { void run(RestaurantUI ui, ByteArrayOutputStream out) throws Exception; }

    private static void capture(String input, Fixture fixture, TestBody body) throws Exception {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(stream);
            body.run(new RestaurantUI(fixture), output);
        } finally {
            System.setIn(previousIn);
            System.setOut(previousOut);
        }
    }

    private static int count(String text, String part) { return text.split(java.util.regex.Pattern.quote(part), -1).length - 1; }
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        checks++;
    }

    private static class Fixture extends RestaurantController {
        int turn, ended, paid, revenue;
        double average;
        boolean victory;
        final List<String> calls = new ArrayList<>();
        final ArrayList<Customer> customers = new ArrayList<>();
        final ArrayList<Order> orders = new ArrayList<>();
        final Table[] tables;

        Fixture() {
            Customer customer = new RegularCustomer(1, 1) {
                public int getCustomerId() { return 1; }
                public int getSatisfaction() { return 76; }
                public CustomerStatus getStatus() { return CustomerStatus.ORDERED; }
                public Order getOrder() { return orders.isEmpty() ? null : orders.get(0); }
            };
            MenuItem pasta = new MenuItem("Pasta", 1800, 2) {
                public String getName() { return "Pasta"; }
                public int getPriceCents() { return 1800; }
            };
            customers.add(customer);
            orders.add(new Order(1, customer, pasta) {
                public int getOrderNo() { return 1; }
                public Customer getCustomer() { return customer; }
                public MenuItem getItem() { return pasta; }
                public OrderStatus getStatus() { return OrderStatus.PREPARING; }
                public int getRemainingUnits() { return 1; }
            });
            tables = new Table[]{new Table(1) {
                public int getTableNo() { return 1; }
                public boolean isFree() { return false; }
                public Customer getCustomer() { return customer; }
            }, new Table(2) {
                public int getTableNo() { return 2; }
                public boolean isFree() { return true; }
            }};
        }
        public void startTurn() { turn++; }
        public void endTurn() { ended++; }
        public boolean isGameOver() { return turn >= 18; }
        public boolean isVictory() { return victory; }
        public int getCurrentTurn() { return turn; }
        public int getPaidCount() { return paid; }
        public int getServedCount() { return 5; }
        public int getAbandonedCount() { return 1; }
        public int getRevenueCents() { return revenue; }
        public double getAverageSatisfaction() { return average; }
        public ArrayList<Customer> getCustomers() { return customers; }
        public ArrayList<Customer> getWaitingQueue() { return new ArrayList<>(); }
        public ArrayList<Order> getOrders() { return orders; }
        public Table[] getTables() { return tables; }
        final HappyHour happyHour = new HappyHour();
        public HappyHour getHappyHour() { return happyHour; }
        public ActionResult activateHappyHour() { calls.add("happy"); return happyHour.activate(); }
        public ActionResult hostSeatCustomer(int id) { calls.add("host:" + id); return ActionResult.success("Host seated"); }
        public ActionResult seatCustomer(int id) {
            calls.add("seat:" + id);
            return calls.size() == 1 ? ActionResult.failure("Seat rejected for test") : ActionResult.success("Seated");
        }
        public ActionResult takeOrder(int id) { calls.add("order:" + id); return ActionResult.success("Ordered"); }
        public ActionResult serveOrder(int id) { calls.add("serve:" + id); return ActionResult.success("Served"); }
        public ActionResult prepareOrder(int id) { calls.add("prepare:" + id); return ActionResult.success("Prepared"); }
    }
}
