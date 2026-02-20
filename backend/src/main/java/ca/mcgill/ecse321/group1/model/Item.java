/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;

// line 45 "../../../../../model.ump"
public class Item
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Item Attributes
  private String itemID;
  private int quantity;

  //Item Associations
  private ClothingVariant variants;
  private Order order;
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Item(String aItemID, int aQuantity, ClothingVariant aVariants)
  {
    itemID = aItemID;
    quantity = aQuantity;
    if (!setVariants(aVariants))
    {
      throw new RuntimeException("Unable to create Item due to aVariants. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setItemID(String aItemID)
  {
    boolean wasSet = false;
    itemID = aItemID;
    wasSet = true;
    return wasSet;
  }

  public boolean setQuantity(int aQuantity)
  {
    boolean wasSet = false;
    quantity = aQuantity;
    wasSet = true;
    return wasSet;
  }

  public String getItemID()
  {
    return itemID;
  }

  public int getQuantity()
  {
    return quantity;
  }
  /* Code from template association_GetOne */
  public ClothingVariant getVariants()
  {
    return variants;
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
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }

  public boolean hasCustomer()
  {
    boolean has = customer != null;
    return has;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setVariants(ClothingVariant aNewVariants)
  {
    boolean wasSet = false;
    if (aNewVariants != null)
    {
      variants = aNewVariants;
      wasSet = true;
    }
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
  /* Code from template association_SetOptionalOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeItem(this);
    }
    if (aCustomer != null)
    {
      aCustomer.addItem(this);
    }
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    variants = null;
    if (order != null)
    {
      Order placeholderOrder = order;
      this.order = null;
      placeholderOrder.removeItem(this);
    }
    if (customer != null)
    {
      Customer placeholderCustomer = customer;
      this.customer = null;
      placeholderCustomer.removeItem(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "itemID" + ":" + getItemID()+ "," +
            "quantity" + ":" + getQuantity()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "variants = "+(getVariants()!=null?Integer.toHexString(System.identityHashCode(getVariants())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "order = "+(getOrder()!=null?Integer.toHexString(System.identityHashCode(getOrder())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}