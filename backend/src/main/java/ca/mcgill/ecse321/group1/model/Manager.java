/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package ca.mcgill.ecse321.group1.model;
import jakarta.persistence.*;

// line 15 "../../../../../model.ump"
@Entity
public class Manager extends PersonRole
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Manager() {}

  public Manager(String aPersonRoleID, Person aPerson)
  {
    super(aPersonRoleID, aPerson);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public void delete()
  {
    super.delete();
  }

}