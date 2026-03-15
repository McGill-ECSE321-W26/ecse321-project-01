package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface ItemRepository extends ListCrudRepository<Item, String> {
  Item findItemByItemID(String itemID);

  List<Item> findItemsByCustomer(Customer customer);

  int deleteByRoleID(String roleID);
}
