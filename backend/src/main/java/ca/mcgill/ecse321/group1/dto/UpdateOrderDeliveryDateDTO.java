package ca.mcgill.ecse321.group1.dto;

import java.sql.Date;

public class UpdateOrderDeliveryDateDTO {
  private Date deliveryDate;

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }
}
