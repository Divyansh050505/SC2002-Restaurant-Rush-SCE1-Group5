# AI chat history – Stage 2 – customer subclasses, billing, class diagram and arrival planning

- **Member:** Yueer Lei
- **AI tool:** Codex (OpenAI)
- **Format:** Every prompt and every reply, in the original order. Attached documents are named beside the relevant prompts.

---

## Exchange 1

### Prompt (Yueer)

**Referenced document:** `SC2002_Restaurant_Rush_Group_Assignment.docx`

refer to the SC2002_restaurant_rush_group assignment document. I am to implement stage 2, it requires introducing two additional type of customers: the vip and the critic. I am thinking about turning our current customer class into an abstract class, with the loseSatisfaction, leave methods, etc, as abstract, and have three classes(regular, vip, and critic) to inherit from the customer class. for the special feature of the critic customer, I am thinking that the customer would need 2 turns to finish eating instead of one turn. and the vip customer can have a 10% off. as for the controller, it needs to do another random shuffle so that each game has at least one vip and one critic customer arrival (can have more) Can we make all the changes in the customer classes and the controller&UI section? or do we need to change cashier to realise the 10% off?

tell me what you think. do not make any changes to the workspace just yet.

### Reply (Codex)

I’ll read the assignment and trace how customers, arrivals, eating turns, and billing currently work, then assess where the stage 2 changes belong. I won’t modify any files.

I’m using the documents skill to read the assignment. The current cashier returns the menu item’s full price directly, so a customer-specific discount will need a small billing integration change.

**Your inheritance idea is sound, but I’d keep the shared lifecycle methods implemented in `Customer` and make only the varying behaviour customizable. A small change to `Cashier` is also appropriate.**

Section 4.2 requires waiting satisfaction losses of **8 for Regular, 5 for VIP, and 12 for Critic**, a **10% VIP discount**, and a documented Critic order preference or reaction to service. Behaviour must be dispatched through customer objects. `SC2002_Restaurant_Rush_Group_Assignment.docx`

1. **Make `Customer` abstract, but keep common methods concrete.**

   `RegularCustomer`, `VIPCustomer`, and `CriticCustomer` are reasonable subclasses. However, `leave()`, seating, serving, payment transitions, and most of `loseSatisfaction()` follow the same rules for everyone.

   Your current methods already validate state, cancel abandoned orders, and release tables. Reimplementing them in each subclass would duplicate that logic.

   Instead, keep `loseSatisfaction()` in the parent and have it call something like:

   ```java
   protected abstract int getSatisfactionLoss();
   ```

   Each subclass supplies 8, 5, or 12. Similarly, a customer method such as `calculateBillCents(int basePriceCents)` can return full price by default, with VIP overriding it. An abstract class can contain mostly concrete methods.

2. **I’d choose a different Critic feature.**

   Two eating turns is understandable as “the critic carefully evaluates the meal,” but it is a risky interpretation: the brief specifically asks for an **order preference or reaction to service**, while its shared flow specifies one full eating turn.

   My recommendation is a documented preference—for example, **the Critic always orders Pasta**. That fits the requirement directly and gives the manager a meaningful scheduling decision because Pasta needs two preparation units.

   Your existing Waiter already calls `customer.chooseItem(menu)`, so an override in `CriticCustomer` would work without changing the Waiter or adding controller type checks.

3. **Yes, change the Cashier slightly for the discount.**

   Currently, `Cashier.java` reads the menu price directly. It should instead obtain the amount through the customer’s pricing method:

   ```java
   int amountPaid =
           customer.calculateBillCents(order.getItem().getPriceCents());
   ```

   The **customer owns the discount rule**; the **cashier collects the resulting amount**. No `instanceof VIPCustomer` is needed. The controller can keep adding the cashier’s returned amount to revenue.

   Also update the UI: it currently prints the original menu price as the amount paid. Both billing and display should use the same amount. Keep calculations in integer cents.

   Looking ahead, Stage 3 requires prices to be locked when ordering, so storing the final bill in `Order` would be a sensible later extension.

4. **Your guaranteed arrival shuffle works.**

   Build six type slots containing one guaranteed VIP and one guaranteed Critic, fill the other four randomly, then shuffle once per game. Keep the existing arrival turns unchanged.

   Use the replay seed so the same seed reproduces both customer types and food choices. One detail: the UI currently sets the seed **after the controller is constructed**, so generating the plan in the controller constructor would need an initialization adjustment.

