package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.exception.InvalidInputException;
import ca.mcgill.ecse321.group1.exception.NotFoundException;
import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OrderServiceTests {
  @Mock private OrderRepository orderRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private ItemRepository itemRepository;
  @Mock private EmployeeRepository employeeRepository;

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
    Item item = new Item();
    item.setClothingVariant(clothingVariant);
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
    when(itemRepository.findItemsByCustomer(customer)).thenReturn(List.of(item));
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
    assertEquals(25, customer.getLoyaltyPoints());
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  public void testCreateOrderWithInvalidCustomer() {
    // Arrange
    String customerId = "nonexistent";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    // WAITING FOR EXCEPTION
    NotFoundException e =
        assertThrows(
            NotFoundException.class,
            () ->
                orderService.createOrder(customerId, Date.valueOf(LocalDate.now().plusDays(2)), 0));
    assertEquals("There is no customer with id " + customerId + ".", e.getMessage());
  }

  @Test
  public void testCreateOrderWithNullItems() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findItemsByCustomer(customer)).thenReturn(null);

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class,
            () ->
                orderService.createOrder(customerId, Date.valueOf(LocalDate.now().plusDays(2)), 0));
    assertEquals("There are no items in the cart of customer " + customerId + ".", e.getMessage());
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
    when(itemRepository.findItemsByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class, () -> orderService.createOrder(customerId, null, 0));
    assertEquals("Delivery Date is null.", e.getMessage());
  }

  @Test
  public void testCreateOrderWithTooSoonDeliveryDate() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    customer.setRoleID(customerId);
    Item item = buildItemWithPrice(50.0f);
    item.setCustomer(customer);

    Date tooSoon = Date.valueOf(LocalDate.now()); // same day — not 24 h ahead

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findItemsByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class, () -> orderService.createOrder(customerId, tooSoon, 0));
    assertEquals("Delivery Date must be at least 24 hours after the order date.", e.getMessage());
  }

  @Test
  public void testCreateOrderWithInvalidLoyaltyPoints() {
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

    Date deliveryDate = Date.valueOf(LocalDate.now().plusDays(2));

    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findItemsByCustomer(customer)).thenReturn(List.of(item));

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class,
            () -> orderService.createOrder(customerId, deliveryDate, -1));
    assertEquals("Loyalty points must be positive.", e.getMessage());
  }

  // ===== assignOrderToEmployee =====

  @Test
  public void testAssignOrderToValidEmployee() {
    // Arrange
    String orderId = "order1";
    String employeeId = "emp1";

    Order order = new Order();
    order.setOrderID(orderId);
    Employee employee = new Employee();
    employee.setRoleID(employeeId);

    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.assignOrderToEmployee(orderId, employeeId);

    // Assert
    assertNotNull(result);
    assertEquals(employee, result.getEmployee());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testAssignOrderToNonExistentEmployee() {
    // Arrange
    String orderId = "order1";
    String employeeId = "badEmp";
    when(employeeRepository.findByRoleID(employeeId)).thenReturn(null);

    // Act & Assert
    NotFoundException e =
        assertThrows(
            NotFoundException.class, () -> orderService.assignOrderToEmployee(orderId, employeeId));
    assertEquals("There is no employee with id " + employeeId + ".", e.getMessage());
  }

  @Test
  public void testAssignOrderWithInvalidOrderId() {
    // Arrange
    String orderId = "badOrder";
    String employeeId = "emp1";
    Employee employee = new Employee();
    employee.setRoleID(employeeId);

    when(employeeRepository.findByRoleID(employeeId)).thenReturn(employee);
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    NotFoundException e =
        assertThrows(
            NotFoundException.class, () -> orderService.assignOrderToEmployee(orderId, employeeId));
    assertEquals("There is no order with id " + orderId + ".", e.getMessage());
  }

  // ===== updateOrderDeliveryDate =====

  @Test
  public void testUpdateOrderDeliveryDateValid() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    order.setOrderID(orderId);
    Date newDate = Date.valueOf(LocalDate.now().plusDays(3));

    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrderDeliveryDate(orderId, newDate);

    // Assert
    assertNotNull(result);
    assertEquals(newDate, result.getDeliveryDate());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testUpdateOrderDeliveryDateWithInvalidOrder() {
    // Arrange
    String orderId = "badOrder";
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    NotFoundException e =
        assertThrows(
            NotFoundException.class,
            () ->
                orderService.updateOrderDeliveryDate(
                    orderId, Date.valueOf(LocalDate.now().plusDays(2))));
    assertEquals("There is no order with id " + orderId + ".", e.getMessage());
  }

  @Test
  public void testUpdateOrderDeliveryDateWithNullDate() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class, () -> orderService.updateOrderDeliveryDate(orderId, null));
    assertEquals("Delivery Date is null.", e.getMessage());
  }

  @Test
  public void testUpdateOrderDeliveryDateWithTooSoonDate() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    Date tooSoon = Date.valueOf(LocalDate.now());

    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class,
            () -> orderService.updateOrderDeliveryDate(orderId, tooSoon));
    assertEquals("Delivery Date must be at least 24 hours after the order date.", e.getMessage());
  }

  // ===== updateOrderStatus =====

  @Test
  public void testUpdateOrderStatusValid() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    Order.OrderStatus status = Order.OrderStatus.Preparing;
    order.setOrderID(orderId);
    order.setOrderStatus(status);
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Order result = orderService.updateOrderStatus(orderId, "Delivered");

    // Assert
    assertNotNull(result);
    assertEquals(Order.OrderStatus.Delivered, result.getOrderStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void testUpdateOrderStatusWithInvalidOrder() {
    // Arrange
    String orderId = "badOrder";
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(null);

    // Act & Assert
    NotFoundException e =
        assertThrows(
            NotFoundException.class, () -> orderService.updateOrderStatus(orderId, "Delivered"));
    assertEquals("There is no order with id " + orderId + ".", e.getMessage());
  }

  @Test
  public void testUpdateOrderStatusWithInvalidStatus() {
    // Arrange
    String orderId = "order1";
    String invalidStatus = "Flying";
    Order order = new Order();
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);

    // Act & Assert
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class,
            () -> orderService.updateOrderStatus(orderId, invalidStatus));
    assertEquals("Invalid order status " + invalidStatus, e.getMessage());
  }

  // ===== getOrders =====

  @Test
  public void testGetOrders() {
    // Arrange
    Order order1 = new Order();
    Order order2 = new Order();
    when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

    // Act
    Iterable<Order> orders = orderService.getOrders();

    // Assert
    assertNotNull(orders);
    int count = 0;
    for (Order o : orders) count++;
    assertEquals(2, count);
  }

  // ===== getOrderByID =====

  @Test
  public void testGetOrderByValidID() {
    // Arrange
    String orderId = "order1";
    Order order = new Order();
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(order);

    // Act
    Order result = orderService.getOrderByID(orderId);

    // Assert
    assertNotNull(result);
    assertEquals(order, result);
  }

  @Test
  public void testGetOrderByInvalidID() {
    String orderId = "badOrder";
    when(orderRepository.findOrderByOrderID(orderId)).thenReturn(null);
    NotFoundException e =
        assertThrows(NotFoundException.class, () -> orderService.getOrderByID(orderId));
    assertEquals("There is no order with id " + orderId + ".", e.getMessage());
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
    NotFoundException e =
        assertThrows(NotFoundException.class, () -> orderService.getOrdersByCustomerID(customerId));
    assertEquals("There is no customer with id " + customerId + ".", e.getMessage());
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
    InvalidInputException e =
        assertThrows(
            InvalidInputException.class, () -> orderService.getOrdersByOrderStatus(invalidStatus));
    assertEquals("Invalid order status " + invalidStatus, e.getMessage());
  }
}
