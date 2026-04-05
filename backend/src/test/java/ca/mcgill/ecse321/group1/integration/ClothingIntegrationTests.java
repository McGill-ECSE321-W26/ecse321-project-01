package ca.mcgill.ecse321.group1.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.dto.ClothingModelCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingModelListResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingModelResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantUpdateRequestDto;
import ca.mcgill.ecse321.group1.model.ClothingModel;
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
public class ClothingIntegrationTests {

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
  private final String VALID_DESCRIPTION = "A warm winter jacket";
  private final String VALID_BRAND = "Nike";
  private final ClothingModel.Category VALID_CATEGORY = ClothingModel.Category.Outerwear;
  private final float VALID_PRICE = 99.99f;
  private final ClothingVariant.Size VALID_SIZE = ClothingVariant.Size.M;
  private final String VALID_COLOR = "#0000FF";
  private final String VALID_VARIANT_IMAGE = "variant.png";
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
        new ClothingModelCreateRequestDto(
            VALID_NAME, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, VALID_PRICE);

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
    assertEquals(VALID_DESCRIPTION, body.getDescription());
    assertEquals(VALID_BRAND, body.getBrand());
    assertEquals(VALID_CATEGORY, body.getCategory());
    assertEquals(VALID_PRICE, body.getPrice());
    assertNotNull(body.getClothingModelID());

