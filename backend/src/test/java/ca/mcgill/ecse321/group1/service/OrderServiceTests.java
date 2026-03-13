package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;


import ca.mcgill.ecse321.group1.model.Item;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.group1.repository.OrderRepository;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;

@SpringBootTest
public class OrderServiceTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService orderService;

    @SuppressWarnings("null")
    @Test
    public void testCreateValidOrder() {
        //CREATE CUSTOMER
        String customerId = "Bob";
        Customer customer = new Customer();
        customer.setRoleID(customerId);
        //CREATE AN ITEM
        String itemId = "1";
        Item item = new Item();
        item.setItemID(itemId);

        //Mock save the customer id
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

    }
}