Overall, I’d expect changes to the customer classes, controller, UI, **one small Cashier integration**, and the relevant tests and documentation. Existing `new Customer(...)` calls would become `new RegularCustomer(...)`. With the order-preference approach, Waiter, Chef, Table, and the service flow can remain as they are.

No workspace files were changed.

---

## Exchange 2

### Prompt (Yueer)

**Referenced diagrams:** `stage1_class_diagram.drawio`, `stage1_class_diagram.drawio.svg`

I want you to copy and paste the stage 1 diagram, then add stage 2 implementations based on the stage 1 diagram. do not create a new diagram with new styling. make sure you include all the methods and attributes (including those updated in current commit.)

### Reply (Codex)

I’ll compare the workspace with the current commit, restore the tracked changes, and remove the Stage 2 files I created. Then I’ll copy the Stage 1 diagram and extend it in the same style, checking every attribute and method against the current commit.

The tracked files now match commit `084d133`, and I’ve removed my previous Stage 2 files. The assignment document is retained. I also found omissions in the old Stage 1 diagram: two UI fields, six UI methods, `paidCustomers`, and `CHOICES_BY_MENU`. I’ll include those in the copied diagram alongside the Stage 2 additions.

The replacement now uses the Stage 1 diagram’s yellow boxes, Arial font, compartments, arrow styles, and single-page layout. I’ve checked its members against the Java compiler’s parsed inventory of the current commit. I’m finishing the visual check so labels don’t cover any methods or attributes.

Restored tracked files to commit `084d133` and replaced my redesign with a copy of the Stage 1 diagram, preserving its styling and single-page format.

- `stage2_class_diagram.drawio`
- `stage2_class_diagram.drawio.svg`

Verified all current-commit attributes and method signatures, including omissions from the original diagram, and added the planned Stage 2 extensions.

Stage 1 diagrams remain untouched. No Java implementation changes remain.

---

## Exchange 3

### Prompt (Yueer)

why is customerarrivalplan a control class while actionresult is not?

### Reply (Codex)

The distinction is **whether the class coordinates behaviour or carries information**.

`CustomerArrivalPlan` actively organizes part of the game setup: it builds six arrival slots, guarantees a VIP and Critic, randomizes the remaining types, shuffles them, and creates the appropriate customer for each arrival. That coordinating role makes `«control»` a reasonable classification.

`ActionResult` carries the outcome of an action—success or failure and a message. It does not choose what happens next. The UI and controller interpret the result and continue the game.

There is some design freedom here: `CustomerArrivalPlan` could also be described more specifically as a **factory and scheduling helper**, rather than given a `«control»` stereotype. In our proposed design, it earns that stereotype through its planning and creation responsibilities. If it merely stored a list generated elsewhere, the control classification would be less convincing.

---

## Exchange 4

### Prompt (Yueer)

Here is my groupmates' suggested corrections: The brief says we “may replace two of the regular arrivals”, so the base game should be exactly 4 Regular + 1 VIP + 1 Critic. Right now the other 4 slots are random, so about 1 in 5 games has no Regular at all (the recorded victory is 4 VIP + 2 Critic). Could you make CustomerArrivalPlan always 4R/1V/1C (just shuffle positions), re-record the stage 2 runs?

Do make the changes.

### Reply (Codex)

I’ll change the arrival plan to four Regulars, one VIP, and one Critic, then re-record both Stage 2 runs and update the tests, documentation, and UML note to match.

The fixed mix is implemented, and the arrival test now checks for exactly 4R/1V/1C across 201 seeds. I’m using seed `482193` for the new recordings because its victory pays both special customer types; with the corrected mix, the old seed leaves the Critic too late to pay. Your existing README edit is preserved.

Changed arrivals to **exactly 4 Regular + 1 VIP + 1 Critic**, with only positions shuffled.

Re-recorded both Stage 2 runs using seed **482193**. Victory: **4 paid, $58.20 revenue, 74.00 average satisfaction**, including both special customers.

Updated tests, documentation, and UML/SVG note. All **4,897 checks pass**. Stage 1 artifacts and your existing README edit are preserved.

Changes are not committed.
