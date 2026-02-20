package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer>{

    Order findOrderByOrderID(String id);

}


