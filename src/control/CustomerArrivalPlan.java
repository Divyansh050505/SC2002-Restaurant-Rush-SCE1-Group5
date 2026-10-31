package control;

import entity.CriticCustomer;
import entity.Customer;
import entity.RegularCustomer;
import entity.VIPCustomer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Six factory slots, including at least one VIP and one Critic, shuffled once.
 * Customer construction is confined here; service code uses Customer only.
 */
public class CustomerArrivalPlan {
    private static final int SLOT_COUNT = 6;
    private List<BiFunction<Integer, Integer, Customer>> slots;

    public CustomerArrivalPlan(long seed) {
        slots = new ArrayList<>();
        // Separate stream from food selection; fixed salt is ASCII "CUSTOMER".
        buildSlots(new Random(seed ^ 0x435553544F4D4552L));
    }

    /** Creates the scheduled customer; retries never reshuffle its type. */
    public Customer createCustomer(int customerId, int arrivalTurn) {
        if (customerId < 1 || customerId > SLOT_COUNT) {
            throw new IllegalArgumentException("The arrival plan supports customer IDs 1 through 6.");
        }
        return slots.get(customerId - 1).apply(customerId, arrivalTurn);
    }

    private void buildSlots(Random random) {
        List<BiFunction<Integer, Integer, Customer>> types = List.of(
                RegularCustomer::new, VIPCustomer::new, CriticCustomer::new);
        slots.add(VIPCustomer::new);
        slots.add(CriticCustomer::new);
        for (int i = slots.size(); i < SLOT_COUNT; i++) {
            slots.add(types.get(random.nextInt(types.size())));
        }
        Collections.shuffle(slots, random);
    }
}
