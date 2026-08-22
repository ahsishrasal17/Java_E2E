# Day 1 Mini-Project — Mini Banking System

## 1. Purpose

This project exists to **assemble** all four OOP pillars into one working program, the way real systems combine them — not as isolated demos, but as pieces that depend on and reinforce each other. Every design choice below traces back to a specific lesson from `01`–`05`.

---

## 2. Architecture Overview

```
NotificationService (interface)        ← Abstraction
 ├── EmailNotification
 └── SmsNotification

BaseAccount (abstract class)           ← Abstraction + Encapsulation
 ├── SavingsAccount                    ← Inheritance
 └── CurrentAccount                    ← Inheritance

BaseAccount --composes--> NotificationService   ← Composition
OOPSDemo.main() --uses--> BaseAccount references, calls same methods on different
                            concrete types                ← Polymorphism
```

**Optional extension (not built in this version):** a `Bank` class holding `List<BaseAccount>` would represent **aggregation** — accounts referenced by a bank but not owned/created by it, existing independently (could theoretically transfer between banks). Worth building later as more practice, but the four pillars are already fully demonstrated without it.

---

## 3. How Each Pillar Maps to This Project

### Abstraction
- `NotificationService` — an interface contract (`sendAlert(accountNumber, message)`). `BaseAccount` doesn't know or care whether it's emailing or texting the customer — it just calls the contract.
- `BaseAccount` — an abstract class defining the shared account contract (`deposit`, `withdraw`, abstract `calculateInterest()`), hiding the *how* of interest calculation from anything that just wants to use an account.

### Encapsulation
- `accountNumber` is `private final` — set once, never mutable.
- `balance` is `private` — no `setBalance()` exists anywhere; the only mutation paths are validated `deposit()`/`withdraw()`.
- `transactionHistory` is `private`, and `getTransactionHistory()` returns `Collections.unmodifiableList(...)` — the fail-fast fix from the Encapsulation concept, preventing the reference-leak bug we deliberately triggered and fixed earlier.
- Validation logic (`amount <= 0` checks, insufficient-funds checks) lives inside the class, not left to callers to remember to check themselves.

### Inheritance vs Composition
- **Inheritance:** `SavingsAccount extends BaseAccount` and `CurrentAccount extends BaseAccount` — genuine "is-a" relationships, sharing `deposit()`/`withdraw()`/`toString()` logic from the parent, differing only in `calculateInterest()`.
- **Composition:** `BaseAccount` holds a `NotificationService notificationService` field, injected via constructor. The account doesn't create its own notification logic — it's handed a working implementation (`EmailNotification` or `SmsNotification`) from outside, exactly like the `PaymentGateway`/`TransferService` pattern from Abstraction, and the `User`/`SubscriptionPlan` pattern from Composition.

### Polymorphism
- **Runtime polymorphism:** `savingsAccount.calculateInterest()` and `currentAccount.calculateInterest()` — same method call, different actual behavior, resolved via each object's vtable at runtime based on its real class.
- **Runtime polymorphism (again, on a composed object):** `notificationService.sendAlert(...)` inside `BaseAccount` — the same call correctly dispatches to `EmailNotification`'s or `SmsNotification`'s version depending on which was injected. This proves polymorphism isn't limited to inheritance hierarchies — it works through *any* interface reference, including composed dependencies.
- `toString()` uses `getClass().getSimpleName()` — reused, unmodified, in `BaseAccount`, and correctly prints `"SavingsAccount{...}"` or `"CurrentAccount{...}"` depending on the real object. The same pattern was deliberately extended to the composed `notificationService` field too — `notificationService.getClass().getSimpleName()` — avoiding the ugly default `Object.toString()` output for a collaborator object, not just the account itself.

---

## 4. Full Code

### `NotificationService.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public interface NotificationService {
    void sendAlert(String accountNumber, String message);
}
```

### `EmailNotification.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public class EmailNotification implements NotificationService {
    @Override
    public void sendAlert(String accountNumber, String message) {
        System.out.println("Email sent to account number: " + accountNumber + " with message: " + message);
    }
}
```

### `SmsNotification.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public class SmsNotification implements NotificationService {
    @Override
    public void sendAlert(String accountNumber, String message) {
        System.out.println("SMS sent to account number: " + accountNumber + " with message: " + message);
    }
}
```

### `BaseAccount.java`
```java
package org.ashish.learning.OOPS.MiniProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseAccount {
    private final String accountNumber;
    private double balance;
    private List<String> transactionHistory = new ArrayList<>();
    private NotificationService notificationService;

    BaseAccount(String accountNumber, double balance, NotificationService notificationService) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.notificationService = notificationService;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            notificationService.sendAlert(accountNumber, "Deposit failed: Amount must be positive.");
        } else {
            balance += amount;
            notificationService.sendAlert(accountNumber, "Deposited: " + amount);
            transactionHistory.add("Deposited: " + amount);
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            notificationService.sendAlert(accountNumber, "Withdrawal failed: Amount must be positive.");
        } else if (amount > balance) {
            notificationService.sendAlert(accountNumber, "Withdrawal failed: Insufficient funds.");
        } else {
            balance -= amount;
            notificationService.sendAlert(accountNumber, "Withdrew: " + amount);
            transactionHistory.add("Withdrew: " + amount);
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory); // fail-fast protection
    }

    public abstract double calculateInterest();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                ", notificationService=" + notificationService.getClass().getSimpleName() +
                '}';
    }
}
```

### `SavingsAccount.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public class SavingsAccount extends BaseAccount {
    public SavingsAccount(String accountNumber, double balance, NotificationService notificationService) {
        super(accountNumber, balance, notificationService);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * 0.04;
    }
}
```

### `CurrentAccount.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public class CurrentAccount extends BaseAccount {
    public CurrentAccount(String accountNumber, double balance, NotificationService notificationService) {
        super(accountNumber, balance, notificationService);
    }

    @Override
    public double calculateInterest() {
        return 0;
    }
}
```

### `OOPSDemo.java`
```java
package org.ashish.learning.OOPS.MiniProject;

