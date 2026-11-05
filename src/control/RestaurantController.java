package control;

import entity.ActionResult;
import entity.Cashier;
import entity.Chef;
import entity.Customer;
import entity.HappyHour;
import entity.Menu;
import entity.Order;
import entity.Table;
import entity.Waiter;
import java.util.ArrayList;
import java.util.Random;
import entity.CustomerStatus;
import entity.OrderStatus;

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
    private ArrayList<Customer> paidCustomers;
    private Table[] tables;
    private Menu menu;
    private Waiter waiter;
    private Chef chef;
    private Cashier cashier;
    private HappyHour happyHour;
    private CustomerArrivalPlan arrivalPlan = null;

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
        happyHour = new HappyHour();
        paidCustomers = new ArrayList<>();
    }

    
    public void startTurn() {
        if (isGameOver()) {
            return;
        }
        initializeArrivalPlan();
        currentTurn++;
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();
        handleArrival();
        if (happyHour.isActive()) {
            handleHappyHourRecovery();
        }
    }

    
    public ActionResult activateHappyHour() {
        if (currentTurn == 0) {
            return ActionResult.failure("The game has not started yet.");
        }
        if (waiter.hasActed() || chef.hasActed()) {
            return ActionResult.failure("Happy Hour can only be switched on at the start of a turn.");
        }
        ActionResult result = happyHour.activate();
        if (result.isSuccess()) {
            handleHappyHourRecovery(); 
        }
        return result;
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
        ActionResult result = waiter.takeOrder(customer, nextOrderNo, menu, happyHour);
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

    
    public void endTurn() {
        handlePayment();  
        handleEating();   
        handleWaiting(); 
        happyHour.endTurn();
    }

    public boolean isGameOver() {
        return currentTurn >= MAX_TURNS;
    }

    public boolean isVictory() {
        return getPaidCount() >= 4
                && getRevenueCents() >= 4500
                && getAverageSatisfaction() >= 60.0;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getRevenueCents() {
        return this.revenueCents;
    }

    public double getAverageSatisfaction() {
        if (paidCustomers.isEmpty()) {
            return 0.0; 
        }
        double totalSatisfaction = 0;
        for (Customer customer : paidCustomers) {
            totalSatisfaction += customer.getSatisfaction();
        }
        return totalSatisfaction / paidCustomers.size();
    }

    public int getPaidCount() {
        return this.paidCustomers.size();
    }

    public int getServedCount() {
        int served = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.SERVED || order.getStatus() == OrderStatus.PAID) {
                served++;
            }
        }
        return served;
    }

    public int getAbandonedCount() {
        int abandoned = 0;
        for (Customer customer : customers) {
            if (customer.getStatus() == entity.CustomerStatus.LEFT) {
                abandoned++;
            }
        }
        return abandoned;
    }

    public ArrayList<Customer> getWaitingQueue() {
        return this.waitingQueue;
    }

    public ArrayList<Customer> getCustomers() {
        return this.customers;
    }

    public ArrayList<Order> getOrders() {
        return this.orders;
    }
    
    public Table[] getTables() {
        return this.tables;
    }

    public HappyHour getHappyHour() {
        return this.happyHour;
    }

    private void handleArrival() {
        for (int arrivalTurn : ARRIVAL_TURNS) {
            if (arrivalTurn == currentTurn) {
                Customer customer = arrivalPlan.createCustomer(nextCustomerId, currentTurn);
                nextCustomerId++;
                customers.add(customer);
                waitingQueue.add(customer);
            }
        }
    }

    private void handlePayment() {
        if (cashier.hasActed()) {
            return; 
        }

       
        Customer customerToPay = null;
        for (Customer customer : customers) {
            if (customer.getStatus() == CustomerStatus.READY_TO_PAY) {
                customerToPay = customer;
                break; 
            }
        }

        if (customerToPay != null) {
            int amount = cashier.collectPayment(customerToPay);
            if (amount > 0) {
                this.revenueCents += amount;
                this.paidCustomers.add(customerToPay);
            }
        }
    }

    private void handleEating() {
        for (Customer customer : customers) {
            if (customer.getStatus() == CustomerStatus.EATING) {
                customer.finishEating(this.currentTurn);
            }
        }
    }

    private void handleWaiting() {
        for (Customer customer : customers) {
            if (customer.isWaitingForService()) {
                
                customer.loseSatisfaction();
                if (customer.getStatus() == CustomerStatus.LEFT) {
                    waitingQueue.remove(customer);
                }
            }
        }
    }

    private void handleHappyHourRecovery() {
        for (Customer customer : customers) {
            customer.recoverSatisfaction(happyHour.getRecoveryPoints());
        }
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
        for (Table table : tables) {
            if (table.isFree()) {
                return table;
            }
        }
        return null; 
    }

    
    private void initializeArrivalPlan() {
        if (arrivalPlan == null) {
            Long seed = Long.getLong("restaurant.rush.seed");
            arrivalPlan = new CustomerArrivalPlan(seed == null ? new Random().nextLong() : seed);
        }
    }
}
