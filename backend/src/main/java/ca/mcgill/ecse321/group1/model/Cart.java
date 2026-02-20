/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import jakarta.persistence.*;

// line 32 "../../../../../model.ump"
@Entity
public class Cart
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Cart Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String cartID;

  //Cart Associations
  @OneToMany(mappedBy="cart", cascade=CascadeType.ALL)
  private List<CartItem> items;
  @OneToOne(optional=false)
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Cart() {}

  public Cart(String aCartID, Customer aCustomer)
  {
    cartID = aCartID;
    items = new ArrayList<CartItem>();
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create cart due to customer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
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
  public CartItem getItem(int index)
  {
    CartItem aItem = items.get(index);
    return aItem;
  }

  public List<CartItem> getItems()
  {
    List<CartItem> newItems = Collections.unmodifiableList(items);
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

  public int indexOfItem(CartItem aItem)
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
  public boolean addItem(CartItem aItem)
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

  public boolean removeItem(CartItem aItem)
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
  public boolean addItemAt(CartItem aItem, int index)
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

  public boolean addOrMoveItemAt(CartItem aItem, int index)
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
  /* Code from template association_SetOneToOptionalOne */
  public boolean setCustomer(Customer aNewCustomer)
  {
    boolean wasSet = false;
    if (aNewCustomer == null)
    {
      //Unable to setCustomer to null, as cart must always be associated to a customer
      return wasSet;
    }
    
    Cart existingCart = aNewCustomer.getCart();
    if (existingCart != null && !equals(existingCart))
    {
      //Unable to setCustomer, the current customer already has a cart, which would be orphaned if it were re-assigned
      return wasSet;
    }
    
    Customer anOldCustomer = customer;
    customer = aNewCustomer;
    customer.setCart(this);

    if (anOldCustomer != null)
    {
      anOldCustomer.setCart(null);
    }
    wasSet = true;
    return wasSet;
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
      existingCustomer.setCart(null);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "cartID" + ":" + getCartID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}