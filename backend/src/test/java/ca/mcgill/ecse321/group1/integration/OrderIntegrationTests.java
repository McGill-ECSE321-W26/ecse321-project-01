package ca.mcgill.ecse321.group1.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.mcgill.ecse321.group1.dto.*;
import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.repository.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class OrderIntegrationTests {

  @LocalServerPort private int port;

  private RestClient client;

  @Autowired private OrderRepository orderRepository;

  @Autowired private ItemRepository itemRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private EmployeeRepository employeeRepository;

  @Autowired private PersonRepository personRepository;

  @Autowired private ClothingModelRepository clothingModelRepository;

  @Autowired private ClothingVariantRepository clothingVariantRepository;

  private static final String INVALID_ID = "not-a-real-id";
  private static final String INVALID_STATUS = "NotARealStatus";
  private final Date VALID_DELIVERY_DATE = Date.valueOf(LocalDate.now().plusDays(2));
  private final Date INVALID_DELIVERY_DATE = Date.valueOf(LocalDate.now());

  // Test objects
  private Customer testCustomer;
  private Employee testEmployee;
  private Person testCustomerPerson;
  private Person testEmployeePerson;
  private ClothingModel testModel;
  private ClothingVariant testVariant;
  private Item testItem;

  // Used for GET tests afterward
  private String validOrderID;

  @BeforeAll
  public void setup() {
    // Configure rest client (needed due to Spring Boot V4 instead of TestRestTemplate)
    client =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {})
            .build();

    // Create clothing model
    testModel = new ClothingModel();
    testModel.setName("McGill Hoodie");
    testModel.setPrice(6.99f);
    testModel = clothingModelRepository.save(testModel);

    // Create clothing variant
    testVariant = new ClothingVariant();
    testVariant.setSize(ClothingVariant.Size.XL);
    testVariant.setColor("black");
    testVariant.setStockQuantity(3);
    testVariant.setModel(testModel);
    testVariant = clothingVariantRepository.save(testVariant);

    // Create customer
    testCustomerPerson = new Person();
    testCustomerPerson.setEmail("customer@test.com");
    testCustomerPerson.setPassword("12345");
    testCustomerPerson = personRepository.save(testCustomerPerson);

    testCustomer = new Customer();
    testCustomer.setAddress("456 Test Avenue");
    testCustomer.setLoyaltyPoints(200);
    testCustomer.setPerson(testCustomerPerson);
    testCustomer = customerRepository.save(testCustomer);

    // Create cart item
    testItem = new Item();
    testItem.setQuantity(1);
    testItem.setClothingVariant(testVariant);
    testItem.setCustomer(testCustomer);
    testItem = itemRepository.save(testItem);

    // Create employee
    testEmployeePerson = new Person();
    testEmployeePerson.setEmail("employee@test.com");
    testEmployeePerson.setPassword("12345");
    testEmployeePerson = personRepository.save(testEmployeePerson);

    testEmployee = new Employee();
    testEmployee.setPerson(testEmployeePerson);
    testEmployee = employeeRepository.save(testEmployee);
  }

  @AfterAll
  public void cleanup() {
    if (validOrderID != null) orderRepository.deleteById(validOrderID);
    itemRepository.deleteById(testItem.getItemID());
    customerRepository.deleteById(testCustomer.getRoleID());
    employeeRepository.deleteById(testEmployee.getRoleID());
    personRepository.deleteById(testCustomerPerson.getPersonID());
    personRepository.deleteById(testEmployeePerson.getPersonID());
    clothingVariantRepository.deleteById(testVariant.getClothingVariantID());
    clothingModelRepository.deleteById(testModel.getClothingModelID());
  }

  // ==== POST /api/orders/create ====

  @Test
  @Order(1)
  public void testCreateOrderWithInvalidCustomer() {
    // Arrange
    CreateOrderRequestDto dto = new CreateOrderRequestDto();
    dto.setCustomerID(INVALID_ID);
    dto.setDeliveryDate(VALID_DELIVERY_DATE);
    dto.setUsedLoyaltyPoints(0);

    // Act
    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/orderss")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(2)
  public void testCreateOrderWithInvalidDeliveryDate() {
    // Arrange – delivery date is today, which is not at least 24 h ahead
    CreateOrderRequestDto dto = new CreateOrderRequestDto();
    dto.setCustomerID(testCustomer.getRoleID());
    dto.setDeliveryDate(INVALID_DELIVERY_DATE);
    dto.setUsedLoyaltyPoints(0);

    // Act
    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(3)
  public void testCreateOrderValid() {
    // Arrange
    CreateOrderRequestDto dto = new CreateOrderRequestDto();
    dto.setCustomerID(testCustomer.getRoleID());
    dto.setDeliveryDate(VALID_DELIVERY_DATE);
    dto.setUsedLoyaltyPoints(0);

    // Act
    ResponseEntity<OrderResponseDto> response =
        client
            .post()
            .uri("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(OrderResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    OrderResponseDto body = response.getBody();
    assertNotNull(body);
    assertNotNull(body.getOrderID());
    assertEquals(testCustomer.getRoleID(), body.getCustomerID());
    assertEquals("Preparing", body.getOrderStatus());
    assertEquals(VALID_DELIVERY_DATE, body.getDeliveryDate());
    assertEquals(Date.valueOf(LocalDate.now()), body.getOrderDate());
    assertEquals(0.0f, body.getLoyaltySaving());
    assertEquals("456 Test Avenue", body.getAddress());
    assertFalse(body.getItemIDs().isEmpty());

    // Store the ID for subsequent tests
    validOrderID = body.getOrderID();
  }

  // ==== GET /api/orders/{orderID} ====

  @Test
  @Order(4)
  public void testGetOrderByValidID() {
    // Arrange
    String url = "/api/orders/" + validOrderID;

    // Act
    ResponseEntity<OrderResponseDto> response =
        client.get().uri(url).retrieve().toEntity(OrderResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(validOrderID, body.getOrderID());
    assertEquals(testCustomer.getRoleID(), body.getCustomerID());
    assertEquals("Preparing", body.getOrderStatus());
  }

  @Test
  @Order(5)
  public void testGetOrderByInvalidID() {
    // Arrange
    String url = "/api/orders/" + INVALID_ID;

    // Act
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ==== GET /api/orders ====

  @Test
  @Order(6)
  public void testGetAllOrders() {
    // Act
    ResponseEntity<List<OrderResponseDto>> response =
        client
            .get()
            .uri("/api/orders")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<OrderResponseDto>>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<OrderResponseDto> orders = response.getBody();
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
    assertEquals(orders.getFirst().getOrderID(), validOrderID);
  }

  // ==== GET /api/orders?customerID={customerID} ====

  @Test
  @Order(7)
  public void testGetOrdersByValidCustomerID() {
    // Act
    ResponseEntity<List<OrderResponseDto>> response =
        client
            .get()
            .uri("/api/orders?customerID=" + testCustomer.getRoleID())
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<OrderResponseDto>>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<OrderResponseDto> orders = response.getBody();
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
    assertTrue(orders.stream().anyMatch(o -> validOrderID.equals(o.getOrderID())));
  }

  @Test
  @Order(8)
  public void testGetOrdersByInvalidCustomerID() {
    // Act
    ResponseEntity<String> response =
        client.get().uri("/api/orders?customerID=" + INVALID_ID).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ==== GET /api/orders?orderStatus={orderStatus} ====

  @Test
  @Order(9)
  public void testGetOrdersByValidStatus() {
    // Act
    ResponseEntity<List<OrderResponseDto>> response =
        client
            .get()
            .uri("/api/orders?orderStatus=Preparing")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<OrderResponseDto>>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<OrderResponseDto> orders = response.getBody();
    assertNotNull(orders);
    assertEquals(validOrderID, orders.getFirst().getOrderID());
    assertEquals("Preparing", orders.getFirst().getOrderStatus());
  }

  @Test
  @Order(10)
  public void testGetOrdersByInvalidStatus() {
    // Act
    ResponseEntity<String> response =
        client
            .get()
            .uri("/api/orders?orderStatus=" + INVALID_STATUS)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  // ==== GET /api/orders?customerID={customerID}&orderStatus={orderStatus} ====

  @Test
  @Order(11)
  public void testGetOrdersByValidCustomerIDAndValidStatus() {
    // Act
    ResponseEntity<List<OrderResponseDto>> response =
        client
            .get()
            .uri("/api/orders?customerID=" + testCustomer.getRoleID() + "&orderStatus=Preparing")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<OrderResponseDto>>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<OrderResponseDto> orders = response.getBody();
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
    assertTrue(orders.stream().anyMatch(o -> validOrderID.equals(o.getOrderID())));
  }

  @Test
  @Order(12)
  public void testGetOrdersByInvalidCustomerIDAndValidStatus() {
    // Act
    ResponseEntity<String> response =
        client
            .get()
            .uri("/api/orders?customerID=" + INVALID_ID + "&orderStatus=Preparing")
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(13)
  public void testGetOrdersByValidCustomerIDAndInvalidStatus() {
    // Act
    ResponseEntity<String> response =
        client
            .get()
            .uri(
                "/api/orders?customerID="
                    + testCustomer.getRoleID()
                    + "&orderStatus="
                    + INVALID_STATUS)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  // ==== PATCH /api/orders/{orderID} (assign employee) ====

  @Test
  @Order(14)
  public void testAssignInvalidOrderToEmployee() {
    // Arrange
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setEmployeeID(testEmployee.getRoleID());

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/orders/" + INVALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(15)
  public void testAssignOrderToInvalidEmployee() {
    // Arrange
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setEmployeeID(INVALID_ID);

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(16)
  public void testAssignOrderToValidEmployee() {
    // Arrange
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setEmployeeID(testEmployee.getRoleID());

    // Act
    ResponseEntity<OrderResponseDto> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(OrderResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(validOrderID, body.getOrderID());
    assertEquals(testEmployee.getRoleID(), body.getEmployeeID());
  }

  // ==== PATCH /api/orders/{orderID} (delivery date) ====

  @Test
  @Order(17)
  public void testUpdateOrderInvalidDeliveryDate() {
    // Arrange – delivery date is today, which is not at least 24 h ahead
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setDeliveryDate(INVALID_DELIVERY_DATE);

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(18)
  public void testUpdateOrderValidDeliveryDate() {
    // Arrange
    Date newDeliveryDate = Date.valueOf(LocalDate.now().plusDays(5));
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setDeliveryDate(newDeliveryDate);

    // Act
    ResponseEntity<OrderResponseDto> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(OrderResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(validOrderID, body.getOrderID());
    assertEquals(newDeliveryDate, body.getDeliveryDate());
  }

  // ==== PATCH /api/orders/{orderID} (status) ====

  @Test
  @Order(19)
  public void testUpdateInvalidOrderStatus() {
    // Arrange
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setOrderStatus(INVALID_STATUS);

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(20)
  public void testUpdateValidOrderStatus() {
    // Arrange
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setOrderStatus("Delivered");

    // Act
    ResponseEntity<OrderResponseDto> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(OrderResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(validOrderID, body.getOrderID());
    assertEquals("Delivered", body.getOrderStatus());
  }

  @Test
  @Order(21)
  public void testCancelDeliveredOrder() {
    // Arrange – order is now "Delivered" after test 20
    UpdateOrderRequestDto dto = new UpdateOrderRequestDto();
    dto.setOrderStatus("Cancelled");

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/orders/" + validOrderID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
