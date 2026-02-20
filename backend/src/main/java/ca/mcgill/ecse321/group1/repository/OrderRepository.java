package ca.mcgill.ecse321.group1.repository;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.group1.model.Order;

public interface OrderRepository extends CrudRepository<Order, Integer>{

    Order findOrderById(String id);

}


