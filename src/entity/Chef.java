package entity;

/**
 * Stage One chef skeleton.
 */
public class Chef extends Staff {
    public Chef() {
        // insert implementation here
    }

    public ActionResult prepare(Order order) {
        

        if (order.getStatus()==OrderStatus.PREPARING) {

            String message=
            "Added 1 to the unitsDone , "
            +"for the Order No. "
            +order.getOrderNo();

            return ActionResult.success(message);

        }
        
        else {

            String message=
            "Error (Order no. "
            +order.getOrderNo()
            +" not in \"PREPARING\" status)";

            return ActionResult.failure(message);
        }
    }
}
