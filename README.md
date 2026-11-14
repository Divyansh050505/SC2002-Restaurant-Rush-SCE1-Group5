# SC2002 Restaurant Rush – SCE1 Group 5

Turn-based restaurant management game in Java (command-line). The current version implements Stage 2 customer behaviour.

**Repository:** https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5

## Team
| Member | Responsibility |
|---|---|
| Divyansh (Divyansh050505) | Game controller, repo lead |
| (name) | Customers + satisfaction |
| (name) | Menu, orders, kitchen |
| (name) | Waiter, Cashier, restaurant state |
| (name) | Console UI |

## Requirements
JDK 17 or later. No external libraries; works fully offline.

## Build and run
Run these from the repository root (the folder containing `src`).

**Mac / Linux:**
```
find src -name "*.java" > sources.txt
javac --release 17 -d out @sources.txt
java -cp out RestaurantApp
```
**Windows (Command Prompt):**
```
dir /s /b src\*.java > sources.txt
javac --release 17 -d out @sources.txt
java -cp out RestaurantApp
```

## How to play
At launch, choose **1. New game** or **2. Replay using a seed**.
New games print a six-digit seed; enter that seed in Replay to reproduce customer types, the ComboMeal on offer and food choices.
You still choose the staff actions, so the result can differ. Choose `0` to exit the startup menu or return from the seed prompt.

Each turn the game shows the restaurant state, then asks for one Host task, one Waiter task and one Chef task by number.
The Host can only seat a waiting customer; the Host menu also offers Happy Hour until it has been used.
Customers and orders are chosen by the number shown next to them; `0` goes back or makes the staff member wait.
An invalid choice explains why it failed and asks again without using up the staff task.

## Replaying the test runs
The recorded runs in `test-runs/` can be replayed by redirecting them into the game (after building as above):
```
java -cp out RestaurantApp < test-runs/stage2_victory.txt
java -cp out RestaurantApp < test-runs/stage2_defeat.txt
```

Both Stage 2 examples use seed **482193**, contain VIP and Critic arrivals, and run all 18 turns. The victory pays four customers, including the VIP and Critic, earns **$58.20**, and records average paid-customer satisfaction **74.00**. See [test notes](test-runs/README.md) for the strategies and full transcripts. Stage 1 recordings are preserved for the Stage 1 revision; they are not Stage 2 replay fixtures.

## Stage 2 customer rules

| Customer | Loss per unserved turn | Bill | Food choice |
|---|---:|---|---|
| Regular | 8 | Full price | Seeded menu plan |
| VIP | 5 | 10% off | Same seeded menu plan |
| Critic | 12 | Full price | Pasta; default seeded selection if a custom menu has no Pasta |

Every customer starts at satisfaction 100. Waiting for a table, waiting to order, or waiting for food causes the subtype's loss at turn end, clamped at zero. At zero the customer leaves, cancels an unserved order and releases their table. Served/eating/ready-to-pay customers lose no satisfaction. All types eat for one full turn: service on turn T, readiness at the end of T+1, payment on T+2 at the earliest.

Six arrivals retain the base schedule. Their type plan always contains exactly four Regular customers, one VIP, and one Critic. Only their arrival positions are shuffled; the customer counts never vary. `CustomerArrivalPlan` uses `new Random(seed ^ 0x435553544F4D4552L)`; its random stream is separate from the existing food-plan stream. The controller creates this plan once on the first turn, after the UI selects the seed.

Regular/VIP food choices retain the existing six-slot plan: one of each menu item plus three random choices, shuffled using the game seed and indexed by customer ID. Retries and the manager's ordering priority do not reroll choices. Critics replace their own slots with Pasta, so mixed groups need not order every menu item. The manager never chooses the dish.

All bills use integer cents with half cents rounded up. VIP bills for Salad/Burger/Pasta are **$8.10 / $13.50 / $16.20**. Cashier and UI share the same customer pricing method, so displayed payments match revenue.

## Stage 3 Happy Hour rules

- Switch it on with option **2** in the Host menu, the first menu of each turn. It is only allowed at the start of a turn (before the Host, Waiter or Chef acts), does not use the Host's task, and can be used once per game.
- It lasts for the turn it is switched on and the next turn. The turn display shows how many turns are left.
- Orders taken while it is active get 20% off. The customer's own discount comes first, then Happy Hour, rounding half cents up: a VIP Burger is $15 × 0.9 × 0.8 = **$10.80**.
- Each order's bill is locked when the order is taken (`Order` stores it). Orders placed before Happy Hour keep their price, and Happy Hour orders keep their discount after it ends. The Cashier charges the locked bill.
- At the start of each active turn, every customer still waiting for a table or for food (waiting, seated, or ordered and not yet served) recovers 15 satisfaction, up to 100. Customers who have been served or have left are not affected.

## Stage 1 to Stage 2 design changes

The implementation follows the [Stage 2 class diagram](docs/stage2_class_diagram.drawio.svg), with an [editable draw.io file](docs/stage2_class_diagram.drawio). Original Stage 1 diagrams and recordings remain unchanged. Implementation changes from Stage 1 to Stage 2: [stage-1...stage-2 comparison](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2).

