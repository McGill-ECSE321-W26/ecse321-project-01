package ca.mcgill.ecse321.group1.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.dto.ClothingModelCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingModelResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantResponseDto;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.ClothingModelRepository;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.ManagerRepository;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import ca.mcgill.ecse321.group1.security.JwtUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClothingIntegrationTesting {

  @LocalServerPort private int port;

  private RestClient client;

  @Autowired private ClothingVariantRepository variantRepository;

  @Autowired private ClothingModelRepository modelRepository;

  @Autowired private PersonRepository personRepository;

  @Autowired private ManagerRepository managerRepository;

  @Autowired private JwtUtil jwtUtil;

  private Person managerPerson;
  private String managerToken;

  // Valid test data
  private final String VALID_NAME = "Winter Jacket";
  private final float VALID_PRICE = 99.99f;
  private final ClothingVariant.Size VALID_SIZE = ClothingVariant.Size.M;
  private final String VALID_COLOR = "Blue";
  private final int VALID_STOCK = 10;

  // Invalid test data
  private final String INVALID_MODEL_ID = "nonexistent-id";
  private final String INVALID_VARIANT_ID = "nonexistent-variant-id";

  // Stored between tests
  private String validModelId;
  private String validVariantId;

  @BeforeAll
  public void setup() {
    client =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {})
            .build();

    managerPerson = new Person();
    managerPerson.setEmail("clothing-manager@test.com");
    managerPerson.setPassword("password");
    managerPerson = personRepository.save(managerPerson);

    Manager manager = new Manager();
    manager.setPerson(managerPerson);
    managerRepository.save(manager);

    managerToken = jwtUtil.generateToken(managerPerson, "Manager");
  }

  @AfterAll
  public void clearDatabase() {
    variantRepository.deleteAll();
    modelRepository.deleteAll();
    managerRepository.deleteAll();
    personRepository.deleteById(managerPerson.getPersonID());
  }

  @Test
  @Order(1)
  public void testCreateValidClothingModel() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(VALID_NAME, VALID_PRICE);

    // Act
    ResponseEntity<ClothingModelResponseDto> response =
        client
            .post()
            .uri("/api/clothing")
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ClothingModelResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ClothingModelResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(VALID_NAME, body.getName());
    assertEquals(VALID_PRICE, body.getPrice());
    assertNotNull(body.getClothingModelID());

    this.validModelId = body.getClothingModelID();
  }

  @Test
  @Order(2)
  public void testGetAllClothingModels() {
    // Act
    ResponseEntity<ClothingModelResponseDto[]> response =
        client.get().uri("/api/clothing").retrieve().toEntity(ClothingModelResponseDto[].class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelResponseDto[] body = response.getBody();
    assertNotNull(body);
    assertTrue(body.length > 0, "Should return at least one clothing model.");
  }

  @Test
  @Order(3)
  public void testGetClothingModelByValidId() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId;

    // Act
    ResponseEntity<ClothingModelResponseDto> response =
        client.get().uri(url).retrieve().toEntity(ClothingModelResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(this.validModelId, body.getClothingModelID());
    assertEquals(VALID_NAME, body.getName());
    assertEquals(VALID_PRICE, body.getPrice());
  }

  @Test
  @Order(4)
  public void testGetClothingModelByInvalidId() {
    // Arrange
    String url = "/api/clothing/" + INVALID_MODEL_ID;

    // Act
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(5)
  public void testAddVariantToModel() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(VALID_SIZE, VALID_COLOR, VALID_STOCK);

    // Act
    ResponseEntity<ClothingVariantResponseDto> response =
        client
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ClothingVariantResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ClothingVariantResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(VALID_SIZE, body.getSize());
    assertEquals(VALID_COLOR, body.getColor());
    assertEquals(VALID_STOCK, body.getStockQuantity());
    assertNotNull(body.getClothingVariantID());

    this.validVariantId = body.getClothingVariantID();
  }

  @Test
  @Order(6)
  public void testAddVariantToInvalidModel() {
    // Arrange
    String url = "/api/clothing/" + INVALID_MODEL_ID + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(VALID_SIZE, VALID_COLOR, VALID_STOCK);

    // Act
    ResponseEntity<String> response =
        client
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(7)
  public void testGetVariantsByModel() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants";

    // Act
    ResponseEntity<ClothingVariantResponseDto[]> response =
        client.get().uri(url).retrieve().toEntity(ClothingVariantResponseDto[].class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingVariantResponseDto[] body = response.getBody();
    assertNotNull(body);
    assertTrue(body.length > 0, "Should return at least one variant.");
  }

  @Test
  @Order(8)
  public void testGetVariantByValidId() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants/" + this.validVariantId;

    // Act
    ResponseEntity<ClothingVariantResponseDto> response =
        client.get().uri(url).retrieve().toEntity(ClothingVariantResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingVariantResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(this.validVariantId, body.getClothingVariantID());
    assertEquals(VALID_SIZE, body.getSize());
    assertEquals(VALID_COLOR, body.getColor());
  }

  @Test
  @Order(9)
  public void testGetVariantByInvalidId() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants/" + INVALID_VARIANT_ID;

    // Act
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(10)
  public void testDeleteVariant() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants/" + this.validVariantId;

    // Act
    client
        .delete()
        .uri(url)
        .header("Authorization", "Bearer " + managerToken)
        .retrieve()
        .toBodilessEntity();

    // Assert - confirm it's gone
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(11)
  public void testDeleteClothingModel() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId;

    // Act
    client
        .delete()
        .uri(url)
        .header("Authorization", "Bearer " + managerToken)
        .retrieve()
        .toBodilessEntity();

    // Assert - confirm it's gone
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(12)
  public void testDeleteNonExistentModel() {
    // Arrange
    String url = "/api/clothing/" + INVALID_MODEL_ID;

    // Act
    ResponseEntity<String> response =
        client
            .delete()
            .uri(url)
            .header("Authorization", "Bearer " + managerToken)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
