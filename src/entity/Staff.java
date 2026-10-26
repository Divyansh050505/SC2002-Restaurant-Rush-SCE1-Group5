package entity;

/**
 * Stage One staff base class skeleton.
 */
public abstract class Staff {
    private boolean actedThisTurn = false;

    /**
     * Constructs a staff member. The constructor is empty as the actedThisTurn
     * flag is already initialized to false by default.
     */
    public Staff() {
        // No specific initialization is needed for the base class.
    }

    /**
     * Checks if the staff member has already acted this turn.
     * @return true if they have acted, false otherwise.
     */
    public boolean hasActed() {
        return actedThisTurn;
    }

    /**
     * Resets the staff member's action status for the start of a new turn.
     * This is called by the controller at the beginning of each turn.
     */
    public void resetTurn() {
        this.actedThisTurn = false;
    }

    /**
     * Marks that the staff member has performed their action for the current turn.
     * This should be called by the subclass (e.g., Waiter, Chef) after a successful action.
     */
    protected void markActed() {
        this.actedThisTurn = true;
    }
}
