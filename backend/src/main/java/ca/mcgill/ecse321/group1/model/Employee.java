/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import java.sql.Date;
import jakarta.persistence.*;

// line 19 "../../../../../model.ump"
@Entity
public class Employee extends PersonRole
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Employee Associations
  @OneToMany(mappedBy = "employee")
  private List<Order> preparingOrders;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Employee() {}

  public Employee(String aRoleID, Person aPerson)
  {
    super(aRoleID, aPerson);
    preparingOrders = new ArrayList<Order>();
  }

  //------------------------
  // INTERFACE
  //------------------------
  /* Code from template association_GetMany */
  public Order getPreparingOrder(int index)
  {
    Order aPreparingOrder = preparingOrders.get(index);
    return aPreparingOrder;
  }

  public List<Order> getPreparingOrders()
  {
    List<Order> newPreparingOrders = Collections.unmodifiableList(preparingOrders);
    return newPreparingOrders;
  }

  public int numberOfPreparingOrders()
  {
    int number = preparingOrders.size();
    return number;
  }

  public boolean hasPreparingOrders()
  {
    boolean has = preparingOrders.size() > 0;
    return has;
  }

  public int indexOfPreparingOrder(Order aPreparingOrder)
  {
    int index = preparingOrders.indexOf(aPreparingOrder);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPreparingOrders()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Order addPreparingOrder(String aOrderID, Order.OrderStatus aOrderStatus, Date aOrderDate, Date aDeliveryDate, String aAddress, Customer aCustomer)
  {
    return new Order(aOrderID, aOrderStatus, aOrderDate, aDeliveryDate, aAddress, this, aCustomer);
  }

  public boolean addPreparingOrder(Order aPreparingOrder)
  {
    boolean wasAdded = false;
    if (preparingOrders.contains(aPreparingOrder)) { return false; }
    Employee existingEmployee = aPreparingOrder.getEmployee();
    boolean isNewEmployee = existingEmployee != null && !this.equals(existingEmployee);
    if (isNewEmployee)
    {
      aPreparingOrder.setEmployee(this);
    }
    else
    {
      preparingOrders.add(aPreparingOrder);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removePreparingOrder(Order aPreparingOrder)
  {
    boolean wasRemoved = false;
    //Unable to remove aPreparingOrder, as it must always have a employee
    if (!this.equals(aPreparingOrder.getEmployee()))
    {
      preparingOrders.remove(aPreparingOrder);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addPreparingOrderAt(Order aPreparingOrder, int index)
  {  
    boolean wasAdded = false;
    if(addPreparingOrder(aPreparingOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPreparingOrders()) { index = numberOfPreparingOrders() - 1; }
      preparingOrders.remove(aPreparingOrder);
      preparingOrders.add(index, aPreparingOrder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMovePreparingOrderAt(Order aPreparingOrder, int index)
  {
    boolean wasAdded = false;
    if(preparingOrders.contains(aPreparingOrder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPreparingOrders()) { index = numberOfPreparingOrders() - 1; }
      preparingOrders.remove(aPreparingOrder);
      preparingOrders.add(index, aPreparingOrder);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addPreparingOrderAt(aPreparingOrder, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=preparingOrders.size(); i > 0; i--)
    {
      Order aPreparingOrder = preparingOrders.get(i - 1);
      aPreparingOrder.delete();
    }
    super.delete();
  }

}