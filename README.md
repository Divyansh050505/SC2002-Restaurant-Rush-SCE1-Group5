# SC2002 Restaurant Rush – SCE1 Group 5

Turn-based restaurant management game in Java (command-line). The current version implements Stage 3: customer subtypes, a Host, seeded ComboMeals and Happy Hour.

**Repository:** https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5

The repository URL is also included on the cover page of the [group report](docs/report/SCE1_Grp5_Report.pdf).

## Submission links

**Stage tags** (each one is a runnable commit):
[stage-1](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/stage-1) |
[stage-2](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/stage-2) |
[stage-3](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/stage-3)

**Stage comparisons:**
[stage-1...stage-2](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2) |
[stage-2...stage-3](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-2...stage-3)

**Diagrams:**
[Stage 1 class](docs/diagrams/class/stage1_class_diagram.drawio.svg) |
[Stage 2 class](docs/diagrams/class/stage2_class_diagram.drawio.svg) |
[Stage 3 class](docs/diagrams/class/stage3_class_diagram.drawio.svg) |
[Stage 3 sequence](docs/diagrams/sequence/stage3_sequence_diagram.pdf)

**AI history:** each member's full AI chat history is kept on their own branch, in its `ai-history/` folder, split by stage.

| Member | Branch |
|---|---|
| Divyansh | [ai-history/divyansh](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/ai-history/divyansh) |
| Lei Yueer | [ai-history/yueer](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/ai-history/yueer) |
| Shi Hong | [ai-history/shi-hong](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/ai-history/shi-hong) |
| Jan Guirey | [ai-history/jan-guirey](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/ai-history/jan-guirey) |
| Liu Yanxi | [ai-history/liuyanxi](https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/tree/ai-history/liuyanxi) |

**Report, video and declarations:**
[Group report (PDF)](docs/report/SCE1_Grp5_Report.pdf) |
[Demo video](docs/video/SCE1_Grp5_GrpAssignment_Video.mp4) |
[GAI declarations](docs/declarations/)

## Video timestamps
These timestamps are also included in Appendix A of the [group report](docs/report/SCE1_Grp5_Report.pdf).

Timestamps refer to the submitted video (total length 14:55).

**Required video evidence**

| Required evidence | Timestamp |
|---|---|
| Launch from the submitted project | 02:52 |
| Meaningful choices and constrained staff (Host, Waiter and Chef tasks; rejected early serve) | 02:52 – 03:27; outcome comparison at 04:24 – 04:56 |
| READY, SERVED, eating and payment | 03:27 – 03:49; sequence walkthrough at 11:14 – 12:41 |
| VIP and Critic behaviour | Critic chooses Pasta at 02:52 – 03:27; VIP pricing at 03:49 – 04:24; design explanation at 05:44 – 06:30 |
| Stage 3 features (Host, ComboMeal, Happy Hour) | Host at 02:52; Happy Hour and ComboMeal at 03:49 – 04:24; design at 07:58 – 10:39 |
| One reproducible victory and one defeat (seed 482193) | 04:24 – 04:56 |
| Stage comparisons and diagrams | Class diagrams at 00:37 – 01:53; Stage 1 to Stage 2 at 04:56 – 07:58; Stage 2 to Stage 3 at 07:58 – 10:39; sequence diagram at 10:39 – 13:06 |
| Individual explanations and reflection | All main segments, then 13:06 – 14:52 |

**Video index**

