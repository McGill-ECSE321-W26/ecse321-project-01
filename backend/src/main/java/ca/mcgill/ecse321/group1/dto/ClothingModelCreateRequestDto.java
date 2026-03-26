package ca.mcgill.ecse321.group1.dto;

public class ClothingModelCreateRequestDto {

  private String name;
  private float price;
  private String imagePath;

  public ClothingModelCreateRequestDto() {}

  public ClothingModelCreateRequestDto(String name, float price, String imagePath) {
    this.name = name;
    this.price = price;
    this.imagePath = imagePath;
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

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }
}
