package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;

public class ClothingModelResponseDto {

  private String clothingModelID;
  private String name;
  private String description;
  private String brand;
  private ClothingModel.Category category;
  private float price;
  private int totalStockQuantity;

  public ClothingModelResponseDto() {}

  public ClothingModelResponseDto(ClothingModel model) {
    this.clothingModelID = model.getClothingModelID();
    this.name = model.getName();
    this.description = model.getDescription();
    this.brand = model.getBrand();
    this.category = model.getCategory();
    this.price = model.getPrice();
    this.totalStockQuantity =
        model.getClothingVariants().stream()
            .filter(v -> !v.getArchived())
            .mapToInt(ClothingVariant::getStockQuantity)
            .sum();
  }

  public String getClothingModelID() {
    return clothingModelID;
  }

  public void setClothingModelID(String clothingModelID) {
    this.clothingModelID = clothingModelID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public ClothingModel.Category getCategory() {
    return category;
  }

  public void setCategory(ClothingModel.Category category) {
    this.category = category;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public int getTotalStockQuantity() {
    return totalStockQuantity;
  }

  public void setTotalStockQuantity(int totalStockQuantity) {
    this.totalStockQuantity = totalStockQuantity;
  }
}
