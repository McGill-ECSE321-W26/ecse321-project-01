## Meeting Minutes 13/02/2026

- **Meeting Type:** Standup
- **Date:** 13/02/2026
- **Time and place:** 18h, Online (Discord)
- **Project:** [ECSE 321 Project - Group 1](https://github.com/orgs/McGill-ECSE321-W26/projects/30)
- **Milestone:** Deliverable 1
- **Note-taker:** @B-Baki
- **Attendees:** @B-Baki, @Gnardisitor, @MinhVo2005, @Tangdavid1
- **Absent:** @tran-ethan, @santipadron, @jayjay4387

### Agenda

1. Discuss the domain model
2. Write first draft of Umple domain model
3. Separate work for use-case diagrams

### Discussion and notes

First draft for domain model, it still needs some work, but it is a start:

```java
namespace ca.mcgill.ecse321.group1.model;

class ClothingManager {
  1 <@>- * User users;
  1 <@>- 0..1 Manager;
  1 <@>- * Employee employees;
  1 <@>- * Customer customers;
  1 <@>- * Address addresses;
  1 <@>- * ClothingItem items;
  1 <@>- * ClothingItemProperties properties;
  1 <@>- * ClothingModel models;
}

class User {
  unique email;
  password;
  1 -- 0..2 UserRole roles;
}

class UserRole {
  abstract;
}

class Manager {
  isA UserRole;
}

class Employee {
  isA UserRole;
  1 -- * Order preparingOrders;
}

class Customer {
  isA UserRole;
  int loyaltyPoints;
  1 -- * Address addresses;
  1 <@>- 1 Cart;
  1 -- * Order orders;
}

class Address {
  autounique id;
  street;
  city;
  province;
  postalCode;
}

class Cart {
  autounique id;
  0..1 -- * ClothingItem items;
}

class Order {
  autounique id;
  enum OrderStatus {
    Preparing,
    Delivered,
    Cancelled
  }
  OrderStatus orderStatus;
  Date orderDate;
  Date deliveryDate;
  0..1 -- * ClothingItem items;
}

class ClothingItem {
  autounique id;
  * -- 1 ClothingItemProperties properties;
}

class ClothingItemProperties {
  autounique id;
  enum Size {
    S,
    M,
    L,
    XL
  }
  Size size;
  color;
  int inStock;
  * -- 1 ClothingModel model;
}

class ClothingModel {
  unqiue name;
  float price;
}
```

### Decisions

Ethan, once he is available, will help setup the JPA annotations on the domain model.
The following people self-assigned for the following use-case diagrams:
- Dragos: FR10
- Bassam: FR1
- Jason: FR2

### Action items

| Action                        | Owner       | Due Date | GitHub Link |
| ----------------------------- | ----------- | -------- | ----------- |
| Setup JPA annotation in model | @tran-ethan | ASAP     |             |
|                               |             |          |             |

### Blockers

- Is the domain model good enough? It still probably needs tweaks and stuff.

### Other