| Time | Speaker | Segment |
|---|---|---|
| 00:00 – 00:37 | Tomar Divyansh | Introduction: Restaurant Rush, base game settings and victory conditions |
| 00:37 – 01:15 | Tomar Divyansh | The three stages and the Stage 1, 2 and 3 class diagrams |
| 01:15 – 02:14 | Tomar Divyansh | Responsibilities (boundary, control, entity), Single Responsibility Principle, composition and association |
| 02:14 – 02:52 | Tomar Divyansh | Encapsulation and inheritance (Customer, Staff) |
| 02:52 – 03:27 | Eng Shi Hong | Gameplay: launch with replay seed 482193, Host seats the Critic, Critic orders Pasta, early serve rejected |
| 03:27 – 03:49 | Eng Shi Hong | Serving the READY dish, eating, payment and table release |
| 03:49 – 04:24 | Eng Shi Hong | Happy Hour activation and the locked VIP ComboMeal bill ($15.55) |
| 04:24 – 04:56 | Eng Shi Hong | Stage 3 victory and defeat replays compared |
| 04:56 – 05:44 | Lei Yueer | Stage 1 to Stage 2 changes: 4 added, 4 modified, 9 unchanged classes |
| 05:44 – 06:30 | Lei Yueer | Abstraction and polymorphism across customer types |
| 06:30 – 07:15 | Lei Yueer | Open-Closed and Dependency Inversion Principles |
| 07:15 – 07:58 | Lei Yueer | Liskov Substitution and Interface Segregation Principles |
| 07:58 – 08:40 | Liu Yanxi | Stage 2 to Stage 3 changes: 3 added, 7 modified, 10 unchanged classes |
| 08:40 – 09:30 | Liu Yanxi | Reuse in Host and ComboMeal |
| 09:30 – 10:16 | Liu Yanxi | HappyHour and the required changes to Waiter, Order and Cashier |
| 10:16 – 10:39 | Liu Yanxi | Extensibility and design trade-offs |
| 10:39 – 11:14 | Jan Guirey | Sequence diagram: VIP arrival, Happy Hour activation and seating |
| 11:14 – 11:56 | Jan Guirey | Sequence diagram: ordering and preparation |
| 11:56 – 12:41 | Jan Guirey | Sequence diagram: eating, payment and final result |
| 12:41 – 13:06 | Jan Guirey | Effect of the manager's Happy Hour decision on the bill |
| 13:06 – 13:39 | Tomar Divyansh | Individual contribution and reflection |
| 13:39 – 13:58 | Eng Shi Hong | Individual contribution and reflection |
| 13:58 – 14:17 | Lei Yueer | Individual contribution and reflection |
| 14:17 – 14:36 | Liu Yanxi | Individual contribution and reflection |
| 14:36 – 14:52 | Jan Guirey | Individual contribution and reflection |
| 14:52 – 14:55 | All | Closing |

