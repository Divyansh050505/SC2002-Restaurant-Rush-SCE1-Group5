package boundary;

import control.RestaurantController;
import entity.ActionResult;
import java.util.Scanner;

/**
 * Stage One console boundary skeleton.
 */
public class RestaurantUI {
    private RestaurantController controller;
    private Scanner sc;

    public RestaurantUI(RestaurantController controller) {
        this.controller = controller;
        this.sc = new Scanner(System.in);
    }

    /**
     * Plays the whole game: every turn runs arrivals, the Waiter's task,
     * the Chef's task (Waiter always before Chef), then eating, payment and
     * waiting. The game always runs all turns before showing the result.
     */
    public void start() {
        while (!controller.isGameOver()) {
            controller.startTurn();
            displayTurnStart();
            chooseWaiterTask();
            chooseChefTask();
            controller.endTurn();
            displayTurnEnd();
        }
        displayFinalResult();
    }

    private void displayTurnStart() {
        // insert implementation here
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
        // insert implementation here
    }

    private void displayFinalResult() {
        // insert implementation here
    }

    private int readChoice(int min, int max) {
        // insert implementation here
        return 0;
    }

    private String formatMoney(int cents) {
        // insert implementation here
        return null;
    }
}
