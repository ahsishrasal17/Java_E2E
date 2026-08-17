# Inheritance vs Composition (Day 1)

## 1. What Are They (Simple Version)

**Inheritance ("is-a"):** A `SavingsAccount` **is a** `BaseAccount`. A `Dog` **is an** `Animal`. The child is fundamentally the same *kind* of thing as the parent, just more specific.

**Composition ("has-a"):** A `Car` **has an** `Engine`. A `Wallet` **has a** `BankAccount`. The container isn't *a type of* the thing it holds — it just **uses** it internally to do its job.

### Banking analogy
Ask: *"Is a `PremiumUser` a type of `User`? Or does a `User` just have a `SubscriptionPlan`?"*

The tempting-but-wrong answer is `class PremiumUser extends User`. The better modeling is:
```java
class User {
    private SubscriptionPlan plan; // composition — User "has a" plan
}
```

Why? A user's plan **changes over time** — free → premium → free again, maybe multiple times. If `PremiumUser` were a separate class via inheritance, "downgrading" would mean **destroying the `PremiumUser` object and creating a brand-new `User` object** — losing identity, requiring manual field copying, breaking anything holding a reference to the old object. With composition, downgrading is one line: `user.setPlan(freePlan);`.

---

## 2. Syntax Fundamentals

### Inheritance — `extends`
```java
public class SavingsAccount extends BaseAccount {
    // inherits all non-private members of BaseAccount
}
```
- A class can `extends` **only one** other class — Java has single inheritance for classes (unlike interfaces, which allow multiple).
- Subclass inherits all `public`/`protected` members automatically. `private` members exist in the object's memory but are **not directly accessible** by the subclass's own code.
- `super` has two uses:
  - `super(...)` — calls the parent's constructor (must be the first line)
  - `super.methodName()` — calls the parent's version of a method, even if overridden in the subclass

### Composition — no special keyword
```java
public class Wallet {
    private BaseAccount account; // Wallet "has-a" BaseAccount — composition

    public Wallet(BaseAccount account) {
        this.account = account;
    }
}
```
There's no `extends`, no special syntax — composition is simply a class holding a **reference to another object** as a field and delegating work to it.

### Rules
1. **Java does not support multiple inheritance of classes** — `class X extends A, B` is a compile error (avoids the "diamond problem"). Multiple interfaces are fine; multiple classes are not.
2. **`final` classes cannot be extended at all.** `public final class SavingsAccount` → no subclass possible, ever.
3. **Constructors are never inherited.** Every subclass must define its own constructors (or rely on Java's default no-arg constructor if eligible).
4. **`private` members are not directly accessible** by subclass code, even though they exist in memory for instance fields.
5. **Composition has no restriction on "how many."** A class can compose as many other objects as needed — no single-parent limitation.

---

## 3. Deep Dive

### Why "favor composition over inheritance" is a famous principle

Inheritance creates **the tightest possible coupling** between two classes. A subclass inherits behavior it didn't ask for, breaks if the parent changes, and is locked into that relationship at **compile time**, permanently.

Composition is more flexible: you can **swap** the composed object at runtime, mock it easily in tests, and the containing class only depends on whatever **interface/contract** the composed object exposes — not its full implementation.

### The classic failure case — Liskov Substitution Principle (LSP)

**LSP:** *"Objects of a subclass should be replaceable with objects of the parent class without breaking correctness."*

Classic example: `Square extends Rectangle`. Mathematically a square *is a* rectangle (equal sides), so inheritance seems natural. But if `Rectangle` has independent `setWidth()`/`setHeight()`, a `Square` **must** override both to keep width/height equal — silently breaking any code that assumed changing width alone wouldn't affect height. This looks like clean modeling but hides a real design flaw — a favorite interview question for exactly this reason.

**Banking parallel:** `SavingsAccount extends CurrentAccount` (or vice versa) is tempting since both are "accounts" — but if `CurrentAccount` supports overdrafts and `SavingsAccount` must **never** allow negative balances, a `SavingsAccount` inheriting overdraft logic could silently violate a core business rule the moment someone calls an inherited method they didn't realize existed.

### When inheritance IS the right choice
1. A genuine, **stable "is-a" relationship** that won't need to change at runtime (a `SavingsAccount` never "becomes" a `CurrentAccount` — that's a different account entirely, not a state change)
2. You want to **share concrete implementation** across closely related types (e.g. `BaseAccount.deposit()` — every account type deposits money the same way)
3. You want **runtime polymorphism** through method overriding (e.g. `calculateInterest()` differing by account type)