public class OOPSDemo {
    public static void main(String[] args) {
        BaseAccount savingsAccount = new SavingsAccount("SA123", 1000, new EmailNotification());
        BaseAccount currentAccount = new CurrentAccount("CA123", 2000, new SmsNotification());

        savingsAccount.deposit(10000);
        currentAccount.deposit(5000);

        savingsAccount.withdraw(5000);
        currentAccount.withdraw(2000);

        System.out.println("Savings Account's Balance is: " + savingsAccount.getBalance());
        System.out.println("Current Account's Balance is: " + currentAccount.getBalance());

        System.out.println("Interest for Savings Account is: " + savingsAccount.calculateInterest());
        System.out.println("Interest for Current Account is: " + currentAccount.calculateInterest());

        System.out.println(savingsAccount.getTransactionHistory());
        System.out.println(currentAccount.getTransactionHistory());

        System.out.println(savingsAccount);
        System.out.println(currentAccount);
    }
}
```

---

## 5. Sample Output

```
Email sent to account number: SA123 with message: Deposited: 10000.0
SMS sent to account number: CA123 with message: Deposited: 5000.0
Email sent to account number: SA123 with message: Withdrew: 5000.0
SMS sent to account number: CA123 with message: Withdrew: 2000.0
Savings Account's Balance is: 6000.0
Current Account's Balance is: 5000.0
Interest for Savings Account is: 240.0
Interest for Current Account is: 0.0
[Deposited: 10000.0, Withdrew: 5000.0]
[Deposited: 5000.0, Withdrew: 2000.0]
SavingsAccount{accountNumber='SA123', balance=6000.0, transactionHistory=[Deposited: 10000.0, Withdrew: 5000.0], notificationService=EmailNotification}
CurrentAccount{accountNumber='CA123', balance=5000.0, transactionHistory=[Deposited: 5000.0, Withdrew: 2000.0], notificationService=SmsNotification}
```

---

## 6. Bugs Hit & Fixed During This Build (worth remembering)

1. **Dead self-assignment:** `this.transactionHistory = transactionHistory;` inside the constructor, when the constructor never accepted a `transactionHistory` parameter — silently assigns the field to itself. Harmless here since the field was already correctly initialized at declaration, but a reminder to check that every constructor line is doing meaningful work.
2. **`toString()` reverted to a hardcoded class name** (`"BaseAccount{"` instead of `getClass().getSimpleName()`) — same bug already fixed once in the Inheritance concept, worth double-checking for in any new class going forward.
3. **Validation inconsistency:** `deposit()` initially checked `amount < 0` while `withdraw()` checked `amount <= 0` — allowing a meaningless "deposit of 0" to silently succeed. Aligned both to `<= 0`.
4. **`main()` signature** — still catching `static void main()` instead of `public static void main(String[] args)`. Worth actively double-checking this on every new demo class until it becomes automatic.

---

## 7. Extending This Project Further (optional, not required for Day 1)

If you want extra practice later, consider adding:
- **`Bank` class** — composes/aggregates `List<BaseAccount>`, demonstrating aggregation specifically (accounts created outside, passed in, existing independently of any one `Bank` instance) — mirrors the `Branch`/`Employee` example from the Inheritance vs Composition notes.
- **Method overloading** — a `transfer(BaseAccount from, BaseAccount to, double amount)` alongside a `transfer(String fromAccNum, String toAccNum, double amount, Bank bank)` — practicing compile-time polymorphism in a realistic context, not just a standalone `InterestCalculator` demo.
- **A second interface** — e.g. `Auditable` with a `logAudit()` default method, implemented by `BaseAccount`, to practice a class implementing multiple interfaces at once.

None of these are required — Day 1's four pillars are already fully and correctly demonstrated in the current version.

---

## 8. Quick Revision Checklist

- [ ] `NotificationService` interface = abstraction; two implementations (`Email`, `Sms`) swapped via constructor injection, resolved polymorphically at runtime
- [ ] `BaseAccount` abstract class = abstraction + shared concrete logic (`deposit`/`withdraw`) + encapsulated state
- [ ] `private final accountNumber`, `private balance` with no direct setter, `Collections.unmodifiableList()` for transaction history = encapsulation, including the fail-fast reference-leak fix
- [ ] `SavingsAccount`/`CurrentAccount extends BaseAccount` = genuine "is-a" inheritance; `BaseAccount` composes `NotificationService` = "has-a" composition
- [ ] `calculateInterest()` and `sendAlert()` both resolve polymorphically at runtime, based on the real object behind the reference — the same mechanism, applied both to an inheritance hierarchy and to a composed interface field
- [ ] `getClass().getSimpleName()` pattern applies to ANY object needing a readable `toString()` — not just the class it's written in, but also composed collaborator objects referenced from within it
