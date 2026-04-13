package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Order;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface OrderRepository extends ListCrudRepository<Order, String> {
  Order findByOrderID(String orderID);

  List<Order> findByCustomer(Customer customer);

  List<Order> findByEmployee(Employee employee);

  List<Order> findByOrderStatus(Order.OrderStatus orderStatus);

  List<Order> findByCustomerAndOrderStatus(Customer customer, Order.OrderStatus orderStatus);

  List<Order> findByEmployeeAndOrderStatus(Employee employee, Order.OrderStatus orderStatus);
}
