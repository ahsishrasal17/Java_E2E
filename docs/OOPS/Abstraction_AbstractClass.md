# Abstraction — Abstract Classes

## 1. What is Abstraction (Simple Version)

Abstraction means **showing the "what," hiding the "how."**

Real-world analogy: driving a car — press the accelerator, car moves. You don't know or care about fuel injection, pistons, gear ratios happening underneath. You just get a simple interface: accelerator, brake, steering wheel.

**Banking analogy:** At an ATM, you press "Withdraw ₹2000." You don't know which bank server it hits, how it checks your balance, or how the core banking system gets involved. You just: insert card → PIN → withdraw → get cash. Years of backend complexity are collapsed into one simple action.

## 2. Deep Dive — Where to Draw the Abstraction Boundary

Abstraction is a **spectrum**, not a switch.

- **Too little abstraction** → tight coupling. Every class knows every other class's internals. Change one thing, break 15 files.
- **Too much abstraction** → "abstraction hell." Too many wrapper layers, debugging becomes archaeology.

**Rule of thumb:** abstract at the boundary where things are **likely to change**. Ask: *"What's likely to vary here, and what should stay stable?"*

Banking example: payment gateway internals (Visa vs UPI vs RuPay) → likely to change → abstract this behind an interface. The *concept* of "process a payment" → stable → this is what gets exposed.

### Abstraction vs Encapsulation (commonly confused)
| | Abstraction | Encapsulation |
|---|---|---|
| Hides | Complexity | Data |
| Concern | Design — "what should the caller know?" | Access control — "how do I protect internal state?" |
| Example | `PaymentGateway` interface hides *how* payment happens | `private String apiKey` hides sensitive data from being accessed |

---

## 3. Abstract Class — Syntax Fundamentals

```java
public abstract class BaseAccount {
    protected final String accountNumber;
    protected double balance;

    public BaseAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount) {   // concrete method — shared by all subclasses
        balance += amount;
    }

    public abstract double calculateInterest(); // no body — every subclass MUST implement this
}
```

- `abstract` can be applied to a **class** or a **method**.
- An `abstract class` **cannot be instantiated** directly — `new BaseAccount(...)` is a compile error.
- An `abstract method` has **no body**, just a signature ending in `;`. Any concrete subclass **must** override it, or the subclass must also be declared `abstract`.

---

## 4. Edge Cases / Interview Q&A

### Q: Can an abstract class be `final`?
**No.** Contradiction: `abstract` = "must be extended to be useful." `final` = "cannot be extended." Compile error if combined.

### Q: Can an abstract class be `private`?
- **Top-level classes** (matching the filename) can never be `private` or `protected` — only `public` or package-private. This applies to *all* top-level classes, abstract or not.
- **Nested/inner** abstract classes CAN be `private`:
```java
public class Bank {
    private abstract class InternalAudit {
        abstract void audit();
    }
}
```

### Q: Can an abstract class have a constructor?
**Yes.** Abstract classes can have no-arg, parameterized, or multiple overloaded constructors — same as any normal class. The constructor never runs via `new BaseAccount(...)` directly; it only runs **through a subclass**, via `super(...)`.

### Q: Can abstract class fields be `private` / `protected` / `final`?
**Yes, no restrictions.** An abstract class is a normal class in every way except it can't be instantiated and can declare abstract methods.
- `private` fields → hidden even from subclasses (pure encapsulation)
- `protected` fields → visible to subclasses and same package
- `final` fields → must be assigned exactly once (at declaration or in every constructor); great for values like `accountNumber` that should never change after creation

### Q: Can abstract *methods* be `final`?
**No.** `abstract` = "no implementation yet, must be overridden." `final` = "cannot be overridden." Direct contradiction → compile error.

### Q: Can abstract methods be `private`?
**No.** `private` = not visible outside the class, cannot be inherited/overridden at all. `abstract` requires a subclass to override it — if the subclass can't see the method, it can't override it → compile error.

### Q: Can abstract methods be `static`?
**No.** `static` methods aren't polymorphic (no dynamic dispatch), but `abstract` methods exist specifically to be overridden polymorphically. Contradiction.

### Q: What access modifiers CAN abstract methods have?
`public`, `protected`, or package-private (no modifier). Just not `private`, `static`, or `final`.
```java
public abstract class BaseAccount {
    protected abstract double calculateInterest(); // legal
    abstract void auditLog();                       // legal (package-private)
    public abstract double getBalance();             // legal
}
```

### Q: Can an abstract class have zero abstract methods?
**Yes.** You can mark a class `abstract` purely to prevent direct instantiation, even if every method is concrete. Rare, but interviewers ask this.

### Q: Does a subclass have to implement ALL abstract methods?
**Yes** — unless the subclass is *itself* declared `abstract`, deferring implementation further down the chain.

---

## 5. Constructors, Inheritance & `super()`

**Core rule:** Every constructor's first line, explicitly or implicitly, calls a constructor of its parent class. Construction always happens **top-down**: `Object` → each ancestor → finally the actual class.

