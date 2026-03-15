package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Item;

public class ItemDTO {
  private String itemID;
  private float price;
  private int quantity;
  private String clothingVariantID;
  private String customerID;

  @SuppressWarnings("unused")
  private ItemDTO() {}

  public ItemDTO(Item item) {
    this.itemID = item.getItemID();
    this.price = item.getPrice();
    this.quantity = item.getQuantity();
    this.clothingVariantID =
        (item.getClothingVariant() != null)
            ? item.getClothingVariant().getClothingVariantID()
            : null;
    this.customerID = (item.getCustomer() != null) ? item.getCustomer().getRoleID() : null;
  }

  public String getItemID() {
    return this.itemID;
  }

  public void setItemID(String newItemID) {
    this.itemID = newItemID;
  }

  public float getPrice() {
    return this.price;
  }

  public void setPrice(float newPrice) {
    this.price = newPrice;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public void setQuantity(int newQuantity) {
    this.quantity = newQuantity;
  }

  public String getClothingVariantID() {
    return this.clothingVariantID;
  }

  public void setClothingVariantID(String newClothingVariantID) {
    this.clothingVariantID = newClothingVariantID;
  }

  public String getCustomerID() {
    return this.customerID;
  }

  public void setCustomerID(String newCustomerID) {
    this.customerID = newCustomerID;
  }
}