---

## 4. Composition vs Aggregation

**No special Java keyword or syntax distinguishes them** — both look identical in code (a class holding a reference to another object as a field). The difference is purely **design intent and object lifecycle**, not syntax.

### Composition (strong "has-a") — exclusive ownership
- The contained object's **lifecycle is entirely dependent** on the container.
- Typically created **inside** the container (often in its constructor) — no way to create it independently from outside.
- If the container is destroyed and nothing else references the contained object, it gets garbage-collected too.
- Analogy: your **heart** and **your body** — the heart doesn't exist independently of you.
- Banking example: `BankStatement` **owns** its `List<TransactionLine>`. Transaction lines have zero meaning outside their specific statement.

```java
public class BankStatement {
    private String statementId;
    private List<TransactionLine> lines;

    public BankStatement(String statementId, List<String> rawTransactions) {
        this.statementId = statementId;
        this.lines = new ArrayList<>();
        for (String raw : rawTransactions) {
            // created INSIDE — never passed in from outside
            this.lines.add(new TransactionLine(raw));
        }
    }
}

class TransactionLine {
    private String description;
    TransactionLine(String description) {
        this.description = description;
    }
}
```
Nobody outside `BankStatement` ever creates a `TransactionLine`. They're born inside the constructor — if `BankStatement` is discarded, its `TransactionLine`s have no other references and get garbage collected with it.

### Aggregation (weak "has-a") — shared/independent existence
- The contained object's **lifecycle is independent** of the container.
- Typically created **outside** the container and **passed in** (constructor or setter).
- If the container is destroyed, the contained object continues to exist — other code/containers may still reference it.
- Analogy: a **university** and its **students** — students exist before enrolling and after leaving.
- Banking example: `Branch` has a `List<Employee>`. Employees exist independently — hired before assignment, and if the branch closes, employees get reassigned, not destroyed.

```java
class User {
    private String userName;
    private SubscriptionPlan plan; // created OUTSIDE, passed in — aggregation

    public User(String userName, SubscriptionPlan plan) {
        this.userName = userName;
        this.plan = plan;
    }

    public void updatePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan; // swap at runtime — no object destruction, no identity loss
    }
}
```
`SubscriptionPlan` (e.g. `"Basic"`) could realistically be shared across many users — it's not something that exists only because one specific `User` exists. This makes it aggregation, not strict composition — even though casually people say "composition" to mean any "has-a" relationship via object references. The stricter split only matters when specifically asked to differentiate.

### Quick reference examples
| Relationship | Type | Why |
|---|---|---|
| `Car` — `Engine` | Composition | A specific engine is built for and exclusive to that car; destroying the car destroys the engine's purpose |
| `Playlist` — `Song` | Aggregation | Same song can exist in multiple playlists; deleting a playlist doesn't delete the song |
| `BankStatement` — `TransactionLine` | Composition | Lines created inside the statement, meaningless outside it |
| `Branch` — `Employee` | Aggregation | Employees exist independently, can be reassigned |

### Why this matters practically — database/JPA implications (Day 10 preview)
- **Composition-style** relationships usually map to `ON DELETE CASCADE` in SQL — deleting a `BankStatement` row deletes its `TransactionLine` rows too.
- **Aggregation-style** relationships mean the child exists independently, referenced by a foreign key, but **not** cascade-deleted — deleting a `Branch` should never delete `Employee` rows, just reassign/nullify the reference.

This becomes directly relevant with `@OneToMany` and cascade configuration in Spring Data JPA — understanding this conceptually first makes those annotations make sense instead of being magic copy-paste.

---

## 5. `toString()`, `super.toString()`, and `getClass()` (covered alongside this topic)

### What `toString()` is
Every class automatically inherits from `Object` (even without writing `extends Object`). `Object` provides `toString()` — a method that returns a String representation of an object. `System.out.println(obj)` **implicitly calls `obj.toString()`** behind the scenes.

`Object`'s default implementation:
```java
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```
Without overriding it, printing an object gives an unhelpful result like:
```
org.ashish.learning.OOPS.Inheritance.SavingsAccount@1b6d3586
```
This is why almost every class you write should override `toString()`.

