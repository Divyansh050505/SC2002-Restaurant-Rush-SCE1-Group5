package control;

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
        // insert implementation here
    }

    public void startTurn() {
        // insert implementation here
    }

    public void seatCustomer(int customerId) {
        // insert implementation here
    }

    public void takeOrder(int customerId) {
        // insert implementation here
    }

    public void serveOrder(int orderNo) {
        // insert implementation here
    }

    public void prepareOrder(int orderNo) {
        // insert implementation here
    }

    public void endTurn() {
        // insert implementation here
    }

    public boolean isGameOver() {
        // insert implementation here
        return false;
    }

    public boolean isVictory() {
        // insert implementation here
        return false;
    }

    public int getCurrentTurn() {
        // insert implementation here
        return 0;
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
        // insert implementation here
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
        // insert implementation here
        return null;
    }

    private Order findOrder(int orderNo) {
        // insert implementation here
        return null;
    }

    private Table findFreeTable() {
        // insert implementation here
        return null;
    }
}
