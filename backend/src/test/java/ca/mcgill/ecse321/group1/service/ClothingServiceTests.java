package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingModel.Category;
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

  private static final String VALID_VARIANT_IMAGE = "variant.png";
  private static final String VALID_DESCRIPTION = "A warm jacket";
  private static final String VALID_BRAND = "Nike";
  private static final Category VALID_CATEGORY = Category.Outerwear;

  // ===== getAllClothingModels =====

  @Test
  public void testGetAllClothingModels() {
    // Arrange
    ClothingModel model1 =
        new ClothingModel(
            null, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingModel model2 =
        new ClothingModel(
            null, "Winter Coat", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 149.99f);
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
    float price = 79.99f;
    ClothingModel model =
        new ClothingModel(modelId, name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId)).thenReturn(model);

    // Act
    ClothingModel result = clothingService.getClothingModel(modelId);

    // Assert
    assertNotNull(result);
    assertEquals("Summer Jacket", result.getName());
    assertEquals(price, result.getPrice());
    assertEquals(VALID_BRAND, result.getBrand());
    assertEquals(VALID_CATEGORY, result.getCategory());
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
    ClothingModel saved =
        new ClothingModel(null, name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenReturn(saved);

    // Act
    ClothingModel result =
        clothingService.createClothingModel(
            name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);

    // Assert
    assertNotNull(result);
    assertEquals(name, result.getName());
    assertEquals(price, result.getPrice());
    assertEquals(VALID_BRAND, result.getBrand());
    verify(clothingModelRepository, times(1)).save(any(ClothingModel.class));
  }

  @Test
  public void testCreateClothingModelWithInvalidName() {
    // ACT & ASSERT
    float price = 79.99f;
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createClothingModel(
                    null, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithExistingName() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    ClothingModel saved =
        new ClothingModel(null, name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);
    when(clothingModelRepository.findByName(name)).thenReturn(saved);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createClothingModel(
                    name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", name),
        e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithBlankName() {
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createClothingModel(
                    "  ", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 100f));
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
            () ->
                clothingService.createClothingModel(
                    name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithBlankBrand() {
    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createClothingModel(
                    "Summer Jacket", VALID_DESCRIPTION, "  ", VALID_CATEGORY, 79.99f));
    assertEquals("400 BAD_REQUEST \"Brand must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithNullBrand() {
    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.createClothingModel(
                    "Summer Jacket", VALID_DESCRIPTION, null, VALID_CATEGORY, 79.99f));
    assertEquals("400 BAD_REQUEST \"Brand must not be blank\"", e.getMessage());
  }

  // ===== updateClothingModel =====

  @Test
  public void testUpdateValidClothingModel() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float newPrice = 129.99f;
    String newDescription = "Updated description";
    String newBrand = "Adidas";
    Category newCategory = Category.Tops;
    ClothingModel model =
        new ClothingModel(
            id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant variant =
        new ClothingVariant(id, ClothingVariant.Size.M, "blue", VALID_VARIANT_IMAGE, 5, model);
    Item item = new Item(id, 1, 1f, variant);
    when(itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(id))
        .thenReturn(List.of(item));
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingModel res =
        clothingService.updateClothingModel(
            id, newName, newDescription, newBrand, newCategory, newPrice);

    // Assert
    assertNotNull(res);
    assertEquals(newName, res.getName());
    assertEquals(newPrice, res.getPrice());
    assertEquals(newDescription, res.getDescription());
    assertEquals(newBrand, res.getBrand());
    assertEquals(newCategory, res.getCategory());
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
            () ->
                clothingService.updateClothingModel(
                    id, "New Name", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 99.99f));
    assertEquals("404 NOT_FOUND \"Clothing model with ID " + id + " not found\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithBlankName() {
    // Arrange
    String id = "1";
    ClothingModel model =
        new ClothingModel(
            id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.updateClothingModel(
                    id, "  ", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithExistingName() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float price = 79.99f;
    ClothingModel saved =
        new ClothingModel(
            id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);
    ClothingModel exist =
        new ClothingModel("2", newName, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(saved);
    when(clothingModelRepository.findByName(newName)).thenReturn(exist);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.updateClothingModel(
                    id, newName, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, price));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", newName),
        e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithInvalidPrice() {
    // Arrange
    String id = "1";
    ClothingModel model =
        new ClothingModel(
            id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.updateClothingModel(
                    id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, -1f));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithBlankBrand() {
    // Arrange
    String id = "1";
    ClothingModel model =
        new ClothingModel(
            id, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    when(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () ->
                clothingService.updateClothingModel(
                    id, "Summer Jacket", VALID_DESCRIPTION, "  ", VALID_CATEGORY, 79.99f));
    assertEquals("400 BAD_REQUEST \"Brand must not be blank\"", e.getMessage());
  }

  // ===== deleteClothingModel =====

  @Test
  public void testDeleteValidClothingModel() {
    // Arrange
    String modelId = "model1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant variant =
        new ClothingVariant(
            "v1", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
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

  // ===== getVariantsByModel =====

  @Test
  public void testGetVariantsByModel() {
    // Arrange
    String modelId = "model1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant saved =
        new ClothingVariant(
            null,
            ClothingVariant.Size.L,
            "#0000FF",
            VALID_VARIANT_IMAGE,
            5,
            new ClothingModel(
                modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f));
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    // Adding a variant to the model creates the duplicate
    new ClothingVariant(
        "existing", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
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

  // ===== getAllClothingModelsIncludingArchived =====

  @Test
  public void testGetAllClothingModelsIncludingArchived() {
    // Arrange
    ClothingModel active =
        new ClothingModel(
            "1", "Active Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingModel archived =
        new ClothingModel(
            "2", "Archived Coat", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 49.99f);
    archived.setArchived(true);
    when(clothingModelRepository.findAll()).thenReturn(List.of(active, archived));

    // Act
    List<ClothingModel> result = clothingService.getAllClothingModelsIncludingArchived();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  // ===== restoreClothingModel =====

  @Test
  public void testRestoreClothingModelValid() {
    // Arrange
    String modelId = "model1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Winter Coat", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 99.99f);
    model.setArchived(true);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);
    when(clothingModelRepository.findByName("Winter Coat")).thenReturn(model);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingModel result = clothingService.restoreClothingModel(modelId);

    // Assert
    assertNotNull(result);
    assertEquals(false, result.getArchived());
    verify(clothingModelRepository, times(1)).save(model);
  }

  @Test
  public void testRestoreClothingModelNotFound() {
    // Arrange
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.restoreClothingModel(modelId));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  @Test
  public void testRestoreClothingModelNotArchived() {
    // Arrange
    String modelId = "model1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Active Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    // archived is false by default
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.restoreClothingModel(modelId));
    assertEquals(
        "400 BAD_REQUEST \"Clothing model with ID " + modelId + " is not archived\"",
        e.getMessage());
  }

  @Test
  public void testRestoreClothingModelDuplicateName() {
    // Arrange: an archived model and a different live model that already has the same name
    String modelId = "model1";
    String name = "Shared Name";
    ClothingModel archivedModel =
        new ClothingModel(modelId, name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    archivedModel.setArchived(true);
    ClothingModel liveConflict =
        new ClothingModel("model2", name, VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 59.99f);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(archivedModel);
    when(clothingModelRepository.findByName(name)).thenReturn(liveConflict);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.restoreClothingModel(modelId));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", name),
        e.getMessage());
  }

  // ===== getAllVariantsByModelIncludingArchived =====

  @Test
  public void testGetAllVariantsByModelIncludingArchived() {
    // Arrange
    String modelId = "model1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant active =
        new ClothingVariant(
            "v1", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    ClothingVariant archived =
        new ClothingVariant("v2", ClothingVariant.Size.L, "#00FF00", VALID_VARIANT_IMAGE, 0, model);
    archived.setArchived(true);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act
    List<ClothingVariant> result = clothingService.getAllVariantsByModelIncludingArchived(modelId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    // Suppress unused variable warnings — variants are added to model via constructor side-effect
    assertNotNull(active);
    assertNotNull(archived);
  }

  // ===== restoreVariant =====

  @Test
  public void testRestoreVariantValid() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    variant.setArchived(true);
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(variant);
    when(clothingVariantRepository.save(any(ClothingVariant.class)))
        .thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingVariant result = clothingService.restoreVariant(modelId, variantId);

    // Assert
    assertNotNull(result);
    assertEquals(false, result.getArchived());
    verify(clothingVariantRepository, times(1)).save(variant);
  }

  @Test
  public void testRestoreVariantNotFound() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.restoreVariant(modelId, variantId));
    assertEquals(
        "404 NOT_FOUND \"Clothing variant with ID "
            + variantId
            + " not found under model "
            + modelId
            + "\"",
        e.getMessage());
  }

  @Test
  public void testRestoreVariantNotArchived() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant variant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 10, model);
    // archived is false by default
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(variant);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.restoreVariant(modelId, variantId));
    assertEquals(
        "400 BAD_REQUEST \"Clothing variant with ID " + variantId + " is not archived\"",
        e.getMessage());
  }

  @Test
  public void testRestoreVariantDuplicateSizeColor() {
    // Arrange: an archived variant and a different live variant with the same size+color
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model =
        new ClothingModel(
            modelId, "Summer Jacket", VALID_DESCRIPTION, VALID_BRAND, VALID_CATEGORY, 79.99f);
    ClothingVariant archivedVariant =
        new ClothingVariant(
            variantId, ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 0, model);
    archivedVariant.setArchived(true);
    // A live variant with the same size+color already exists in the model
    new ClothingVariant("other", ClothingVariant.Size.M, "#FF0000", VALID_VARIANT_IMAGE, 5, model);
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(archivedVariant);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.restoreVariant(modelId, variantId));
    assertEquals(
        "409 CONFLICT \"A variant with size M and color #FF0000 already exists for this clothing model\"",
        e.getMessage());
  }
}
