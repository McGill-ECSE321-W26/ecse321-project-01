package ca.mcgill.ecse321.group1.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.dto.*;
import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.repository.*;
import ca.mcgill.ecse321.group1.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class PersonIntegrationTests {

  @LocalServerPort private int port;

  @Autowired private PersonRepository personRepository;
  @Autowired private ManagerRepository managerRepository;
  @Autowired private JwtUtil jwtUtil;

  private static final String INVALID_ID = "not-a-real-id";
  private static final String VALID_PASSWORD = "password123";
  private static final String SHORT_PASSWORD = "pass";

  // Manager created directly in DB for auth
  private Person managerPerson;
  private String managerToken;

  // RestClients
  private RestClient managerClient;
  private RestClient unauthClient;

  // State shared across ordered tests
  private String createdCustomerId;
  private String createdCustomerEmail;
  private String customerToken;
  private String createdEmployeeId;
  private String createdEmployeeEmail;

  @BeforeAll
  public void setup() {
    managerPerson = new Person();
    managerPerson.setEmail("person-test-manager@test.com");
    managerPerson.setPassword("irrelevant");
    managerPerson = personRepository.save(managerPerson);

    Manager manager = new Manager();
    manager.setPerson(managerPerson);
    managerRepository.save(manager);

    managerToken = jwtUtil.generateToken(managerPerson, "Manager");

    managerClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + managerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    unauthClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();
  }

  @AfterAll
  public void cleanup() {
    if (createdCustomerEmail != null) {
      Person p = personRepository.findByEmail(createdCustomerEmail);
      if (p != null) personRepository.delete(p);
    }
    if (createdEmployeeEmail != null) {
      Person p = personRepository.findByEmail(createdEmployeeEmail);
      if (p != null) personRepository.delete(p);
    }
    personRepository.deleteById(managerPerson.getPersonID());
  }

  // ==== POST /api/persons/customers ====

  @Test
  @Order(1)
  public void testCreateCustomerInvalidEmail() {
    // Arrange
    CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
    dto.setEmail("notanemail");
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("123 Main St");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(2)
  public void testCreateCustomerPasswordTooShort() {
    // Arrange
    CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
    dto.setEmail("valid@email.com");
    dto.setPassword(SHORT_PASSWORD);
    dto.setAddress("123 Main St");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(3)
  public void testCreateCustomerEmptyAddress() {
    // Arrange
    CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
    dto.setEmail("validaddress@email.com");
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(4)
  public void testCreateCustomerValid() {
    // Arrange
    createdCustomerEmail = "person-test-customer@test.com";
    CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
    dto.setEmail(createdCustomerEmail);
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("456 Customer Ave");

    // Act
    ResponseEntity<CustomerResponseDto> response =
        unauthClient
            .post()
            .uri("/api/persons/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(CustomerResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    CustomerResponseDto body = response.getBody();
    assertNotNull(body);
    assertNotNull(body.getId());
    assertNotNull(body.getPersonId());
    assertEquals(createdCustomerEmail, body.getEmail());
    assertEquals("456 Customer Ave", body.getAddress());
    assertEquals(0, body.getLoyaltyPoints());

    createdCustomerId = body.getPersonId();
  }

  @Test
  @Order(5)
  public void testCreateCustomerDuplicateEmail() {
    // Arrange
    CustomerCreateRequestDto dto = new CustomerCreateRequestDto();
    dto.setEmail(createdCustomerEmail);
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("789 Other St");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  // ==== POST /api/persons/sessions ====

  @Test
  @Order(6)
  public void testLoginNonExistentEmail() {
    // Arrange
    LoginRequestDto dto = new LoginRequestDto();
    dto.setEmail("nobody@example.com");
    dto.setPassword(VALID_PASSWORD);
    dto.setRole("Customer");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(7)
  public void testLoginWrongPassword() {
    // Arrange
    LoginRequestDto dto = new LoginRequestDto();
    dto.setEmail(createdCustomerEmail);
    dto.setPassword("wrongpassword");
    dto.setRole("Customer");

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @Order(8)
  public void testLoginWrongRole() {
    // Arrange
    LoginRequestDto dto = new LoginRequestDto();
    dto.setEmail(createdCustomerEmail);
    dto.setPassword(VALID_PASSWORD);
    dto.setRole("Employee"); // Customer does not have this role

    // Act
    ResponseEntity<String> response =
        unauthClient
            .post()
            .uri("/api/persons/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @Order(9)
  public void testLoginValid() {
    // Arrange
    LoginRequestDto dto = new LoginRequestDto();
    dto.setEmail(createdCustomerEmail);
    dto.setPassword(VALID_PASSWORD);
    dto.setRole("Customer");

    // Act
    ResponseEntity<AuthResponseDto<CustomerResponseDto>> response =
        unauthClient
            .post()
            .uri("/api/persons/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    AuthResponseDto<CustomerResponseDto> body = response.getBody();
    assertNotNull(body);
    assertNotNull(body.getToken());
    assertNotNull(body.getPerson());
    assertEquals(createdCustomerEmail, body.getPerson().getEmail());

    customerToken = body.getToken();
  }

  // ==== PATCH /api/persons/{id}/password ====

  @Test
  @Order(17)
  public void testUpdatePasswordInvalidPersonId() {
    // Arrange
    PersonPasswordUpdateRequestDto dto = new PersonPasswordUpdateRequestDto();
    dto.setOldPassword(VALID_PASSWORD);
    dto.setNewPassword("newpassword123");

    // Act
    ResponseEntity<String> response =
        managerClient
            .patch()
            .uri("/api/persons/" + INVALID_ID + "/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(18)
  public void testUpdatePasswordWrongOldPassword() {
    // Arrange
    PersonPasswordUpdateRequestDto dto = new PersonPasswordUpdateRequestDto();
    dto.setOldPassword("wrongoldpassword");
    dto.setNewPassword("newpassword123");

    // Act
    ResponseEntity<String> response =
        managerClient
            .patch()
            .uri("/api/persons/" + createdCustomerId + "/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @Order(19)
  public void testUpdatePasswordNewTooShort() {
    // Arrange
    PersonPasswordUpdateRequestDto dto = new PersonPasswordUpdateRequestDto();
    dto.setOldPassword(VALID_PASSWORD);
    dto.setNewPassword(SHORT_PASSWORD);

    // Act
    ResponseEntity<String> response =
        managerClient
            .patch()
            .uri("/api/persons/" + createdCustomerId + "/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(20)
  public void testUpdatePasswordValid() {
    // Arrange
    PersonPasswordUpdateRequestDto dto = new PersonPasswordUpdateRequestDto();
    dto.setOldPassword(VALID_PASSWORD);
    dto.setNewPassword("updatedpassword123");

    // Act
    ResponseEntity<Void> response =
        managerClient
            .patch()
            .uri("/api/persons/" + createdCustomerId + "/password")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(Void.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  // ==== PATCH /api/persons/{id}/address ====

  @Test
  @Order(21)
  public void testUpdateAddressEmptyAddress() {
    // Arrange
    RestClient customerClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + customerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    AddressDto dto = new AddressDto();
    dto.setAddress("");

    // Act
    ResponseEntity<String> response =
        customerClient
            .patch()
            .uri("/api/persons/" + createdCustomerId + "/address")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(22)
  public void testUpdateAddressValid() {
    // Arrange
    RestClient customerClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + customerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    AddressDto dto = new AddressDto();
    dto.setAddress("789 New Address Blvd");

    // Act
    ResponseEntity<CustomerResponseDto> response =
        customerClient
            .patch()
            .uri("/api/persons/" + createdCustomerId + "/address")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(CustomerResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    CustomerResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals("789 New Address Blvd", body.getAddress());
  }

  // ==== POST /api/persons/employees ====

  @Test
  @Order(23)
  public void testCreateEmployeePublicSignup() {
    // Employee signup is now public (no token required), anyone can self-register as an employee
    RestClient publicClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    EmployeeCreateRequestDto dto = new EmployeeCreateRequestDto();
    dto.setEmail("public.employee.signup@test.com");
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("456 Public Ave");

    // Act
    ResponseEntity<EmployeeResponseDto> response =
        publicClient
            .post()
            .uri("/api/persons/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(EmployeeResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    EmployeeResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals("public.employee.signup@test.com", body.getEmail());
  }

  @Test
  @Order(24)
  public void testCreateEmployeeValid() {
    // Arrange
    createdEmployeeEmail = "person-test-employee@test.com";
    EmployeeCreateRequestDto dto = new EmployeeCreateRequestDto();
    dto.setEmail(createdEmployeeEmail);
    dto.setPassword(VALID_PASSWORD);
    dto.setAddress("123 Employee St");

    // Act
    ResponseEntity<EmployeeResponseDto> response =
        managerClient
            .post()
            .uri("/api/persons/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
            .retrieve()
            .toEntity(EmployeeResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    EmployeeResponseDto body = response.getBody();
    assertNotNull(body);
    assertNotNull(body.getId());
    assertNotNull(body.getPersonId());
    assertEquals(createdEmployeeEmail, body.getEmail());

    createdEmployeeId = body.getPersonId();
  }

  // ==== POST /api/persons/{id}/roles/employee ====

  @Test
  @Order(28)
  public void testAddEmployeeRoleToPersonAlreadyHasEmployee() {
    // Act
    // createdEmployeeId now has both Customer and Employee roles (from Order 27)
    ResponseEntity<String> response =
        managerClient
            .post()
            .uri("/api/persons/" + createdEmployeeId + "/roles/employee")
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  @Order(29)
  public void testAddEmployeeRoleToNonCustomer() {
    // Act
    // managerPerson only has a Manager role, not a Customer role
    ResponseEntity<String> response =
        managerClient
            .post()
            .uri("/api/persons/" + managerPerson.getPersonID() + "/roles/employee")
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(30)
  public void testAddEmployeeRoleToCustomerValid() {
    // Act
    // createdCustomerId is a pure Customer
    ResponseEntity<EmployeeResponseDto> response =
        managerClient
            .post()
            .uri("/api/persons/" + createdCustomerId + "/roles/employee")
            .retrieve()
            .toEntity(EmployeeResponseDto.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    EmployeeResponseDto body = response.getBody();
    assertNotNull(body);
    assertEquals(createdCustomerId, body.getPersonId());
  }

  // ==== DELETE /api/persons/{id} ====

  @Test
  @Order(31)
  public void testDeletePersonInvalidId() {
    // Act
    ResponseEntity<String> response =
        managerClient.delete().uri("/api/persons/" + INVALID_ID).retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(32)
  public void testDeleteManagerAccountForbidden() {
    // Act
    // Manager cannot delete their own account
    ResponseEntity<String> response =
        managerClient
            .delete()
            .uri("/api/persons/" + managerPerson.getPersonID())
            .retrieve()
            .toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(33)
  public void testDeletePersonValid() {
    // Act
    ResponseEntity<Void> response =
        managerClient
            .delete()
            .uri("/api/persons/" + createdEmployeeId)
            .retrieve()
            .toEntity(Void.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    // Prevent duplicate cleanup in @AfterAll
    createdEmployeeId = null;
    createdEmployeeEmail = null;
  }

  // ==== GET /api/persons/employees ====

  @Test
  @Order(34)
  public void testGetEmployeesUnauthorized() {
    // Arrange
    RestClient customerClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + customerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    // Act
    ResponseEntity<String> response =
        customerClient.get().uri("/api/persons/employees").retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  @Order(35)
  public void testGetEmployeesValid() {
    // Act
    ResponseEntity<List<EmployeeResponseDto>> response =
        managerClient
            .get()
            .uri("/api/persons/employees")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<EmployeeResponseDto> body = response.getBody();
    assertNotNull(body);
    assertFalse(body.isEmpty());
    assertTrue(body.stream().allMatch(p -> p.getEmail() != null));
  }

  // ==== GET /api/persons/customers ====

  @Test
  @Order(36)
  public void testGetCustomersUnauthorized() {
    // Arrange
    RestClient customerClient =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer " + customerToken)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
            .build();

    // Act
    ResponseEntity<String> response =
        customerClient.get().uri("/api/persons/customers").retrieve().toEntity(String.class);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  @Order(37)
  public void testGetCustomersValid() {
    // Act
    ResponseEntity<List<CustomerResponseDto>> response =
        managerClient
            .get()
            .uri("/api/persons/customers")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<CustomerResponseDto> body = response.getBody();
    assertNotNull(body);
    assertFalse(body.isEmpty());
    assertTrue(body.stream().anyMatch(p -> createdCustomerEmail.equals(p.getEmail())));
    assertTrue(
        body.stream()
            .filter(p -> createdCustomerEmail.equals(p.getEmail()))
            .allMatch(p -> p.getId() != null));
  }
}
