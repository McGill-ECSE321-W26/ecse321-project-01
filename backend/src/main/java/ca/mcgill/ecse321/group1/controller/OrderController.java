package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/order")
@RestController
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("")
  public Iterable<Order> getOrders() {
    return orderService.getOrders();
  }

  @GetMapping("{orderID}")
  public Order getOrderByID(@PathVariable String orderID) {
    return orderService.getOrderByID(orderID);
  }

  @GetMapping("customer/{customerID}")
  public Iterable<Order> getOrdersByCustomerID(@PathVariable String customerID) {
    return orderService.getOrdersByCustomerID(customerID);
  }

  @GetMapping("status/{orderStatus}")
  public Iterable<Order> getOrdersByOrderStatus(@PathVariable String orderStatus) {
    return orderService.getOrdersByOrderStatus(orderStatus);
  }
}
