package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.repository.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final ItemRepository itemRepository;
  private final EmployeeRepository employeeRepository;
  static float loyaltyModifier = 0.2f; // Conversion rate: 1 loyalty point = $0.20
  private final ClothingVariantRepository clothingVariantRepository;

  public OrderService(
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      CustomerRepository customerRepository,
      EmployeeRepository employeeRepository,
      ClothingVariantRepository clothingVariantRepository) {
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.clothingVariantRepository = clothingVariantRepository;
  }

  private Customer findCustomer(String customerID) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no customer with id " + customerID + ".");
    }
    return customer;
  }

  private Order findOrder(String orderID) {
    Order order = orderRepository.findByOrderID(orderID);
    if (order == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no order with id " + orderID + ".");
    }
    return order;
  }

  private Employee findEmployee(String employeeID) {
    Employee employee = employeeRepository.findByRoleID(employeeID);
    if (employee == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no employee with id " + employeeID + ".");
    }
    return employee;
  }

  private Order.OrderStatus parseOrderStatus(String orderStatus) {
    try {
      return Order.OrderStatus.valueOf(orderStatus);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid order status " + orderStatus + ".");
    }
  }

  private void validateCartNotEmpty(Customer customer, String customerID) {
    List<Item> items = itemRepository.findByCustomer(customer);
    if (items == null || items.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "There are no items in the cart of customer " + customerID + ".");
    }
  }

  private void validateDeliveryDate(Date deliveryDate) {
    if (deliveryDate == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery Date is null.");
    }
    if (deliveryDate.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Delivery Date must be at least 24 hours after the order date.");
    }
  }

  private void validateLoyaltyPoints(Customer customer, int usedLoyaltyPoints) {
    if (usedLoyaltyPoints < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loyalty points must be positive.");
    }
    if (customer.getLoyaltyPoints() < usedLoyaltyPoints) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "The customer does not have enough loyalty points to complete the purchase.");
    }
  }

  private void validateEmployeeNotCustomer(Employee employee, Order order) {
    if (employee.getPerson().getPersonID().equals(order.getCustomer().getPerson().getPersonID())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The employee cannot be assigned to their own order.");
    }
  }

  private void validateCurrentDeliveryDateNotWithin24Hours(Order order) {
    if (order.getDeliveryDate().toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Delivery Date cannot be changed within 24 hours of the current delivery date.");
    }
  }

  private void validateCancellation(Order order, Order.OrderStatus newStatus) {
    if (newStatus != Order.OrderStatus.Cancelled) return;
    if (order.getOrderStatus() == Order.OrderStatus.Delivered) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Cannot cancel an order that has already been delivered.");
    }
    if (order.getDeliveryDate().toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Cannot cancel an order within 24 hours of its delivery date.");
    }
  }

  private Order initializeOrder(Customer customer, Date deliveryDate) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setOrderDate(Date.valueOf(LocalDate.now()));
    order.setDeliveryDate(deliveryDate);
    order.setAddress(customer.getAddress());
    order.setOrderStatus(Order.OrderStatus.Preparing);
    return order;
  }

  private void computeLoyaltyPoints(Order order, Customer customer, int usedLoyaltyPoints) {
    float total = 0.0f;

    // Required to not modify same list you're iterating through
    List<Item> items = List.copyOf(customer.getItems());

    for (Item item : items) {
      // Compute final price of item and total price of the order
      float itemPrice = item.getClothingVariant().getModel().getPrice();
      item.setPrice(itemPrice);
      total += item.getQuantity() * itemPrice;

      // Decrement stock quantity
      ClothingVariant variant = item.getClothingVariant();
      if (item.getQuantity() > variant.getStockQuantity()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "There are not enough items in stock to check out.");
      }
      variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());

      // Swap items from the cart to the order
      order.addItem(item);
      customer.removeItem(item);
    }

    // Compute loyalty savings
    order.setLoyaltySaving((float) usedLoyaltyPoints * loyaltyModifier);
    if (order.getLoyaltySaving() >= total) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Cannot pay for an entire order with only loyalty points.");
    }

    // Check gained loyalty savings and compute final loyalty points of the customer
    int gainedLoyaltyPoints = (int) (total * loyaltyModifier);
    customer.setLoyaltyPoints(
        customer.getLoyaltyPoints() + gainedLoyaltyPoints - usedLoyaltyPoints);
  }

  @Transactional
  public Order createOrder(String customerID, Date deliveryDate, int usedLoyaltyPoints) {
    Customer customer = findCustomer(customerID);
    validateCartNotEmpty(customer, customerID);
    validateDeliveryDate(deliveryDate);
    validateLoyaltyPoints(customer, usedLoyaltyPoints);

    Order order = initializeOrder(customer, deliveryDate);
    computeLoyaltyPoints(order, customer, usedLoyaltyPoints);

    List<Item> items = order.getItems();
    for (Item item : items) {
      itemRepository.save(item);
      clothingVariantRepository.save(item.getClothingVariant());
    }
    customerRepository.save(customer);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrder(
      String orderID, String employeeID, Date deliveryDate, String orderStatus) {
    Order order = findOrder(orderID);

    // This is a PATCH method, we allow partial modifications (not all fields must be specified)
    if (employeeID != null) {
      Employee employee = findEmployee(employeeID);
      validateEmployeeNotCustomer(employee, order);
      order.setEmployee(employee);
    }

    if (deliveryDate != null) {
      validateDeliveryDate(deliveryDate);
      validateCurrentDeliveryDateNotWithin24Hours(order);
      order.setDeliveryDate(deliveryDate);
    }

    if (orderStatus != null) {
      Order.OrderStatus newStatus = parseOrderStatus(orderStatus);
      validateCancellation(order, newStatus);
      order.setOrderStatus(newStatus);
    }

    return orderRepository.save(order);
  }

  /** This function updates upon GET */
  private void markDelivered(Order order) {
    if (order.getOrderStatus() == Order.OrderStatus.Preparing
        && !order.getDeliveryDate().toLocalDate().isAfter(LocalDate.now())) {
      order.setOrderStatus(Order.OrderStatus.Delivered);
      orderRepository.save(order);
    }
  }

  @Transactional
  public List<Order> getOrders() {
    List<Order> orders = orderRepository.findAll();
    orders.forEach(this::markDelivered);
    return orders;
  }

  @Transactional
  public Order getOrderByID(String orderID) {
    Order order = findOrder(orderID);
    markDelivered(order);
    return order;
  }

  @Transactional(readOnly = true)
  public List<Item> getOrderItems(String orderID) {
    return findOrder(orderID).getItems();
  }

  @Transactional
  public List<Order> getOrdersByCustomerID(String customerID) {
    Customer customer = findCustomer(customerID);
    List<Order> orders = orderRepository.findByCustomer(customer);
    orders.forEach(this::markDelivered);
    return orders;
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByEmployeeID(String employeeID) {
    Employee employee = findEmployee(employeeID);
    return orderRepository.findByEmployee(employee);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByOrderStatus(String orderStatus) {
    Order.OrderStatus orderStatusEnum = parseOrderStatus(orderStatus);
    return orderRepository.findByOrderStatus(orderStatusEnum);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerIDAndStatus(String customerID, String orderStatus) {
    Customer customer = findCustomer(customerID);
    Order.OrderStatus orderStatusEnum = parseOrderStatus(orderStatus);
    return orderRepository.findByCustomerAndOrderStatus(customer, orderStatusEnum);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByEmployeeIDAndStatus(String employeeID, String orderStatus) {
    Employee employee = findEmployee(employeeID);
    Order.OrderStatus orderStatusEnum = parseOrderStatus(orderStatus);
    return orderRepository.findByEmployeeAndOrderStatus(employee, orderStatusEnum);
  }
}
