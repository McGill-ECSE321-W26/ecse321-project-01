package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.repository.ClothingModelRepository;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ClothingServiceTests {
  @Mock ClothingModelRepository clothingModelRepository;
  @Mock ClothingVariantRepository clothingVariantRepository;
  @Mock ItemRepository itemRepository;
  @InjectMocks ClothingService clothingService;

  private static final String VALID_MODEL_IMAGE = "model.jpg";
  private static final String VALID_VARIANT_IMAGE = "variant.png";

  // ===== getAllClothingModels =====

  @Test
  public void testGetAllClothingModels() {
    // Arrange
    ClothingModel model1 = new ClothingModel(null, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingModel model2 = new ClothingModel(null, "Winter Coat", 149.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByArchivedFalse()).thenReturn(List.of(model1, model2));

    // Act
    List<ClothingModel> result = clothingService.getAllClothingModels();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Summer Jacket", result.get(0).getName());
    assertEquals("Winter Coat", result.get(1).getName());
  }

  @Test
  public void testGetClothingModelById() {
    // Arrange
    String modelId = "1";
    String name = "Summer Jacket";
    Float price = 79.99f;
    ClothingModel model = new ClothingModel(modelId, name, price, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act
    ClothingModel result = clothingService.getClothingModel(modelId);

    // Assert
    assertNotNull(result);
    assertEquals("Summer Jacket", result.getName());
    assertEquals(price, result.getPrice());
    assertEquals(VALID_MODEL_IMAGE, result.getImagePath());
  }

  @Test
  public void testGetClothingModelByInvalidId() {
    // Assert
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(null);
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.getClothingModel(modelId));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  // ===== createClothingModel =====

  @Test
  public void testCreateValidClothingModel() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    ClothingModel saved = new ClothingModel(null, name, price, VALID_MODEL_IMAGE);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenReturn(saved);

    // Act
    ClothingModel result = clothingService.createClothingModel(name, price, VALID_MODEL_IMAGE);

    // Assert
    assertNotNull(result);
    assertEquals(name, result.getName());
    assertEquals(price, result.getPrice());
    assertEquals(VALID_MODEL_IMAGE, result.getImagePath());
    verify(clothingModelRepository, times(1)).save(any(ClothingModel.class));
  }

  @Test
  public void testCreateClothingModelWithInvalidName() {
    // Arrange
    float price = 79.99f;
    // ACT & ASSERT
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel(null, price, VALID_MODEL_IMAGE));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithExistingName() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    ClothingModel saved = new ClothingModel(null, name, price, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByNameAndArchivedFalse(name)).thenReturn(saved);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel(name, price, VALID_MODEL_IMAGE));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", name),
        e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithBlankName() {
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel("  ", 100f, VALID_MODEL_IMAGE));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithInvalidPrice() {
    // Arrange
    float price = -1f;
    String name = "Summer Jacket";

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel(name, price, VALID_MODEL_IMAGE));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithBlankImagePath() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    when(clothingModelRepository.findByNameAndArchivedFalse(name)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel(name, price, ""));
    assertEquals("400 BAD_REQUEST \"Image path must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithInvalidImageExtension() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    when(clothingModelRepository.findByNameAndArchivedFalse(name)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createClothingModel(name, price, "image.bmp"));
    assertEquals(
        "400 BAD_REQUEST \"Image path must end with a valid image extension (.jpg, .jpeg, .png, .gif, .webp, .svg)\"",
        e.getMessage());
  }

  // ===== updateClothingModel =====

  @Test
  public void testUpdateValidClothingModel() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float newPrice = 129.99f;
    String newImage = "updated.jpg";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(id, ClothingVariant.Size.M, "blue", VALID_VARIANT_IMAGE, 5, model);
    Item item = new Item(id, 1, 1f, variant);
    when(itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(id))
        .thenReturn(List.of(item));
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingModel res = clothingService.updateClothingModel(id, newName, newPrice, newImage);

    // Assert
    assertNotNull(res);
    assertEquals(newName, res.getName());
    assertEquals(newPrice, res.getPrice());
    assertEquals(newImage, res.getImagePath());
    verify(itemRepository, times(1)).saveAll(any());
    verify(clothingModelRepository, times(1)).save(any(ClothingModel.class));
  }

  @Test
  public void testUpdateClothingModelWithInvalidId() {
    // Arrange
    String id = "badId";
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "New Name", 99.99f, VALID_MODEL_IMAGE));
    assertEquals("404 NOT_FOUND \"Clothing model with ID " + id + " not found\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithBlankName() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "  ", 79.99f, VALID_MODEL_IMAGE));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithExistingName() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float price = 79.99f;
    ClothingModel saved = new ClothingModel(id, "Summer Jacket", price, VALID_MODEL_IMAGE);
    ClothingModel exist = new ClothingModel("2", newName, price, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(saved);
    when(clothingModelRepository.findByNameAndArchivedFalse(newName)).thenReturn(exist);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, newName, price, VALID_MODEL_IMAGE));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", newName),
        e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithInvalidPrice() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "Summer Jacket", -1f, VALID_MODEL_IMAGE));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithBlankImagePath() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "Summer Jacket", 79.99f, "  "));
    assertEquals("400 BAD_REQUEST \"Image path must not be blank\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithInvalidImageExtension() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "Summer Jacket", 79.99f, "image.tiff"));
    assertEquals(
        "400 BAD_REQUEST \"Image path must end with a valid image extension (.jpg, .jpeg, .png, .gif, .webp, .svg)\"",
        e.getMessage());
  }

  // ===== deleteClothingModel =====

  @Test
  public void testDeleteValidClothingModel() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant("v1", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);
    when(itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(modelId))
        .thenReturn(List.of());
    when(clothingModelRepository.save(any(ClothingModel.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    clothingService.deleteClothingModel(modelId);

    // Assert
    assertEquals(true, model.getArchived());
    assertEquals(true, variant.getArchived());
    verify(clothingModelRepository, times(1)).save(model);
  }

  @Test
  public void testDeleteClothingModelWithInvalidId() {
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(null);
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.deleteClothingModel(modelId));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  // ===== getVariant =====

  @Test
  public void testGetVariantById() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);

    // Act
    ClothingVariant result = clothingService.getVariant(modelId, variantId);

    // Assert
    assertNotNull(result);
    assertEquals(variantId, result.getClothingVariantID());
    assertEquals(ClothingVariant.Size.M, result.getSize());
    assertEquals("#FF0000", result.getColor());
    assertEquals(VALID_VARIANT_IMAGE, result.getImagePath());
  }

  @Test
  public void testGetVariantByInvalidId() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.getVariant(modelId, variantId));
    assertEquals(
        "404 NOT_FOUND \"Clothing variant with ID "
            + variantId
            + " not found under model "
            + modelId
            + "\"",
        e.getMessage());
  }

  @Test
  public void testGetVariantByWrongModelId() {
    // Arrange
    String correctModelId = "model1";
    String wrongModelId = "model2";
    String variantId = "variant1";
    ClothingModel model =
        new ClothingModel(correctModelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    new ClothingVariant(variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(
            new ClothingVariant(
                variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model));

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.getVariant(wrongModelId, variantId));
    assertEquals(
        "404 NOT_FOUND \"Clothing variant with ID "
            + variantId
            + " not found under model "
            + wrongModelId
            + "\"",
        e.getMessage());
  }

  // ===== getVariantsByModel =====

  @Test
  public void testGetVariantsByModel() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(
            "variant1", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act
    List<ClothingVariant> result = clothingService.getVariantsByModel(modelId);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(variant.getClothingVariantID(), result.get(0).getClothingVariantID());
  }

  @Test
  public void testGetVariantsByInvalidModel() {
    // Arrange
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.getVariantsByModel(modelId));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  // ===== createVariant =====

  @Test
  public void testCreateValidVariant() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant saved =
        new ClothingVariant(
            null,
            ClothingVariant.Size.L,
            "#0000FF",
            VALID_VARIANT_IMAGE,
            5,
            new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE));
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);
    when(clothingVariantRepository.save(any(ClothingVariant.class))).thenReturn(saved);

    // Act
    ClothingVariant result =
        clothingService.createVariant(
            modelId, ClothingVariant.Size.L, "#0000FF", VALID_VARIANT_IMAGE, 5);

    // Assert
    assertNotNull(result);
    assertEquals(ClothingVariant.Size.L, result.getSize());
    assertEquals("#0000FF", result.getColor());
    assertEquals(VALID_VARIANT_IMAGE, result.getImagePath());
    assertEquals(5, result.getStockQuantity());
    verify(clothingVariantRepository, times(1)).save(any(ClothingVariant.class));
  }

  @Test
  public void testCreateVariantWithInvalidModel() {
    // Arrange
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithNullSize() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, null, "#FF0000", VALID_VARIANT_IMAGE, 5));
    assertEquals("400 BAD_REQUEST \"Size must be specified\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithBlankColor() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "  ", VALID_VARIANT_IMAGE, 5));
    assertEquals("400 BAD_REQUEST \"Color must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithInvalidHexColorNoPound() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "FF5733", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "400 BAD_REQUEST \"Color must be a valid hex color (e.g. #FF5733)\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithInvalidHexColorTooShort() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#FFF", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "400 BAD_REQUEST \"Color must be a valid hex color (e.g. #FF5733)\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithInvalidHexColorBadChars() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#GGGGGG", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "400 BAD_REQUEST \"Color must be a valid hex color (e.g. #FF5733)\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithInvalidHexColorNamedColor() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "red", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "400 BAD_REQUEST \"Color must be a valid hex color (e.g. #FF5733)\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithNegativeStock() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, -1));
    assertEquals("400 BAD_REQUEST \"Stock quantity must be >= 0\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithBlankImagePath() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, ClothingVariant.Size.M, "#FF0000", "", 5));
    assertEquals("400 BAD_REQUEST \"Image path must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithInvalidImageExtension() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#FF0000", "image.bmp", 5));
    assertEquals(
        "400 BAD_REQUEST \"Image path must end with a valid image extension (.jpg, .jpeg, .png, .gif, .webp, .svg)\"",
        e.getMessage());
  }

  @Test
  public void testCreateVariantWithDuplicateSizeAndColor() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    // Adding a variant to the model creates the duplicate
    new ClothingVariant("existing", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createVariant(
                    modelId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 5));
    assertEquals(
        "409 CONFLICT \"A variant with size M and color #FF0000 already exists for this clothing model\"",
        e.getMessage());
  }

  // ===== deleteVariant =====

  @Test
  public void testDeleteValidVariant() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(itemRepository.findByClothingVariantAndOrderIsNull(variant)).thenReturn(List.of());
    when(clothingVariantRepository.save(any(ClothingVariant.class)))
        .thenAnswer(i -> i.getArgument(0));

    // Act
    clothingService.deleteVariant(modelId, variantId);

    // Assert
    assertEquals(true, variant.getArchived());
    verify(clothingVariantRepository, times(1)).save(variant);
  }

  @Test
  public void testDeleteVariantWithInvalidId() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.deleteVariant(modelId, variantId));
    assertEquals(
        "404 NOT_FOUND \"Clothing variant with ID "
            + variantId
            + " not found under model "
            + modelId
            + "\"",
        e.getMessage());
  }

  // ===== updateVariantStock =====

  @Test
  public void testUpdateValidVariant() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(clothingVariantRepository.save(any(ClothingVariant.class)))
        .thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingVariant result = clothingService.updateVariantStock(modelId, variantId, 5);

    // Assert
    assertNotNull(result);
    assertEquals(5, result.getStockQuantity());
    verify(clothingVariantRepository, times(1)).save(any(ClothingVariant.class));
  }

  @Test
  public void testUpdateVariantStockWithInvalidId() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateVariantStock(modelId, variantId, 5));
    assertEquals(
        "404 NOT_FOUND \"Clothing variant with ID "
            + variantId
            + " not found under model "
            + modelId
            + "\"",
        e.getMessage());
  }

  @Test
  public void testUpdateVariantStockWithNegativeStock() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f, VALID_MODEL_IMAGE);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateVariantStock(modelId, variantId, -1));
    assertEquals("400 BAD_REQUEST \"Stock quantity must be >= 0\"", e.getMessage());
  }
}
