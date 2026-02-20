package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    public void clearDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadOrder() {
        // Create person

        Order.OrderStatus status = Order.OrderStatus.Preparing;
        Date orderDate = Date.valueOf("2026-01-01");
        Date deliveryDate = Date.valueOf("2026-01-10");
        String address = "123 fisher lane";
        Order orderTest = new Order();
        orderTest.setOrderStatus(status);
        orderTest.setOrderDate(orderDate);
        orderTest.setDeliveryDate(deliveryDate);
        orderTest.setAddress(address);

        // Save Order
        orderTest = orderRepository.save(orderTest);
        String id = orderTest.getOrderID();

        // Read order from database
        Order orderTestFromDb = orderRepository.findOrderById(id);

        // Assert correct response
        assertNotNull(orderTest);
        assertEquals(orderTestFromDb.getOrderStatus(), status);
        assertEquals(orderTestFromDb.getOrderDate(), orderDate);
        assertEquals(orderTestFromDb.getDeliveryDate(), deliveryDate);
        assertEquals(orderTestFromDb.getAddress(), address);
    }
}