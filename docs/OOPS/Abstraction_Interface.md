# Abstraction — Interfaces

## 1. What is Interface-Based Abstraction (Simple Version)

Think of an interface as a **job contract**, not a blueprint for a specific worker.

If you hire someone as a "Payment Processor," the contract just says: *"You must be able to process a payment and return a result."* It says nothing about **how** — whether you're a Visa specialist, a UPI specialist, or brand new to the job. The contract is purely about **capability**, not identity or shared history.

**Banking analogy:** `PaymentGateway` is a contract every payment rail signs: *"Whoever you are — Visa, UPI, RuPay, even something invented next year — you must be able to `processPayment(amount, accountNumber)` and hand back a result."* `TransferService` only ever reads this contract. It never needs to know or care which specific company is behind the signature.

---

## 2. Syntax Fundamentals

```java
public interface PaymentGateway {
    PaymentResult processPayment(double amount, String accountNumber);
}
```

- Declared with `interface`, not `class`.
- Every method without a body is **implicitly `public abstract`** — you never write those keywords, they're there regardless.
- A class uses `implements` (not `extends`) to fulfill an interface's contract.

### Rules

1. **All fields are implicitly `public static final`** — constants only, no instance state:
```java
public interface PaymentGateway {
    int MAX_RETRIES = 3; // same as: public static final int MAX_RETRIES = 3;
    // private String apiSecret; // ❌ compile error — interfaces can't hold instance fields
}
```

2. **A class CAN implement multiple interfaces** (comma-separated) — the key structural difference from `extends`, which allows only one parent class:
```java
public class VisaGateway implements PaymentGateway, Auditable, Loggable {
    // must implement ALL abstract methods from ALL three
}
```

3. **`default` methods (Java 8+)** — interfaces can have method bodies:
```java
default void logTransaction() {
    System.out.println("Logging transaction...");
}
```
Implementing classes inherit this automatically; they *can* override it, but don't have to.

4. **`static` methods (Java 8+)** — utility/factory methods belonging to the interface itself:
```java
static PaymentGateway getDefault() {
    return new UpiGateway();
}
// called as: PaymentGateway.getDefault() — never via an instance
```

5. **`private` methods (Java 9+)** — private helper methods used internally by `default` methods to avoid duplication. Not visible to implementing classes.

6. **An interface can `extend` another interface** (not `implements`), and can extend **multiple** interfaces at once:
```java
public interface SecureGateway extends PaymentGateway, Auditable {
    // inherits abstract methods from BOTH
}
```

7. **A class implementing an interface must implement every abstract method**, or the class itself must be declared `abstract`.

---

## 3. Edge Cases / Interview Q&A

### Q: Can you instantiate an interface directly?
**No.** `new PaymentGateway()` → compile error: *"PaymentGateway is abstract; cannot be instantiated."* Same rule as abstract classes.

### Q: Can an interface have instance fields?
**No.** Any field is automatically `public static final` — a constant shared across all implementers, never mutable, never per-instance.

### Q: Can a class implement multiple interfaces?
**Yes** — unlimited, comma-separated. This is how Java works around single inheritance for classes (`extends` only allows one parent).

### Q: What's the difference between `default` and `static` interface methods?
- `default` → has a body, inherited by implementing classes, callable on an **instance** (`rupay.logTransaction()`), can be overridden.
- `static` → has a body, belongs to the **interface itself**, called via `InterfaceName.method()`, cannot be overridden by implementers.

### Q: Why were `default` methods added in Java 8?
Before Java 8, adding a new method to an interface broke **every** implementing class (they'd fail to compile — didn't implement the new method). This made evolving interfaces like the Collections framework (`forEach`, `stream()`, etc.) impractical. `default` methods let you add new methods with a fallback implementation, so old implementers keep compiling without changes.

**Risk to remember:** a new `default` method with a no-op body can silently pass unnoticed in existing implementers (e.g., a `default validateCompliance() {}` that does nothing) unless someone remembers to override it — a real, quiet pitfall in production systems.

### Q: Interface vs Abstract class — how do you decide which to use?
Ask: *"Are these classes fundamentally related, sharing real behavior and state? Or do they just need to honor the same capability despite being unrelated?"*
- Fundamentally same kind of thing, shares state/logic → **abstract class** (e.g. `SavingsAccount`/`CurrentAccount`, both "are" accounts with a balance)
- Unrelated internally, must honor the same contract → **interface** (e.g. `VisaGateway`/`UpiGateway`/`RupayGateway` — different vendors, zero shared implementation)

Rule of thumb: **"is-a" + shared state/logic → abstract class. "can-do" contract across unrelated types → interface.**

---

## 4. `record` — Why It's Used for Return Types Like `PaymentResult`

```java
public record PaymentResult(boolean isSuccess, String transactionId) {}
```

A `record` is Java's shorthand for an **immutable data-holder class**. This one line generates:
- `private final` fields for every component
- A constructor accepting all components
- Accessor methods **without** `get`/`is` prefix — `result.isSuccess()`, `result.transactionId()`
- Auto-generated `equals()`, `hashCode()`, `toString()`

### Rules
- All fields implicitly `private final` — always immutable, no setters, ever.
- A record is implicitly `final` — cannot be extended.
- Introduced as a standard feature in Java 16 (preview in 14/15), to eliminate boilerplate "dumb data class" code.

