package entity;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;


public abstract class Customer {
    
    private static final Map<Menu, int[]> CHOICES_BY_MENU = new WeakHashMap<>();

    private final int customerId;
    private final int arrivalTurn;
    private int satisfaction = 100;
    private CustomerStatus status;
    private int servedTurn = -1;
    private Table table;
    private Order order;

   
    protected Customer(int customerId, int arrivalTurn) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        if (arrivalTurn <= 0) {
            throw new IllegalArgumentException("Arrival turn must be positive.");
        }
        this.customerId = customerId;
        this.arrivalTurn = arrivalTurn;
        this.status = CustomerStatus.WAITING;
    }

   
    public final MenuItem chooseItem(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("A menu is required to choose food.");
        }
        if (status != CustomerStatus.SEATED) {
            throw new IllegalStateException("Only a seated customer without an order can choose food.");
        }
        int itemCount = menu.getItemCount();
        if (itemCount <= 0) {
            throw new IllegalArgumentException("Cannot choose food from an empty menu.");
        }
        if (itemCount > 6) {
            throw new IllegalArgumentException("Six customers cannot order every item from a menu larger than six items.");
        }

        MenuItem item;
        try {
            item = selectItem(menu);
        } catch (IndexOutOfBoundsException exception) {
            throw new IllegalStateException("The menu does not contain the selected item.", exception);
        }
        if (item == null) {
            throw new IllegalStateException("The selected menu item is missing.");
        }
        return item;
    }

    
    protected MenuItem selectItem(Menu menu) {
        int itemCount = menu.getItemCount();
        int index;
        synchronized (CHOICES_BY_MENU) {
            int[] choices = CHOICES_BY_MENU.get(menu);
            if (choices == null) {
                Long seed = Long.getLong("restaurant.rush.seed");
                Random random = seed == null ? new Random() : new Random(seed);
                choices = new int[6];
                for (int i = 0; i < choices.length; i++) {
                    choices[i] = i < itemCount ? i : random.nextInt(itemCount);
                }
                
                for (int i = choices.length - 1; i > 0; i--) {
                    int other = random.nextInt(i + 1);
                    int temporary = choices[i];
                    choices[i] = choices[other];
                    choices[other] = temporary;
                }
                CHOICES_BY_MENU.put(menu, choices);
            }
            index = choices[(customerId - 1) % choices.length];
        }
        return menu.getItem(index);
    }

    
    public final void loseSatisfaction() {
        if (!isWaitingForService()) {
            return;
        }
        int previousSatisfaction = satisfaction;
        satisfaction = Math.max(0, satisfaction - getSatisfactionLoss());
        if (satisfaction == 0) {
            try {
                leave();
            } catch (RuntimeException exception) {
                
                satisfaction = previousSatisfaction;
                throw exception;
            }
        }
    }

   
    public void recoverSatisfaction(int points) {
        if (isWaitingForService()) {
            satisfaction = Math.min(100, satisfaction + points);
        }
    }

    public boolean isWaitingForService() {
        return status == CustomerStatus.WAITING
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED;
    }

    
    public void finishEating(int currentTurn) {
        if (currentTurn < arrivalTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the customer's arrival.");
        }
        if (status != CustomerStatus.EATING) {
            return;
        }
        if (currentTurn < servedTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the served turn.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException("A customer can only eat their own served order.");
        }
        if (currentTurn > servedTurn) {
            status = CustomerStatus.READY_TO_PAY;
        }
    }

   
    public void leave() {
        if (status == CustomerStatus.LEFT) {
            return;
        }
        if (!isWaitingForService() || satisfaction != 0) {
            throw new IllegalStateException("Only an unserved customer with zero satisfaction can leave.");
        }
        if (status != CustomerStatus.WAITING && table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order != null) {
            OrderStatus orderStatus = order.getStatus();
            if (orderStatus != OrderStatus.PREPARING
                    && orderStatus != OrderStatus.READY
                    && orderStatus != OrderStatus.CANCELLED) {
                throw new IllegalStateException("A served or paid order cannot be abandoned.");
            }
            if (orderStatus != OrderStatus.CANCELLED) {
                order.cancel();
            }
        }
        if (table != null) {
            table.release();
        }
        table = null;
        status = CustomerStatus.LEFT;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

   
    public void markServed(int currentTurn) {
        if (currentTurn < arrivalTurn) {
            throw new IllegalArgumentException("The current turn cannot be before the customer's arrival.");
        }
        if (status != CustomerStatus.ORDERED) {
            throw new IllegalStateException("Only a customer awaiting their order can be served.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.READY && orderStatus != OrderStatus.SERVED) {
            throw new IllegalStateException("The customer's order must be ready before it can be served.");
        }

        if (orderStatus == OrderStatus.READY) {
            order.markServed();
        }
        servedTurn = currentTurn;
        status = CustomerStatus.EATING;
    }

    public Table getTable() {
        return table;
    }

   
    public void seatAt(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("A table is required to seat a customer.");
        }
        if (status != CustomerStatus.WAITING) {
            throw new IllegalStateException("Only a waiting customer without a table or order can be seated.");
        }
        Customer occupant = table.getCustomer();
        if (occupant != this && (occupant != null || !table.isFree())) {
            throw new IllegalStateException("The table is already occupied.");
        }

        
        if (occupant != this) {
            table.assign(this);
        }
        this.table = table;
        status = CustomerStatus.SEATED;
    }

    public Order getOrder() {
        return order;
    }

    
    public void placeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("An order is required.");
        }
        if (status != CustomerStatus.SEATED) {
            throw new IllegalStateException("Only a seated customer without an order can place one.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        if (order.getCustomer() != this) {
            throw new IllegalArgumentException("The order belongs to a different customer.");
        }
        if (order.getItem() == null || order.getStatus() != OrderStatus.PREPARING) {
            throw new IllegalArgumentException("A new order must contain a menu item and be PREPARING.");
        }

        this.order = order;
        status = CustomerStatus.ORDERED;
    }

   
    public void markPaid() {
        if (status != CustomerStatus.READY_TO_PAY) {
            throw new IllegalStateException("Only a customer who has finished eating can pay.");
        }
        if (table.getCustomer() != this) {
            throw new IllegalStateException("The customer must occupy their assigned table.");
        }
        OrderStatus orderStatus = order.getStatus();
        if (orderStatus != OrderStatus.SERVED && orderStatus != OrderStatus.PAID) {
            throw new IllegalStateException("Only a served order can be paid for.");
        }

        
        if (orderStatus == OrderStatus.SERVED) {
            order.markPaid();
        }
        table.release();
        table = null;
        status = CustomerStatus.PAID;
    }

    
    protected abstract int getSatisfactionLoss();

    
    public abstract String getTypeLabel();

    
    protected int getDiscountPercent() {
        return 0;
    }

   
    public final int calculateBillCents(int basePriceCents) {
        if (basePriceCents < 0) {
            throw new IllegalArgumentException("The base price cannot be negative.");
        }
        int discount = getDiscountPercent();
        if (discount < 0 || discount > 100) {
            throw new IllegalStateException("The discount percentage must be between 0 and 100.");
        }
        return (int) ((basePriceCents * (100L - discount) + 50) / 100);
    }
}
