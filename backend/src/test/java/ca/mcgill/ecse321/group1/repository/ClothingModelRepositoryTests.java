package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ClothingModelRepositoryTests {
    @Autowired
    private ClothingModelRepository clothingModelRepository;

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
        ClothingModel clothingModelTestFromDb = clothingModelRepository.findClothingModelById(id);

        // Assert correct response
        assertNotNull(clothingModelTestFromDb);
        assertEquals(clothingModelTestFromDb.getName(), name);
        assertEquals(clothingModelTestFromDb.getPrice(), price);
    }
}