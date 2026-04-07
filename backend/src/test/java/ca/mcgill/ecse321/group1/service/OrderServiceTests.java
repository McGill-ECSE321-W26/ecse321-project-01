package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import ca.mcgill.ecse321.group1.repository.OrderRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OrderServiceTests {
  @Mock private OrderRepository orderRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private ItemRepository itemRepository;
  @Mock private EmployeeRepository employeeRepository;
  @Mock private ClothingVariantRepository clothingVariantRepository;

  @InjectMocks private OrderService orderService;

  /**
   * Builds an Item wired to a ClothingVariant → ClothingModel with the given price, which is what
   * the service reads during order creation.
   */
  private Item buildItemWithPrice(float price) {
    ClothingModel clothingModel = new ClothingModel();
    clothingModel.setPrice(price);
    ClothingVariant clothingVariant = new ClothingVariant();
    clothingVariant.setModel(clothingModel);
    clothingVariant.setStockQuantity(10);
    Item item = new Item();
    item.setClothingVariant(clothingVariant);
    item.setQuantity(1);
    return item;
  }

  // ===== createOrder =====

  @Test
  public void testCreateValidOrder() {
    // Arrange
    String customerId = "customer1";
    String address = "123 Main St";
    int initialLoyaltyPoints = 10;
    int usedLoyaltyPoints = 5;
    float itemPrice = 100.0f;

    Customer customer = new Customer();
    customer.setRoleID(customerId);
    customer.setAddress(address);
    customer.setLoyaltyPoints(initialLoyaltyPoints);

    Item item = buildItemWithPrice(itemPrice);
    item.setCustomer(customer); // populates customer.items via bi-directional link

    Order.OrderStatus status = Order.OrderStatus.Preparing;
    Date orderDate = Date.valueOf(LocalDate.now());
    Date deliveryDate = Date.valueOf(LocalDate.now().plusDays(2));

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

    // Act
    Order order = orderService.createOrder(customerId, deliveryDate, usedLoyaltyPoints);

    // Assert
    assertNotNull(order);
    assertEquals(status, order.getOrderStatus());
    assertEquals(address, order.getAddress());
    assertEquals(orderDate, order.getOrderDate());
    assertEquals(deliveryDate, order.getDeliveryDate());
    assertEquals(
        (float) usedLoyaltyPoints * OrderService.loyaltyModifier, order.getLoyaltySaving());
    int loyaltyPointsFinal =
        (int) (initialLoyaltyPoints - usedLoyaltyPoints + OrderService.loyaltyModifier * itemPrice);
    assertEquals(loyaltyPointsFinal, customer.getLoyaltyPoints());
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  public void testCreateOrderWithInvalidCustomer() {
    // Arrange
    String customerId = "nonexistent";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                orderService.createOrder(customerId, Date.valueOf(LocalDate.now().plusDays(2)), 0));
    assertEquals("There is no customer with id " + customerId + ".", e.getReason());
  }

  @Test
  public void testCreateOrderWithNullItems() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                orderService.createOrder(customerId, Date.valueOf(LocalDate.now().plusDays(2)), 0));
    assertEquals("There are no items in the cart of customer " + customerId + ".", e.getReason());
  }

  @Test
  public void testCreateOrderWithEmptyCart() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of());

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                orderService.createOrder(customerId, Date.valueOf(LocalDate.now().plusDays(2)), 0));
    assertEquals("There are no items in the cart of customer " + customerId + ".", e.getReason());
  }

  @Test
  public void testCreateOrderWithNullDeliveryDate() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    Item item = buildItemWithPrice(50.0f);
    item.setCustomer(customer);

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> orderService.createOrder(customerId, null, 0));
    assertEquals("Delivery Date is null.", e.getReason());
  }

  @Test
  public void testCreateOrderWithTooSoonDeliveryDate() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    Item item = buildItemWithPrice(50.0f);
    item.setCustomer(customer);

    // Same day, not at least 24 hours
    Date tooSoon = Date.valueOf(LocalDate.now());

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> orderService.createOrder(customerId, tooSoon, 0));
    assertEquals("Delivery Date must be at least 24 hours after the order date.", e.getReason());
  }

  @Test
  public void testCreateOrderWithInvalidLoyaltyPoints() {
    // Arrange
    String customerId = "customer1";
    String address = "123 Main St";
    int initialLoyaltyPoints = 10;
    float itemPrice = 100.0f;

    Customer customer = new Customer();
    customer.setRoleID(customerId);
    customer.setAddress(address);
    customer.setLoyaltyPoints(initialLoyaltyPoints);

    Item item = buildItemWithPrice(itemPrice);
    item.setCustomer(customer); // populates customer.items via bi-directional link

    Date deliveryDate = Date.valueOf(LocalDate.now().plusDays(2));

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.createOrder(customerId, deliveryDate, -1));
    assertEquals("Loyalty points must be positive.", e.getReason());
  }

  @Test
  public void testCreateOrderWithInsufficientLoyaltyPoints() {
    // Arrange
    String customerId = "customer1";
    int initialLoyaltyPoints = 5;
    int usedLoyaltyPoints = 50; // More than the customer owns

    Customer customer = new Customer();
    customer.setRoleID(customerId);
    customer.setLoyaltyPoints(initialLoyaltyPoints);

    Item item = buildItemWithPrice(100.0f);
    item.setCustomer(customer);

    Date deliveryDate = Date.valueOf(LocalDate.now().plusDays(2));

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.createOrder(customerId, deliveryDate, usedLoyaltyPoints));
    assertEquals(
        "The customer does not have enough loyalty points to complete the purchase.",
        e.getReason());
  }

  @Test
  public void testCreateOrderWithLoyaltyPointsCoveringEntireOrder() {
    // Arrange
    // loyaltySaving = 100 * 0.2 = 20 >= 10 (item total)
    String customerId = "customer1";
    int usedLoyaltyPoints = 100;
    float itemPrice = 10.0f;

    Customer customer = new Customer();
    customer.setRoleID(customerId);
    customer.setLoyaltyPoints(usedLoyaltyPoints);

    Item item = buildItemWithPrice(itemPrice);
    item.setCustomer(customer);

    Date deliveryDate = Date.valueOf(LocalDate.now().plusDays(2));

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.createOrder(customerId, deliveryDate, usedLoyaltyPoints));
    assertEquals("Cannot pay for an entire order with only loyalty points.", e.getReason());
  }

  // ===== updateOrder (assign employee) =====

  @Test
  public void testAssignOrderToValidEmployee() {
    // Arrange
    String person1Id = "person1";
    String person2Id = "person2";
    String orderId = "order1";
    String employeeId = "emp1";

    Person person1 = new Person();
    person1.setPersonID(person1Id);
    Person person2 = new Person();
    person2.setPersonID(person2Id);
    Customer customer = new Customer();
    customer.setPerson(person1);
    Order order = new Order();
    order.setOrderID(orderId);
    order.setCustomer(customer);
    Employee employee = new Employee();
    employee.setRoleID(employeeId);
    employee.setPerson(person2);

    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);
    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrder(orderId, employeeId, null, null);

    // Assert
    assertNotNull(result);
    assertEquals(employee, result.getEmployee());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testAssignOrderToInvalidEmployee() {
    // Arrange
    String person1Id = "person1";
    String orderId = "order1";
    String employeeId = "emp1";

    Person person1 = new Person();
    person1.setPersonID(person1Id);
    Customer customer = new Customer();
    customer.setPerson(person1);
    Order order = new Order();
    order.setOrderID(orderId);
    order.setCustomer(customer);
    Employee employee = new Employee();
    employee.setRoleID(employeeId);
    employee.setPerson(person1);

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, employeeId, null, null));
    assertEquals("The employee cannot be assigned to their own order.", e.getReason());
  }

  @Test
  public void testAssignOrderToNonExistentEmployee() {
    // Arrange
    String orderId = "order1";
    String employeeId = "badEmp";
    Order order = new Order();
    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, employeeId, null, null));
    assertEquals("There is no employee with id " + employeeId + ".", e.getReason());
  }

  @Test
  public void testUpdateOrderWithInvalidOrderId() {
    // Arrange
    String orderId = "badOrder";
    when(orderRepository.findByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, "emp1", null, null));
    assertEquals("There is no order with id " + orderId + ".", e.getReason());
  }

  // ===== updateOrder (delivery date) =====

  @Test
  public void testUpdateOrderDeliveryDateValid() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(5)));
    order.setOrderID(orderId);
    Date newDate = Date.valueOf(LocalDate.now().plusDays(3));

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrder(orderId, null, newDate, null);

    // Assert
    assertNotNull(result);
    assertEquals(newDate, result.getDeliveryDate());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testUpdateOrderDeliveryDateWithInvalidOrder() {
    // Arrange
    String orderId = "badOrder";
    when(orderRepository.findByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                orderService.updateOrder(
                    orderId, null, Date.valueOf(LocalDate.now().plusDays(2)), null));
    assertEquals("There is no order with id " + orderId + ".", e.getReason());
  }

  @Test
  public void testUpdateOrderDeliveryDateWithTooSoonDate() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(5)));
    Date tooSoon = Date.valueOf(LocalDate.now());

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, tooSoon, null));
    assertEquals("Delivery Date must be at least 24 hours after the order date.", e.getReason());
  }

  @Test
  public void testUpdateOrderDeliveryDateWithin24Hours() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    order.setDeliveryDate(Date.valueOf(LocalDate.now())); // delivery is today within 24 hours
    Date newDate = Date.valueOf(LocalDate.now().plusDays(3));

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, newDate, null));
    assertEquals(
        "Delivery Date cannot be changed within 24 hours of the current delivery date.",
        e.getReason());
  }

  // ===== updateOrder (status) =====

  @Test
  public void testUpdateOrderStatusValid() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    order.setOrderStatus(Order.OrderStatus.Preparing);
    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrder(orderId, null, null, "Delivered");

    // Assert
    assertNotNull(result);
    assertEquals(Order.OrderStatus.Delivered, result.getOrderStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testUpdateOrderStatusWithInvalidOrder() {
    // Arrange
    String orderId = "badOrder";
    when(orderRepository.findByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, null, "Delivered"));
    assertEquals("There is no order with id " + orderId + ".", e.getReason());
  }

  @Test
  public void testUpdateOrderStatusWithInvalidStatus() {
    // Arrange
    String orderId = "order1";
    String invalidStatus = "Flying";
    Order order = new Order();
    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, null, invalidStatus));
    assertEquals("Invalid order status " + invalidStatus + ".", e.getReason());
  }

  @Test
  public void testCancelOrderValid() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(3)));

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrder(orderId, null, null, "Cancelled");

    // Assert
    assertNotNull(result);
    assertEquals(Order.OrderStatus.Cancelled, result.getOrderStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testCancelDeliveredOrder() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    order.setOrderStatus(Order.OrderStatus.Delivered);
    order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(3)));

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, null, "Cancelled"));
    assertEquals("Cannot cancel an order that has already been delivered.", e.getReason());
  }

  @Test
  public void testCancelOrderWithin24Hours() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setDeliveryDate(Date.valueOf(LocalDate.now())); // Delivery date within 24 hours

    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.updateOrder(orderId, null, null, "Cancelled"));
    assertEquals("Cannot cancel an order within 24 hours of its delivery date.", e.getReason());
  }

  // ===== getOrders =====

  @Test
  public void testGetOrders() {
    // Arrange
    Order order1 = new Order();
    Order order2 = new Order();
    when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

    // Act
    List<Order> orders = orderService.getOrders();

    // Assert
    assertNotNull(orders);
    assertEquals(2, orders.size());
  }

  // ===== getOrderByID =====

  @Test
  public void testGetOrderByValidID() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    when(orderRepository.findByOrderID(orderId)).thenReturn(order);

    // Act
    Order result = orderService.getOrderByID(orderId);

    // Assert
    assertNotNull(result);
    assertEquals(order, result);
  }

  @Test
  public void testGetOrderByInvalidID() {
    String orderId = "badOrder";
    when(orderRepository.findByOrderID(orderId)).thenReturn(null);
    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> orderService.getOrderByID(orderId));
    assertEquals("There is no order with id " + orderId + ".", e.getReason());
  }

  // ===== getOrdersByCustomerID =====

  @Test
  public void testGetOrdersByValidCustomerID() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    Order order = new Order();
    Order order2 = new Order();

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(orderRepository.findByCustomer(customer)).thenReturn(List.of(order, order2));

    // Act
    List<Order> orders = orderService.getOrdersByCustomerID(customerId);

    // Assert
    assertNotNull(orders);
    assertEquals(order, orders.getFirst());
    assertEquals(order2, orders.getLast());
  }

  @Test
  public void testGetOrdersByInvalidCustomerID() {
    // Arrange
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> orderService.getOrdersByCustomerID(customerId));
    assertEquals("There is no customer with id " + customerId + ".", e.getReason());
  }

  // ===== getOrdersByOrderStatus =====

  @Test
  public void testGetOrdersByValidStatus() {
    // Arrange
    Order order = new Order();
    Order order2 = new Order();
    when(orderRepository.findByOrderStatus(Order.OrderStatus.Preparing))
        .thenReturn(List.of(order, order2));

    // Act
    List<Order> orders = orderService.getOrdersByOrderStatus("Preparing");

    // Assert
    assertNotNull(orders);
    assertEquals(order, orders.getFirst());
    assertEquals(order2, orders.getLast());
  }

  @Test
  public void testGetOrdersByInvalidStatus() {
    // Arrange
    String invalidStatus = "Unknown";

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.getOrdersByOrderStatus(invalidStatus));
    assertEquals("Invalid order status " + invalidStatus + ".", e.getReason());
  }

  // ===== getOrdersByCustomerIDAndStatus =====

  @Test
  public void testGetOrdersByCustomerIDAndStatusValid() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    Order order1 = new Order();
    order1.setOrderStatus(Order.OrderStatus.Preparing);
    Order order2 = new Order();
    order2.setOrderStatus(Order.OrderStatus.Preparing);

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(orderRepository.findByCustomerAndOrderStatus(customer, Order.OrderStatus.Preparing))
        .thenReturn(List.of(order1, order2));

    // Act
    List<Order> orders = orderService.getOrdersByCustomerIDAndStatus(customerId, "Preparing");

    // Assert
    assertNotNull(orders);
    assertEquals(2, orders.size());
    assertEquals(order1, orders.getFirst());
    assertEquals(order2, orders.getLast());
  }

  @Test
  public void testGetOrdersByCustomerIDAndStatusInvalidCustomer() {
    // Arrange
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.getOrdersByCustomerIDAndStatus(customerId, "Preparing"));
    assertEquals("There is no customer with id " + customerId + ".", e.getReason());
  }

  @Test
  public void testGetOrdersByCustomerIDAndStatusInvalidStatus() {
    // Arrange
    String customerId = "customer1";
    String invalidStatus = "Unknown";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.getOrdersByCustomerIDAndStatus(customerId, invalidStatus));
    assertEquals("Invalid order status " + invalidStatus + ".", e.getReason());
  }

  // ===== getOrdersByEmployeeID =====

  @Test
  public void testGetOrdersByValidEmployeeID() {
    // Arrange
    String employeeId = "emp1";
    Employee employee = new Employee();
    employee.setRoleID(employeeId);
    Order order1 = new Order();
    Order order2 = new Order();

    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);
    when(orderRepository.findByEmployee(employee)).thenReturn(List.of(order1, order2));

    // Act
    List<Order> orders = orderService.getOrdersByEmployeeID(employeeId);

    // Assert
    assertNotNull(orders);
    assertEquals(order1, orders.getFirst());
    assertEquals(order2, orders.getLast());
  }

  @Test
  public void testGetOrdersByInvalidEmployeeID() {
    // Arrange
    String employeeId = "badEmployee";
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> orderService.getOrdersByEmployeeID(employeeId));
    assertEquals("There is no employee with id " + employeeId + ".", e.getReason());
  }

  // ===== getOrdersByEmployeeIDAndStatus =====

  @Test
  public void testGetOrdersByEmployeeIDAndStatusValid() {
    // Arrange
    String employeeId = "emp1";
    Employee employee = new Employee();
    employee.setRoleID(employeeId);
    Order order1 = new Order();
    order1.setOrderStatus(Order.OrderStatus.Preparing);
    Order order2 = new Order();
    order2.setOrderStatus(Order.OrderStatus.Preparing);

    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);
    when(orderRepository.findByEmployeeAndOrderStatus(employee, Order.OrderStatus.Preparing))
        .thenReturn(List.of(order1, order2));

    // Act
    List<Order> orders = orderService.getOrdersByEmployeeIDAndStatus(employeeId, "Preparing");

    // Assert
    assertNotNull(orders);
    assertEquals(2, orders.size());
    assertEquals(order1, orders.getFirst());
    assertEquals(order2, orders.getLast());
  }

  @Test
  public void testGetOrdersByEmployeeIDAndStatusInvalidEmployee() {
    // Arrange
    String employeeId = "badEmployee";
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.getOrdersByEmployeeIDAndStatus(employeeId, "Preparing"));
    assertEquals("There is no employee with id " + employeeId + ".", e.getReason());
  }

  @Test
  public void testGetOrdersByEmployeeIDAndStatusInvalidStatus() {
    // Arrange
    String employeeId = "emp1";
    String invalidStatus = "Unknown";
    Employee employee = new Employee();
    employee.setRoleID(employeeId);
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> orderService.getOrdersByEmployeeIDAndStatus(employeeId, invalidStatus));
    assertEquals("Invalid order status " + invalidStatus + ".", e.getReason());
  }
}
