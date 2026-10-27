package entity;

/**
 * Stage One chef: prepares at most one unit per turn.
 */
public class Chef extends Staff {
    public Chef() {

    }

    public ActionResult prepare(Order order) {
        if (hasActed()) {
            return ActionResult.failure("The Chef has already acted this turn.");
        }
        if (order == null) {
            return ActionResult.failure("Select an order to prepare.");
        }
        if (order.getStatus() != OrderStatus.PREPARING) {
            return ActionResult.failure("Order #" + order.getOrderNo() + " is not awaiting preparation.");
        }

        order.advanceOneUnit();
        markActed();

        String description = "Order #" + order.getOrderNo() + " (" + order.getItem().getName() + ")";
        if (order.isReady()) {
            return ActionResult.success(description + " is READY to serve next turn.");
        }
        int remaining = order.getRemainingUnits();
        return ActionResult.success("Chef prepared one unit of " + description + ". " + remaining
                + (remaining == 1 ? " preparation unit remaining." : " preparation units remaining."));
    }
}
