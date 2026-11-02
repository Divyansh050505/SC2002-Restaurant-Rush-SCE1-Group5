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
New games print a six-digit seed; enter that seed in Replay to reproduce customer types and food choices within Stage 2.
You still choose the staff actions, so the result can differ. Choose `0` to exit the startup menu or return from the seed prompt.

Each turn the game shows the restaurant state, then asks for one Waiter task and one Chef task by number.
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

## Stage 1 to Stage 2 design changes

The implementation follows the [Stage 2 class diagram](docs/stage2_class_diagram.drawio.svg), with an [editable draw.io file](docs/stage2_class_diagram.drawio). Original Stage 1 diagrams and recordings remain unchanged. Add the implementation commit/compare link when this transition is committed and tagged.

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

Run integration checks from the repository root so they can read the recorded Stage 2 input files. The older UI checks use controlled fixtures; the Stage 2 integration checks use the real controller, staff, entities and UI.

## Source layout
| Package | Role |
|---|---|
| `RestaurantApp` (default package) | Entry point: creates the controller and UI |
| `boundary` | `RestaurantUI`: console input and output only |
| `control` | `RestaurantController`: turn sequence/game state; `CustomerArrivalPlan`: seeded type setup |
| `entity` | Customers, staff, menu, orders, tables, enums |

## Game settings
- 18 turns, 2 tables
- Staff: 1 Waiter, 1 Chef, 1 Cashier
- Customers arrive at the start of turns 1, 4, 7, 10, 13 and 16
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units)
- Victory: at least 4 paid customers, revenue at least $45.00, average satisfaction of paid customers at least 60
