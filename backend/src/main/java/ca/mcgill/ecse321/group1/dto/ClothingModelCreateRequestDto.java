package ca.mcgill.ecse321.group1.dto;

public class ClothingModelCreateRequestDto {

  private String name;
  private float price;

  public ClothingModelCreateRequestDto() {}

  public ClothingModelCreateRequestDto(String name, float price) {
    this.name = name;
    this.price = price;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
