# AI chat history – Stage 3 – HappyHour, ComboMeal, Host and game review

- **Member:** Yueer Lei
- **AI tool:** Codex (OpenAI)
- **Format:** Every prompt and every reply, in the original order. Attached documents are named beside the relevant prompts.

---

## Exchange 1

### Prompt (Yueer)

Explain to me how the addition of HappyHour, ComboMeal and Host works. How does the classes interact with each other? Do they affect previous implementations?

### Reply (Codex)

I’ll check the current classes and explain how the three additions connect to the existing game flow, including what changed in the earlier classes.

`RestaurantController` connects all three additions to the existing game. **Host adds staff capacity, ComboMeal adds a menu option, and HappyHour adds promotion rules.** They reuse much of the earlier implementation, with changes where integration is necessary.

**Host extends the existing staff system.**  
`Host.java` extends `Staff`, just like `Waiter`, `Chef` and `Cashier`. It inherits `hasActed()`, `markActed()` and `resetTurn()` to enforce one successful task per turn.

When the manager selects a customer:

```text
RestaurantUI → RestaurantController.hostSeatCustomer(id)
             → Host.seat(customer, freeTable)
```

The Host checks that it has not acted, the customer is `WAITING`, and a table is free. It then updates both sides of the seating relationship:

```java
table.assign(customer);
customer.seatAt(table);
markActed();
```

The controller removes that customer from the waiting queue.

This changes what the manager can accomplish in one turn: **the Host can seat a customer and the Waiter can immediately take that customer’s order.** Previously, seating and ordering required separate Waiter turns. Alternatively, the Host and Waiter can each seat a different customer when two tables are free. The Waiter’s own one-task limit still applies.

**ComboMeal extends the existing menu-item system.**  
`ComboMeal.java` extends `MenuItem`. Its constructor receives two existing items and calculates:

- Name: the two component names.
- Price: 90% of their combined price, rounded to cents.
- Preparation units: the sum of their preparation units.

For Salad + Burger, that produces **$21.60 and 2 preparation units**.

`Menu(long seed)` adds the three original dishes and one ComboMeal. The seed determines which combination is available:

| `seed mod 3` | ComboMeal |
|---:|---|
| 0 | Salad + Burger |
| 1 | Burger + Pasta |
| 2 | Salad + Pasta |

Regular and VIP customers can select it through their existing seeded food-selection method. Critics retain their Pasta preference.

The useful part of inheritance here is that **the existing order and kitchen code can treat a ComboMeal as a `MenuItem`**. An `Order` stores one item reference; the Chef reads that item’s preparation units; pricing reads its price. Those classes need no special `if (item instanceof ComboMeal)` branch.

The constructor uses the component items to calculate values. It does **not** retain `item1` and `item2` as fields or create two separate orders.

**HappyHour is a separate object owned by the controller.**  
`HappyHour.java` stores whether the promotion has been used and how many active turns remain. It also provides the discount calculation and recovery amount.

Activation follows this path:

```text
Host menu → RestaurantController.activateHappyHour()
          → HappyHour.activate()
```

The controller checks that no staff member has acted. `HappyHour` checks that it has not already been used, then sets its duration to two turns. Activation leaves the Host’s seating task available.

Two interactions then happen:

- **Satisfaction:** the controller applies recovery immediately on activation and again at the next turn’s start. `Customer.recoverSatisfaction(15)` checks eligibility and caps satisfaction at 100.
- **Pricing:** the Waiter asks the customer to calculate its normal bill, including any VIP discount, then applies Happy Hour if active.

The Waiter stores the resulting bill in the new `Order`. At payment time, the Cashier reads that stored amount. **It never recalculates the discount based on whether Happy Hour is still active.**

For example, a VIP choosing Salad + Burger during Happy Hour gets:

```text
Component total                 $24.00
ComboMeal price: × 90%           $21.60
VIP discount: × 90%              $19.44
Happy Hour discount: × 80%       $15.55 after rounding
```

The Chef prepares two units, and the Cashier eventually collects **$15.55**, even after Happy Hour expires.

**The additions required changes to earlier classes, while preserving their core responsibilities.**

| Earlier class | Stage 3 change |
|---|---|
| `RestaurantController` | Owns Host and HappyHour; resets Host; initializes the seeded menu; coordinates recovery and promotion countdown. |
| `RestaurantUI` | Adds the Host decision before Waiter/Chef decisions, activation option and promotion display. |
| `Menu` | Adds a seeded ComboMeal. |
| `Waiter` | Accepts HappyHour when taking an order and calculates the locked bill. |
| `Order` | Stores `billCents` and `happyHourPrice`, with an additional constructor. |
| `Cashier` | Collects the order’s stored bill. |
| `Customer` | Adds satisfaction recovery. |

