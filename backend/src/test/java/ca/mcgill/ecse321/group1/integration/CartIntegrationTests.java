package ca.mcgill.ecse321.group1.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.mcgill.ecse321.group1.dto.*;
import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.repository.*;
import ca.mcgill.ecse321.group1.security.JwtUtil;
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
public class CartIntegrationTests {

  @LocalServerPort private int port;

  private RestClient client;

  @Autowired private ItemRepository itemRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private PersonRepository personRepository;
  @Autowired private ClothingModelRepository clothingModelRepository;
  @Autowired private ClothingVariantRepository clothingVariantRepository;
  @Autowired private JwtUtil jwtUtil;

  private static final String INVALID_ID = "not-a-real-id";
  private static final int STOCK_QUANTITY = 3;

  private String customerToken;

  // Test objects created in setup
  private Customer testCustomer;
  private Person testCustomerPerson;
  private ClothingModel testModel;
  private ClothingVariant testVariant;

  // Stored across tests
  private String validItemID;

  @BeforeAll
  public void setup() {
    // Create clothing model
    testModel = new ClothingModel();
    testModel.setName("Test Hoodie");
    testModel.setPrice(29.99f);
    testModel = clothingModelRepository.save(testModel);

    // Create clothing variant with stock
    testVariant = new ClothingVariant();
    testVariant.setSize(ClothingVariant.Size.M);
    testVariant.setColor("red");
    testVariant.setStockQuantity(STOCK_QUANTITY);
    testVariant.setModel(testModel);
    testVariant = clothingVariantRepository.save(testVariant);

    // Create customer
    testCustomerPerson = new Person();
    testCustomerPerson.setEmail("cartcustomer@test.com");
    testCustomerPerson.setPassword("password123");
    testCustomerPerson = personRepository.save(testCustomerPerson);

    testCustomer = new Customer();
    testCustomer.setAddress("123 Cart Street");
    testCustomer.setLoyaltyPoints(0);
    testCustomer.setPerson(testCustomerPerson);
    testCustomer = customerRepository.save(testCustomer);

    customerToken = jwtUtil.generateToken(testCustomerPerson, "Customer");

    client =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + customerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {})
            .build();
  }

  @AfterAll
  public void cleanup() {
    // Delete any remaining items for this customer
    List<Item> remainingItems = itemRepository.findItemsByCustomer(testCustomer);
    for (Item item : remainingItems) {
      itemRepository.deleteById(item.getItemID());
    }
    customerRepository.deleteById(testCustomer.getRoleID());
    personRepository.deleteById(testCustomerPerson.getPersonID());
    clothingModelRepository.deleteById(testModel.getClothingModelID());
  }

  // ==== POST /api/carts/{customerID}/items ====

  @Test
  @Order(1)
  public void testAddItemInvalidVariant() {
    AddItemDTO dto = new AddItemDTO();
    dto.setClothingVariantID(INVALID_ID);
    dto.setQuantity(1);

    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(2)
  public void testAddItemQuantityZero() {
    AddItemDTO dto = new AddItemDTO();
    dto.setClothingVariantID(testVariant.getClothingVariantID());
    dto.setQuantity(0);

    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(3)
  public void testAddItemQuantityExceedsStock() {
    AddItemDTO dto = new AddItemDTO();
    dto.setClothingVariantID(testVariant.getClothingVariantID());
    dto.setQuantity(STOCK_QUANTITY + 1);

    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(4)
  public void testAddItemValid() {
    AddItemDTO dto = new AddItemDTO();
    dto.setClothingVariantID(testVariant.getClothingVariantID());
    dto.setQuantity(2);

    ResponseEntity<ItemDTO> response =
        client
            .post()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(ItemDTO.class);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ItemDTO body = response.getBody();
    assertNotNull(body);
    assertNotNull(body.getItemID());
    assertEquals(2, body.getQuantity());
    assertEquals(testVariant.getClothingVariantID(), body.getClothingVariantID());
    assertEquals(testCustomer.getRoleID(), body.getCustomerID());
    assertEquals(testModel.getPrice(), body.getPrice());

    validItemID = body.getItemID();
  }

  // ==== GET /api/carts/{customerID}/items ====

  @Test
  @Order(5)
  public void testGetCartItemsValid() {
    ResponseEntity<List<ItemDTO>> response =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<ItemDTO> items = response.getBody();
    assertNotNull(items);
    assertFalse(items.isEmpty());
    assertTrue(items.stream().anyMatch(i -> validItemID.equals(i.getItemID())));
  }

  // ==== GET /api/carts/{customerID}/items/{itemID} ====

  @Test
  @Order(6)
  public void testGetItemByInvalidID() {
    ResponseEntity<String> response =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + INVALID_ID)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(7)
  public void testGetItemByValidID() {
    ResponseEntity<ItemDTO> response =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .retrieve()
            .toEntity(ItemDTO.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ItemDTO body = response.getBody();
    assertNotNull(body);
    assertEquals(validItemID, body.getItemID());
    assertEquals(testVariant.getClothingVariantID(), body.getClothingVariantID());
  }

  // ==== GET /api/carts/{customerID} ====

  @Test
  @Order(8)
  public void testGetCartTotalValid() {
    ResponseEntity<CartTotalDTO> response =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID())
            .retrieve()
            .toEntity(CartTotalDTO.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    CartTotalDTO body = response.getBody();
    assertNotNull(body);
    // quantity=2, price=29.99 → total=59.98
    assertEquals(testModel.getPrice() * 2, body.getCartTotal(), 0.01f);
  }

  // ==== PATCH /api/carts/{customerID}/items/{itemID} ====

  @Test
  @Order(9)
  public void testModifyQuantityInvalidItem() {
    UpdateItemQuantityDTO dto = new UpdateItemQuantityDTO();
    dto.setQuantity(1);

    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + INVALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(10)
  public void testModifyQuantityToZero() {
    UpdateItemQuantityDTO dto = new UpdateItemQuantityDTO();
    dto.setQuantity(0);

    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(11)
  public void testModifyQuantityExceedsStock() {
    UpdateItemQuantityDTO dto = new UpdateItemQuantityDTO();
    dto.setQuantity(STOCK_QUANTITY + 1);

    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(12)
  public void testModifyQuantityValid() {
    UpdateItemQuantityDTO dto = new UpdateItemQuantityDTO();
    dto.setQuantity(1);

    ResponseEntity<ItemDTO> response =
        client
            .patch()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(ItemDTO.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ItemDTO body = response.getBody();
    assertNotNull(body);
    assertEquals(validItemID, body.getItemID());
    assertEquals(1, body.getQuantity());
  }

  // ==== DELETE /api/carts/{customerID}/items/{itemID} ====

  @Test
  @Order(13)
  public void testRemoveItemInvalidItemID() {
    ResponseEntity<String> response =
        client
            .delete()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + INVALID_ID)
            .retrieve()
            .toEntity(String.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(14)
  public void testRemoveItemValid() {
    ResponseEntity<Void> response =
        client
            .delete()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .retrieve()
            .toEntity(Void.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    // Verify it's gone
    ResponseEntity<String> getResponse =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items/" + validItemID)
            .retrieve()
            .toEntity(String.class);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  // ==== DELETE /api/carts/{customerID}/items ====

  @Test
  @Order(15)
  public void testRemoveAllItemsValid() {
    // Add a fresh item to the cart first
    AddItemDTO dto = new AddItemDTO();
    dto.setClothingVariantID(testVariant.getClothingVariantID());
    dto.setQuantity(1);
    client
        .post()
        .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .toBodilessEntity();

    // Delete all
    ResponseEntity<Void> response =
        client
            .delete()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .retrieve()
            .toEntity(Void.class);

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    // Verify cart is empty
    ResponseEntity<List<ItemDTO>> getResponse =
        client
            .get()
            .uri("/api/carts/" + testCustomer.getRoleID() + "/items")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});
    assertNotNull(getResponse.getBody());
    assertTrue(getResponse.getBody().isEmpty());
  }
}
