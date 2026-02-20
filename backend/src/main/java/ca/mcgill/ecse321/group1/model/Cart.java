/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;

// line 40 "../../../../../model.ump"
public class Cart
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Cart Attributes
  private String cartID;

  //Cart Associations
  private List<ClothingItem> items;
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Cart(String aCartID, Customer aCustomer)
  {
    cartID = aCartID;
    items = new ArrayList<ClothingItem>();
    if (aCustomer == null || aCustomer.getCart() != null)
    {
      throw new RuntimeException("Unable to create Cart due to aCustomer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    customer = aCustomer;
  }

  public Cart(String aCartID, String aPersonRoleIDForCustomer, Person aPersonForCustomer, int aLoyaltyPointsForCustomer)
  {
    cartID = aCartID;
    items = new ArrayList<ClothingItem>();
    customer = new Customer(aPersonRoleIDForCustomer, aPersonForCustomer, aLoyaltyPointsForCustomer, this);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setCartID(String aCartID)
  {
    boolean wasSet = false;
    cartID = aCartID;
    wasSet = true;
    return wasSet;
  }

  public String getCartID()
  {
    return cartID;
  }
  /* Code from template association_GetMany */
  public ClothingItem getItem(int index)
  {
    ClothingItem aItem = items.get(index);
    return aItem;
  }

  public List<ClothingItem> getItems()
  {
    List<ClothingItem> newItems = Collections.unmodifiableList(items);
    return newItems;
  }

  public int numberOfItems()
  {
    int number = items.size();
    return number;
  }

  public boolean hasItems()
  {
    boolean has = items.size() > 0;
    return has;
  }

  public int indexOfItem(ClothingItem aItem)
  {
    int index = items.indexOf(aItem);
    return index;
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfItems()
  {
    return 0;
  }
  /* Code from template association_AddManyToOptionalOne */
  public boolean addItem(ClothingItem aItem)
  {
    boolean wasAdded = false;
    if (items.contains(aItem)) { return false; }
    Cart existingCart = aItem.getCart();
    if (existingCart == null)
    {
      aItem.setCart(this);
    }
    else if (!this.equals(existingCart))
    {
      existingCart.removeItem(aItem);
      addItem(aItem);
    }
    else
    {
      items.add(aItem);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeItem(ClothingItem aItem)
  {
    boolean wasRemoved = false;
    if (items.contains(aItem))
    {
      items.remove(aItem);
      aItem.setCart(null);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addItemAt(ClothingItem aItem, int index)
  {  
    boolean wasAdded = false;
    if(addItem(aItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfItems()) { index = numberOfItems() - 1; }
      items.remove(aItem);
      items.add(index, aItem);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveItemAt(ClothingItem aItem, int index)
  {
    boolean wasAdded = false;
    if(items.contains(aItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfItems()) { index = numberOfItems() - 1; }
      items.remove(aItem);
      items.add(index, aItem);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addItemAt(aItem, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    while( !items.isEmpty() )
    {
      items.get(0).setCart(null);
    }
    Customer existingCustomer = customer;
    customer = null;
    if (existingCustomer != null)
    {
      existingCustomer.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "cartID" + ":" + getCartID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}