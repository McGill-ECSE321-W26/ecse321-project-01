/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;

// line 63 "../../../../../model.ump"
public class ClothingItemProperties
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Size { S, M, L, XL }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingItemProperties Attributes
  private String clothingItemPropertiesID;
  private Size size;
  private String color;
  private int inStock;

  //ClothingItemProperties Associations
  private ClothingModel model;
  private List<ClothingItem> clothingItems;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingItemProperties(String aClothingItemPropertiesID, Size aSize, String aColor, int aInStock, ClothingModel aModel)
  {
    clothingItemPropertiesID = aClothingItemPropertiesID;
    size = aSize;
    color = aColor;
    inStock = aInStock;
    boolean didAddModel = setModel(aModel);
    if (!didAddModel)
    {
      throw new RuntimeException("Unable to create clothingItemProperty due to model. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    clothingItems = new ArrayList<ClothingItem>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setClothingItemPropertiesID(String aClothingItemPropertiesID)
  {
    boolean wasSet = false;
    clothingItemPropertiesID = aClothingItemPropertiesID;
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

  public boolean setInStock(int aInStock)
  {
    boolean wasSet = false;
    inStock = aInStock;
    wasSet = true;
    return wasSet;
  }

  public String getClothingItemPropertiesID()
  {
    return clothingItemPropertiesID;
  }

  public Size getSize()
  {
    return size;
  }

  public String getColor()
  {
    return color;
  }

  public int getInStock()
  {
    return inStock;
  }
  /* Code from template association_GetOne */
  public ClothingModel getModel()
  {
    return model;
  }
  /* Code from template association_GetMany */
  public ClothingItem getClothingItem(int index)
  {
    ClothingItem aClothingItem = clothingItems.get(index);
    return aClothingItem;
  }

  public List<ClothingItem> getClothingItems()
  {
    List<ClothingItem> newClothingItems = Collections.unmodifiableList(clothingItems);
    return newClothingItems;
  }

  public int numberOfClothingItems()
  {
    int number = clothingItems.size();
    return number;
  }

  public boolean hasClothingItems()
  {
    boolean has = clothingItems.size() > 0;
    return has;
  }

  public int indexOfClothingItem(ClothingItem aClothingItem)
  {
    int index = clothingItems.indexOf(aClothingItem);
    return index;
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
      existingModel.removeClothingItemProperty(this);
    }
    model.addClothingItemProperty(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfClothingItems()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ClothingItem addClothingItem(String aClothingItemID)
  {
    return new ClothingItem(aClothingItemID, this);
  }

  public boolean addClothingItem(ClothingItem aClothingItem)
  {
    boolean wasAdded = false;
    if (clothingItems.contains(aClothingItem)) { return false; }
    ClothingItemProperties existingProperties = aClothingItem.getProperties();
    boolean isNewProperties = existingProperties != null && !this.equals(existingProperties);
    if (isNewProperties)
    {
      aClothingItem.setProperties(this);
    }
    else
    {
      clothingItems.add(aClothingItem);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeClothingItem(ClothingItem aClothingItem)
  {
    boolean wasRemoved = false;
    //Unable to remove aClothingItem, as it must always have a properties
    if (!this.equals(aClothingItem.getProperties()))
    {
      clothingItems.remove(aClothingItem);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addClothingItemAt(ClothingItem aClothingItem, int index)
  {  
    boolean wasAdded = false;
    if(addClothingItem(aClothingItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingItems()) { index = numberOfClothingItems() - 1; }
      clothingItems.remove(aClothingItem);
      clothingItems.add(index, aClothingItem);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveClothingItemAt(ClothingItem aClothingItem, int index)
  {
    boolean wasAdded = false;
    if(clothingItems.contains(aClothingItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingItems()) { index = numberOfClothingItems() - 1; }
      clothingItems.remove(aClothingItem);
      clothingItems.add(index, aClothingItem);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addClothingItemAt(aClothingItem, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    ClothingModel placeholderModel = model;
    this.model = null;
    if(placeholderModel != null)
    {
      placeholderModel.removeClothingItemProperty(this);
    }
    for(int i=clothingItems.size(); i > 0; i--)
    {
      ClothingItem aClothingItem = clothingItems.get(i - 1);
      aClothingItem.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "clothingItemPropertiesID" + ":" + getClothingItemPropertiesID()+ "," +
            "color" + ":" + getColor()+ "," +
            "inStock" + ":" + getInStock()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "size" + "=" + (getSize() != null ? !getSize().equals(this)  ? getSize().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "model = "+(getModel()!=null?Integer.toHexString(System.identityHashCode(getModel())):"null");
  }
}