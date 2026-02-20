/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;

// line 77 "../../../../../model.ump"
public class ClothingModel
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingModel Attributes
  private String clothingModelID;
  private String name;
  private float price;

  //ClothingModel Associations
  private List<ClothingItemProperties> clothingItemProperties;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingModel(String aClothingModelID, String aName, float aPrice)
  {
    clothingModelID = aClothingModelID;
    name = aName;
    price = aPrice;
    clothingItemProperties = new ArrayList<ClothingItemProperties>();
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
  /* Code from template association_GetMany */
  public ClothingItemProperties getClothingItemProperty(int index)
  {
    ClothingItemProperties aClothingItemProperty = clothingItemProperties.get(index);
    return aClothingItemProperty;
  }

  public List<ClothingItemProperties> getClothingItemProperties()
  {
    List<ClothingItemProperties> newClothingItemProperties = Collections.unmodifiableList(clothingItemProperties);
    return newClothingItemProperties;
  }

  public int numberOfClothingItemProperties()
  {
    int number = clothingItemProperties.size();
    return number;
  }

  public boolean hasClothingItemProperties()
  {
    boolean has = clothingItemProperties.size() > 0;
    return has;
  }

  public int indexOfClothingItemProperty(ClothingItemProperties aClothingItemProperty)
  {
    int index = clothingItemProperties.indexOf(aClothingItemProperty);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfClothingItemProperties()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ClothingItemProperties addClothingItemProperty(String aClothingItemPropertiesID, ClothingItemProperties.Size aSize, String aColor, int aInStock)
  {
    return new ClothingItemProperties(aClothingItemPropertiesID, aSize, aColor, aInStock, this);
  }

  public boolean addClothingItemProperty(ClothingItemProperties aClothingItemProperty)
  {
    boolean wasAdded = false;
    if (clothingItemProperties.contains(aClothingItemProperty)) { return false; }
    ClothingModel existingModel = aClothingItemProperty.getModel();
    boolean isNewModel = existingModel != null && !this.equals(existingModel);
    if (isNewModel)
    {
      aClothingItemProperty.setModel(this);
    }
    else
    {
      clothingItemProperties.add(aClothingItemProperty);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeClothingItemProperty(ClothingItemProperties aClothingItemProperty)
  {
    boolean wasRemoved = false;
    //Unable to remove aClothingItemProperty, as it must always have a model
    if (!this.equals(aClothingItemProperty.getModel()))
    {
      clothingItemProperties.remove(aClothingItemProperty);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addClothingItemPropertyAt(ClothingItemProperties aClothingItemProperty, int index)
  {  
    boolean wasAdded = false;
    if(addClothingItemProperty(aClothingItemProperty))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingItemProperties()) { index = numberOfClothingItemProperties() - 1; }
      clothingItemProperties.remove(aClothingItemProperty);
      clothingItemProperties.add(index, aClothingItemProperty);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveClothingItemPropertyAt(ClothingItemProperties aClothingItemProperty, int index)
  {
    boolean wasAdded = false;
    if(clothingItemProperties.contains(aClothingItemProperty))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingItemProperties()) { index = numberOfClothingItemProperties() - 1; }
      clothingItemProperties.remove(aClothingItemProperty);
      clothingItemProperties.add(index, aClothingItemProperty);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addClothingItemPropertyAt(aClothingItemProperty, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=clothingItemProperties.size(); i > 0; i--)
    {
      ClothingItemProperties aClothingItemProperty = clothingItemProperties.get(i - 1);
      aClothingItemProperty.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "clothingModelID" + ":" + getClothingModelID()+ "," +
            "name" + ":" + getName()+ "," +
            "price" + ":" + getPrice()+ "]";
  }
}