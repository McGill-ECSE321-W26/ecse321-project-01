package ca.mcgill.ecse321.group1.service;

import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;

@Service
public class CartService {
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    public CartService(CustomerRepository customerRepository, ItemRepository itemRepository) {
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
    }
}
