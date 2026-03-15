package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import ca.mcgill.ecse321.group1.repository.OrderRepository;

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
  public Order createOrder(String customerID, Date deliveryDate, int usedLoyaltyPoints) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no customer with id " + customerID + ".");
    }

    Iterable<Item> items = itemRepository.findItemsByCustomer(customer);
    if (items == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "There are no items in the cart of customer " + customerID + ".");
    }

    if (deliveryDate == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery Date is null.");
    }

    if (deliveryDate.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Delivery Date must be at least 24 hours after the order date.");
    }

    // Check that there are enough loyalty points in the customer's account
    if (usedLoyaltyPoints < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loyalty points must be positive.");
    }
    if (customer.getLoyaltyPoints() < usedLoyaltyPoints) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "The customer does not have enough loyalty points to complete the purchase.");
    }

    // Set all basic attributes of the order
    Order order = new Order();
    order.setCustomer(customer);
    order.setOrderDate(Date.valueOf(LocalDate.now())); // Transform from util to SQL date
    order.setDeliveryDate(deliveryDate);
    order.setAddress(customer.getAddress());
    order.setOrderStatus(Order.OrderStatus.Preparing);

    float total = 0.0f;
    float itemPrice;

    // Set item attributes for the order
    for (Item item : items) {
      order.addItem(item);

      // Item price contains price of single item
      itemPrice = item.getClothingVariant().getModel().getPrice();
      item.setPrice(itemPrice);

      // Add price of single item times the item quantity
      total += item.getQuantity() * itemPrice;

      // Remove item from customer cart
      customer.removeItem(item);
    }

    // Compute use loyalty points to money saved
    order.setLoyaltySaving((float) usedLoyaltyPoints * loyaltyModifier);

    // Compute gained loyalty points for money gained
    int gainedLoyaltyPoints = (int) (total * loyaltyModifier);

    // Set new value for loyalty points
    customer.setLoyaltyPoints(
        customer.getLoyaltyPoints() + gainedLoyaltyPoints - usedLoyaltyPoints);

    // Save new loyalty points value to customer
    customerRepository.save(customer);
    return orderRepository.save(order);
  }

  @Transactional
  public Order assignOrderToEmployee(String orderID, String employeeID) {
    Employee employee = employeeRepository.findByRoleID(employeeID);
    if (employee == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no employee with id " + employeeID + ".");
    }

    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no order with id " + orderID + ".");
    }

    // Check that the employee is not the same person as the customer
    if (employee.getPerson().getPersonID().equals(order.getCustomer().getPerson().getPersonID())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The employee cannot be assigned to their own order.");
    }

    order.setEmployee(employee);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrderDeliveryDate(String orderID, Date deliveryDate) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no order with id " + orderID + ".");
    }

    if (deliveryDate == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery Date is null.");
    }

    // can not change delivery date if within 24 hrs prior to current delivery date
    if (order.getDeliveryDate().toLocalDate().isBefore((LocalDate.now().plusDays(1)))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Delivery Date cannot be changed within 24 hours of the current delivery date.");
    }

    if (deliveryDate.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Delivery Date must be at least 24 hours after the order date.");
    }

    order.setDeliveryDate(deliveryDate);
    return orderRepository.save(order);
  }

  @Transactional
  public Order updateOrderStatus(String orderID, String orderStatus) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no order with id " + orderID + ".");
    }

    Order.OrderStatus orderStatusEnum;
    try {
      orderStatusEnum = Order.OrderStatus.valueOf(orderStatus);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid order status " + orderStatus + ".");
    }

    order.setOrderStatus(orderStatusEnum);
    return orderRepository.save(order);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Order getOrderByID(String orderID) {
    Order order = orderRepository.findOrderByOrderID(orderID);
    if (order == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no order with id " + orderID + ".");
    }
    return order;
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerID(String customerID) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no customer with id " + customerID + ".");
    }
    return orderRepository.findByCustomer(customer);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByOrderStatus(String orderStatus) {
    Order.OrderStatus orderStatusEnum;
    try {
      orderStatusEnum = Order.OrderStatus.valueOf(orderStatus);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid order status " + orderStatus + ".");
    }
    return orderRepository.findByOrderStatus(orderStatusEnum);
  }

  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerIDAndStatus(String customerID, String orderStatus) {
    Customer customer = customerRepository.findByRoleID(customerID);
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no customer with id " + customerID + ".");
    }

    Order.OrderStatus orderStatusEnum;
    try {
      orderStatusEnum = Order.OrderStatus.valueOf(orderStatus);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid order status " + orderStatus + ".");
    }

    return orderRepository.findByCustomerAndOrderStatus(customer, orderStatusEnum);
  }
}
