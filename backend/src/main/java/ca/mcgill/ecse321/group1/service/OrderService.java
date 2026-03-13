package ca.mcgill.ecse321.group1.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import ca.mcgill.ecse321.group1.repository.OrderRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final ItemRepository itemRepository;
  private final EmployeeRepository employeeRepository;
  static float loyaltyModifier = 0.2f; // Modifier between loyalty points and money

  public OrderService(
      OrderRepository orderRepository,
      ItemRepository itemRepository,
      CustomerRepository customerRepository,
      EmployeeRepository employeeRepository) {
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
  }

  @Transactional
  public Order createOrder(String customerID, String deliveryDate, int usedLoyaltyPoints) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new RuntimeException("There is no customer with id " + customerID + ".");
    }

    Iterable<Item> items = itemRepository.findItemsByCustomer(customer);
    if (items == null) {
      throw new RuntimeException("There are no items in the cart of customer " + customerID + ".");
    }

    if (deliveryDate == null) {
      throw new RuntimeException("Delivery Date is null.");
    }
    Date delivery;
    try {
      delivery = Date.valueOf(deliveryDate);
    } catch (Exception e) {
      throw new RuntimeException("Delivery date is not valid.");
    }

    if (delivery.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new RuntimeException("Delivery Date must be at least 24 hours after the order date.");
    }

    Order order = new Order();
    order.setCustomer(customer);
    order.setDeliveryDate(delivery);
    order.setAddress(customer.getAddress());

    float total = 0.0f;
    float itemPrice;

    for (Item item : items) {
      order.addItem(item);
      itemPrice = item.getClothingVariant().getModel().getPrice();
      total += itemPrice;
      item.setPrice(itemPrice);
      customer.removeItem(item);
    }

    // Compute loyalty points gained and loyalty savings
    order.setLoyaltySaving((float) usedLoyaltyPoints * loyaltyModifier);
    int gainedLoyaltyPoints = (int) (total * loyaltyModifier);
    customer.setLoyaltyPoints(
        customer.getLoyaltyPoints() + gainedLoyaltyPoints - usedLoyaltyPoints);

    return orderRepository.save(order);
  }

  @Transactional
  public Order assignOrderToEmployee(String orderID, String employeeID) {
    Employee employee = employeeRepository.findByRoleID(employeeID);
    if (employee == null) {
      throw new RuntimeException("There is no employee with id " + employeeID + ".");
    }

    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new RuntimeException("There is no order with id " + orderID + ".");
    }

    order.setEmployee(employee);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrderDeliveryDate(String orderID, String deliveryDate) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new RuntimeException("There is no order with id " + orderID + ".");
    }

    if (deliveryDate == null) {
      throw new RuntimeException("Delivery Date is null.");
    }
    Date delivery;
    try {
      delivery = Date.valueOf(deliveryDate);
    } catch (Exception e) {
      throw new RuntimeException("Delivery date is not valid.");
    }

    if (delivery.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new RuntimeException("Delivery Date must be at least 24 hours after the order date.");
    }

    order.setDeliveryDate(delivery);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrderStatus(String orderID, String orderStatus) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new RuntimeException("There is no order with id " + orderID + ".");
    }

    Order.OrderStatus orderStatusEnum;
    try {
      orderStatusEnum = Order.OrderStatus.valueOf(orderStatus);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid order status " + orderStatus);
    }

    order.setOrderStatus(orderStatusEnum);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrderAddress(String orderID) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new RuntimeException("There is no order with id " + orderID + ".");
    }

    Customer customer = order.getCustomer();
    if (customer == null) {
      throw new RuntimeException("There is no customer for the order id " + orderID + ".");
    }

    order.setAddress(customer.getAddress());
    return orderRepository.save(order);
  }

  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  public Order getOrderByID(String orderID) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new RuntimeException("There is no order with id " + orderID + ".");
    }

    return order;
  }

  public List<Order> getOrdersByCustomerID(String customerID) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new RuntimeException("There is no customer with id " + customerID + ".");
    }

    return orderRepository.findByCustomer(customer);
  }

  public List<Order> getOrdersByOrderStatus(String orderStatus) {
    Order.OrderStatus orderStatusEnum;
    try {
      orderStatusEnum = Order.OrderStatus.valueOf(orderStatus);
    } catch (Exception e) {
      throw new RuntimeException("Invalid order status " + orderStatus);
    }

    return orderRepository.findByOrderStatus(orderStatusEnum);
  }
}
