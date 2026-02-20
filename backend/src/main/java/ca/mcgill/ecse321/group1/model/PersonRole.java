/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;

// line 10 "../../../../../model.ump"
public abstract class PersonRole
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //PersonRole Attributes
  private String personRoleID;

  //PersonRole Associations
  private Person person;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public PersonRole(String aPersonRoleID, Person aPerson)
  {
    personRoleID = aPersonRoleID;
    boolean didAddPerson = setPerson(aPerson);
    if (!didAddPerson)
    {
      throw new RuntimeException("Unable to create role due to person. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setPersonRoleID(String aPersonRoleID)
  {
    boolean wasSet = false;
    personRoleID = aPersonRoleID;
    wasSet = true;
    return wasSet;
  }

  public String getPersonRoleID()
  {
    return personRoleID;
  }
  /* Code from template association_GetOne */
  public Person getPerson()
  {
    return person;
  }
  /* Code from template association_SetOneToAtMostN */
  public boolean setPerson(Person aPerson)
  {
    boolean wasSet = false;
    //Must provide person to role
    if (aPerson == null)
    {
      return wasSet;
    }

    //person already at maximum (2)
    if (aPerson.numberOfRoles() >= Person.maximumNumberOfRoles())
    {
      return wasSet;
    }
    
    Person existingPerson = person;
    person = aPerson;
    if (existingPerson != null && !existingPerson.equals(aPerson))
    {
      boolean didRemove = existingPerson.removeRole(this);
      if (!didRemove)
      {
        person = existingPerson;
        return wasSet;
      }
    }
    person.addRole(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Person placeholderPerson = person;
    this.person = null;
    if(placeholderPerson != null)
    {
      placeholderPerson.removeRole(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "personRoleID" + ":" + getPersonRoleID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "person = "+(getPerson()!=null?Integer.toHexString(System.identityHashCode(getPerson())):"null");
  }
}