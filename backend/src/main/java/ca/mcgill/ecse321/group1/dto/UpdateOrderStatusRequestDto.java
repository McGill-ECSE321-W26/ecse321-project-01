package ca.mcgill.ecse321.group1.dto;

public class UpdateOrderStatusRequestDto {
  private String orderStatus;

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
}
