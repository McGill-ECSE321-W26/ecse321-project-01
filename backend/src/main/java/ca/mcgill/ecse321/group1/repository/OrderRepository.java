package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Order;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderRepository extends ListCrudRepository<Order, Integer> {
  Order findOrderByOrderID(String id);

  List<Order> findByCustomer(Customer customer);

  List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
}
