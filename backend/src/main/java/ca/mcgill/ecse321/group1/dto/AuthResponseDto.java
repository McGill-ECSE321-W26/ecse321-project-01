package ca.mcgill.ecse321.group1.dto;

public class AuthResponseDto {

  private String token;
  private PersonResponseDto person;

  @SuppressWarnings("unused")
  private AuthResponseDto() {}

  public AuthResponseDto(String token, PersonResponseDto person) {
    this.token = token;
    this.person = person;
  }

  public String getToken() {
    return token;
  }

  public PersonResponseDto getPerson() {
    return person;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setPerson(PersonResponseDto person) {
    this.person = person;
  }
}
