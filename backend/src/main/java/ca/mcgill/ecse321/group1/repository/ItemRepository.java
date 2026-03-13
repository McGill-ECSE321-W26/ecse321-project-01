package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, String> {
  Item findItemByItemID(String itemID);

  List<Item> findItemsByCustomer(Customer customer);
}