## Repository layout
| Path | Contents |
|---|---|
| `src/` | Game source (`boundary`, `control`, `entity`, and `RestaurantApp`) |
| `tests/` | Dependency-free checks (see [Run the checks](#run-the-checks)) |
| `test-runs/` | Victory and defeat inputs and transcripts for each stage, with [notes](test-runs/README.md) |
| `docs/diagrams/class/` | Class diagrams for Stages 1–3 (`.drawio` source and `.svg` export) |
| `docs/diagrams/sequence/` | Stage 3 sequence diagram |
| `docs/report/` | Group report (PDF) |
| `docs/video/` | Demo video |
| `docs/declarations/` | Each member's Declaration on Use of GAI Assistance |

## Team
| Member | Classes | Other roles |
|---|---|---|
| Tomar Divyansh (Divyansh050505) | `RestaurantApp`, `RestaurantController`, `HappyHour` | Repository lead, reviews, merges and tags |
| Lei Yueer | `Customer`, `CustomerStatus`, `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `CustomerArrivalPlan` | Class diagram and tests, recorded victory and defeat runs |
| Eng Shi Hong | `Menu`, `MenuItem`, `Order`, `OrderStatus`, `Table`, `Chef`, `ComboMeal` | Stage 1 class diagram, ComboMeal rule in the README |
| Jan Guirey | `Staff`, `Waiter`, `Cashier`, `ActionResult`, `Host` | Test notes, sequence diagram |
| Liu Yanxi | `RestaurantUI` | Design decisions log, console UI review, Stage 2 documentation and testing summary |

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
java -cp out RestaurantApp < test-runs/stage3_victory.txt
java -cp out RestaurantApp < test-runs/stage3_defeat.txt
```

Both Stage 3 examples use seed **482193**, contain VIP and Critic arrivals, offer **ComboMeal (Salad + Burger)**, activate Happy Hour on turn 10, and run all 18 turns. The victory pays five customers, including the VIP and Critic, earns **$79.15**, and records average paid-customer satisfaction **87.80**. The defeat collects no payments and records one departure. See [test notes](test-runs/README.md) for the strategies and full transcripts. Stage 1 and Stage 2 recordings are preserved for their respective revisions; they are not Stage 3 replay fixtures.

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

This stage comparison is also included in Section 4.2 of the [group report](docs/report/SCE1_Grp5_Report.pdf).

### New Requirements
Stage 2 introduces three customer types while retaining 18 turns, two tables, six arrivals and the original victory targets. Regular customers lose 8 satisfaction per unserved turn and pay full price; VIP customers lose 5 and receive a 10% discount; Critics lose 12, pay full price and choose Pasta from the standard menu.

Each seeded arrival plan contains exactly four Regulars, one VIP and one Critic, with their positions shuffled. Regular and VIP customers use the seeded food plan. All three types share the same seating, ordering, service, eating, payment and departure lifecycle. Satisfaction loss applies while WAITING, SEATED or ORDERED, stops after service, and reaching zero cancels unfinished orders and releases the table.

All bills use integer cents, with half-cent results rounded up. The VIP bills for Salad, Burger and Pasta are $8.10, $13.50 and $16.20 respectively. Host, ComboMeal and Happy Hour are introduced in Stage 3 and are not part of the Stage 2 version.

### New Files
| New file or file set | Purpose |
|---|---|
| `src/entity/RegularCustomer.java` | Regular label and loss of 8 points per unserved turn. |
| `src/entity/VIPCustomer.java` | VIP label, loss of 5 points and 10% discount policy. |
| `src/entity/CriticCustomer.java` | Critic label, loss of 12 points and Pasta preference. |
| `src/control/CustomerArrivalPlan.java` | Seeded schedule containing four Regular, one VIP and one Critic factory slots. |
| `tests/entity/Stage2CustomerTest.java` | Shared lifecycle, abandonment, food choice, bill rounding and Cashier collection for all subtypes. |
| `tests/control/CustomerArrivalPlanTest.java` | Fixed type counts, seed repeatability, stable creation order and invalid bounds. |
| `tests/Stage2IntegrationTest.java` | Real controller/staff integration and complete CLI replay checks. |
| `test-runs/stage2_victory.txt` and `stage2_defeat.txt` | Recorded startup and manager inputs for reproducible winning and losing strategies. |
| `test-runs/stage2_victory_transcript.txt` and `stage2_defeat_transcript.txt` | Expected console output for those recordings. |
| `docs/stage2_class_diagram.drawio` and `.drawio.svg` | Editable Stage 2 class diagram and vector export. |

### Existing Files Changed or Deleted, with Reasons
| Changed file | Change and reason |
|---|---|
| `Customer.java` | Becomes abstract. Shared lifecycle algorithms call subtype hooks for loss, discount and food choice while retaining common validation. |
| `RestaurantController.java` | Initializes CustomerArrivalPlan after seed selection and delegates arrivals to it; ordinary service logic continues to use Customer. |
| `Cashier.java` | Collects the customer-calculated bill rather than the undiscounted menu price. |
| `RestaurantUI.java` | Displays customer-supplied type labels and the same payable bill that Cashier collects. |
| `tests/entity/CustomerTest.java` and `tests/RestaurantUITest.java` | Existing Regular-customer fixtures instantiate RegularCustomer after Customer becomes abstract. |
| `README.md` and `test-runs/README.md` | Document subtype rules, stage-specific recordings and verification commands. |

No paths were deleted between the stage-1 and stage-2 tags. The earlier diagrams and recordings remain available as historical evidence.

### Stage 2 Class Diagram
The completed stage 2 class diagram can be also viewed in the github repository under docs:
https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/blob/main/docs/diagrams/class/stage2_class_diagram.drawio.svg

### Compare Link
All implementation changes from Stage 1 to Stage 2 can be viewed in the group repository:
https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-1...stage-2

| Item | Detail |
|---|---|
| Base tag | stage-1 |
| Compare tag | stage-2 |
| Commits between the tags | 5 |
| Files changed | 21 |
| Contributors | 3 |

The version at the stage-2 tag compiles and runs on its own and contains only Stage 1 and Stage 2 features.

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

This stage comparison is also included in Section 4.3 of the [group report](docs/report/SCE1_Grp5_Report.pdf).

### New Requirements
Stage 3 adds three new features to the Stage 2 game. All Stage 1 and Stage 2 rules stay the same.

- **Host:** a new staff member who seats at most one queued customer per turn, in addition to the Waiter's task. The Waiter can still seat customers, so two customers can be seated in the same turn. The Host does not take orders or serve food.
- **ComboMeal:** each game offers one combo made of two existing menu items, decided by the game seed using Math.floorMod(seed, 3): remainder 0 gives Salad + Burger, 1 gives Burger + Pasta and 2 gives Salad + Pasta. It costs 90% of the two items' combined price, and its preparation units are the sum of both items'. Regular and VIP customers can choose it through the existing seeded food selection, while Critics keep their Pasta preference.
- **Happy Hour:** the manager can switch it on once per game from the Host menu at the start of a turn, without using up the Host's task. It lasts for that turn and the next. Orders taken while it is active get 20% off, applied after the customer's own discount (a VIP Burger costs $15 × 0.9 × 0.8 = $10.80). At the start of each active turn, every customer still waiting for a table or for food recovers 15 satisfaction, up to a maximum of 100.
- **Locked order price:** each order's bill is fixed when the order is taken, so orders placed before Happy Hour keep their normal price and Happy Hour orders keep their discount after it ends.

### New Files
Three new classes were added to the entity package:

| New file | Purpose |
|---|---|
| `src/entity/Host.java` | New staff member. Extends Staff, so it reuses the one-task-per-turn rule; its only action is seating a queued customer. |
| `src/entity/ComboMeal.java` | New menu item made of two existing items. Extends MenuItem, so orders, the Chef and pricing use it without any type checks. |
| `src/entity/HappyHour.java` | New pricing policy object: once per game, lasts two turns, gives 20% off and satisfaction recovery. |

Two sequence diagrams for ordering a ComboMeal (one without and one with error handling) were also added to the docs folder of the repository.

### Existing Files Changed or Deleted, with Reasons
No files were deleted in Stage 3. The following existing files were changed:

| Existing file | Change and reason |
|---|---|
| `src/entity/Menu.java` | Adds the seeded ComboMeal after the three base items. |
| `src/entity/Order.java` | Stores the bill when the order is taken, so the price stays locked even after Happy Hour ends. |
| `src/entity/Waiter.java` | Works out the locked bill when taking an order: the customer's discount first, then Happy Hour. |
| `src/entity/Cashier.java` | Charges the order's locked bill instead of recalculating it at payment time. |
| `src/entity/Customer.java` | Adds recoverSatisfaction() for Happy Hour. Existing rules are unchanged. |
| `src/control/RestaurantController.java` | Owns the Host and Happy Hour, creates the menu from the game seed, applies Happy Hour recovery at the start of a turn and counts it down at the end of a turn. |
| `src/boundary/RestaurantUI.java` | Adds the Host step before the Waiter, the Happy Hour option and status line, and shows each order's locked bill. |
| `README.md` | Documents the Stage 3 rules (Host, ComboMeal, Happy Hour) and the Stage 2 to Stage 3 design changes. |

Because the bill uses item.getPriceCents(), the ComboMeal discount is applied automatically when ComboMeal overrides that method, so the Waiter and Cashier did not need any special case for it. Several source files also had comments removed during Stage 3; this does not change their behaviour. The Stage 2 recorded runs are kept for the Stage 2 version only, since the new Host prompt and ComboMeal mean they no longer replay the same way in Stage 3.

### Compare Link
All implementation changes from Stage 2 to Stage 3 can be viewed in the group repository:
https://github.com/Divyansh050505/SC2002-Restaurant-Rush-SCE1-Group5/compare/stage-2...stage-3

| Item | Detail |
|---|---|
| Base tag | stage-2 |
| Compare tag | stage-3 |
| Pull requests merged for Stage 3 | #15 Happy Hour and locked order price, #16 Host seats one queued customer per turn, #17 Seeded ComboMeal on the menu |

The version at the stage-3 tag is the final submitted version of the game.

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
