package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;

    @AfterEach
    public void clearDatabase() {
        itemRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadItem() {
        // Create person

        int quantity = 20;
        Item itemTest = new Item();
        itemTest.setQuantity(quantity);


        // Save Item
        itemTest = itemRepository.save(itemTest);
        String id = itemTest.getItemID();

        // Read item from database
        Item itemTestFromDb = itemRepository.findItemById(id);

        // Assert correct response
        assertNotNull(itemTestFromDb);
        assertEquals(itemTestFromDb.getQuantity(), quantity);
    }
}