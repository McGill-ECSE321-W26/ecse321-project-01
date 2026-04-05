/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import jakarta.persistence.*;

// line 70 "../../../../../model.ump"
@Entity
public class ClothingModel
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Category { Tops, Bottoms, Dresses, Outerwear, Accessories }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingModel Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String clothingModelID;
  @Column(unique = true)
  private String name;
  private String description;
  private String brand;
  private Category category;
  private float price;
  @Column(columnDefinition = "boolean default false")
  private boolean archived;

  //ClothingModel Associations
  @OneToMany(mappedBy = "model", cascade = {CascadeType.ALL}, orphanRemoval = true)
  private List<ClothingVariant> clothingVariants;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingModel() {
    clothingVariants = new ArrayList<ClothingVariant>();
  }

  public ClothingModel(String aClothingModelID, String aName, String aDescription, String aBrand, Category aCategory, float aPrice)
  {
    clothingModelID = aClothingModelID;
    name = aName;
    description = aDescription;
    brand = aBrand;
    category = aCategory;
    price = aPrice;
    archived = false;
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

  public boolean setDescription(String aDescription)
  {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setBrand(String aBrand)
  {
    boolean wasSet = false;
    brand = aBrand;
    wasSet = true;
    return wasSet;
  }

  public boolean setCategory(Category aCategory)
  {
    boolean wasSet = false;
    category = aCategory;
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

  public boolean setArchived(boolean aArchived)
  {
    boolean wasSet = false;
    archived = aArchived;
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

  public String getDescription()
  {
    return description;
  }

  public String getBrand()
  {
    return brand;
  }

  public Category getCategory()
  {
    return category;
  }

  public float getPrice()
  {
    return price;
  }

  public boolean getArchived()
  {
    return archived;
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
  public ClothingVariant addClothingVariant(String aClothingVariantID, ClothingVariant.Size aSize, String aColor, String aImagePath, int aStockQuantity)
  {
    return new ClothingVariant(aClothingVariantID, aSize, aColor, aImagePath, aStockQuantity, this);
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
            "description" + ":" + getDescription()+ "," +
            "brand" + ":" + getBrand()+ "," +
            "price" + ":" + getPrice()+ "," +
            "archived" + ":" + getArchived()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "category" + "=" + (getCategory() != null ? !getCategory().equals(this)  ? getCategory().toString().replaceAll("  ","    ") : "this" : "null");
  }
}