    this.validModelId = body.getClothingModelID();
  }

  @Test
  @Order(2)
  public void testCreateClothingModelWithBlankBrand() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "Another Jacket", "desc", "  ", VALID_CATEGORY, 59.99f);

    // Act
    ResponseEntity<String> response =
        client
            .post()
            .uri("/api/clothing")
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(3)
  public void testGetAllClothingModels() {
    // Act
    ResponseEntity<ClothingModelListResponseDto[]> response =
        client.get().uri("/api/clothing").retrieve().toEntity(ClothingModelListResponseDto[].class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelListResponseDto[] body = response.getBody();
    assertNotNull(body);
    assertTrue(body.length > 0, "Should return at least one clothing model.");
  }

  @Test
  @Order(4)
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
    assertEquals(VALID_DESCRIPTION, body.getDescription());
    assertEquals(VALID_BRAND, body.getBrand());
    assertEquals(VALID_CATEGORY, body.getCategory());
    assertEquals(VALID_PRICE, body.getPrice());
  }

  @Test
  @Order(5)
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
  @Order(6)
  public void testAddVariantToModel() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(
            VALID_SIZE, VALID_COLOR, VALID_VARIANT_IMAGE, VALID_STOCK);

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
    assertEquals(VALID_VARIANT_IMAGE, body.getImagePath());
    assertEquals(VALID_STOCK, body.getStockQuantity());
    assertNotNull(body.getClothingVariantID());

    this.validVariantId = body.getClothingVariantID();
  }

  @Test
  @Order(7)
  public void testGetAllClothingModelsIncludesVariantSummaries() {
    // Act
    ResponseEntity<ClothingModelListResponseDto[]> response =
        client
            .get()
            .uri("/api/clothing")
            .retrieve()
            .toEntity(ClothingModelListResponseDto[].class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelListResponseDto[] body = response.getBody();
    assertNotNull(body);
    assertTrue(body.length > 0);
    // Find our model in the list
    ClothingModelListResponseDto found = null;
    for (ClothingModelListResponseDto dto : body) {
      if (this.validModelId.equals(dto.getClothingModelID())) {
        found = dto;
        break;
      }
    }
    assertNotNull(found, "Should find the created model in the list");
    assertNotNull(found.getVariants());
    assertEquals(1, found.getVariants().size());
    assertEquals(VALID_VARIANT_IMAGE, found.getVariants().get(0).getImagePath());
    assertEquals(VALID_COLOR, found.getVariants().get(0).getColor());
  }

  @Test
  @Order(8)
  public void testGetAllClothingModelsDeduplicatesVariantsByColor() {
    // Arrange — add a second variant with the SAME color but different size
    String url = "/api/clothing/" + this.validModelId + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(
            ClothingVariant.Size.L, VALID_COLOR, "variant2.png", 5);

    ResponseEntity<ClothingVariantResponseDto> createResponse =
        client
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ClothingVariantResponseDto.class);
    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

    // Also add a variant with a DIFFERENT color
    ClothingVariantCreateRequestDto request2 =
        new ClothingVariantCreateRequestDto(
            ClothingVariant.Size.S, "#FF0000", "variant3.png", 3);

    ResponseEntity<ClothingVariantResponseDto> createResponse2 =
        client
            .post()
            .uri(url)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request2)
            .retrieve()
            .toEntity(ClothingVariantResponseDto.class);
    assertEquals(HttpStatus.CREATED, createResponse2.getStatusCode());

    // Act — get all models
    ResponseEntity<ClothingModelListResponseDto[]> response =
        client
            .get()
            .uri("/api/clothing")
            .retrieve()
            .toEntity(ClothingModelListResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelListResponseDto[] body = response.getBody();
    assertNotNull(body);
    ClothingModelListResponseDto found = null;
    for (ClothingModelListResponseDto dto : body) {
      if (this.validModelId.equals(dto.getClothingModelID())) {
        found = dto;
        break;
      }
    }
    assertNotNull(found);
    // Model has 3 variants (2 blue, 1 red) but list should deduplicate by color → 2 entries
    assertEquals(2, found.getVariants().size());
    long blueCount =
        found.getVariants().stream().filter(v -> VALID_COLOR.equals(v.getColor())).count();
    long redCount =
        found.getVariants().stream().filter(v -> "#FF0000".equals(v.getColor())).count();
    assertEquals(1, blueCount, "Should have exactly one variant entry for blue");
    assertEquals(1, redCount, "Should have exactly one variant entry for red");
  }

  @Test
  @Order(9)
  public void testAddVariantWithInvalidImagePath() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(ClothingVariant.Size.S, "#FF0000", "image.bmp", 5);

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
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(10)
  public void testAddVariantToInvalidModel() {
    // Arrange
    String url = "/api/clothing/" + INVALID_MODEL_ID + "/variants";
    ClothingVariantCreateRequestDto request =
        new ClothingVariantCreateRequestDto(
            VALID_SIZE, VALID_COLOR, VALID_VARIANT_IMAGE, VALID_STOCK);

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
  @Order(11)
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
  @Order(12)
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
    assertEquals(VALID_VARIANT_IMAGE, body.getImagePath());
  }

  @Test
  @Order(13)
  public void testGetVariantByInvalidId() {
    // Arrange
    String url = "/api/clothing/" + this.validModelId + "/variants/" + INVALID_VARIANT_ID;

    // Act
    ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ==== PUT /api/clothing/{modelId} ====

  @Test
  @Order(14)
  public void testUpdateClothingModelValid() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "Updated Jacket", "Updated description", "Adidas", ClothingModel.Category.Tops, 149.99f);

    // Act
    ResponseEntity<ClothingModelResponseDto> response =
        client
            .put()
            .uri("/api/clothing/" + this.validModelId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ClothingModelResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingModelResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(this.validModelId, body.getClothingModelID());
    assertEquals("Updated Jacket", body.getName());
    assertEquals("Updated description", body.getDescription());
    assertEquals("Adidas", body.getBrand());
    assertEquals(ClothingModel.Category.Tops, body.getCategory());
    assertEquals(149.99f, body.getPrice());
  }

  @Test
  @Order(15)
  public void testUpdateClothingModelBlankName() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 149.99f);

    // Act
    ResponseEntity<String> response =
        client
            .put()
            .uri("/api/clothing/" + this.validModelId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(16)
  public void testUpdateClothingModelNonPositivePrice() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "Updated Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 0f);

    // Act
    ResponseEntity<String> response =
        client
            .put()
            .uri("/api/clothing/" + this.validModelId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(17)
  public void testUpdateClothingModelBlankBrand() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "Updated Jacket", VALID_DESCRIPTION, "  ", VALID_CATEGORY, 149.99f);

    // Act
    ResponseEntity<String> response =
        client
            .put()
            .uri("/api/clothing/" + this.validModelId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(18)
  public void testUpdateClothingModelInvalidId() {
    // Arrange
    ClothingModelCreateRequestDto request =
        new ClothingModelCreateRequestDto(
            "Updated Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 149.99f);

    // Act
    ResponseEntity<String> response =
        client
            .put()
            .uri("/api/clothing/" + INVALID_MODEL_ID)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ==== PATCH /api/clothing/{modelId}/variants/{variantId} ====

  @Test
  @Order(19)
  public void testUpdateVariantStockValid() {
    // Arrange
    ClothingVariantUpdateRequestDto request = new ClothingVariantUpdateRequestDto(25);

    // Act
    ResponseEntity<ClothingVariantResponseDto> response =
        client
            .patch()
            .uri("/api/clothing/" + this.validModelId + "/variants/" + this.validVariantId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(ClothingVariantResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ClothingVariantResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(this.validVariantId, body.getClothingVariantID());
    assertEquals(25, body.getStockQuantity());
  }

  @Test
  @Order(20)
  public void testUpdateVariantStockNegative() {
    // Arrange
    ClothingVariantUpdateRequestDto request = new ClothingVariantUpdateRequestDto(-1);

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/clothing/" + this.validModelId + "/variants/" + this.validVariantId)
            .header("Authorization", "Bearer " + managerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(21)
  public void testUpdateVariantStockInvalidVariantId() {
    // Arrange
    ClothingVariantUpdateRequestDto request = new ClothingVariantUpdateRequestDto(5);

    // Act
    ResponseEntity<String> response =
        client
            .patch()
            .uri("/api/clothing/" + this.validModelId + "/variants/" + INVALID_VARIANT_ID)
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
  @Order(22)
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
  @Order(23)
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
  @Order(24)
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