### `super.toString()`
Explicitly calls the **parent class's** version. If the parent doesn't define its own `toString()` either, Java keeps walking **up the inheritance chain** until it finds one that does — falling back to `Object`'s ugly default if nothing overrides it anywhere in the chain.

### `getClass().getSimpleName()` — the smarter, reusable `toString()`
```java
// inside BaseAccount
@Override
public String toString() {
    return getClass().getSimpleName() + "{accountNumber='" + accountNumber + "', balance=" + balance + "}";
}
```
`getClass()` returns the **actual runtime type** of the object — not the type of the reference variable, and not the class the code is physically written in. This is runtime polymorphism again: the same method, written once in `BaseAccount`, correctly prints `"SavingsAccount{...}"` when called on a `SavingsAccount` object and `"CurrentAccount{...}"` when called on a `CurrentAccount` object — because it looks at the real object type at runtime.

**Payoff:** subclasses don't need to duplicate `toString()` logic at all — one method in the base class, reused correctly everywhere. This is a real pattern used constantly in logging across large codebases with many entity subclasses.

---

## 6. Full Working Examples

### Inheritance (genuine "is-a")
```java
public abstract class BaseAccount {
    protected String accountNumber;
    protected double balance;

    public BaseAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount) { // shared concrete behavior
        balance += amount;
    }

    public abstract double calculateInterest();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{accountNumber='" + accountNumber + "', balance=" + balance + "}";
    }
}

public class SavingsAccount extends BaseAccount {
    public SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public double calculateInterest() {
        return balance * 0.04;
    }
}

public class CurrentAccount extends BaseAccount {
    public CurrentAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public double calculateInterest() {
        return 0;
    }
}
```

### Composition/Aggregation (avoiding the `PremiumUser extends User` trap)
```java
public class SubscriptionPlan {
    private String planName;
    private double monthlyFee;

    public SubscriptionPlan(String planName, double monthlyFee) {
        this.planName = planName;
        this.monthlyFee = monthlyFee;
    }

    public String getPlanName() { return planName; }
}

public class User {
    private String userName;
    private SubscriptionPlan plan; // aggregation — plan created outside, passed in

    public User(String userName, SubscriptionPlan plan) {
        this.userName = userName;
        this.plan = plan;
    }

    public void updatePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan; // swap at runtime — no object destruction, no identity loss
        System.out.println(userName + " updated the plan to " + newPlan.getPlanName());
    }

    public String getPlanName() {
        return plan.getPlanName();
    }
}
```

---

## 7. Quick Revision Checklist

- [ ] Inheritance = "is-a", single-parent only for classes, tight compile-time coupling
- [ ] Composition = "has-a", no keyword, just an object reference field, flexible/swappable at runtime
- [ ] Java has no multiple inheritance for classes (diamond problem avoidance); interfaces allow multiple
- [ ] `final` classes cannot be extended; constructors are never inherited
- [ ] "Favor composition over inheritance" — composition avoids tight coupling and permanent compile-time relationships
- [ ] Liskov Substitution Principle: subclass objects must be substitutable for parent objects without breaking correctness — classic failure: `Square extends Rectangle`
- [ ] Use inheritance when: genuine stable "is-a", shared concrete logic, and runtime polymorphism via overriding is the goal
- [ ] Composition vs Aggregation is a lifecycle/ownership distinction, not a syntax distinction
- [ ] Composition = contained object created inside container, dies with it (e.g. `BankStatement`/`TransactionLine`, `Car`/`Engine`)
- [ ] Aggregation = contained object created outside, passed in, exists independently, can be shared (e.g. `Branch`/`Employee`, `Playlist`/`Song`, `User`/`SubscriptionPlan`)
- [ ] Maps to DB design: composition → cascade delete; aggregation → foreign key without cascade delete (relevant for JPA `@OneToMany` later)
- [ ] `toString()` inherited from `Object`, implicitly called by `System.out.println()`; override it for readable output
- [ ] `super.toString()` walks up to the nearest ancestor that overrides it, falling back to `Object`'s default `ClassName@hashcode` format
- [ ] `getClass().getSimpleName()` returns the actual runtime class name — lets one `toString()` in a base class work correctly for every subclass
