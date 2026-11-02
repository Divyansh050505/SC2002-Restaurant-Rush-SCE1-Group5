# Recorded runs

## Stage Two

Build the current source using the main README, then run:

```sh
java -cp out RestaurantApp < test-runs/stage2_victory.txt
java -cp out RestaurantApp < test-runs/stage2_defeat.txt
```

Both recordings use replay seed **482193**. Their exact numeric inputs include the startup selection and seed. The corresponding `stage2_*_transcript.txt` files contain the application's complete redirected output; input is not echoed. Compare a fresh replay with its saved transcript to verify repeatability.

The customer types for this seed are Critic, Regular, Regular, VIP, Regular, Regular at turns 1, 4, 7, 10, 13, 16. Every seed has exactly four Regulars, one VIP and one Critic; only their positions change.

Victory strategy: serve the oldest READY order first, otherwise take the oldest seated customer's order, otherwise seat the oldest waiting customer if a table is free. The Chef advances the oldest PREPARING order each turn, including a newly taken order. There is no customer-type branch in this strategy.

| Customer | Type | Food | Final state | Satisfaction | Amount collected |
| --- | --- | --- | --- | ---: | ---: |
| 1 | Critic | Pasta | PAID | 64 | $18.00 |
| 2 | Regular | Salad | PAID | 76 | $9.00 |
| 3 | Regular | Burger | PAID | 76 | $15.00 |
| 4 | VIP | Pasta | PAID | 80 | $16.20 |
| 5 | Regular | Pasta | READY_TO_PAY | 68 | $0.00 |
| 6 | Regular | No order | SEATED | 76 | $0.00 |

```text
FINAL RESULT: VICTORY
Served: 5 | Paid: 4 | Departures: 0
Revenue: $58.20 | Average paid-customer satisfaction: 74.00
Turns used: 18 / 18
```

Defeat uses the same Waiter priorities but leaves the Chef on Wait. The Critic arriving on turn 1 leaves on turn 9 and cancels its unfinished Pasta; the Regular arriving on turn 4 leaves on turn 16 and cancels its unfinished Salad. Later arrivals reuse the freed tables, but no payments occur.

```text
FINAL RESULT: DEFEAT
Served: 0 | Paid: 0 | Departures: 2
Revenue: $0.00 | Average paid-customer satisfaction: N/A (no paid customers)
Turns used: 18 / 18
```

The dependency-free Stage 2 tests also cover all three customer types through the same lifecycle, abandonment of both preparing and READY orders, half-cent rounding, actual Cashier transactions, exact 4R/1V/1C counts across 201 seeded type plans, initialization after seed selection, and repeated real UI replays. A separate controller scenario seats the turn-1 Critic, takes its Pasta order, leaves it unprepared, and verifies cancellation and table release on turn 9. See the main README for test commands.

## Stage One (historical)

These inputs and transcripts are unchanged evidence for source baseline `084d133`. Run them with that Stage 1 revision, rather than with the current Stage 2 source, whose types and Critic preferences change the schedule.

Both runs use replay seed **482190** and the same six-customer food plan.
Each run starts from a fresh game and completes all 18 turns.

- `stage1_victory.txt`: exact numeric inputs, including startup selection and seed.
- `stage1_victory_transcript.txt`: complete terminal output with every manager input echoed at its prompt.
- `stage1_defeat.txt`: exact numeric inputs, including startup selection and seed.
- `stage1_defeat_transcript.txt`: complete terminal output with every manager input echoed at its prompt.

### Strategies and results

Victory: serve READY orders first, then take seated customers' orders, then seat waiting customers. The Chef advances the oldest PREPARING order whenever possible.

```text
FINAL RESULT: VICTORY
Served: 5 | Paid: 5 | Departures: 0
Revenue: $69.00 | Average paid-customer satisfaction: 77.60
Turns used: 18 / 18
Victory targets (all three must pass):
  PASS Paid customers: 5 / 4
  PASS Revenue: $69.00 / $45.00
  PASS Average paid-customer satisfaction: 77.60 / 60
```

Defeat: seat customers and take their orders, but keep the Chef on Wait. Unserved customers eventually leave, their unfinished orders are cancelled, and no revenue is collected.

```text
FINAL RESULT: DEFEAT
Served: 0 | Paid: 0 | Departures: 2
Revenue: $0.00 | Average paid-customer satisfaction: N/A (no paid customers)
Turns used: 18 / 18
Victory targets (all three must pass):
  FAIL Paid customers: 0 / 4
  FAIL Revenue: $0.00 / $45.00
  FAIL Average paid-customer satisfaction: N/A (no paid customers) / 60
```

### Reproduce from the repository root at the Stage 1 revision

Build using the instructions in the main README, then run:

```bash
java -cp out RestaurantApp < test-runs/stage1_victory.txt
java -cp out RestaurantApp < test-runs/stage1_defeat.txt
```

Redirected input is not echoed by the application; the saved terminal transcripts include the original input echoes.

### All manager choices by turn

| Turn | Victory: Waiter | Victory: Chef | Defeat: Waiter | Defeat: Chef |
| --- | --- | --- | --- | --- |
| 1 | Seat Customer #1 | Wait | Seat Customer #1 | Wait |
| 2 | Take Customer #1 order | Prepare Order #1 | Take Customer #1 order | Wait |
| 3 | Serve Order #1 | Wait | Wait | Wait |
| 4 | Seat Customer #2 | Wait | Seat Customer #2 | Wait |
| 5 | Take Customer #2 order | Prepare Order #2 | Take Customer #2 order | Wait |
| 6 | Serve Order #2 | Wait | Wait | Wait |
| 7 | Seat Customer #3 | Wait | Wait | Wait |
| 8 | Take Customer #3 order | Prepare Order #3 | Wait | Wait |
| 9 | Wait | Prepare Order #3 | Wait | Wait |
| 10 | Serve Order #3 | Wait | Wait | Wait |
| 11 | Seat Customer #4 | Wait | Wait | Wait |
| 12 | Take Customer #4 order | Prepare Order #4 | Wait | Wait |
| 13 | Seat Customer #5 | Prepare Order #4 | Wait | Wait |
| 14 | Serve Order #4 | Wait | Seat Customer #3 | Wait |
| 15 | Take Customer #5 order | Prepare Order #5 | Take Customer #3 order | Wait |
| 16 | Serve Order #5 | Wait | Wait | Wait |
| 17 | Seat Customer #6 | Wait | Seat Customer #4 | Wait |
| 18 | Take Customer #6 order | Prepare Order #6 | Take Customer #4 order | Wait |
