package ca.mcgill.ecse321.group1.dto;

public class AuthResponseDto<T> {

  private String token;
  private T person;

  @SuppressWarnings("unused")
  private AuthResponseDto() {}

  public AuthResponseDto(String token, T person) {
    this.token = token;
    this.person = person;
  }

  public String getToken() {
    return token;
  }

  public T getPerson() {
    return person;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setPerson(T person) {
    this.person = person;
  }
}
