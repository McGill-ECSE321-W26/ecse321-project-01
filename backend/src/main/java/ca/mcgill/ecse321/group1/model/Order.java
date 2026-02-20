/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.sql.Date;
import java.util.*;

// line 45 "../../../../../model.ump"
public class Order
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum OrderStatus { Preparing, Delivered, Cancelled }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Order Attributes
  private String orderID;
  private OrderStatus orderStatus;
  private Date orderDate;
  private Date deliveryDate;

  //Order Associations
  private List<ClothingItem> items;
  private Employee employee;
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Order(String aOrderID, OrderStatus aOrderStatus, Date aOrderDate, Date aDeliveryDate, Employee aEmployee, Customer aCustomer)
  {
    orderID = aOrderID;
    orderStatus = aOrderStatus;
    orderDate = aOrderDate;
    deliveryDate = aDeliveryDate;
    items = new ArrayList<ClothingItem>();
    boolean didAddEmployee = setEmployee(aEmployee);
    if (!didAddEmployee)
    {
      throw new RuntimeException("Unable to create preparingOrder due to employee. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create order due to customer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setOrderID(String aOrderID)
  {
    boolean wasSet = false;
    orderID = aOrderID;
    wasSet = true;
    return wasSet;
  }

  public boolean setOrderStatus(OrderStatus aOrderStatus)
  {
    boolean wasSet = false;
    orderStatus = aOrderStatus;
    wasSet = true;
    return wasSet;
  }

  public boolean setOrderDate(Date aOrderDate)
  {
    boolean wasSet = false;
    orderDate = aOrderDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setDeliveryDate(Date aDeliveryDate)
  {
    boolean wasSet = false;
    deliveryDate = aDeliveryDate;
    wasSet = true;
    return wasSet;
  }

  public String getOrderID()
  {
    return orderID;
  }

  public OrderStatus getOrderStatus()
  {
    return orderStatus;
  }

  public Date getOrderDate()
  {
    return orderDate;
  }

  public Date getDeliveryDate()
  {
    return deliveryDate;
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
  public Employee getEmployee()
  {
    return employee;
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
    Order existingOrder = aItem.getOrder();
    if (existingOrder == null)
    {
      aItem.setOrder(this);
    }
    else if (!this.equals(existingOrder))
    {
      existingOrder.removeItem(aItem);
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
      aItem.setOrder(null);
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
  /* Code from template association_SetOneToMany */
  public boolean setEmployee(Employee aEmployee)
  {
    boolean wasSet = false;
    if (aEmployee == null)
    {
      return wasSet;
    }

    Employee existingEmployee = employee;
    employee = aEmployee;
    if (existingEmployee != null && !existingEmployee.equals(aEmployee))
    {
      existingEmployee.removePreparingOrder(this);
    }
    employee.addPreparingOrder(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    if (aCustomer == null)
    {
      return wasSet;
    }

    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeOrder(this);
    }
    customer.addOrder(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    while( !items.isEmpty() )
    {
      items.get(0).setOrder(null);
    }
    Employee placeholderEmployee = employee;
    this.employee = null;
    if(placeholderEmployee != null)
    {
      placeholderEmployee.removePreparingOrder(this);
    }
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeOrder(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "orderID" + ":" + getOrderID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "orderStatus" + "=" + (getOrderStatus() != null ? !getOrderStatus().equals(this)  ? getOrderStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "orderDate" + "=" + (getOrderDate() != null ? !getOrderDate().equals(this)  ? getOrderDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "deliveryDate" + "=" + (getDeliveryDate() != null ? !getDeliveryDate().equals(this)  ? getDeliveryDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "employee = "+(getEmployee()!=null?Integer.toHexString(System.identityHashCode(getEmployee())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}