/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import jakarta.persistence.*;

// line 50 "../../../../../model.ump"
@Entity
public class CartItem
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //CartItem Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String cartItemID;
  private int quantity;

  //CartItem Associations
  @ManyToOne
  private ClothingVariant variants;
  @ManyToOne
  private Order order;
  @ManyToOne
  private Cart cart;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public CartItem() {}

  public CartItem(String aCartItemID, int aQuantity, ClothingVariant aVariants)
  {
    cartItemID = aCartItemID;
    quantity = aQuantity;
    if (!setVariants(aVariants))
    {
      throw new RuntimeException("Unable to create CartItem due to aVariants. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setCartItemID(String aCartItemID)
  {
    boolean wasSet = false;
    cartItemID = aCartItemID;
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

  public String getCartItemID()
  {
    return cartItemID;
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
  public Cart getCart()
  {
    return cart;
  }

  public boolean hasCart()
  {
    boolean has = cart != null;
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
  /* Code from template association_SetUnidirectionalOptionalOne */
  public boolean setOrder(Order aNewOrder)
  {
    boolean wasSet = false;
    order = aNewOrder;
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

  public void delete()
  {
    variants = null;
    order = null;
    if (cart != null)
    {
      Cart placeholderCart = cart;
      this.cart = null;
      placeholderCart.removeItem(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "cartItemID" + ":" + getCartItemID()+ "," +
            "quantity" + ":" + getQuantity()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "variants = "+(getVariants()!=null?Integer.toHexString(System.identityHashCode(getVariants())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "order = "+(getOrder()!=null?Integer.toHexString(System.identityHashCode(getOrder())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "cart = "+(getCart()!=null?Integer.toHexString(System.identityHashCode(getCart())):"null");
  }
}