# Person 5 implementation and verification

This contribution implements the console boundary against main commit `b408940`. The report contribution is provided as a draft for review.

## Files

- `src/boundary/RestaurantUI.java`: implemented turn screens, queue/table/customer/order details, summaries, final result and all three target checks, safe input and exact money formatting. Existing public API and action loops are preserved.
- `tests/RestaurantUITest.java`: plain Java test harness with controlled controller/entity fixtures; no external test dependencies.
- `docs/person5-design-decisions.md`: decisions and integration observations.
- `docs/person5-report-draft.md`: drafts for sections 3.2, 4.1.3 and a reflection for personal review.

## Build and run the UI checks (PowerShell, repository root)

```powershell
$sourceFiles = @(Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
javac --release 17 -d out $sourceFiles tests\RestaurantUITest.java
java -cp out RestaurantUITest
```

Verified locally with JDK 21 targeting Java 17: **48 checks passed**.

Coverage: monetary edge cases; locale-independent decimals; letters, blanks, fractions, range errors and integer overflow; EOF at main menus and selection submenus; 18-turn completion with controlled fixtures; back/rejected/successful action flow; customer/order ID routing; empty-order recovery; displayed entities and totals; exact victory thresholds, below-threshold failure and N/A averages.

## Full-game limitation

The actual application was also run with empty input. It currently stops on turn 1 because `RestaurantController.getWaitingQueue()` in baseline main returns null. Other controller getters and entity methods are also placeholders. This is an integration dependency, not a passing full-game test. The UI expects the declared getters to return the controller's initialized state; it does not disguise missing implementations as an empty restaurant.

The fixture test result must not be presented as evidence of a real victory/defeat replay or a completed domain implementation. The teammate contributions must be integrated before that check is possible.

## How to explain the UI

1. `start()` retains the agreed turn order. `displayTurnStart()` reads the state after arrivals.
2. The Waiter and Chef menus ask for numbers, call controller methods and display their `ActionResult` messages. They keep asking after a rejected action.
3. `readChoice()` reads whole lines. Invalid input re-prompts; EOF behaves as back/Wait without crashing or hanging.
4. The end screen shows cumulative totals and turns used. Final pass/fail labels explain the three targets while `controller.isVictory()` remains the outcome source.
5. The UI prints and reads; it does not change satisfaction, table occupancy, cooking progress or revenue.

Before submission, review the reflection in your own words and follow your course's rules for declaring assistance.
