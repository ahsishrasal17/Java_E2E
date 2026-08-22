# Polymorphism (Day 1)

## 1. What is Polymorphism (Simple Version)

**Compile-time polymorphism (overloading):** Think of a restaurant menu item called "Coffee." You can order "Coffee," "Coffee with milk," or "Coffee with milk and sugar" — same base name, but the specific order you place determines exactly what you get, and the kitchen knows exactly which recipe to use **the moment you place the order**.

**Runtime polymorphism (overriding):** Think of pressing a "Pay" button at checkout. The button always says "Pay," but whether it swipes a card, deducts from a wallet, or processes UPI depends entirely on **what's actually plugged in behind it** — not decided until you actually press the button.

### Banking analogy
`TransferService.transfer()` calling `gateway.processPayment(...)` is runtime polymorphism — the same line of code runs Visa's logic if a `VisaGateway` was injected, UPI's logic if a `UpiGateway` was injected. The decision happens **while the program runs**, based on the real object.

An overloaded `calculate(double, double)` vs `calculate(double, double, int)` is compile-time — the compiler looks at how many arguments you wrote **in source code** and picks the matching method **before the program ever runs**.

---

## 2. Syntax Fundamentals

### Compile-time polymorphism (Method Overloading)
Same method **name**, different **parameter list**, same class.
```java
public class InterestCalculator {
    public double calculate(double principal, double rate) {
        return principal * rate;
    }
    public double calculate(double principal, double rate, int years) {
        return principal * rate * years;
    }
    public double calculate(int principal, double rate) {
        return principal * rate;
    }
}
```
- Decided **at compile time** — also called **static binding / early binding**.
- **Return type alone does NOT count as a valid overload.** Identical parameter lists with different return types → compile error.

### Runtime polymorphism (Method Overriding)
Same method **signature**, parent class defines it, subclass redefines it.
```java
public abstract class BaseAccount {
    public abstract double calculateInterest();
}
public class SavingsAccount extends BaseAccount {
    @Override
    public double calculateInterest() { return balance * 0.04; }
}
```
- Decided **at runtime**, based on the actual object — also called **dynamic binding / late binding**.

### Rules
1. **`@Override` is optional but strongly recommended.** Without it, a typo (e.g. `calculateInterset()`) silently creates a brand-new, unrelated method — no compile error, just a silent bug. With `@Override`, the compiler verifies a matching parent method exists and errors out immediately if not.
2. **Overriding requires the same signature** — same name, same parameter types/order. Return type must be the same or a covariant subtype.
3. **Cannot override `private`, `static`, or `final` methods.** `static` methods aren't overridden — they're **hidden** (different mechanism, see below).
4. **Overriding cannot reduce visibility.** `public` parent method → overriding method must also be `public` (can widen, never narrow).
5. **Constructors are never overridden**, but they **can be overloaded** (multiple constructors, different parameter lists, same class).

---

## 3. Deep Dive

### The classic interview trap — overloading + `null`
```java
public void process(String s) { System.out.println("String version"); }
public void process(Object o) { System.out.println("Object version"); }

process(null); // → "String version"
```
The compiler picks the **most specific** matching type when ambiguous. `String` is more specific than `Object`. Overload resolution happens at compile time based on the most specific applicable type — not any runtime value.

### Widening in overload resolution
```java
calc.calculate(1000, 0.04, 3); // matches calculate(double, double, int)
```
`1000` is an `int` literal, but the only 3-parameter overload expects `(double, double, int)`. Java applies **automatic widening** (`int` → `double`, lossless, since every `int` fits exactly in a `double`) to make the match. **Rule:** Java always prefers an **exact match** first; widening is only used as a fallback when no exact match exists. If both `calculate(double, double, int)` and `calculate(int, double, int)` existed, the exact `(int, double, int)` match would win over the widened one.

### Static methods and "hiding" — looks like overriding, isn't
```java
public class BaseAccount {
    public static void printBankName() { System.out.println("Base Bank"); }
}
public class SavingsAccount extends BaseAccount {
    public static void printBankName() { System.out.println("Savings Bank"); } // HIDING, not overriding
}
```
`static` methods belong to the **class**, not instances — there's no object to look up at runtime, so they can't be polymorphic. Calling through a reference uses the **declared type**, not the runtime object — the opposite of overriding. A favorite "gotcha" question because it looks identical to overriding syntactically but behaves completely differently.

### Why polymorphism matters — Open/Closed Principle
`TransferService` never needs `if (gateway instanceof VisaGateway)` branches. Without polymorphism, every new payment provider would require modifying every such branch throughout the codebase. With it, adding `RupayGateway` requires **zero changes** to `TransferService` — just a new class implementing the existing contract. This is the **Open/Closed Principle** (part of SOLID): code should be **open for extension, closed for modification.**

---

## 4. JVM Perspective — How Polymorphism Actually Works Under the Hood

### The three memory regions involved
- **Method Area** (part of Metaspace since Java 8): stores class-level info — bytecode of every method, and a **vtable (virtual method table)** per class, mapping method signatures to the actual bytecode to run for that class.
- **Heap**: where actual objects live (`new VisaGateway()`, `new SavingsAccount(...)`). Every object also silently carries a **pointer back to its class metadata** in the Method Area.
- **Stack**: one per thread, holds method call frames and local variables — including **references** (memory addresses), never the actual objects themselves.

```
STACK (thread)                 HEAP                          METHOD AREA
+----------------+       +---------------------+        +------------------------+
| main() frame   |       | VisaGateway object   |        | VisaGateway class      |
| gateway = ref -----------> apiKey = "..."      |          | metadata + VTABLE      |
+----------------+       | [class pointer] --------------->|                        |
                         +---------------------+        | processPayment() ----> [bytecode] |
                                                          +------------------------+
```

