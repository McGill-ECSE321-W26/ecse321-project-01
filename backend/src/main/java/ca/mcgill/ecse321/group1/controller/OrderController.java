package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.*;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/order")
@RestController
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("create")
  public OrderDTO createOrder(@RequestBody CreateOrderDTO dto) {
    Order order =
        orderService.createOrder(
            dto.getCustomerID(), dto.getDeliveryDate(), dto.getUsedLoyaltyPoints());
    return new OrderDTO(order);
  }

  @PutMapping("{orderID}/assign-employee")
  public OrderDTO assignOrderToEmployee(
      @PathVariable String orderID, @RequestBody AssignOrderToEmployeeDTO dto) {
    Order order = orderService.assignOrderToEmployee(orderID, dto.getEmployeeID());
    return new OrderDTO(order);
  }

  @PutMapping("{orderID}/delivery-date")
  public OrderDTO updateOrderDeliveryDate(
      @PathVariable String orderID, @RequestBody UpdateOrderDeliveryDateDTO dto) {
    Order order = orderService.updateOrderDeliveryDate(orderID, dto.getDeliveryDate());
    return new OrderDTO(order);
  }

  @PutMapping("{orderID}/status")
  public OrderDTO updateOrderStatus(
      @PathVariable String orderID, @RequestBody UpdateOrderStatusDTO dto) {
    Order order = orderService.updateOrderStatus(orderID, dto.getOrderStatus());
    return new OrderDTO(order);
  }

  @GetMapping("")
  public List<OrderDTO> getOrders() {
    List<Order> orders = orderService.getOrders();

    // Convert list of orders to list of DTO
    List<OrderDTO> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderDTO(order));
    }

    return ordersDTO;
  }

  @GetMapping("{orderID}")
  public OrderDTO getOrderByID(@PathVariable String orderID) {
    Order order = orderService.getOrderByID(orderID);
    return new OrderDTO(order);
  }

  @GetMapping("customer/{customerID}")
  public List<OrderDTO> getOrdersByCustomerID(@PathVariable String customerID) {
    List<Order> orders = orderService.getOrdersByCustomerID(customerID);

    // Convert list of orders to list of DTO
    List<OrderDTO> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderDTO(order));
    }

    return ordersDTO;
  }

  @GetMapping("status/{orderStatus}")
  public List<OrderDTO> getOrdersByOrderStatus(@PathVariable String orderStatus) {
    List<Order> orders = orderService.getOrdersByOrderStatus(orderStatus);

    // Convert list of orders to list of DTO
    List<OrderDTO> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderDTO(order));
    }

    return ordersDTO;
  }
}