`Staff`, `MenuItem` and the Chef’s preparation algorithm are reused. Customer subtype losses, Critic preference, eating/payment timing, abandonment and victory thresholds retain their existing rules.

The gameplay and inputs do change: seating can happen faster, customers have another food option, and promotions affect satisfaction and revenue. Consequently, historical Stage 2 replay inputs need the Stage 2 version; the earlier tagged implementations themselves remain unaffected.

---

## Exchange 2

### Prompt (Yueer)

**Referenced document:** `SC2002_Restaurant_Rush_Group_Assignment.docx`

imagine yourself as an expert developer, examine the code and read the assignment document. is there anywhere in the game that does not follow the requirement in the document? or is there anywhere in the game that has logic bugs that complicates a user's experience?

### Reply (Codex)

I’ll compare the assignment requirements with the current implementation, then check suspected issues in the running game. I’ll report requirement gaps and user-facing logic bugs with evidence, without changing the code.

I’ve read the assignment’s turn sequence and Stage 3 rules. I’m using the `ij-debugger` skill to verify state changes across the UI, controller, and entities, especially payment timing, Happy Hour, and failed actions. I’ll distinguish confirmed bugs from optional usability improvements.

The source compiles, and the customer, pricing, and arrival tests pass. Two documented checks fail because they still assume Stage 2 prompts and replay data. I also verified that GitHub has `stage-1` and `stage-2` tags, but no `stage-3` tag. Next I’m checking whether turn-order rules are enforced by the game logic or depend on the CLI calling methods in the right order.

Both Stage 3 replays match their saved transcripts, and the tagged Stage 1 and Stage 2 versions compile and reproduce both endings. The debugger confirmed a controller gap: direct calls can serve a dish in the same turn the Chef finishes it, and can activate Happy Hour after a turn has ended. The current CLI avoids these sequences, but the engine does not reject them itself. I’m wrapping up the requirement and usability findings.

Yes. I compared the code with the `SC2002_Restaurant_Rush_Group_Assignment.docx`. The normal CLI flow mostly follows the requirements, but I found these gaps.

1. **The controller does not enforce turn phases.**  
   The assignment requires the Waiter to act before the Chef, with game rules enforced outside the UI. I reproduced a sequence where the Chef finishes Pasta on turn 2, then `serveOrder()` successfully serves it on turn 2. The debugger confirmed `orderStatus=SERVED` and `customerState=EATING`. I also activated Happy Hour after turn 18 had ended.

   The current CLI prevents these sequences through its calling order, so players cannot normally trigger them. However, the engine accepts illegal actions. Add explicit turn phases and reject actions outside their phase. See `Waiter.java` and `RestaurantController.java`.

2. **The README’s test workflow currently fails.**  
   Compilation succeeds, but:
   - `RestaurantUITest` fails because its scripted choices do not account for the Host prompt.
   - `Stage2IntegrationTest` fails while comparing the current strategy with historical Stage 2 inputs.

   These are outdated tests rather than proof that ordinary gameplay is broken. Keep historical checks associated with their stage, and add current Stage 3 checks for Host actions, Happy Hour duration, recovery and locked bills. See `README.md`, `RestaurantUITest.java`, and `Stage2IntegrationTest.java`.

3. **The start-of-turn display only partially meets Section 3.1.**  
   It shows the restaurant state and names the staff, but the Waiter and Chef action menus appear only after earlier decisions. The assignment asks for available staff tasks at the start of every turn. Showing a compact overview before the Host decision would satisfy that requirement and help players plan. See `RestaurantUI.java`.

4. **Action selection creates avoidable retries.**  
   Seating and ordering prompts display every customer, including customers who have paid or left. Serving and preparation prompts similarly include completed or cancelled orders.

   Invalid selections are rejected correctly and preserve the staff task, but the player repeatedly navigates irrelevant entries. Filter candidates for each action while retaining their original IDs; display a clear message when no eligible target exists. This is a usability improvement, rather than a broken game rule. See `RestaurantUI.java`.

5. **Two submission requirements remain outstanding.**  
   I checked the remote repository: `stage-1` and `stage-2` exist, but `stage-3` is missing. Sections 4–5 require all three tagged runnable versions. The README also contains no video timestamps, required by Section 6.2.

The checks confirmed correct ordinary payment timing, subtype satisfaction loss, abandonment, ComboMeal calculations, Happy Hour recovery and locked pricing. All three customer/arrival test suites pass. Both recorded endings also reproduce correctly for each stage, using its corresponding source version.

No source changes were retained.
