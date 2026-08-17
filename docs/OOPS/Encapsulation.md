# Encapsulation

## 1. What is Encapsulation (Simple Version)

Think of a **medicine dispenser** at a hospital. Nurses don't reach into the cabinet and grab pills directly. They put in a request — patient ID, medication, dosage — and the dispenser's internal system checks: is this the right dose? Is this patient allergic? Has this been given already today? Only if all checks pass does it release the medicine.

The cabinet's internal inventory and safety logic are **hidden**. Nobody can bypass the checks by reaching in directly. That's encapsulation: **protecting internal state by forcing all interaction through controlled, validated methods.**

### Banking analogy
You never let `balance` be a public field any class can directly set:
```java
account.balance = -5000; // if public, this compiles and runs — catastrophic bug
```
Instead, `balance` is `private`, and the only way to change it is through methods like `deposit()`/`withdraw()`, which enforce rules: no negative deposits, no over-withdrawal, every change traceable. This is a **security and correctness boundary**, not a style preference.

---

## 2. Syntax Fundamentals

```java
public class Account {
    private double balance; // private — no outside class can touch this directly

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }
}
```

### Rules
1. **`private`** is the strictest access modifier — visible only inside the same class, not even subclasses can see it.
2. There's **no dedicated keyword** for encapsulation — it's a discipline enforced through access modifiers + method design, not a single language feature.
3. **Getters don't have to exist for every field.** If a field should never be readable externally (e.g. an internal audit token or CVV), simply don't write a getter for it.
4. **Setters aren't required to be plain field assignments.** A well-encapsulated setter typically contains **validation logic** — that's the entire point; a public field can't validate itself, a method can.
5. Encapsulation works at the **field level**, not the class level — a class can mix `private` fields with `protected` ones, depending on what subclasses legitimately need direct access to.
6. **Immutability is the strongest form of encapsulation** — a `private final` field with **no setter at all** can never change after construction (this is why `record` types are fully encapsulated, immutable data holders by design).

---

## 3. Deep Dive

### Encapsulation protects **invariants**

An **invariant** is a rule that must always hold true for an object to be considered valid. For a bank account: *balance can never go negative* (without overdraft), *account number never changes after creation*, *every balance change must be traceable*.

If fields are public, any invariant can be silently broken by any code, anywhere, with zero warning:
```java
account.balance = -999999; // no validation, no error, silent corruption
```

Encapsulation makes it **structurally impossible** to violate invariants — the only door in is a method, and that method enforces the rules every time.

### Encapsulation enables information hiding / change management

Say `Account.balance` is a `double`, but later you switch it to `BigDecimal` (the correct type for money in production, since `double` causes floating-point rounding errors). If `balance` were a public field, every class touching `account.balance` directly would break.

But if access only ever goes through `getBalance()`/`deposit()`/`withdraw()`, you can change the **internal representation** freely as long as public method signatures stay consistent. Callers never notice. This is **information hiding** — the real engineering payoff of encapsulation: decoupling internal implementation from everyone who depends on you.

### Encapsulation vs Abstraction (reinforced)
- **Abstraction** = hides **complexity** (design-level: "what does the caller need to know?")
- **Encapsulation** = hides **data** (implementation-level: "how do I protect and control access to my own state?")

