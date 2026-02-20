package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.model.ClothingVariant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ClothingVariantRepositoryTests {
  @Autowired private ClothingVariantRepository clothingVariantRepository;

  @AfterEach
  public void clearDatabase() {
    clothingVariantRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadClothingVariant() {
    // Create clothing variant

    ClothingVariant.Size size = ClothingVariant.Size.L;
    String color = "White";
    int stockQuantity = 4;
    ClothingVariant clothingVariantTest = new ClothingVariant();
    clothingVariantTest.setSize(size);
    clothingVariantTest.setColor(color);
    clothingVariantTest.setStockQuantity(stockQuantity);

    // Save Clothing Variant
    clothingVariantTest = clothingVariantRepository.save(clothingVariantTest);
    String id = clothingVariantTest.getClothingVariantID();

    // Read clothing variant from database
    ClothingVariant clothingVariantTestFromDb =
        clothingVariantRepository.findClothingVariantByClothingVariantID(id);

    // Assert correct response
    assertNotNull(clothingVariantTestFromDb);
    assertEquals(clothingVariantTestFromDb.getSize(), size);
    assertEquals(clothingVariantTestFromDb.getColor(), color);
    assertEquals(clothingVariantTestFromDb.getStockQuantity(), stockQuantity);
  }

  @Test
  public void testFindClothingVariantByInvalidId() {
    ClothingVariant result =
        clothingVariantRepository.findClothingVariantByClothingVariantID("nonexistent-id");
    assertNull(result);
  }

  @Test
  public void testUpdateClothingVariant() {
    // Create and save
    ClothingVariant variant = new ClothingVariant();
    variant.setSize(ClothingVariant.Size.S);
    variant.setColor("Red");
    variant.setStockQuantity(10);
    variant = clothingVariantRepository.save(variant);
    String id = variant.getClothingVariantID();
    // Update fields
    variant.setSize(ClothingVariant.Size.XL);
    variant.setColor("Blue");
    variant.setStockQuantity(25);
    clothingVariantRepository.save(variant);

    // Read back and assert
    ClothingVariant updatedVariant =
        clothingVariantRepository.findClothingVariantByClothingVariantID(id);
    assertNotNull(updatedVariant);
    assertEquals(ClothingVariant.Size.XL, updatedVariant.getSize());
    assertEquals("Blue", updatedVariant.getColor());
    assertEquals(25, updatedVariant.getStockQuantity());
    // basically ensuresthat all the updates values are done and not outdated once queried from DB.
  }

  @Test
  public void testDeleteClothingVariant() {
    // Create and save
    ClothingVariant variant = new ClothingVariant();
    variant.setSize(ClothingVariant.Size.M);
    variant.setColor("GreenFN");
    variant.setStockQuantity(5);
    variant = clothingVariantRepository.save(variant);
    String id = variant.getClothingVariantID();

    // Delete
    clothingVariantRepository.delete(variant);

    // Assert it no longer exists
    ClothingVariant deletedVariant =
        clothingVariantRepository.findClothingVariantByClothingVariantID(id);
    assertNull(deletedVariant);
  }

  @Test
  public void testFindAllClothingVariants() {
    // Create and save multiple variants
    ClothingVariant variant1 = new ClothingVariant();
    variant1.setSize(ClothingVariant.Size.S);
    variant1.setColor("Black");
    variant1.setStockQuantity(3);

    ClothingVariant variant2 = new ClothingVariant();
    variant2.setSize(ClothingVariant.Size.M);
    variant2.setColor("White");
    variant2.setStockQuantity(7);

    ClothingVariant variant3 = new ClothingVariant();
    variant3.setSize(ClothingVariant.Size.XL);
    variant3.setColor("Yellow");
    variant3.setStockQuantity(12);

    clothingVariantRepository.save(variant1);
    clothingVariantRepository.save(variant2);
    clothingVariantRepository.save(variant3);

    // Fetch all
    List<ClothingVariant> allClothingVariants =
        (List<ClothingVariant>) clothingVariantRepository.findAll();
    assertNotNull(allClothingVariants);
    assertEquals(3, allClothingVariants.size());
  }

  @Test
  public void testPersistClothingVariantWithZeroStock() {
    // Edge case: stock quantity of 0
    ClothingVariant variant = new ClothingVariant();
    variant.setSize(ClothingVariant.Size.M);
    variant.setColor("Purple");
    variant.setStockQuantity(0);
    variant = clothingVariantRepository.save(variant);
    String id = variant.getClothingVariantID();

    ClothingVariant fromDb = clothingVariantRepository.findClothingVariantByClothingVariantID(id);
    assertNotNull(fromDb);
    assertEquals(0, fromDb.getStockQuantity());
  }

  @Test
  public void testAllSizeEnumValuesPersist() {
    // Ensure every Size enum variant is stored and retrieved correctly...
    for (ClothingVariant.Size size : ClothingVariant.Size.values()) {
      ClothingVariant variant = new ClothingVariant();
      variant.setSize(size);
      variant.setColor("Gray");
      variant.setStockQuantity(1);
      variant = clothingVariantRepository.save(variant);

      ClothingVariant fromDb =
          clothingVariantRepository.findClothingVariantByClothingVariantID(
              variant.getClothingVariantID());
      assertNotNull(fromDb);
      assertEquals(size, fromDb.getSize());
    }
  }

  @Test
  public void testAutoGeneratedIdIsUnique() {
    ClothingVariant variant1 = new ClothingVariant();
    variant1.setSize(ClothingVariant.Size.S);
    variant1.setColor("Orange");
    variant1.setStockQuantity(2);

    ClothingVariant variant2 = new ClothingVariant();
    variant2.setSize(ClothingVariant.Size.L);
    variant2.setColor("Pink");
    variant2.setStockQuantity(6);
    variant1 = clothingVariantRepository.save(variant1);
    variant2 = clothingVariantRepository.save(variant2);

    // if they have non-unique IDs in the database, they would be equal, and so this test will fail.
    assertNotNull(variant1.getClothingVariantID());
    assertNotNull(variant2.getClothingVariantID());
    assertNotEquals(variant1.getClothingVariantID(), variant2.getClothingVariantID());
  }
}