### Why `PaymentResult` exists in `PaymentGateway`
`processPayment()` needs to return **two related pieces of information** (success flag + transaction ID), but a Java method can only return **one value**. `record` bundles both into a single, named, type-safe object instead of hacking it with `Object[]` or `Map<String,Object>`.

```java
PaymentResult result = gateway.processPayment(5000, "ACC001");
result.isSuccess();      // clear, compiler-checked
result.transactionId();  // clear, compiler-checked
```

### Why immutable specifically?
A transaction result, once created, should never change. If `VisaGateway` says "success, TXN123," nothing downstream should ever mutate that to "failure" later — that would be a serious bug/audit issue in a financial system. `record`'s enforced immutability fits perfectly for representing a fact that already happened.

**Banking angle:** real payment gateway responses carry more than just success/failure — timestamp, gateway error code, retry-eligibility, etc. `record` types are exactly how Spring Boot models these **response DTOs** (Data Transfer Objects) passed between service → controller → client, or between microservices.

---

## 5. Constructor Injection & Runtime Polymorphism (Preview)

```java
public class TransferService {
    private final PaymentGateway gateway;

    public TransferService(PaymentGateway gateway) { // constructor injection
        this.gateway = gateway;
    }

    public void transfer(double amount, String accountNumber) {
        PaymentResult result = gateway.processPayment(amount, accountNumber);
        System.out.println("Transfer successful: " + result.isSuccess());
    }
}
```

- `TransferService`'s constructor receives **any** object implementing `PaymentGateway`, and stores it — this is **constructor injection**, a form of **dependency injection**. This is literally what Spring's `@Autowired` automates later.
- `gateway` is declared as type `PaymentGateway` (the interface), but the **actual object** in memory is `VisaGateway` or `UpiGateway`. At runtime, Java calls whichever class's `processPayment()` actually backs the object — this is **runtime polymorphism / dynamic method dispatch**. The decision happens while the program runs, not at compile time.
- The payoff: `TransferService` never changes, regardless of which gateway is swapped in.

---

## 6. Full Working Example (Banking Domain)

```java
public record PaymentResult(boolean isSuccess, String transactionId) {}

public interface PaymentGateway {
    PaymentResult processPayment(double amount, String accountNumber);
}

public class VisaGateway implements PaymentGateway {
    private final String apiKey = "visa-secret-key"; // hidden from every caller

    @Override
    public PaymentResult processPayment(double amount, String accountNumber) {
        System.out.println("Routing ₹" + amount + " via Visa network...");
        return new PaymentResult(true, "TXN123");
    }
}

public class UpiGateway implements PaymentGateway {
    @Override
    public PaymentResult processPayment(double amount, String accountNumber) {
        System.out.println("Routing ₹" + amount + " via UPI switch...");
        return new PaymentResult(true, "TXN456");
    }
}

public class TransferService {
    private final PaymentGateway gateway;

    public TransferService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void transfer(double amount, String accountNumber) {
        PaymentResult result = gateway.processPayment(amount, accountNumber);
        System.out.println("Transfer successful: " + result.isSuccess());
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        // PaymentGateway gw = new PaymentGateway(); // ❌ compile error — interface can't be instantiated

        PaymentGateway visa = new VisaGateway();
        new TransferService(visa).transfer(2000, "ACC001");

        PaymentGateway upi = new UpiGateway();
        new TransferService(upi).transfer(5000, "ACC002");
    }
}
```

---

## 7. `main()` Method — Important Note

The **standard, always-correct entry point signature** is:
```java
public static void main(String[] args)
```
This should be used in ALL production code and is what interviewers expect.

**Newer Java (21 preview → 25 finalized, JEP 445/512)** allows a simplified form for beginners:
```java
void main() {
    System.out.println("Hello");
}
```
This drops `public`, `static`, and `String[] args` — but it's a teaching/onboarding convenience feature, not something used in real backend codebases. **Always default to the traditional signature** for interview prep and production-style code.

---

## 8. Quick Revision Checklist

- [ ] Interface methods without a body are implicitly `public abstract`
- [ ] Interface fields are implicitly `public static final` — constants only, never instance state
- [ ] Interfaces cannot be instantiated directly (`new InterfaceName()` → compile error)
- [ ] A class can implement MULTIPLE interfaces (unlike single-class `extends`)
- [ ] `default` methods (Java 8+) have a body, are inherited, callable on instances, overridable
- [ ] `static` methods (Java 8+) belong to the interface itself, called via `InterfaceName.method()`
- [ ] `private` methods (Java 9+) are internal helpers for default methods, not visible to implementers
- [ ] Interfaces can `extend` multiple other interfaces
- [ ] Interface vs abstract class: "can-do" unrelated contract → interface; "is-a" shared state/logic → abstract class
- [ ] `record` = immutable data holder: `private final` fields, no-prefix accessors, auto equals/hashCode/toString, implicitly `final`
- [ ] Constructor injection = passing an interface-typed dependency via the constructor (manual DI, same idea as Spring's `@Autowired`)
- [ ] Runtime polymorphism = actual object's method runs, decided at runtime, regardless of the declared reference type