| Files | Change and reason |
|---|---|
| `src/entity/RegularCustomer.java`, `VIPCustomer.java`, `CriticCustomer.java` | New concrete customer types implement the UML's loss, label, discount and preference hooks. |
| `src/control/CustomerArrivalPlan.java` | New helper confines concrete customer construction and seeded type scheduling to game setup. |
| `src/entity/Customer.java` | Abstract parent retains private state and lifecycle validation; final choice/loss/pricing methods dispatch through narrow protected hooks. |
| `src/entity/Cashier.java` | Collects the customer-supplied bill instead of the undiscounted menu price. |
| `src/control/RestaurantController.java` | Initializes the arrival plan after seed selection and uses it for the six arrivals. Service loops and win criteria retain their existing rules. |
| `src/boundary/RestaurantUI.java` | Displays customer-supplied labels and the same bill used by the Cashier. |
| `tests/entity/CustomerTest.java`, `tests/RestaurantUITest.java` | Existing Regular regression fixtures now instantiate `RegularCustomer`. |
| `tests/entity/Stage2CustomerTest.java`, `tests/control/CustomerArrivalPlanTest.java`, `tests/Stage2IntegrationTest.java` | Add subtype contracts, actual billing/abandonment integration, seeded scheduling, and real CLI replays. |
| `test-runs/stage2_*.txt` | New reproducible manager inputs and application output for both endings. |

SRP separates scheduling, lifecycle/behaviour, transactions and presentation. OCP and DIP appear in service code that uses `Customer` without subtype checks; concrete type registration stays in the arrival helper. LSP is checked by shared lifecycle and validation tests across all types, including the Critic's menu fallback. The hook API stays small without artificial interfaces (ISP). `ActionResult` remains the outcome value object, rather than a control class.


## Stage 3: ComboMeal selection rule

Each game offers one ComboMeal consisting of two existing menu items.

The ComboMeal offered is determined by the game seed using `Math.floorMod(seed, 3L)`:

- Remainder 0: Salad + Burger
- Remainder 1: Burger + Pasta
- Remainder 2: Salad + Pasta

The ComboMeal costs 90% of the combined component prices, rounded to the nearest cent using integer arithmetic. Its preparation units equal the sum of the preparation units of both components.

The menu is initialized once at the beginning of the first turn, after the game seed has been selected.

Regular and VIP customers may select the ComboMeal through the existing seeded six-slot food selection mechanism. Critic customers retain their Pasta preference.

Replaying the same seed produces the same available ComboMeal and customer food choices. The manager does not choose the customers' food.

## Stage 3 Host rule

The Host seats at most one queued customer per turn, in addition to the Waiter's task. The Waiter can still seat customers, so two customers can be seated in the same turn. The Host does not take orders or serve.

## Stage 2 to Stage 3 design changes

Implementation changes from Stage 2 to Stage 3: [stage-2...stage-3 comparison](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-2...stage-3).

| Files | Change and reason |
|---|---|
| `src/entity/Host.java` | New staff member. Extends `Staff`, so it reuses the one-task-per-turn rule; its only action is seating. |
| `src/entity/ComboMeal.java` | New menu item made of two existing items. Extends `MenuItem`, so orders, the Chef and pricing use it without type checks. |
| `src/entity/HappyHour.java` | New pricing policy object: once per game, two turns, 20% off, satisfaction recovery. |
| `src/entity/Menu.java` | Adds the seeded ComboMeal after the three base items. |
| `src/entity/Order.java` | Stores the bill when the order is taken, so the price is locked even after Happy Hour ends. |
| `src/entity/Waiter.java` | Works out the locked bill (customer discount first, then Happy Hour) when taking an order. |
| `src/entity/Cashier.java` | Charges the order's locked bill instead of recalculating it. |
| `src/entity/Customer.java` | Adds `recoverSatisfaction` for Happy Hour. Existing rules are unchanged. |
| `src/control/RestaurantController.java` | Owns the Host and Happy Hour, creates the menu from the game seed, applies Happy Hour recovery at turn start and counts it down at turn end. |
| `src/boundary/RestaurantUI.java` | Adds the Host step before the Waiter, the Happy Hour option and status line, and shows each order's locked bill. |

Several source files also had comments removed during Stage 3; this does not change behaviour. Stage 2 recordings are kept for the Stage 2 revision; the Host prompt and ComboMeal mean they are not Stage 3 replay fixtures.


## Run the checks

No external test framework is needed. On Mac/Linux, from the repository root:

```sh
find src tests -name '*.java' > /tmp/restaurant-rush-test-sources.txt
javac --release 17 -d out/tests @/tmp/restaurant-rush-test-sources.txt
java -cp out/tests entity.CustomerTest
java -cp out/tests entity.Stage2CustomerTest
java -cp out/tests control.CustomerArrivalPlanTest
java -cp out/tests RestaurantUITest
java -cp out/tests Stage2IntegrationTest
```

The UI checks use controlled fixtures and cover the Host and Happy Hour menus. The Stage 2 integration checks use the real controller, staff, entities and UI; since Stage 3 the Host waits in those runs, so they check that the Stage 2 customer rules still hold.

## Source layout
| Package | Role |
|---|---|
| `RestaurantApp` (default package) | Entry point: creates the controller and UI |
| `boundary` | `RestaurantUI`: console input and output only |
| `control` | `RestaurantController`: turn sequence/game state; `CustomerArrivalPlan`: seeded type setup |
| `entity` | Customers, staff, menu, orders, tables, enums |

## Game settings
- 18 turns, 2 tables
- Staff: 1 Host (Stage 3), 1 Waiter, 1 Chef, 1 Cashier
- Customers arrive at the start of turns 1, 4, 7, 10, 13 and 16
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units), plus one seeded ComboMeal (Stage 3)
- Victory: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60
