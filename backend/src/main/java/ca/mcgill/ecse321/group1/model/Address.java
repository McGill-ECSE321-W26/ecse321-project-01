/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;

// line 32 "../../../../../model.ump"
public class Address
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Address Attributes
  private String addressID;
  private String street;
  private String city;
  private String province;
  private String postalCode;

  //Address Associations
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Address(String aAddressID, String aStreet, String aCity, String aProvince, String aPostalCode, Customer aCustomer)
  {
    addressID = aAddressID;
    street = aStreet;
    city = aCity;
    province = aProvince;
    postalCode = aPostalCode;
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create address due to customer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setAddressID(String aAddressID)
  {
    boolean wasSet = false;
    addressID = aAddressID;
    wasSet = true;
    return wasSet;
  }

  public boolean setStreet(String aStreet)
  {
    boolean wasSet = false;
    street = aStreet;
    wasSet = true;
    return wasSet;
  }

  public boolean setCity(String aCity)
  {
    boolean wasSet = false;
    city = aCity;
    wasSet = true;
    return wasSet;
  }

  public boolean setProvince(String aProvince)
  {
    boolean wasSet = false;
    province = aProvince;
    wasSet = true;
    return wasSet;
  }

  public boolean setPostalCode(String aPostalCode)
  {
    boolean wasSet = false;
    postalCode = aPostalCode;
    wasSet = true;
    return wasSet;
  }

  public String getAddressID()
  {
    return addressID;
  }

  public String getStreet()
  {
    return street;
  }

  public String getCity()
  {
    return city;
  }

  public String getProvince()
  {
    return province;
  }

  public String getPostalCode()
  {
    return postalCode;
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
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
      existingCustomer.removeAddress(this);
    }
    customer.addAddress(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeAddress(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "addressID" + ":" + getAddressID()+ "," +
            "street" + ":" + getStreet()+ "," +
            "city" + ":" + getCity()+ "," +
            "province" + ":" + getProvince()+ "," +
            "postalCode" + ":" + getPostalCode()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}