### Tracing `gateway.processPayment(...)`
1. **Stack** — `gateway` (declared type `PaymentGateway`) holds a reference into the heap, not an object.
2. **Heap** — the real `VisaGateway` object sits here, carrying a hidden pointer to its own class metadata in the Method Area.
3. **Method Area** — `VisaGateway`'s vtable is consulted: `processPayment` → bytecode address for `VisaGateway`'s specific implementation.

### Overloading vs Overriding — what happens at each stage

**Overloading (compile-time / static binding):**
```
Source code                Compiler                    Bytecode
calc.calculate(1000, 0.04)  → matches args against  →  exact call baked in —
                              available signatures       no lookup needed at runtime
                              picks calculate(int,double)
```
The compiler has the full source in front of it, matches arguments against all available signatures, and **bakes the exact method call directly into the compiled bytecode**. Nothing is left to decide when the program runs.

**Overriding (runtime / dynamic binding):**
```
gateway.processPayment()
   → JVM follows gateway's reference to the heap object
   → reads the heap object's class pointer
   → looks up "processPayment" in THAT class's vtable
   → jumps to the bytecode address found there
   (this entire lookup repeats fresh, every single call)
```
The compiler only knows `gateway` is *declared* as `PaymentGateway` — it can't know the real object's class in advance (it might even be a class that didn't exist yet when this code was compiled, loaded from elsewhere). So the compiler emits an instruction to **resolve this at runtime**, via the vtable, every time that line executes.

### Why static method "hiding" behaves differently at the JVM level
`static` methods have **no vtable entry at all** — they don't belong to any object, so there's no heap object or class pointer to follow at runtime. The compiler resolves static calls the same way it resolves overloads: using the **declared type**, directly, at compile time. This is exactly why calling a static method through a `BaseAccount`-typed reference always runs `BaseAccount`'s version, even if the real object is a `SavingsAccount` — there's no runtime dispatch mechanism for `static` at all.

### Summary table

| | Overloading | Overriding | Static hiding |
|---|---|---|---|
| Binding | Static (compile-time) | Dynamic (runtime) | Static (compile-time) |
| Decided by | Argument types at call site | Actual object's class via vtable | Declared reference type |
| Vtable involved? | No | Yes | No |
| Where resolved | Compiler, baked into bytecode | JVM, every call, at runtime | Compiler, baked into bytecode |

---

## 5. Code Examples (Banking Domain)

### Example 1 — Overloading
```java
public class InterestCalculator {
    public double calculate(double principal, double rate) {
        return principal * rate;
    }
    public double calculate(double principal, double rate, int years) {
        return principal * rate * years;
    }
    public double calculate(int principal, double rate) {
        System.out.println("Using int-principal version");
        return principal * rate;
    }
}

public class OverloadDemo {
    public static void main(String[] args) {
        InterestCalculator calc = new InterestCalculator();
        System.out.println(calc.calculate(1000.0, 0.04));      // (double, double)
        System.out.println(calc.calculate(1000.0, 0.04, 3));   // (double, double, int)
        System.out.println(calc.calculate(1000, 0.04));        // (int, double)
    }
}
```

### Example 2 — Overriding
```java
public abstract class BaseAccount {
    protected double balance;
    public BaseAccount(double balance) { this.balance = balance; }
    public abstract double calculateInterest();
}

public class SavingsAccount extends BaseAccount {
    public SavingsAccount(double balance) { super(balance); }
    @Override
    public double calculateInterest() { return balance * 0.04; }
}

public class CurrentAccount extends BaseAccount {
    public CurrentAccount(double balance) { super(balance); }
    @Override
    public double calculateInterest() { return 0; }
}

public class OverrideDemo {
    public static void main(String[] args) {
        BaseAccount[] accounts = {
            new SavingsAccount(5000),
            new CurrentAccount(8000)
        };
        for (BaseAccount account : accounts) {
            // same line, different behavior each time — vtable lookup per actual object
            System.out.println(account.getClass().getSimpleName() + " interest: " + account.calculateInterest());
        }
    }
}
```
**What to notice:** one line calling `account.calculateInterest()` produces different results per element — Java looks up the actual runtime type of each object (via the vtable), not the declared array type (`BaseAccount[]`). Adding a third account type requires zero changes to this loop.

---

## 6. Quick Revision Checklist

- [ ] Overloading = same name, different parameters, same class, resolved at compile time (static/early binding)
- [ ] Overriding = same signature, parent → subclass, resolved at runtime (dynamic/late binding)
- [ ] Return type alone is NOT a valid overload distinguisher
- [ ] `@Override` is optional but should always be used — catches typos as compile errors instead of silent bugs
- [ ] Cannot override `private`, `static`, or `final` methods; cannot reduce visibility when overriding
- [ ] `null` in overload resolution picks the most specific applicable type
- [ ] Widening (`int`→`double`, etc.) is used in overload matching only as a fallback after exact matches are ruled out
- [ ] Static methods are "hidden," not overridden — resolved by declared type, not runtime object, because there's no vtable entry for static methods
- [ ] Polymorphism enables the Open/Closed Principle — new types added without modifying existing code
- [ ] JVM: Stack holds references; Heap holds objects (+ a pointer to their class metadata); Method Area holds class bytecode + vtables
- [ ] Overloading: compiler bakes the exact method call into bytecode — zero runtime lookup
- [ ] Overriding: JVM follows the object's class pointer to its vtable and dispatches accordingly — this lookup happens fresh on every call
- [ ] Static calls: resolved like overloads, by declared type, at compile time — no vtable, no runtime dispatch
