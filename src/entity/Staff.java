package entity;

public abstract class Staff {
    private boolean actedThisTurn = false;

    public Staff() {
    }

    public boolean hasActed() {
        return actedThisTurn;
    }

    public void resetTurn() {
        this.actedThisTurn = false;
    }

    protected void markActed() {
        this.actedThisTurn = true;
    }
}
