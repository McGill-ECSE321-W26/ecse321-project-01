package ca.mcgill.ecse321.group1.dto;

public class PersonPasswordUpdateRequestDto {
  private String oldPassword;
  private String newPassword;

  public String getOldPassword() {
    return oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
