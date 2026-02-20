/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;

// line 58 "../../../../../model.ump"
public class ClothingItem
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClothingItem Attributes
  private String clothingItemID;

  //ClothingItem Associations
  private ClothingItemProperties properties;
  private Cart cart;
  private Order order;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public ClothingItem(String aClothingItemID, ClothingItemProperties aProperties)
  {
    clothingItemID = aClothingItemID;
    boolean didAddProperties = setProperties(aProperties);
    if (!didAddProperties)
    {
      throw new RuntimeException("Unable to create clothingItem due to properties. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setClothingItemID(String aClothingItemID)
  {
    boolean wasSet = false;
    clothingItemID = aClothingItemID;
    wasSet = true;
    return wasSet;
  }

  public String getClothingItemID()
  {
    return clothingItemID;
  }
  /* Code from template association_GetOne */
  public ClothingItemProperties getProperties()
  {
    return properties;
  }
  /* Code from template association_GetOne */
  public Cart getCart()
  {
    return cart;
  }

  public boolean hasCart()
  {
    boolean has = cart != null;
    return has;
  }
  /* Code from template association_GetOne */
  public Order getOrder()
  {
    return order;
  }

  public boolean hasOrder()
  {
    boolean has = order != null;
    return has;
  }
  /* Code from template association_SetOneToMany */
  public boolean setProperties(ClothingItemProperties aProperties)
  {
    boolean wasSet = false;
    if (aProperties == null)
    {
      return wasSet;
    }

    ClothingItemProperties existingProperties = properties;
    properties = aProperties;
    if (existingProperties != null && !existingProperties.equals(aProperties))
    {
      existingProperties.removeClothingItem(this);
    }
    properties.addClothingItem(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOptionalOneToMany */
  public boolean setCart(Cart aCart)
  {
    boolean wasSet = false;
    Cart existingCart = cart;
    cart = aCart;
    if (existingCart != null && !existingCart.equals(aCart))
    {
      existingCart.removeItem(this);
    }
    if (aCart != null)
    {
      aCart.addItem(this);
    }
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOptionalOneToMany */
  public boolean setOrder(Order aOrder)
  {
    boolean wasSet = false;
    Order existingOrder = order;
    order = aOrder;
    if (existingOrder != null && !existingOrder.equals(aOrder))
    {
      existingOrder.removeItem(this);
    }
    if (aOrder != null)
    {
      aOrder.addItem(this);
    }
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    ClothingItemProperties placeholderProperties = properties;
    this.properties = null;
    if(placeholderProperties != null)
    {
      placeholderProperties.removeClothingItem(this);
    }
    if (cart != null)
    {
      Cart placeholderCart = cart;
      this.cart = null;
      placeholderCart.removeItem(this);
    }
    if (order != null)
    {
      Order placeholderOrder = order;
      this.order = null;
      placeholderOrder.removeItem(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "clothingItemID" + ":" + getClothingItemID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "properties = "+(getProperties()!=null?Integer.toHexString(System.identityHashCode(getProperties())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "cart = "+(getCart()!=null?Integer.toHexString(System.identityHashCode(getCart())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "order = "+(getOrder()!=null?Integer.toHexString(System.identityHashCode(getOrder())):"null");
  }
}