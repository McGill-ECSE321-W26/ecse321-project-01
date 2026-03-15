package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.*;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.service.OrderService;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/orders")
@RestController
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponseDto createOrder(@RequestBody CreateOrderRequestDto dto) {
    Order order =
        orderService.createOrder(
            dto.getCustomerID(), dto.getDeliveryDate(), dto.getUsedLoyaltyPoints());
    return new OrderResponseDto(order);
  }

  @PatchMapping("/{orderID}/employee")
  public OrderResponseDto assignOrderToEmployee(
      @PathVariable String orderID, @RequestBody AssignOrderToEmployeeRequestDto dto) {
    Order order = orderService.assignOrderToEmployee(orderID, dto.getEmployeeID());
    return new OrderResponseDto(order);
  }

  @PatchMapping("/{orderID}/delivery-date")
  public OrderResponseDto updateOrderDeliveryDate(
      @PathVariable String orderID, @RequestBody UpdateOrderDeliveryDateRequestDto dto) {
    Order order = orderService.updateOrderDeliveryDate(orderID, dto.getDeliveryDate());
    return new OrderResponseDto(order);
  }

  @PatchMapping("/{orderID}/status")
  public OrderResponseDto updateOrderStatus(
      @PathVariable String orderID, @RequestBody UpdateOrderStatusRequestDto dto) {
    Order order = orderService.updateOrderStatus(orderID, dto.getOrderStatus());
    return new OrderResponseDto(order);
  }

  @GetMapping()
  public List<OrderResponseDto> getOrders(
      @RequestParam(required = false) String customerID,
      @RequestParam(required = false) String orderStatus) {
    List<Order> orders;
    if (customerID != null && orderStatus != null) {
      orders = orderService.getOrdersByCustomerIDAndStatus(customerID, orderStatus);
    } else if (customerID != null) {
      orders = orderService.getOrdersByCustomerID(customerID);
    } else if (orderStatus != null) {
      orders = orderService.getOrdersByOrderStatus(orderStatus);
    } else {
      orders = orderService.getOrders();
    }

    List<OrderResponseDto> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderResponseDto(order));
    }
    return ordersDTO;
  }

  @GetMapping("/{orderID}")
  public OrderResponseDto getOrderByID(@PathVariable String orderID) {
    Order order = orderService.getOrderByID(orderID);
    return new OrderResponseDto(order);
  }
}
