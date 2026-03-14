package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, String> {
  Item findItemByItemID(String itemID);

  // Find Items linked to a specific model that are not in a cart
  List<Item> findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(String modelId);
}
