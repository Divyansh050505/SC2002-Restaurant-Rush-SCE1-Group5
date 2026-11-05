package entity;

public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;
    private static final int RECOVERY_POINTS = 15;

    private boolean used = false;
    private int turnsRemaining = 0;

    
    public ActionResult activate() {
        if (used) {
            return ActionResult.failure("Happy Hour can only be used once per game.");
        }
        used = true;
        turnsRemaining = DURATION_TURNS;
        return ActionResult.success("Happy Hour is ON for this turn and the next. "
                + "Orders taken now get " + DISCOUNT_PERCENT + "% off.");
    }


    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    
    public int applyDiscount(int billCents) {
        return (int) ((billCents * (100L - DISCOUNT_PERCENT) + 50) / 100);
    }

    public boolean isActive() {
        return turnsRemaining > 0;
    }

    public boolean isUsed() {
        return used;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public int getRecoveryPoints() {
        return RECOVERY_POINTS;
    }
}
