package boundary;

import control.RestaurantController;
import entity.ActionResult;
import entity.Customer;
import entity.CustomerStatus;
import entity.Order;
import entity.Table;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Console input and output for Stage Two. Game actions remain in the controller.
 */
public class RestaurantUI {
    private RestaurantController controller;
    private Scanner sc;
    private boolean inputEnded;
    // Active customers and table numbers before automatic end-of-turn actions.
    private final Map<Customer, Integer> customerTablesBeforeEnd = new LinkedHashMap<>();

    public RestaurantUI(RestaurantController controller) {
        this.controller = controller;
        this.sc = new Scanner(System.in);
    }

    /**
     * Offers a new game or a replay by seed before turn 1.
     * Every turn runs arrivals, the Waiter's task,
     * the Chef's task (Waiter always before Chef), then eating, payment and
     * waiting. The game always runs all turns before showing the result.
     */
    public void start() {
        int seed;
        while (true) {
            System.out.println("\nRESTAURANT RUSH");
            System.out.println("  1. New game");
            System.out.println("  2. Replay using a seed");
            System.out.println("  0. Exit");
            int choice = readChoice(0, 2);
            if (choice == 0) {
                return;
            }
            if (choice == 1) {
                seed = new Random().nextInt(900_000) + 100_000;
            } else {
                System.out.println("Enter the previous game's seed (0 = back):");
                seed = readChoice(0, 999_999);
                if (seed == 0) {
                    if (inputEnded) {
                        return;
                    }
                    continue;
                }
            }
            break;
        }

        String previousSeed = System.getProperty("restaurant.rush.seed");
        System.setProperty("restaurant.rush.seed", Integer.toString(seed));
        System.out.println("Game seed: " + seed);
        try {
            while (!controller.isGameOver()) {
                controller.startTurn();
                displayTurnStart();
                chooseWaiterTask();
                chooseChefTask();
                customerTablesBeforeEnd.clear();
                for (Customer customer : controller.getCustomers()) {
                    CustomerStatus status = customer.getStatus();
                    if (status != CustomerStatus.PAID && status != CustomerStatus.LEFT) {
                        Table table = customer.getTable();
                        customerTablesBeforeEnd.put(customer, table == null ? null : table.getTableNo());
                    }
                }
                controller.endTurn();
                displayTurnEnd();
            }
            displayFinalResult();
        } finally {
            if (previousSeed == null) {
                System.clearProperty("restaurant.rush.seed");
            } else {
                System.setProperty("restaurant.rush.seed", previousSeed);
            }
        }
    }

    private void displayTurnStart() {
        System.out.println("\n==================================================");
        System.out.println("RESTAURANT RUSH | Turn " + controller.getCurrentTurn() + " / 18");
        displayTotals();
        System.out.println("Targets: 4 paid customers | $45.00 revenue | 60 average satisfaction");
        System.out.println("Choose one Waiter task, then one Chef task. Cashier acts automatically.");
        System.out.println("Enter the displayed customer/order number; 0 means back or wait.");
        System.out.println("\nWaiting queue (arrival order):");
        if (controller.getWaitingQueue().isEmpty()) {
            System.out.println("  Empty");
        } else {
            for (Customer customer : controller.getWaitingQueue()) {
                System.out.println("  " + describeCustomer(customer));
            }
        }
        System.out.println("Tables:");
        for (Table table : controller.getTables()) {
            System.out.println("  Table #" + table.getTableNo() + ": "
                    + (table.isFree() ? "FREE" : describeCustomer(table.getCustomer())));
        }
        displayCustomers();
        displayOrders();
    }

    private String describeCustomer(Customer customer) {
        Order order = customer.getOrder();
        return "Customer #" + customer.getCustomerId() + " | " + customer.getTypeLabel()
                + " | " + customer.getStatus()
                + " | satisfaction " + customer.getSatisfaction()
                + (order == null ? " | no order"
                    : " | order #" + order.getOrderNo() + " " + order.getStatus());
    }

    private void displayCustomers() {
        System.out.println("Customers (use Customer # when selecting):");
        if (controller.getCustomers().isEmpty()) {
            System.out.println("  None yet");
        }
        for (Customer customer : controller.getCustomers()) {
            System.out.println("  " + describeCustomer(customer));
        }
    }

    private void displayOrders() {
        System.out.println("Orders (use Order # when selecting):");
        if (controller.getOrders().isEmpty()) {
            System.out.println("  None yet");
        }
        for (Order order : controller.getOrders()) {
            System.out.println("  Order #" + order.getOrderNo()
                    + " | Customer #" + order.getCustomer().getCustomerId()
                    + " | " + order.getItem().getName()
                    + " | Menu " + formatMoney(order.getItem().getPriceCents())
                    + " | Bill " + formatMoney(order.getCustomer()
                            .calculateBillCents(order.getItem().getPriceCents()))
                    + " | " + order.getStatus()
                    + " | preparation units remaining: " + order.getRemainingUnits());
        }
    }

