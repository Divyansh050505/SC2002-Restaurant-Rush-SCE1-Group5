import boundary.RestaurantUI;
import control.CustomerArrivalPlan;
import control.RestaurantController;
import entity.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Actual controller/staff integration and console replay; no controller fixtures. */
public class Stage2IntegrationTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        String previous = System.getProperty("restaurant.rush.seed");
        try {
            lateSeedAndSchedule();
            orderedCriticAbandons();
            Run victory = play(482193, true);
            check(victory.controller.isVictory(), "documented Stage Two strategy wins");
            check(victory.controller.getPaidCount() >= 4 && victory.controller.getRevenueCents() >= 4500
                    && victory.controller.getAverageSatisfaction() >= 60, "all victory thresholds remain required");
            check(victory.controller.getCustomers().stream().anyMatch(c -> c.getTypeLabel().equals("VIP")
                    && c.getStatus() == CustomerStatus.PAID), "victory exercises a paid VIP");
            check(victory.controller.getCustomers().stream().anyMatch(c -> c.getTypeLabel().equals("Critic")
                    && c.getStatus() == CustomerStatus.PAID), "victory exercises a paid Critic");
            Run defeat = play(482193, false);
            check(!defeat.controller.isVictory() && defeat.controller.getRevenueCents() == 0
                    && defeat.controller.getPaidCount() == 0 && defeat.controller.getAbandonedCount() > 0,
                    "unfinished and abandoned orders generate no revenue");
            for (Run run : List.of(victory, defeat)) {
                String name = run == victory ? "victory" : "defeat";
                String saved = Files.readString(Path.of("test-runs/stage2_" + name + ".txt"));
                check(saved.equals(run.input), "recorded " + name + " manager inputs reproduce the strategy");
                String output = console(saved);
                check(output.equals(Files.readString(Path.of("test-runs/stage2_" + name + "_transcript.txt"))),
                        "saved transcript matches a fresh real CLI replay");
                check(output.equals(console(saved)), "same seed and manager choices reproduce the entire console output");
                check(output.contains("FINAL RESULT: " + name.toUpperCase(Locale.ROOT))
                        && count(output, "--- End of turn") == 18 && !output.contains("Invalid input."),
                        "actual CLI replay completes all turns with valid actions");
                check(output.contains(" | VIP | ") && output.contains(" | Critic | "), "CLI displays polymorphic type labels");
                for (Customer c : run.controller.getCustomers()) {
                    if (c.getStatus() == CustomerStatus.PAID) {
                        String money = String.format(Locale.ROOT, "$%.2f", expectedBill(c) / 100.0);
                        check(output.contains("Customer #" + c.getCustomerId() + " paid " + money + "."),
                                "printed payment matches the amount actually collected");
                        check(output.contains(" | Bill " + money + " | "), "order screen shows the payable bill");
                    }
                }
            }
        } finally {
            restoreSeed(previous);
        }
        System.out.println("PASS: " + checks + " real Stage Two integration/replay checks.");
    }

    private static void lateSeedAndSchedule() {
        System.setProperty("restaurant.rush.seed", "1");
        RestaurantController controller = new RestaurantController();
        check(controller.getCustomers().isEmpty(), "construction creates no premature arrivals");
        System.setProperty("restaurant.rush.seed", "482193");
        CustomerArrivalPlan expected = new CustomerArrivalPlan(482193);
        for (int turn = 1; turn <= 18; turn++) {
            controller.startTurn();
            check(controller.getCustomers().size() == (turn + 2) / 3, "only the six scheduled turns create arrivals");
            if ((turn - 1) % 3 == 0) {
                int id = controller.getCustomers().size();
                Customer actual = controller.getCustomers().get(id - 1);
                check(actual.getClass() == expected.createCustomer(id, turn).getClass()
                        && actual.getSatisfaction() == 100 && controller.getWaitingQueue().contains(actual),
                        "UI-selected seed, ID, initial satisfaction and waiting queue are respected");
            }
            controller.endTurn();
            // Once built, arrivals must not be reshuffled even if the process property changes.
            System.setProperty("restaurant.rush.seed", Integer.toString(turn));
        }
        controller.startTurn();
        check(controller.getCurrentTurn() == 18 && controller.getCustomers().size() == 6,
                "no arrivals or additional turns after game over");
        List<String> types = controller.getCustomers().stream().map(Customer::getTypeLabel).toList();
        check(Collections.frequency(types, "Regular") == 4 && Collections.frequency(types, "VIP") == 1
                && Collections.frequency(types, "Critic") == 1,
                "complete base game has exactly four Regulars, one VIP and one Critic");
    }

    private static void orderedCriticAbandons() {
        System.setProperty("restaurant.rush.seed", "482193");
        RestaurantController game = new RestaurantController();
        Customer critic = null;
        Table table = null;
        for (int turn = 1; turn <= 18; turn++) {
            game.startTurn();
            if (turn == 1) {
                critic = game.getCustomers().get(0);
                check(critic.getTypeLabel().equals("Critic"), "fixed test seed has a Critic arriving on turn 1");
                check(game.seatCustomer(1).isSuccess(), "manager seats the Critic");
                table = critic.getTable();
            } else if (turn == 2) {
                check(game.takeOrder(1).isSuccess(), "Critic orders Pasta through the actual Waiter");
            }
            game.endTurn();
            if (turn == 8) check(critic.getSatisfaction() == 4 && !table.isFree(), "Critic still holds table before final loss");
            if (turn == 9) {
                check(critic.getSatisfaction() == 0 && critic.getStatus() == CustomerStatus.LEFT
                        && critic.getOrder().getStatus() == OrderStatus.CANCELLED && table.isFree(),
                        "controller cancels an unfinished Critic order and frees its table on the ninth loss");
            }
        }
        check(game.getRevenueCents() == 0 && game.getPaidCount() == 0 && !game.isVictory(),
                "abandoned Pasta contributes no revenue or paid satisfaction");
    }

    /** Creates exact CLI inputs while exercising the real controller turn rules. */
    static Run play(long seed, boolean cook) {
        String previous = System.getProperty("restaurant.rush.seed");
        try {
            RestaurantController game = new RestaurantController();
            System.setProperty("restaurant.rush.seed", Long.toString(seed));
            StringBuilder inputs = new StringBuilder("2\n" + seed + "\n");
            Map<Customer, Integer> serviceTurns = new HashMap<>();
            while (!game.isGameOver()) {
                game.startTurn();
                int turn = game.getCurrentTurn();
                check(!game.seatCustomer(999).isSuccess() && !game.prepareOrder(999).isSuccess(),
                        "invalid IDs are rejected without preventing the actual action");
                Order ready = game.getOrders().stream().filter(o -> o.getStatus() == OrderStatus.READY).findFirst().orElse(null);
                Customer seated = game.getCustomers().stream().filter(c -> c.getStatus() == CustomerStatus.SEATED).findFirst().orElse(null);
                if (ready != null) {
                    check(game.serveOrder(ready.getOrderNo()).isSuccess(), "serve a previously READY order");
                    inputs.append("3\n").append(ready.getOrderNo()).append('\n');
                    serviceTurns.put(ready.getCustomer(), turn);
                } else if (seated != null) {
                    check(game.takeOrder(seated.getCustomerId()).isSuccess(), "take the seated customer's own choice");
                    inputs.append("2\n").append(seated.getCustomerId()).append('\n');
                    if (seated.getTypeLabel().equals("Critic")) {
                        check(seated.getOrder().getItem().getName().equals("Pasta"), "Waiter dispatches Critic preference");
                    }
                } else if (!game.getWaitingQueue().isEmpty() && Arrays.stream(game.getTables()).anyMatch(Table::isFree)) {
                    Customer waiting = game.getWaitingQueue().get(0);
                    check(game.seatCustomer(waiting.getCustomerId()).isSuccess(), "seat queued customer");
                    inputs.append("1\n").append(waiting.getCustomerId()).append('\n');
                    check(!game.takeOrder(waiting.getCustomerId()).isSuccess(), "Waiter cannot take two tasks");
                } else {
                    inputs.append("0\n");
                }
                Order preparing = game.getOrders().stream().filter(o -> o.getStatus() == OrderStatus.PREPARING).findFirst().orElse(null);
                if (cook && preparing != null) {
                    check(game.prepareOrder(preparing.getOrderNo()).isSuccess(), "Chef advances one unit, including newly taken orders");
                    inputs.append("1\n").append(preparing.getOrderNo()).append('\n');
                    check(!game.prepareOrder(preparing.getOrderNo()).isSuccess(), "Chef cannot prepare a second unit this turn");
                } else {
                    inputs.append("0\n");
                }
                Customer firstToPay = game.getCustomers().stream().filter(c -> c.getStatus() == CustomerStatus.READY_TO_PAY).findFirst().orElse(null);
                int previousRevenue = game.getRevenueCents();
                int previousCount = game.getPaidCount();
                Map<Customer, CustomerStatus> states = new HashMap<>();
                Map<Customer, Integer> satisfaction = new HashMap<>();
                Map<Customer, Table> tables = new HashMap<>();
                for (Customer c : game.getCustomers()) {
                    states.put(c, c.getStatus());
                    satisfaction.put(c, c.getSatisfaction());
                    tables.put(c, c.getTable());
                }
                game.endTurn();
                check(game.getRevenueCents() - previousRevenue == (firstToPay == null ? 0 : expectedBill(firstToPay))
                        && game.getPaidCount() - previousCount == (firstToPay == null ? 0 : 1),
                        "exact discounted payment for the oldest ready customer, at most one per turn");
                for (Customer c : game.getCustomers()) {
                    CustomerStatus before = states.get(c);
                    if (before == CustomerStatus.EATING) {
                        check(c.getStatus() == (serviceTurns.get(c) == turn ? CustomerStatus.EATING : CustomerStatus.READY_TO_PAY),
                                "one full eating turn, followed by payment no earlier than the next turn");
                    }
                    if (before == CustomerStatus.WAITING || before == CustomerStatus.SEATED || before == CustomerStatus.ORDERED) {
                        int loss = switch (c.getTypeLabel()) { case "VIP" -> 5; case "Critic" -> 12; default -> 8; };
                        check(c.getSatisfaction() == Math.max(0, satisfaction.get(c) - loss), "controller dispatches subtype waiting loss");
                        if (c.getStatus() == CustomerStatus.LEFT) {
                            check(!game.getWaitingQueue().contains(c), "abandoned customers removed from waiting queue");
                            if (c.getOrder() != null) check(c.getOrder().getStatus() == OrderStatus.CANCELLED, "abandoned order cancelled");
                        }
                    } else {
                        check(c.getSatisfaction() == satisfaction.get(c), "served and terminal satisfaction remains unchanged");
                    }
                    if ((c.getStatus() == CustomerStatus.PAID || c.getStatus() == CustomerStatus.LEFT) && tables.get(c) != null) {
                        check(tables.get(c).isFree() && c.getTable() == null, "payment/abandonment releases the table");
                    }
                }
            }
            check(game.getCurrentTurn() == 18 && game.getCustomers().size() == 6, "always complete 18 turns and six arrivals");
            double average = game.getCustomers().stream().filter(c -> c.getStatus() == CustomerStatus.PAID)
                    .mapToInt(Customer::getSatisfaction).average().orElse(0);
            check(game.getAverageSatisfaction() == average, "average uses only paid customers' frozen satisfaction");
            return new Run(game, inputs.toString());
        } finally { restoreSeed(previous); }
    }

    static String console(String input) throws Exception {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String previousSeed = System.getProperty("restaurant.rush.seed");
        try (PrintStream stream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(stream);
            new RestaurantUI(new RestaurantController()).start();
        } finally {
            System.setIn(previousIn);
            System.setOut(previousOut);
        }
        check(Objects.equals(previousSeed, System.getProperty("restaurant.rush.seed")), "CLI restores the launch seed");
        return output.toString(StandardCharsets.UTF_8);
    }

    private static int expectedBill(Customer c) {
        int base = c.getOrder().getItem().getPriceCents();
        return c.getTypeLabel().equals("VIP") ? (base * 9 + 5) / 10 : base;
    }

    private static int count(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    private static void restoreSeed(String previous) {
        if (previous == null) System.clearProperty("restaurant.rush.seed");
        else System.setProperty("restaurant.rush.seed", previous);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    static class Run {
        final RestaurantController controller;
        final String input;
        Run(RestaurantController controller, String input) { this.controller = controller; this.input = input; }
    }
}
