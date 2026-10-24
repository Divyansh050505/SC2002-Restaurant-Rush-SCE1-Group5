# Person 5 report contribution — local draft

Prepared 24 October 2026 against main commit `b408940` and the local Person 5 UI changes. Nothing in this file has been inserted into the shared Google document. Recheck the named classes and food-selection rule after the team integrates its contributions.

## 3.2 Boundary–Control–Entity Structure

The program separates console interaction, coordination and domain behaviour. `RestaurantUI` is the boundary: it reads numbered choices, checks that input is an integer within the displayed range, and displays customers, tables, orders, totals and results. It uses customer and order numbers consistently so the player can identify the object selected. It displays an unavailable average as N/A when nobody has paid, rather than presenting zero as a measured average.

`RestaurantController` coordinates the turn steps and owns the customer, waiting, payment and order collections together with the tables, menu and staff. The existing `RestaurantUI.start()` method drives the outer loop: it calls `startTurn()`, displays the updated state, requests a Waiter task followed by a Chef task, calls `endTurn()`, and prints the summary. Arrivals therefore occur before the start-of-turn screen. The controller performs payment before advancing eating, so becoming ready to pay does not cause payment in the same step.

Entities represent the restaurant's domain objects and encapsulate their own data and rules: customers have service states and satisfaction; orders track preparation and service; tables represent occupancy; and staff perform their assigned actions. The boundary requests an action through the controller and displays the returned `ActionResult`. It does not seat customers, advance cooking, deduct satisfaction or collect payments itself. A rejected action leaves the existing menu loop active; a successful action or Wait ends that staff member's selection. This separation allows input and display behaviour to be tested independently of the domain implementation.

## 4.1.3 Key Design Decisions and Reasons

| Decision | Reason and consequence |
| --- | --- |
| Store prices and revenue as integer cents. | Integer arithmetic avoids floating-point rounding in monetary totals. The UI converts cents to a dollar string with exactly two decimal places; for example, 1830 displays as $18.30. |
| Keep the restaurant collections in `RestaurantController`. | The controller coordinates arrivals, selections and turn processing from one state model. The UI reads this state and delegates actions, rather than maintaining another copy of the lists. |
| Use `Staff` as an abstract common base. | The shared action-tracking contract (`hasActed`, `resetTurn`, `markActed`) represents one task per turn consistently for Waiter, Chef and Cashier. |
| Separate input errors from rejected game actions. | The UI catches malformed numbers locally and re-prompts. Staff actions return `ActionResult` with success and a message, which the UI displays. This follows the agreed action contract and keeps game-rule validation out of the boundary. |
| Preserve the existing menu loop and number scheme. | Waiter choices remain 1 = seat, 2 = take order, 3 = serve, 0 = wait. Chef choices remain 1 = prepare, 0 = wait. Selection submenus use 0 = back. Stable choices preserve replay-file compatibility. |
| Run arrivals before displaying the turn. | A customer arriving this turn is visible when the manager chooses the Waiter's task. |
| Refresh the order list before the Chef menu. | An order taken by the Waiter becomes visible before the Chef chooses an order in that same turn. |
| Display collections in the controller's existing order. | The queue retains arrival order and numbered customer/order entries retain their identifiers. The UI does not sort or mutate the shared collections. |
| Treat exhausted input as 0. | Checking `hasNextLine()` prevents replay files from causing a scanner exception. At a submenu, 0 returns to the parent; at the parent it chooses Wait. The remaining turns complete with Wait, and a notice makes input exhaustion visible. |
| Derive averages from paid customers only. | An average is meaningful only when at least one customer has paid. N/A prevents confusing an empty group with a real zero satisfaction score. |
| Obtain the final outcome from `controller.isVictory()`. | The UI reports the controller's outcome and separately shows pass/fail against the specified targets: at least four paid customers, $45.00 revenue and 60 average paid-customer satisfaction. The UI does not change the outcome. |

### Food selection — integrate after confirmation

The chat initially suggested cycling Burger, Pasta and Salad as an example. The pending `feature/customers` implementation instead uses a per-customer pseudorandom seed, `2002 + 31 * customerId + arrivalTurn`, to choose an index in the menu. For a fixed menu order this gives reproducible choices without depending on the order in which customers are asked. Use this explanation only if that implementation is adopted; the current main branch still has an unimplemented `chooseItem()`.

### Accuracy notes before copying into the shared report

- The task sheet's earlier wording “invalid choices throw Exception” has been superseded at the staff/UI boundary by the chat's `ActionResult` contract. Customer methods in the pending customer branch can still reject invalid transitions with exceptions; that does not make uncaught exceptions the UI input strategy.
- The actual main-branch `Customer` is concrete, not abstract. Do not describe it as abstract or claim a `RegularCustomer` subclass exists in this version.
- Main still contains unfinished entity and controller methods. The paragraphs describe the separation and contracts; they are not evidence that the whole game currently works.

## 6.1 Stage 1 Reflection — suggested wording to personalise

My contribution covers the console displays, safe input handling and the design decisions log. UI verification needed controlled game states because several controller and entity methods were still placeholders. A useful improvement would be to agree the getter and action contracts early and arrange a full-game integration test as soon as all implementations are available.

Review this paragraph so it reflects your own experience before submitting it. Do not claim the fixture tests establish a working full game.
