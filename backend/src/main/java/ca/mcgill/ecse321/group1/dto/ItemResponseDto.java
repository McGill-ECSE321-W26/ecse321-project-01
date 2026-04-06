package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Item;

public class ItemResponseDto {
  private String itemID;
  private float price;
  private int quantity;
  private String clothingVariantID;
  private String customerID;
  private boolean variantArchived;
  private String modelID;
  private String modelName;
  private String variantSize;
  private String variantColor;
  private String variantImagePath;

  @SuppressWarnings("unused")
  private ItemResponseDto() {}

  public ItemResponseDto(Item item) {
    this.itemID = item.getItemID();
    this.price = item.getPrice();
    this.quantity = item.getQuantity();
    this.customerID = (item.getCustomer() != null) ? item.getCustomer().getRoleID() : null;

    var variant = item.getClothingVariant();
    if (variant != null) {
      this.clothingVariantID = variant.getClothingVariantID();
      this.variantArchived = variant.getArchived();
      this.variantSize = variant.getSize() != null ? variant.getSize().toString() : null;
      this.variantColor = variant.getColor();
      this.variantImagePath = variant.getImagePath();
      var model = variant.getModel();
      if (model != null) {
        this.modelName = model.getName();
        this.modelID = model.getClothingModelID();
      }
    }
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

  public boolean isVariantArchived() {
    return this.variantArchived;
  }

  public String getModelID() {
    return this.modelID;
  }

  public String getModelName() {
    return this.modelName;
  }

  public String getVariantSize() {
    return this.variantSize;
  }

  public String getVariantColor() {
    return this.variantColor;
  }

  public String getVariantImagePath() {
    return this.variantImagePath;
  }
}
