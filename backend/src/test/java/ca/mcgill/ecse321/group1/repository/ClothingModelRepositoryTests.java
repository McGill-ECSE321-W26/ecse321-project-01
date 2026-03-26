package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ClothingModelRepositoryTests {
  @Autowired private ClothingModelRepository clothingModelRepository;

  @AfterEach
  public void clearDatabase() {
    clothingModelRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadClothingModel() {
    // Create person

    String name = "Supreme";
    float price = 699;
    ClothingModel clothingModelTest = new ClothingModel();
    clothingModelTest.setName(name);
    clothingModelTest.setPrice(price);

    // Save ClothingModel
    clothingModelTest = clothingModelRepository.save(clothingModelTest);
    String id = clothingModelTest.getClothingModelID();

    // Read ClothingModel from database
    ClothingModel clothingModelTestFromDb =
        clothingModelRepository.findByClothingModelIDAndArchivedFalse(id);

    // Assert correct response
    assertNotNull(clothingModelTestFromDb);
    assertEquals(name, clothingModelTestFromDb.getName());
    assertEquals(price, clothingModelTestFromDb.getPrice());
  }

  @Test
  public void testFindClothingModelByInvalidId() {
    // Attempt to find a model with a non-existent ID
    ClothingModel result =
        clothingModelRepository.findByClothingModelIDAndArchivedFalse("nonexistent");

    // Assert nothing is returned
    assertNull(result);
  }

  @Test
  public void testUpdateClothingModel() {
    // Create and save
    ClothingModel model = new ClothingModel();
    model.setName("Gucci");
    model.setPrice(12000f);
    model = clothingModelRepository.save(model);
    // Update fields
    model.setName("Gucci Updated");
    model.setPrice(9999f);
    clothingModelRepository.save(model);

    // Read back and assert updated values
    String id = model.getClothingModelID();
    ClothingModel updatedModel = clothingModelRepository.findByClothingModelIDAndArchivedFalse(id);
    assertNotNull(updatedModel);

    assertEquals("Gucci Updated", updatedModel.getName());
    assertEquals(9999f, updatedModel.getPrice());
  }

  @Test
  public void testDeleteClothingModel() {
    // Create and save
    ClothingModel model = new ClothingModel();
    model.setName("Versace!");
    model.setPrice(850f);
    model = clothingModelRepository.save(model);
    String id = model.getClothingModelID();

    // Delete
    clothingModelRepository.delete(model);

    // Assert it no longer exists
    ClothingModel deletedModel = clothingModelRepository.findByClothingModelIDAndArchivedFalse(id);
    assertNull(deletedModel);
  }

  @Test
  public void testFindAllClothingModels() {
    // Create and save multiple models
    ClothingModel model1 = new ClothingModel();
    model1.setName("Nike");
    model1.setPrice(120f);

    ClothingModel model2 = new ClothingModel();
    model2.setName("Adidas");
    model2.setPrice(95f);

    ClothingModel model3 = new ClothingModel();
    model3.setName("Puma");
    model3.setPrice(80f);

    clothingModelRepository.save(model1);
    clothingModelRepository.save(model2);
    clothingModelRepository.save(model3);

    // Fetch all
    List<ClothingModel> allModels = (List<ClothingModel>) clothingModelRepository.findAll();
    // Assert all three are present
    assertNotNull(allModels);
    assertEquals(3, allModels.size());
  }

  @Test
  public void testPersistClothingModelWithZeroPrice() {
    // Edge case: price of 0
    ClothingModel model = new ClothingModel();
    model.setName("FreeBrand");
    model.setPrice(0f);
    model = clothingModelRepository.save(model);
    String id = model.getClothingModelID();

    ClothingModel fromDb = clothingModelRepository.findByClothingModelIDAndArchivedFalse(id);

    assertNotNull(fromDb);
    assertEquals("FreeBrand", fromDb.getName());
    assertEquals(0f, fromDb.getPrice());
  }

  @Test
  public void testPersistClothingModelWithLargePrice() {
    // Edge case: very large price
    ClothingModel model = new ClothingModel();
    model.setName("LuxuryBrand");
    model.setPrice(Float.MAX_VALUE);
    model = clothingModelRepository.save(model);
    String id = model.getClothingModelID();

    ClothingModel fromDb = clothingModelRepository.findByClothingModelIDAndArchivedFalse(id);

    assertNotNull(fromDb);
    assertEquals("LuxuryBrand", fromDb.getName());
    assertEquals(Float.MAX_VALUE, fromDb.getPrice());
  }

  @Test
  public void testFindByArchivedFalseExcludesArchived() {
    ClothingModel model1 = new ClothingModel();
    model1.setName("Active");
    model1.setPrice(100f);
    clothingModelRepository.save(model1);

    ClothingModel model2 = new ClothingModel();
    model2.setName("Archived");
    model2.setPrice(200f);
    model2.setArchived(true);
    clothingModelRepository.save(model2);

    List<ClothingModel> result = clothingModelRepository.findByArchivedFalse();
    assertEquals(1, result.size());
    assertEquals("Active", result.get(0).getName());
  }

  @Test
  public void testFindByIdAndArchivedFalseReturnsNullForArchived() {
    ClothingModel model = new ClothingModel();
    model.setName("ArchivedModel");
    model.setPrice(100f);
    model.setArchived(true);
    model = clothingModelRepository.save(model);
    String id = model.getClothingModelID();

    assertNull(clothingModelRepository.findByClothingModelIDAndArchivedFalse(id));
  }

  @Test
  public void testFindByNameAndArchivedFalseReturnsNullForArchived() {
    ClothingModel model = new ClothingModel();
    model.setName("ArchivedName");
    model.setPrice(100f);
    model.setArchived(true);
    clothingModelRepository.save(model);

    assertNull(clothingModelRepository.findByNameAndArchivedFalse("ArchivedName"));
  }

  @Test
  public void testAutoGeneratedIdIsUnique() {
    // Two separate models should receive different auto-generated IDs
    ClothingModel model1 = new ClothingModel();
    model1.setName("BrandA");
    model1.setPrice(100f);

    ClothingModel model2 = new ClothingModel();
    model2.setName("BrandB");
    model2.setPrice(200f);

    model1 = clothingModelRepository.save(model1);
    model2 = clothingModelRepository.save(model2);

    assertNotNull(model1.getClothingModelID());
    assertNotNull(model2.getClothingModelID());
    assertNotEquals(model1.getClothingModelID(), model2.getClothingModelID());
  }
}
