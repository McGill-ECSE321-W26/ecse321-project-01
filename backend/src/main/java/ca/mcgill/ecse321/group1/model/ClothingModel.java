/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import jakarta.persistence.*;

// line 66 "../../../../../model.ump"
@Entity
public class ClothingModel
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingModel Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String clothingModelID;
  private String name;
  private float price;

  //ClothingModel Associations
  @OneToMany(mappedBy = "model", cascade = {CascadeType.ALL})
  private List<ClothingVariant> clothingVariants;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingModel() {}

  public ClothingModel(String aClothingModelID, String aName, float aPrice)
  {
    clothingModelID = aClothingModelID;
    name = aName;
    price = aPrice;
    clothingVariants = new ArrayList<ClothingVariant>();
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
  public ClothingVariant getClothingVariant(int index)
  {
    ClothingVariant aClothingVariant = clothingVariants.get(index);
    return aClothingVariant;
  }

  public List<ClothingVariant> getClothingVariants()
  {
    List<ClothingVariant> newClothingVariants = Collections.unmodifiableList(clothingVariants);
    return newClothingVariants;
  }

  public int numberOfClothingVariants()
  {
    int number = clothingVariants.size();
    return number;
  }

  public boolean hasClothingVariants()
  {
    boolean has = clothingVariants.size() > 0;
    return has;
  }

  public int indexOfClothingVariant(ClothingVariant aClothingVariant)
  {
    int index = clothingVariants.indexOf(aClothingVariant);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfClothingVariants()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ClothingVariant addClothingVariant(String aClothingVariantID, ClothingVariant.Size aSize, String aColor, int aStockQuantity)
  {
    return new ClothingVariant(aClothingVariantID, aSize, aColor, aStockQuantity, this);
  }

  public boolean addClothingVariant(ClothingVariant aClothingVariant)
  {
    boolean wasAdded = false;
    if (clothingVariants.contains(aClothingVariant)) { return false; }
    ClothingModel existingModel = aClothingVariant.getModel();
    boolean isNewModel = existingModel != null && !this.equals(existingModel);
    if (isNewModel)
    {
      aClothingVariant.setModel(this);
    }
    else
    {
      clothingVariants.add(aClothingVariant);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeClothingVariant(ClothingVariant aClothingVariant)
  {
    boolean wasRemoved = false;
    //Unable to remove aClothingVariant, as it must always have a model
    if (!this.equals(aClothingVariant.getModel()))
    {
      clothingVariants.remove(aClothingVariant);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addClothingVariantAt(ClothingVariant aClothingVariant, int index)
  {  
    boolean wasAdded = false;
    if(addClothingVariant(aClothingVariant))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingVariants()) { index = numberOfClothingVariants() - 1; }
      clothingVariants.remove(aClothingVariant);
      clothingVariants.add(index, aClothingVariant);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveClothingVariantAt(ClothingVariant aClothingVariant, int index)
  {
    boolean wasAdded = false;
    if(clothingVariants.contains(aClothingVariant))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfClothingVariants()) { index = numberOfClothingVariants() - 1; }
      clothingVariants.remove(aClothingVariant);
      clothingVariants.add(index, aClothingVariant);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addClothingVariantAt(aClothingVariant, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=clothingVariants.size(); i > 0; i--)
    {
      ClothingVariant aClothingVariant = clothingVariants.get(i - 1);
      aClothingVariant.delete();
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