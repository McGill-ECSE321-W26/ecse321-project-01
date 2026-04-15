package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.ItemResponseDto;
import ca.mcgill.ecse321.group1.dto.OrderCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.OrderRequestUpdateDto;
import ca.mcgill.ecse321.group1.dto.OrderResponseDto;
import ca.mcgill.ecse321.group1.model.Item;
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponseDto createOrder(@RequestBody OrderCreateRequestDto dto) {
    Order order =
        orderService.createOrder(
            dto.getCustomerID(), dto.getDeliveryDate(), dto.getUsedLoyaltyPoints());
    return new OrderResponseDto(order);
  }

  @PatchMapping("/{orderID}")
  public OrderResponseDto updateOrder(
      @PathVariable String orderID, @RequestBody OrderRequestUpdateDto dto) {
    Order order =
        orderService.updateOrder(
            orderID, dto.getEmployeeID(), dto.getDeliveryDate(), dto.getOrderStatus());
    return new OrderResponseDto(order);
  }

  @GetMapping
  public List<OrderResponseDto> getOrders() {
    List<Order> orders = orderService.getOrders();
    List<OrderResponseDto> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderResponseDto(order));
    }
    return ordersDTO;
  }

  @GetMapping("/customer/{customerID}")
  public List<OrderResponseDto> getOrdersByCustomerID(@PathVariable String customerID) {
    List<Order> orders = orderService.getOrdersByCustomerID(customerID);
    List<OrderResponseDto> ordersDTO = new ArrayList<>();
    for (Order order : orders) {
      ordersDTO.add(new OrderResponseDto(order));
    }
    return ordersDTO;
  }

  @GetMapping("/employee/{employeeID}")
  public List<OrderResponseDto> getOrdersByEmployeeID(@PathVariable String employeeID) {
    List<Order> orders = orderService.getOrdersByEmployeeID(employeeID);
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

  @GetMapping("/{orderID}/items")
  public List<ItemResponseDto> getOrderItems(@PathVariable String orderID) {
    List<Item> items = orderService.getOrderItems(orderID);
    List<ItemResponseDto> itemsDTO = new ArrayList<>();
    for (Item item : items) {
      itemsDTO.add(new ItemResponseDto(item));
    }
    return itemsDTO;
  }
}