```java
public class SavingsAccount extends BaseAccount {
    public SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance); // must call parent's constructor explicitly
    }

    @Override
    public double calculateInterest() {
        return balance * 0.04;
    }
}
```

### Why is `super(...)` required here?
`SavingsAccount` inherits `accountNumber` and `balance` from `BaseAccount`. Only `BaseAccount`'s constructor knows how to initialize them correctly.

If you don't write `super(...)` explicitly, Java **automatically inserts** a call to the parent's **no-arg** constructor. But if the parent (`BaseAccount`) only has a **parameterized** constructor (no no-arg version exists), that automatic insertion **fails to compile**:
```
error: constructor BaseAccount in class BaseAccount cannot be applied to given types
required: String, double
found: no arguments
```
→ This is why `SavingsAccount` **must** explicitly call `super(accountNumber, balance)`.

### Does the child constructor need the SAME parameters as the parent?
**No.** The child can have extra parameters of its own — it just must supply whatever the parent constructor requires, somewhere in the `super(...)` call:
```java
public class SavingsAccount extends BaseAccount {
    private double interestRate;

    public SavingsAccount(String accountNumber, double balance, double interestRate) {
        super(accountNumber, balance);
        this.interestRate = interestRate;
    }
}
```

### What if BaseAccount has BOTH a no-arg AND a parameterized constructor?
Whichever `super(...)` call you write explicitly is the one that runs. Writing `super(accountNumber, balance)` always invokes the parameterized version, regardless of what the implicit default would have been.

**Banking angle:** this chaining rule is a safety guarantee — it's impossible to end up with a `SavingsAccount` object where `accountNumber` or `balance` were never initialized. No half-constructed account object can ever leak out and be used.

---

## 6. State — Abstract Class vs Interface

**State** = actual data stored in fields on an object, that persists (stays in memory, holding its value across method calls) and can change over the object's lifetime — as opposed to a local variable inside a method, which is destroyed once the method returns.

**Important nuance:** the abstract class itself is *never instantiated*, so it never "holds" state at runtime. What's accurate to say is: **an abstract class can *define* fields that every subclass instance will carry its own copy of.** There's only ONE object in memory when you do `new SavingsAccount(...)` — not a glued-together `BaseAccount` + `SavingsAccount` pair — but the fields `accountNumber`/`balance` were declared in `BaseAccount`'s blueprint, and that object's memory includes them.

**Instance field** = a variable declared in a class (not inside a method), without `static`. Each object created from that class gets its own independent copy.
```java
SavingsAccount a1 = new SavingsAccount("A1", 5000);
SavingsAccount a2 = new SavingsAccount("A2", 9000);
// a1.balance and a2.balance are completely independent copies
```

**Interfaces cannot have instance fields.** Any field declared in an interface is implicitly `public static final` — a **constant**, shared by all implementers, never changing:
```java
public interface PaymentGateway {
    int MAX_RETRIES = 3; // implicitly public static final — constant, NOT state
    PaymentResult processPayment(double amount, String accountNumber);
}
```
If an implementing class needs something mutable (like a retry counter), it must declare that as its own field — the interface provides zero help with that.

**Summary distinction:**
- Abstract class → models **"is-a," and carries shared, mutable data**
- Interface → models **"can-do," a pure capability contract with no data ownership**

---

## 7. Full Working Example (Banking Domain)

```java
public abstract class BaseAccount {
    protected final String accountNumber;
    protected double balance;

    public BaseAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited:" + amount + " Current Balance: " + balance);
    }

    public abstract double calculateInterest();
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

public class BankDemo {
    public static void main(String[] args) {
        SavingsAccount savings = new SavingsAccount("SAV1234", 5000);
        savings.deposit(1000);
        System.out.println("Interest: " + savings.calculateInterest());

        // BaseAccount b = new BaseAccount("X", 100); // ❌ compile error — abstract class can't be instantiated
    }
}
```

---

## 8. Quick Revision Checklist (read this the night before an interview)

- [ ] Abstraction hides complexity (design decision) vs Encapsulation hides data (access control)
- [ ] Abstract class = partial abstraction; Interface = full abstraction (contract)
- [ ] Abstract class CANNOT be `final` (contradiction)
- [ ] Abstract class CAN be `private` only if it's a nested class, never top-level
- [ ] Abstract class CAN have constructors (no-arg, parameterized, overloaded)
- [ ] Abstract class fields CAN be `private`, `protected`, or `final`
- [ ] Abstract methods CANNOT be `final`, `private`, or `static`
- [ ] Abstract methods CAN be `public`, `protected`, or package-private
- [ ] A class with even ONE abstract method must itself be declared `abstract`
- [ ] Every subclass MUST implement all abstract methods, unless it's also `abstract`
- [ ] Every constructor implicitly/explicitly calls `super()` first — construction is top-down
- [ ] If parent has ONLY a parameterized constructor, child MUST explicitly call `super(args)` — implicit no-arg `super()` will fail to compile
- [ ] Abstract classes can define state (instance fields); interfaces cannot (only `public static final` constants)
