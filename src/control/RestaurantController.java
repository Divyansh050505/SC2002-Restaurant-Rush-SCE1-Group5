package control;

import entity.ActionResult;
import entity.Cashier;
import entity.Chef;
import entity.Customer;
import entity.Menu;
import entity.Order;
import entity.Table;
import entity.Waiter;
import java.util.ArrayList;

/**
 * Stage One turn and game state controller skeleton.
 */
public class RestaurantController {
    private static final int MAX_TURNS = 18;
    private static final int NUM_TABLES = 2;
    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    private int currentTurn = 0;
    private int revenueCents = 0;
    private int nextCustomerId = 1;
    private int nextOrderNo = 1;
    private ArrayList<Customer> customers;
    private ArrayList<Customer> waitingQueue;
    private ArrayList<Customer> paymentQueue;
    private ArrayList<Order> orders;
    private Table[] tables;
    private Menu menu;
    private Waiter waiter;
    private Chef chef;
    private Cashier cashier;

    public RestaurantController() {
        customers = new ArrayList<>();
        waitingQueue = new ArrayList<>();
        paymentQueue = new ArrayList<>();
        orders = new ArrayList<>();
        tables = new Table[NUM_TABLES];
        for (int i = 0; i < NUM_TABLES; i++) {
            tables[i] = new Table(i + 1);
        }
        menu = new Menu();
        waiter = new Waiter();
        chef = new Chef();
        cashier = new Cashier();
    }

    /**
     * Begins the next turn: frees every staff member's task and runs
     * Step 1 (arrivals). Does nothing once all turns have been played.
     */
    public void startTurn() {
        if (isGameOver()) {
            return;
        }
        currentTurn++;
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();
        handleArrival();
    }

    public ActionResult seatCustomer(int customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return ActionResult.failure("There is no customer #" + customerId + ".");
        }
        ActionResult result = waiter.seat(customer, findFreeTable());
        if (result.isSuccess()) {
            waitingQueue.remove(customer);
        }
        return result;
    }

    public ActionResult takeOrder(int customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return ActionResult.failure("There is no customer #" + customerId + ".");
        }
        ActionResult result = waiter.takeOrder(customer, nextOrderNo, menu);
        if (result.isSuccess()) {
            orders.add(customer.getOrder());
            nextOrderNo++;
        }
        return result;
    }

    public ActionResult serveOrder(int orderNo) {
        Order order = findOrder(orderNo);
        if (order == null) {
            return ActionResult.failure("There is no order #" + orderNo + ".");
        }
        return waiter.serve(order, currentTurn);
    }

    public ActionResult prepareOrder(int orderNo) {
        Order order = findOrder(orderNo);
        if (order == null) {
            return ActionResult.failure("There is no order #" + orderNo + ".");
        }
        return chef.prepare(order);
    }

    /**
     * Finishes the current turn after the Waiter and Chef have acted.
     * Payment runs before eating so that a customer who becomes
     * READY_TO_PAY this turn pays next turn at the earliest.
     */
    public void endTurn() {
        handlePayment();  // Step 4a: Cashier pays the oldest READY_TO_PAY customer
        handleEating();   // Step 4b: customers served on an earlier turn finish eating
        handleWaiting();  // Step 5: unserved customers lose satisfaction, may leave
    }

    public boolean isGameOver() {
        return currentTurn >= MAX_TURNS;
    }

    public boolean isVictory() {
        // insert implementation here
        return false;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getRevenueCents() {
        // insert implementation here
        return 0;
    }

    public double getAverageSatisfaction() {
        // insert implementation here
        return 0.0;
    }

    public int getPaidCount() {
        // insert implementation here
        return 0;
    }

    public int getServedCount() {
        // insert implementation here
        return 0;
    }

    public int getAbandonedCount() {
        // insert implementation here
        return 0;
    }

    public ArrayList<Customer> getWaitingQueue() {
        // insert implementation here
        return null;
    }

    public ArrayList<Customer> getCustomers() {
        // insert implementation here
        return null;
    }

    public ArrayList<Order> getOrders() {
        // insert implementation here
        return null;
    }

    public Table[] getTables() {
        // insert implementation here
        return null;
    }

    private void handleArrival() {
        for (int arrivalTurn : ARRIVAL_TURNS) {
            if (arrivalTurn == currentTurn) {
                Customer customer = new Customer(nextCustomerId, currentTurn);
                nextCustomerId++;
                customers.add(customer);
                waitingQueue.add(customer);
            }
        }
    }

    private void handlePayment() {
        // insert implementation here
    }

    private void handleEating() {
        // insert implementation here
    }

    private void handleWaiting() {
        // insert implementation here
    }

    private Customer findCustomer(int customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    private Order findOrder(int orderNo) {
        for (Order order : orders) {
            if (order.getOrderNo() == orderNo) {
                return order;
            }
        }
        return null;
    }

    private Table findFreeTable() {
        // insert implementation here
        return null;
    }
}
