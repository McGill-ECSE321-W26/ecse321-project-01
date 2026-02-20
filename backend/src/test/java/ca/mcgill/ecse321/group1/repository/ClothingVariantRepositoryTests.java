package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingVariant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ClothingVariantRepositoryTests {
    @Autowired
    private ClothingVariantRepository clothingVariantRepository;

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
        ClothingVariant clothingVariantTestFromDb = clothingVariantRepository.findClothingVariantById(id);

        // Assert correct response
        assertNotNull(clothingVariantTestFromDb);
        assertEquals(clothingVariantTestFromDb.getSize(), size);
        assertEquals(clothingVariantTestFromDb.getColor(), color);
        assertEquals(clothingVariantTestFromDb.getStockQuantity(), stockQuantity);
    }
}