package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingModel;

public class ClothingModelCreateRequestDto {

  private String name;
  private String description;
  private String brand;
  private ClothingModel.Category category;
  private float price;

  public ClothingModelCreateRequestDto() {}

  public ClothingModelCreateRequestDto(
      String name, String description, String brand, ClothingModel.Category category, float price) {
    this.name = name;
    this.description = description;
    this.brand = brand;
    this.category = category;
    this.price = price;
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
}
