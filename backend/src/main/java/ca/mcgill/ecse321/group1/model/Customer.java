/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import java.sql.Date;

// line 24 "../../../../../model.ump"
public class Customer extends PersonRole
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Customer Attributes
  private int loyaltyPoints;

  //Customer Associations
  private List<Address> addresses;
  private Cart cart;
  private List<Order> orders;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Customer(String aPersonRoleID, Person aPerson, int aLoyaltyPoints, Cart aCart)
  {
    super(aPersonRoleID, aPerson);
    loyaltyPoints = aLoyaltyPoints;
    addresses = new ArrayList<Address>();
    if (aCart == null || aCart.getCustomer() != null)
    {
      throw new RuntimeException("Unable to create Customer due to aCart. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    cart = aCart;
    orders = new ArrayList<Order>();
  }

  public Customer(String aPersonRoleID, Person aPerson, int aLoyaltyPoints, String aCartIDForCart)
  {
    super(aPersonRoleID, aPerson);
    loyaltyPoints = aLoyaltyPoints;
    addresses = new ArrayList<Address>();
    cart = new Cart(aCartIDForCart, this);
    orders = new ArrayList<Order>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setLoyaltyPoints(int aLoyaltyPoints)
  {
    boolean wasSet = false;
    loyaltyPoints = aLoyaltyPoints;
    wasSet = true;
    return wasSet;
  }

  public int getLoyaltyPoints()
  {
    return loyaltyPoints;
  }
  /* Code from template association_GetMany */
  public Address getAddress(int index)
  {
    Address aAddress = addresses.get(index);
    return aAddress;
  }

  public List<Address> getAddresses()
  {
    List<Address> newAddresses = Collections.unmodifiableList(addresses);
    return newAddresses;
  }

  public int numberOfAddresses()
  {
    int number = addresses.size();
    return number;
  }

  public boolean hasAddresses()
  {
    boolean has = addresses.size() > 0;
    return has;
  }

  public int indexOfAddress(Address aAddress)
  {
    int index = addresses.indexOf(aAddress);
    return index;
  }
  /* Code from template association_GetOne */
  public Cart getCart()
  {
    return cart;
  }
  /* Code from template association_GetMany */
  public Order getOrder(int index)
  {
    Order aOrder = orders.get(index);
    return aOrder;
  }

  public List<Order> getOrders()
  {
    List<Order> newOrders = Collections.unmodifiableList(orders);
    return newOrders;
  }

  public int numberOfOrders()
  {
    int number = orders.size();
    return number;
  }

  public boolean hasOrders()
  {
    boolean has = orders.size() > 0;
    return has;
  }

  public int indexOfOrder(Order aOrder)
  {
    int index = orders.indexOf(aOrder);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfAddresses()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Address addAddress(String aAddressID, String aStreet, String aCity, String aProvince, String aPostalCode)
  {
    return new Address(aAddressID, aStreet, aCity, aProvince, aPostalCode, this);
  }

  public boolean addAddress(Address aAddress)
  {
    boolean wasAdded = false;
    if (addresses.contains(aAddress)) { return false; }
    Customer existingCustomer = aAddress.getCustomer();
    boolean isNewCustomer = existingCustomer != null && !this.equals(existingCustomer);
    if (isNewCustomer)
    {
      aAddress.setCustomer(this);
    }
    else
    {
      addresses.add(aAddress);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeAddress(Address aAddress)
  {
    boolean wasRemoved = false;
    //Unable to remove aAddress, as it must always have a customer
    if (!this.equals(aAddress.getCustomer()))
    {
      addresses.remove(aAddress);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addAddressAt(Address aAddress, int index)
  {  
    boolean wasAdded = false;
    if(addAddress(aAddress))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfAddresses()) { index = numberOfAddresses() - 1; }
      addresses.remove(aAddress);
      addresses.add(index, aAddress);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveAddressAt(Address aAddress, int index)
  {
    boolean wasAdded = false;
    if(addresses.contains(aAddress))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfAddresses()) { index = numberOfAddresses() - 1; }
      addresses.remove(aAddress);
      addresses.add(index, aAddress);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addAddressAt(aAddress, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfOrders()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Order addOrder(String aOrderID, Order.OrderStatus aOrderStatus, Date aOrderDate, Date aDeliveryDate, Employee aEmployee)
  {
    return new Order(aOrderID, aOrderStatus, aOrderDate, aDeliveryDate, aEmployee, this);
  }

  public boolean addOrder(Order aOrder)
  {
    boolean wasAdded = false;
    if (orders.contains(aOrder)) { return false; }
    Customer existingCustomer = aOrder.getCustomer();
    boolean isNewCustomer = existingCustomer != null && !this.equals(existingCustomer);
    if (isNewCustomer)
    {
      aOrder.setCustomer(this);
    }
    else
    {
      orders.add(aOrder);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeOrder(Order aOrder)
  {
    boolean wasRemoved = false;
    //Unable to remove aOrder, as it must always have a customer
    if (!this.equals(aOrder.getCustomer()))
    {
      orders.remove(aOrder);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addOrderAt(Order aOrder, int index)
  {  
    boolean wasAdded = false;
    if(addOrder(aOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfOrders()) { index = numberOfOrders() - 1; }
      orders.remove(aOrder);
      orders.add(index, aOrder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveOrderAt(Order aOrder, int index)
  {
    boolean wasAdded = false;
    if(orders.contains(aOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfOrders()) { index = numberOfOrders() - 1; }
      orders.remove(aOrder);
      orders.add(index, aOrder);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addOrderAt(aOrder, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=addresses.size(); i > 0; i--)
    {
      Address aAddress = addresses.get(i - 1);
      aAddress.delete();
    }
    Cart existingCart = cart;
    cart = null;
    if (existingCart != null)
    {
      existingCart.delete();
    }
    for(int i=orders.size(); i > 0; i--)
    {
      Order aOrder = orders.get(i - 1);
      aOrder.delete();
    }
    super.delete();
  }


  public String toString()
  {
    return super.toString() + "["+
            "loyaltyPoints" + ":" + getLoyaltyPoints()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "cart = "+(getCart()!=null?Integer.toHexString(System.identityHashCode(getCart())):"null");
  }
}