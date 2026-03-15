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

  // ===== getAllClothingModels =====

  @Test
  public void testGetAllClothingModels() {
    // Arrange
    ClothingModel model1 = new ClothingModel(null, "Summer Jacket", 79.99f);
    ClothingModel model2 = new ClothingModel(null, "Winter Coat", 149.99f);
    when(clothingModelRepository.findAll()).thenReturn(List.of(model1, model2));

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
    ClothingModel model = new ClothingModel(modelId, name, price);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act
    ClothingModel result = clothingService.getClothingModel(modelId);

    // Assert
    assertNotNull(result);
    assertEquals("Summer Jacket", result.getName());
    assertEquals(price, result.getPrice());
  }

  @Test
  public void testGetClothingModelByInvalidId() {
    // Assert
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(null);
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
    ClothingModel saved = new ClothingModel(null, name, price);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenReturn(saved);

    // Act
    ClothingModel result = clothingService.createClothingModel(name, price);

    // Assert
    assertNotNull(result);
    assertEquals(name, result.getName());
    assertEquals(price, result.getPrice());
    verify(clothingModelRepository, times(1)).save(any(ClothingModel.class));
  }

  @Test
  public void testCreateClothingModelWithInvalidName() {
    // Arrange
    float price = 79.99f;
    // ACT & ASSERT
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.createClothingModel(null, price));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithExistingName() {
    // Arrange
    String name = "Summer Jacket";
    float price = 79.99f;
    ClothingModel saved = new ClothingModel(null, name, price);
    when(clothingModelRepository.findByName(name)).thenReturn(saved);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.createClothingModel(name, price));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", name),
        e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithBlankName() {
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.createClothingModel("  ", 100f));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateClothingModelWithInvalidPrice() {
    // Arrange
    float price = -1f;
    String name = "Summer Jacket";

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> clothingService.createClothingModel(name, price));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  // ===== updateClothingModel =====

  @Test
  public void testUpdateValidClothingModel() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float newPrice = 129.99f;
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f);
    ClothingVariant variant = new ClothingVariant(id, ClothingVariant.Size.M, "blue", 5, model);
    Item item = new Item(id, 1, 1f, variant);
    when(itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(id))
        .thenReturn(List.of(item));
    when(clothingModelRepository.findByClothingModelID(id)).thenReturn(model);
    when(clothingModelRepository.save(any(ClothingModel.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ClothingModel res = clothingService.updateClothingModel(id, newName, newPrice);

    // Assert
    assertNotNull(res);
    assertEquals(newName, res.getName());
    assertEquals(newPrice, res.getPrice());
    // HOW DO I EVEN ASSERT FOR ITEM PRICE??
    verify(itemRepository, times(1)).saveAll(any());
    verify(clothingModelRepository, times(1)).save(any(ClothingModel.class));
  }

  @Test
  public void testUpdateClothingModelWithInvalidId() {
    // Arrange
    String id = "badId";
    when(clothingModelRepository.findByClothingModelID(id)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "New Name", 99.99f));
    assertEquals("404 NOT_FOUND \"Clothing model with ID " + id + " not found\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithBlankName() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f);
    when(clothingModelRepository.findByClothingModelID(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "  ", 79.99f));
    assertEquals("400 BAD_REQUEST \"Name must not be blank\"", e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithExistingName() {
    // Arrange
    String id = "1";
    String newName = "Winter Coat";
    float price = 79.99f;
    ClothingModel saved = new ClothingModel(id, "Summer Jacket", price);
    ClothingModel exist = new ClothingModel("2", newName, price);
    when(clothingModelRepository.findByClothingModelID(id)).thenReturn(saved);
    when(clothingModelRepository.findByName(newName)).thenReturn(exist);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, newName, price));
    assertEquals(
        String.format("409 CONFLICT \"A clothing model with name '%s' already exists\"", newName),
        e.getMessage());
  }

  @Test
  public void testUpdateClothingModelWithInvalidPrice() {
    // Arrange
    String id = "1";
    ClothingModel model = new ClothingModel(id, "Summer Jacket", 79.99f);
    when(clothingModelRepository.findByClothingModelID(id)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateClothingModel(id, "Summer Jacket", -1f));
    assertEquals("400 BAD_REQUEST \"Price must be > 0\"", e.getMessage());
  }

  // ===== deleteClothingModel =====

  @Test
  public void testDeleteValidClothingModel() {
    // Arrange
    String modelId = "model1";
    when(clothingModelRepository.deleteByClothingModelID(modelId)).thenReturn(1);

    // Act
    clothingService.deleteClothingModel(modelId);

    // Assert
    verify(clothingModelRepository, times(1)).deleteByClothingModelID(modelId);
  }

  @Test
  public void testDeleteClothingModelWithInvalidId() {
    String modelId = "badModel";
    when(clothingModelRepository.deleteByClothingModelID(modelId)).thenReturn(0);
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
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    ClothingVariant variant =
        new ClothingVariant(variantId, ClothingVariant.Size.M, "Red", 10, model);
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(variant);

    // Act
    ClothingVariant result = clothingService.getVariant(modelId, variantId);

    // Assert
    assertNotNull(result);
    assertEquals(variantId, result.getClothingVariantID());
    assertEquals(ClothingVariant.Size.M, result.getSize());
    assertEquals("Red", result.getColor());
  }

  @Test
  public void testGetVariantByInvalidId() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(null);

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
    ClothingModel model = new ClothingModel(correctModelId, "Summer Jacket", 79.99f);
    new ClothingVariant(variantId, ClothingVariant.Size.M, "Red", 10, model);
    when(clothingVariantRepository.findByClothingVariantID(variantId))
        .thenReturn(new ClothingVariant(variantId, ClothingVariant.Size.M, "Red", 10, model));

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
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    ClothingVariant variant =
        new ClothingVariant("variant1", ClothingVariant.Size.M, "Red", 10, model);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

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
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(null);

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
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    ClothingVariant saved =
        new ClothingVariant(
            null,
            ClothingVariant.Size.L,
            "Blue",
            5,
            new ClothingModel(modelId, "Summer Jacket", 79.99f));
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);
    when(clothingVariantRepository.save(any(ClothingVariant.class))).thenReturn(saved);

    // Act
    ClothingVariant result =
        clothingService.createVariant(modelId, ClothingVariant.Size.L, "Blue", 5);

    // Assert
    assertNotNull(result);
    assertEquals(ClothingVariant.Size.L, result.getSize());
    assertEquals("Blue", result.getColor());
    assertEquals(5, result.getStockQuantity());
    verify(clothingVariantRepository, times(1)).save(any(ClothingVariant.class));
  }

  @Test
  public void testCreateVariantWithInvalidModel() {
    // Arrange
    String modelId = "badModel";
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, ClothingVariant.Size.M, "Red", 5));
    assertEquals(
        "404 NOT_FOUND \"Clothing model with ID " + modelId + " not found\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithNullSize() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, null, "Red", 5));
    assertEquals("400 BAD_REQUEST \"Size must be specified\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithBlankColor() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, ClothingVariant.Size.M, "  ", 5));
    assertEquals("400 BAD_REQUEST \"Color must not be blank\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithNegativeStock() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, ClothingVariant.Size.M, "Red", -1));
    assertEquals("400 BAD_REQUEST \"Stock quantity must be >= 0\"", e.getMessage());
  }

  @Test
  public void testCreateVariantWithDuplicateSizeAndColor() {
    // Arrange
    String modelId = "model1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    // Adding a variant to the model creates the duplicate
    new ClothingVariant("existing", ClothingVariant.Size.M, "Red", 10, model);
    when(clothingModelRepository.findByClothingModelID(modelId)).thenReturn(model);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.createVariant(modelId, ClothingVariant.Size.M, "Red", 5));
    assertEquals(
        "409 CONFLICT \"A variant with size M and color Red already exists for this clothing model\"",
        e.getMessage());
  }

  // ===== deleteVariant =====

  @Test
  public void testDeleteValidVariant() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    when(clothingVariantRepository.deleteByClothingVariantID(variantId)).thenReturn(1);

    // Act
    clothingService.deleteVariant(modelId, variantId);

    // Assert
    verify(clothingVariantRepository, times(1)).deleteByClothingVariantID(variantId);
  }

  @Test
  public void testDeleteVariantWithInvalidId() {
    // Arrange
    String modelId = "model1";
    String variantId = "badVariant";

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

  // ===== updateVariant =====

  @Test
  public void testUpdateValidVariant() {
    // Arrange
    String modelId = "model1";
    String variantId = "variant1";
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    ClothingVariant variant =
        new ClothingVariant(variantId, ClothingVariant.Size.M, "Red", 10, model);
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(variant);
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
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(null);

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
    ClothingModel model = new ClothingModel(modelId, "Summer Jacket", 79.99f);
    ClothingVariant variant =
        new ClothingVariant(variantId, ClothingVariant.Size.M, "Red", 10, model);
    when(clothingVariantRepository.findByClothingVariantID(variantId)).thenReturn(variant);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> clothingService.updateVariantStock(modelId, variantId, -1));
    assertEquals("400 BAD_REQUEST \"Stock quantity must be >= 0\"", e.getMessage());
  }
}
