package control;

import entity.Customer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Seeded factory coverage, bounds and repeatability without game/UI fixtures. */
public class CustomerArrivalPlanTest {
    private static int checks;

    public static void main(String[] args) {
        Set<List<String>> arrangements = new HashSet<>();
        for (long seed = -100; seed <= 100; seed++) {
            CustomerArrivalPlan first = new CustomerArrivalPlan(seed);
            CustomerArrivalPlan replay = new CustomerArrivalPlan(seed);
            List<String> types = new ArrayList<>();
            for (int id = 1; id <= 6; id++) {
                Customer c = first.createCustomer(id, 3 * id - 2);
                types.add(c.getTypeLabel());
                check(c.getCustomerId() == id && c.getSatisfaction() == 100 && c.isWaitingForService(),
                        "factory passes ID and creates a fresh unserved arrival");
                check(c.getClass() == first.createCustomer(id, 3 * id - 2).getClass(), "retry never rerolls type");
            }
            for (int id = 6; id >= 1; id--) {
                check(replay.createCustomer(id, 3 * id - 2).getTypeLabel().equals(types.get(id - 1)),
                        "same seed reproduces every slot independently of creation order");
            }
            check(types.size() == 6 && types.contains("VIP") && types.contains("Critic"), "guaranteed special types");
            arrangements.add(types);
        }
        check(arrangements.size() > 1, "different seeds vary customer types/order");
        CustomerArrivalPlan plan = new CustomerArrivalPlan(Long.MAX_VALUE);
        expect(() -> plan.createCustomer(0, 1));
        expect(() -> plan.createCustomer(7, 1));
        expect(() -> plan.createCustomer(1, 0));
        Customer last = plan.createCustomer(6, 16);
        expect(() -> last.finishEating(15));
        System.out.println("PASS: " + checks + " arrival plan checks.");
    }

    private static void expect(Runnable action) {
        try { action.run(); }
        catch (IllegalArgumentException e) { check(!e.getMessage().isBlank(), "descriptive bounds error"); return; }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
