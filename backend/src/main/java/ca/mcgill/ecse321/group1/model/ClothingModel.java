/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import jakarta.persistence.*;

// line 71 "../../../../../model.ump"
@Entity
public class ClothingModel
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingModel Attributes
  @Id
  private String clothingModelID;
  private String name;
  private float price;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingModel() {}

  public ClothingModel(String aClothingModelID, String aName, float aPrice)
  {
    clothingModelID = aClothingModelID;
    name = aName;
    price = aPrice;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setClothingModelID(String aClothingModelID)
  {
    boolean wasSet = false;
    clothingModelID = aClothingModelID;
    wasSet = true;
    return wasSet;
  }

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setPrice(float aPrice)
  {
    boolean wasSet = false;
    price = aPrice;
    wasSet = true;
    return wasSet;
  }

  public String getClothingModelID()
  {
    return clothingModelID;
  }

  public String getName()
  {
    return name;
  }

  public float getPrice()
  {
    return price;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "clothingModelID" + ":" + getClothingModelID()+ "," +
            "name" + ":" + getName()+ "," +
            "price" + ":" + getPrice()+ "]";
  }
}