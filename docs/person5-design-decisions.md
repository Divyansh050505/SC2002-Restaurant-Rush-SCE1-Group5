# Person 5 design decisions log

Date: 24 October 2026. Baseline: `b408940` (latest locally cloned main).

| Topic | Decision | Evidence / status |
| --- | --- | --- |
| Scope | Implement `boundary.RestaurantUI`; preserve constructor, `start()` and both action-selection loops. | Latest team chat explicitly requests preserving these loops. Implemented locally. |
| Naming | Keep `RestaurantUI` and `formatMoney(int)` rather than renaming them to ConsoleUI and formatCents. | Existing shared skeleton names are the integration contract. `formatMoney(1830)` returns `$18.30`. No public API changes. |
| Display | Turn and totals, targets, queue, both tables, customer states/satisfaction, and order details. | Reads controller/entity getters only. Reprints orders before Chef selection to reflect Waiter activity. |
| Numbering | Use actual customer IDs and order numbers; preserve the existing menu values. | Shared controller allocates consecutive IDs and retains its master lists. The inherited range check assumes these lists keep all allocated entries. |
| Money | Format using integer quotient/remainder and a long magnitude. | Exact cents, fixed decimal point independent of locale, safe even for `Integer.MIN_VALUE`. |
| Average | Display N/A only when `getPaidCount() == 0`. | Zero satisfaction for a nonempty paid group remains a genuine numeric value. |
| Invalid input | Consume one complete line, trim it, parse and range-check; print a clear error and retry. | Handles letters, blank input, fractions, negative/out-of-range values and integer overflow. |
| Exhausted input | Print a single notice and return 0 on subsequent prompts. | Returns from submenus, then selects Wait. Preserves completion of all turns. |
| Rejected action | Display `ActionResult.getMessage()` and preserve retry semantics. | Latest chat supersedes the report-task PDF's generic exception wording at this boundary. |
| Final screen | Controller supplies VICTORY/DEFEAT; UI explains each specified target. | Threshold comparisons are display annotations. They do not mutate game state. |
| Food choice | Leave to Customer; pending branch uses deterministic per-customer random selection. | Not merged into the inspected main. Do not claim the initially suggested cycle is implemented. |
| Inheritance | Document abstract Staff; describe Customer as concrete in the current code. | Verified source, rather than the original proposed split. |
| Integration | Do not replace other members' skeleton methods to make the UI test pass. | Isolated fixture tests validate the UI contract. Full-game validation remains blocked on teammate implementations. |

## Follow-up checks when the team merges

- Verify real entity getters return consistent data and controller collection getters return initialized collections.
- Confirm `getServedCount()` means served customers; the inspected pending P4 controller currently returns the paid count, which can undercount customers still eating or waiting to pay.
- Confirm the adopted food rule and update the report text accordingly.
- Run the team's victory and defeat replays with the completed domain code and inspect the real final screens.
- Reconcile any future change to target constants with the descriptive UI labels. The current controller does not expose target constants publicly.
