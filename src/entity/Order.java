package entity;


public class Order {
    private int orderNo;
    private Customer customer;
    private MenuItem item;
    private int unitsDone = 0;
    private OrderStatus status;
    private int billCents;
    private boolean happyHourPrice;

    
    public Order(int orderNo, Customer customer, MenuItem item) {
        this(orderNo, customer, item, 0, false);
        if (customer != null && item != null) {
            this.billCents = customer.calculateBillCents(item.getPriceCents());
        }
    }

   
    public Order(int orderNo, Customer customer, MenuItem item, int billCents, boolean happyHourPrice) {

        this.orderNo=orderNo;
        this.customer=customer;
        this.item=item;
        this.status=OrderStatus.PREPARING;
        this.billCents=billCents;
        this.happyHourPrice=happyHourPrice;
    }

    public void advanceOneUnit() {
        

        if (this.unitsDone<this.item.getPrepUnits()) {

            this.unitsDone+=1;

        }

        if (this.unitsDone<this.item.getPrepUnits()) {

            this.status=OrderStatus.PREPARING;
        }

        else if (this.unitsDone==this.item.getPrepUnits()) {

            this.status=OrderStatus.READY;

        }
        
    }

    public int getRemainingUnits() {
        

       return 
       (this.item.getPrepUnits()-this.unitsDone);

    }

    public boolean isReady() {
        

        if (this.status==OrderStatus.READY) {

            return true;
        }
        else {
            
            return false;
        }
        
    }

    public void markServed() {
       

        this.status=OrderStatus.SERVED;
    }

    public void markPaid() {
       

        this.status=OrderStatus.PAID;
    }

    public void cancel() {
        

        status=OrderStatus.CANCELLED;
    }

    public int getOrderNo() {
        

        return this.orderNo;
    }

    public Customer getCustomer() {
        

        return this.customer;
    }

    public MenuItem getItem() {
        

        return this.item;
    }

    public OrderStatus getStatus() {
       

        return this.status;
    }

    public int getBillCents() {
        return this.billCents;
    }

    public boolean isHappyHourPrice() {
        return this.happyHourPrice;
    }
}
