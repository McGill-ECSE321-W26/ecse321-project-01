/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;

import jakarta.persistence.*;

// line 52 "../../../../../model.ump"
@Entity
public class ClothingVariant
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Size { S, M, L, XL }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingVariant Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String clothingVariantID;
  @Enumerated(EnumType.STRING)
  private Size size;
  private String color;
  private int stockQuantity;

  //ClothingVariant Associations
  @ManyToOne
  private ClothingModel model;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingVariant() {}

  public ClothingVariant(String aClothingVariantID, Size aSize, String aColor, int aStockQuantity, ClothingModel aModel)
  {
    clothingVariantID = aClothingVariantID;
    size = aSize;
    color = aColor;
    stockQuantity = aStockQuantity;
    boolean didAddModel = setModel(aModel);
    if (!didAddModel)
    {
      throw new RuntimeException("Unable to create clothingVariant due to model. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setClothingVariantID(String aClothingVariantID)
  {
    boolean wasSet = false;
    clothingVariantID = aClothingVariantID;
    wasSet = true;
    return wasSet;
  }

  public boolean setSize(Size aSize)
  {
    boolean wasSet = false;
    size = aSize;
    wasSet = true;
    return wasSet;
  }

  public boolean setColor(String aColor)
  {
    boolean wasSet = false;
    color = aColor;
    wasSet = true;
    return wasSet;
  }

  public boolean setStockQuantity(int aStockQuantity)
  {
    boolean wasSet = false;
    stockQuantity = aStockQuantity;
    wasSet = true;
    return wasSet;
  }

  public String getClothingVariantID()
  {
    return clothingVariantID;
  }

  public Size getSize()
  {
    return size;
  }

  public String getColor()
  {
    return color;
  }

  public int getStockQuantity()
  {
    return stockQuantity;
  }
  /* Code from template association_GetOne */
  public ClothingModel getModel()
  {
    return model;
  }
  /* Code from template association_SetOneToMany */
  public boolean setModel(ClothingModel aModel)
  {
    boolean wasSet = false;
    if (aModel == null)
    {
      return wasSet;
    }

    ClothingModel existingModel = model;
    model = aModel;
    if (existingModel != null && !existingModel.equals(aModel))
    {
      existingModel.removeClothingVariant(this);
    }
    model.addClothingVariant(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    ClothingModel placeholderModel = model;
    this.model = null;
    if(placeholderModel != null)
    {
      placeholderModel.removeClothingVariant(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "clothingVariantID" + ":" + getClothingVariantID()+ "," +
            "color" + ":" + getColor()+ "," +
            "stockQuantity" + ":" + getStockQuantity()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "size" + "=" + (getSize() != null ? !getSize().equals(this)  ? getSize().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "model = "+(getModel()!=null?Integer.toHexString(System.identityHashCode(getModel())):"null");
  }
}