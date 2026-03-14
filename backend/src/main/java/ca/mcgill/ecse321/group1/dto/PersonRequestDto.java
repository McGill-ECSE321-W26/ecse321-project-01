package ca.mcgill.ecse321.group1.dto;

public class PersonRequestDto {
    private String id;
    private String email;
    private String password;  // ✅ needed for create and login
    private String role;      // ✅ needed for login

    @SuppressWarnings("unused")
    private PersonRequestDto() {}

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}