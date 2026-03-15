package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface ItemRepository extends ListCrudRepository<Item, String> {
  Item findItemByItemID(String itemID);

  // Find Items linked to a specific model that are not in a cart
  List<Item> findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(String modelId);

  List<Item> findItemsByCustomer(Customer customer);
}
