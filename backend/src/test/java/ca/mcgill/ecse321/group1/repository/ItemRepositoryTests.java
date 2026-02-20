package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemRepositoryTests {

    @Autowired private ItemRepository itemRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        itemRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadItem() {
        // Create and save item
        Item item = new Item();
        item.setQuantity(3);
        itemRepository.save(item);

        String itemID = item.getItemID();

        // Read item
        Item itemFromDb = itemRepository.findByItemID(itemID);

        // Assertions
        assertNotNull(itemFromDb);
        assertEquals(itemID, itemFromDb.getItemID());
        assertEquals(3, itemFromDb.getQuantity());
    }

    @Test
    public void testFindItemByInvalidID() {
        Item itemFromDb = itemRepository.findByItemID("nonexistent-id");
        assertNull(itemFromDb);
    }

    @Test
    public void testUpdateItem() {
        // Create and save item
        Item item = new Item();
        item.setQuantity(1);
        itemRepository.save(item);

        String itemID = item.getItemID();

        // Update quantity
        item.setQuantity(5);
        itemRepository.save(item);

        // Read item
        Item itemFromDb = itemRepository.findByItemID(itemID);

        // Assertions
        assertNotNull(itemFromDb);
        assertEquals(5, itemFromDb.getQuantity());
    }

    @Test
    public void testDeleteItem() {
        // Create and save item
        Item item = new Item();
        item.setQuantity(2);
        itemRepository.save(item);

        String itemID = item.getItemID();

        // Delete item
        itemRepository.delete(item);

        // Read item
        Item itemFromDb = itemRepository.findByItemID(itemID);

        // Assertions
        assertNull(itemFromDb);
    }
}
