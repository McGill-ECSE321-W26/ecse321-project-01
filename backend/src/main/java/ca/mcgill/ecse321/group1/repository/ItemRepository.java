package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, String> {
    Item findByItemID(String itemID);
}