    /**
     * Asks for one Waiter task until a valid one is done or the manager
     * chooses Wait. A rejected choice prints the reason and does not use
     * up the Waiter's task.
     */
    private void chooseWaiterTask() {
        while (true) {
            System.out.println("Waiter task:");
            System.out.println("  1. Seat a waiting customer");
            System.out.println("  2. Take a seated customer's order");
            System.out.println("  3. Serve a READY dish");
            System.out.println("  0. Wait");
            int choice = readChoice(0, 3);
            if (choice == 0) {
                System.out.println("The Waiter waits this turn.");
                return;
            }

            ActionResult result;
            if (choice == 1 || choice == 2) {
                displayCustomers();
                System.out.println("Enter customer number (0 = back):");
                int customerId = readChoice(0, controller.getCustomers().size());
                if (customerId == 0) {
                    continue;
                }
                result = (choice == 1)
                        ? controller.seatCustomer(customerId)
                        : controller.takeOrder(customerId);
            } else {
                if (controller.getOrders().isEmpty()) {
                    System.out.println("There are no orders yet.");
                    continue;
                }
                displayOrders();
                System.out.println("Enter order number (0 = back):");
                int orderNo = readChoice(0, controller.getOrders().size());
                if (orderNo == 0) {
                    continue;
                }
                result = controller.serveOrder(orderNo);
            }

            System.out.println(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    /**
     * Asks for one Chef task until a valid one is done or the manager
     * chooses Wait. A rejected choice prints the reason and does not use
     * up the Chef's task.
     */
    private void chooseChefTask() {
        // Refresh after the Waiter: a newly taken order can be prepared this turn.
        displayOrders();
        while (true) {
            System.out.println("Chef task:");
            System.out.println("  1. Prepare one unit of an order");
            System.out.println("  0. Wait");
            int choice = readChoice(0, 1);
            if (choice == 0) {
                System.out.println("The Chef waits this turn.");
                return;
            }

            if (controller.getOrders().isEmpty()) {
                System.out.println("There are no orders yet.");
                continue;
            }
            System.out.println("Enter order number (0 = back):");
            int orderNo = readChoice(0, controller.getOrders().size());
            if (orderNo == 0) {
                continue;
            }

            ActionResult result = controller.prepareOrder(orderNo);
            System.out.println(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    private void displayTurnEnd() {
        System.out.println("\n--- End of turn " + controller.getCurrentTurn() + " ---");
        for (Map.Entry<Customer, Integer> entry : customerTablesBeforeEnd.entrySet()) {
            Customer customer = entry.getKey();
            if (customer.getStatus() == CustomerStatus.PAID) {
                System.out.println("Customer #" + customer.getCustomerId() + " paid "
                        + formatMoney(customer.calculateBillCents(
                                customer.getOrder().getItem().getPriceCents())) + ".");
            } else if (customer.getStatus() == CustomerStatus.LEFT) {
                System.out.println("Customer #" + customer.getCustomerId() + " left without paying."
                        + (customer.getOrder() == null ? ""
                        : " Order #" + customer.getOrder().getOrderNo() + " was cancelled."));
            } else {
                continue;
            }
            if (entry.getValue() != null) {
                System.out.println("Table #" + entry.getValue() + " is now free.");
            }
        }
        customerTablesBeforeEnd.clear();
        displayTotals();
        System.out.println("Turns used: " + controller.getCurrentTurn() + " / 18");
    }

    private void displayFinalResult() {
        System.out.println("\n==================================================");
        System.out.println("FINAL RESULT: " + (controller.isVictory() ? "VICTORY" : "DEFEAT"));
        displayTotals();
        System.out.println("Turns used: " + controller.getCurrentTurn() + " / 18");
        System.out.println("Victory targets (all three must pass):");
        System.out.println("  " + passLabel(controller.getPaidCount() >= 4)
                + " Paid customers: " + controller.getPaidCount() + " / 4");
        System.out.println("  " + passLabel(controller.getRevenueCents() >= 4500)
                + " Revenue: " + formatMoney(controller.getRevenueCents()) + " / $45.00");
        System.out.println("  " + passLabel(controller.getPaidCount() > 0
                    && controller.getAverageSatisfaction() >= 60.0)
                + " Average paid-customer satisfaction: " + averageText() + " / 60");
    }

    private String passLabel(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }

    private String averageText() {
        return controller.getPaidCount() == 0 ? "N/A (no paid customers)"
                : String.format(Locale.ROOT, "%.2f", controller.getAverageSatisfaction());
    }

    private void displayTotals() {
        System.out.println("Served: " + controller.getServedCount()
                + " | Paid: " + controller.getPaidCount()
                + " | Departures: " + controller.getAbandonedCount());
        System.out.println("Revenue: " + formatMoney(controller.getRevenueCents())
                + " | Average paid-customer satisfaction: " + averageText());
    }

    private int readChoice(int min, int max) {
        while (true) {
            System.out.print("Choice [" + min + "-" + max + "]: ");
            if (!sc.hasNextLine()) {
                if (!inputEnded) {
                    System.out.println(controller.getCurrentTurn() == 0
                            ? "\nInput ended. Exiting before the game starts."
                            : "\nInput ended. Remaining staff tasks will use Wait.");
                    inputEnded = true;
                }
                // Every menu includes 0: exit before play, otherwise back or Wait.
                return 0;
            }
            String input = sc.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException exception) {
                // Blank input, non-integers and integer overflow all re-prompt.
            }
            System.out.println("Invalid input. Enter a whole number from " + min + " to " + max + ".");
        }
    }

    private String formatMoney(int cents) {
        long amount = Math.abs((long) cents);
        return (cents < 0 ? "-$" : "$") + amount / 100
                + String.format(Locale.ROOT, ".%02d", amount % 100);
    }
}