Example: `PaymentGateway` interface = abstraction (caller doesn't know *how* payment happens). `VisaGateway`'s `private final String apiKey` = encapsulation (the key is physically inaccessible, regardless of what the interface exposes).

---

## 4. The Reference Leak Bug — Returning Mutable Internal State

### The trap
```java
public List<String> getTransactionHistory(){
    return transactionHistory; // ⚠️ returns the SAME object, not a copy
}
```

```java
List<String> hacked = account.getTransactionHistory();
hacked.add("FAKE - HACKED TRANSACTION");
System.out.println(account.getTransactionHistory()); // fake entry shows up here too!
```

### Why this happens — reference semantics
`hacked = account.getTransactionHistory()` does **not** create a new list. It creates a new variable `hacked` that stores a **reference** (memory address) pointing to the **exact same `ArrayList` object** that lives inside `Account`. There's only ONE list object — `transactionHistory` (inside `Account`) and `hacked` (outside) are two labels pointing at the same box in memory.

So `hacked.add(...)` mutates the real internal list directly — completely bypassing `deposit()`/`withdraw()`, which are supposed to be the only doors in.

### Why this is a real encapsulation bug
`transactionHistory` is `private` — but `private` only blocks **direct field access** (`account.transactionHistory`). It does nothing to stop this **indirect leak through a returned reference**. Any code anywhere that calls the getter gets a live handle to internal state and can silently corrupt it.

**Banking angle:** this is exactly the kind of bug that could let a bug (or malicious caller) silently inject a fake transaction into an audit trail — a real compliance/security failure.

---

## 5. Fixing the Leak — Two Approaches

### Approach A: Defensive copy
```java
public List<String> getTransactionHistory(){
    return new ArrayList<>(transactionHistory); // brand-new list, copied values
}
```
The returned list is a genuinely separate object. Caller mutations have zero effect on the real internal list. **But:** the caller's `.add()` call still "succeeds" silently — no error, it just does nothing to the real data. If a developer mistakenly believes they modified the real account history, this can cause confusing bugs later.

### Approach B: Unmodifiable view (fail-fast)
```java
import java.util.Collections;

public List<String> getTransactionHistory(){
    return Collections.unmodifiableList(transactionHistory);
}
```
Doesn't copy — wraps the original list in a **read-only view**. Any attempt to call `.add()`/`.remove()`/etc. on it throws `UnsupportedOperationException` **immediately**, at the exact line of the illegal call.

---

## 6. "Fail Fast" / "Fail Loudly" — Terminology

**Fail fast:** if something is wrong, the system should detect and report it **immediately, at the earliest possible point** — rather than silently continuing with bad data/state and causing confusing problems much later, far from the actual root cause.

**Fail loudly:** closely related — when something fails, **make it obvious** (throw a clear exception, log an error) rather than **failing silently**, where the system quietly does the wrong thing without telling anyone.

### Contrast using the two getter approaches
- **Defensive copy** → arguably fails *silently* in a subtle sense: the caller's mutation attempt "succeeds" with no error, but accomplishes nothing real. No crash, but potential confusion later about why a "successful" change disappeared.
- **Unmodifiable list** → fails *fast and loud*: the illegal mutation attempt throws `UnsupportedOperationException` immediately, at the precise line of the mistake. The developer instantly knows this list isn't meant to be modified.

**Why fail-fast is usually preferred in real backend systems:** a bug that accidentally tries to mutate a "read-only" object gets caught immediately (in testing, or visibly in production logs) with the unmodifiable approach — versus silently vanishing into a disposable copy with the defensive-copy approach, where the bug goes unnoticed and could cause a harder-to-trace problem downstream.

---

## 7. Reading a Stack Trace (from `UnsupportedOperationException` example)

```
Exception in thread "main" java.lang.UnsupportedOperationException
	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1095)
	at org.ashish.learning.OOPS.Encapsulation.Encapsulation_Demo.main(Encapsulation_Demo.java:60)
```

- **`UnsupportedOperationException`** — built-in Java exception meaning "this operation exists on the type, but is not supported by this particular object." `.add()` compiles fine (it's part of the `List` interface), but this specific implementation's `.add()` is written to throw rather than actually add.
- **Stack trace order:** the **last line** (bottom) is typically where things trace back to **your own code** — here, `Encapsulation_Demo.java:60`, the exact line calling `hacked.add(...)`. Lines above it show the internal call chain that the JVM went through as a result.
- **`Process finished with exit code 1`** — non-zero exit code = abnormal termination (crash). `exit code 0` = the program ran to completion successfully.

---

## 8. Code Examples (Banking Domain)

### Example 1 — Account with validated mutation paths
```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Account {
    private String accountNumber;
    private double balance;
    private List<String> transactionHistory = new ArrayList<>();

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public double getBalance() {
        return balance; // read-only — no setBalance() exists at all
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
        transactionHistory.add("Deposited: " + amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient Funds");
        }
        balance -= amount;
        transactionHistory.add("Withdrew: " + amount);
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory); // fail-fast protection
    }
}
```

**Key design choice:** there is no `setBalance()` method at all. The only mutation paths are `deposit()`/`withdraw()`, each enforcing rules — deliberately deciding what mutation paths should exist, not blindly adding a getter/setter for every field.

### Example 2 — Fully hidden sensitive field (CVV never leaves the object)
```java
class CardDetails {
    private String cardNumber;
    private String cvv; // no getter for this — ever
    private double balance;

    public CardDetails(String cardNumber, String cvv, double balance) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.balance = balance; // remember: constructor params must be explicitly assigned to fields!
    }

    public String getMaskedCardNumber() {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public boolean validatePayment(String enteredCvv) {
        return this.cvv.equals(enteredCvv); // cvv used internally, never exposed
    }

    public void transferMoney(double amount, String cvv) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (validatePayment(cvv)) {
            balance -= amount;
            System.out.println("Transfer Successful. Remaining Balance: " + balance);
        } else {
            System.out.println("CVV is incorrect");
        }
    }
}
```

**Common bug to watch for:** if a constructor accepts a parameter with the same name as a field (e.g. `balance`) but never writes `this.balance = balance;`, the field silently stays at its default value (`0.0` for `double`) — Java gives no compile error, since it's syntactically valid, just semantically incomplete. Always verify every constructor parameter meant to initialize a field is actually assigned with `this.x = x`.

---

## 9. Quick Revision Checklist

- [ ] Encapsulation = hiding **data** + controlling access via methods; Abstraction = hiding **complexity** via design
- [ ] `private` fields + public methods (getters/validated setters) is the core mechanism — no dedicated keyword exists
- [ ] Not every field needs a getter or setter — deliberately decide what should be exposed/mutable
- [ ] Setters should validate, not just assign — that's the entire point of encapsulation
- [ ] Invariants = rules that must always hold true; encapsulation makes them structurally impossible to violate
- [ ] Information hiding = internal implementation can change freely as long as public method signatures stay stable
- [ ] Returning a mutable internal collection (e.g. `return transactionHistory;`) leaks a live reference — caller can mutate internal state directly, bypassing all validation
- [ ] Fix: defensive copy (`new ArrayList<>(original)`) — safe but silent; or `Collections.unmodifiableList(original)` — safe AND fail-fast (throws `UnsupportedOperationException` on mutation attempts)
- [ ] Fail fast/loud = detect and report errors immediately and visibly; fail silent = errors go unnoticed until later, harder to trace
- [ ] `private final` fields with no setter = strongest form of encapsulation (true immutability)
- [ ] Common bug: constructor parameter shadows a field name but is never assigned via `this.field = param` — field silently keeps its default value
