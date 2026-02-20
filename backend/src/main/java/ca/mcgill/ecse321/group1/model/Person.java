/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import java.util.*;
import jakarta.persistence.*;

// line 3 "../../../../../model.ump"
@Entity
public class Person
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Person Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String personID;
  private String email;
  private String password;

  //Person Associations
  @OneToMany(mappedBy="person", cascade=CascadeType.ALL)
  private List<PersonRole> roles;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Person() {}

  public Person(String aPersonID, String aEmail, String aPassword)
  {
    personID = aPersonID;
    email = aEmail;
    password = aPassword;
    roles = new ArrayList<PersonRole>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setPersonID(String aPersonID)
  {
    boolean wasSet = false;
    personID = aPersonID;
    wasSet = true;
    return wasSet;
  }

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    email = aEmail;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public String getPersonID()
  {
    return personID;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPassword()
  {
    return password;
  }
  /* Code from template association_GetMany */
  public PersonRole getRole(int index)
  {
    PersonRole aRole = roles.get(index);
    return aRole;
  }

  public List<PersonRole> getRoles()
  {
    List<PersonRole> newRoles = Collections.unmodifiableList(roles);
    return newRoles;
  }

  public int numberOfRoles()
  {
    int number = roles.size();
    return number;
  }

  public boolean hasRoles()
  {
    boolean has = roles.size() > 0;
    return has;
  }

  public int indexOfRole(PersonRole aRole)
  {
    int index = roles.indexOf(aRole);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfRoles()
  {
    return 0;
  }
  /* Code from template association_MaximumNumberOfMethod */
  public static int maximumNumberOfRoles()
  {
    return 2;
  }
  /* Code from template association_AddOptionalNToOne */


  public boolean addRole(PersonRole aRole)
  {
    boolean wasAdded = false;
    if (roles.contains(aRole)) { return false; }
    if (numberOfRoles() >= maximumNumberOfRoles())
    {
      return wasAdded;
    }

    Person existingPerson = aRole.getPerson();
    boolean isNewPerson = existingPerson != null && !this.equals(existingPerson);
    if (isNewPerson)
    {
      aRole.setPerson(this);
    }
    else
    {
      roles.add(aRole);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeRole(PersonRole aRole)
  {
    boolean wasRemoved = false;
    //Unable to remove aRole, as it must always have a person
    if (!this.equals(aRole.getPerson()))
    {
      roles.remove(aRole);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addRoleAt(PersonRole aRole, int index)
  {  
    boolean wasAdded = false;
    if(addRole(aRole))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRoles()) { index = numberOfRoles() - 1; }
      roles.remove(aRole);
      roles.add(index, aRole);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveRoleAt(PersonRole aRole, int index)
  {
    boolean wasAdded = false;
    if(roles.contains(aRole))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRoles()) { index = numberOfRoles() - 1; }
      roles.remove(aRole);
      roles.add(index, aRole);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addRoleAt(aRole, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=roles.size(); i > 0; i--)
    {
      PersonRole aRole = roles.get(i - 1);
      aRole.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "personID" + ":" + getPersonID()+ "," +
            "email" + ":" + getEmail()+ "," +
            "password" + ":" + getPassword()+ "]";
  }
}