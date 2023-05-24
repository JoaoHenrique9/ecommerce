package com.example.ec.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.ec.dtos.role.RoleRequestDto;
import com.example.ec.dtos.user.UserRequestDto;
import com.example.ec.exception.EmailException;
import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.exception.PasswordException;
import com.example.ec.models.Role;
import com.example.ec.models.UserModel;
import com.example.ec.repositories.UserRepository;
import com.example.ec.utils.StringUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String JOHN_WICK = "John Wick";
    private static final String MORPHEUS = "Morpheus";
    private static final String MORPHEUS_Email = "morpheus@gmail.com";
    private static final String JOHN_EMAIL = "john@gmail.com";
    private static final String PASSWORD = "S3nh@123456";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldFindById() {
        UUID id = RANDOM_UUID;
        UserModel expectedUserModel = createUserModel();
        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUserModel));

        var actualUserModel = userService.findById(id);

        assertThat(actualUserModel).usingRecursiveComparison().isEqualTo(expectedUserModel);

        assertNotNull(expectedUserModel.getCreatedAt());
        assertEquals(actualUserModel.getCreatedAt(), expectedUserModel.getCreatedAt());
        assertNotEquals(actualUserModel.getCreatedAt(), "");

        assertThat(actualUserModel.getRoles())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedUserModel.getRoles());

    }

    @Test
    void shouldThrowObjectNotFoundExceptionWhenFindById() {
        var id = RANDOM_UUID;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Usuario não encontrado");
    }

    @Test
    void shouldFindAll() {
        var expectedUserModelPageableList = new PageImpl<UserModel>(Arrays.asList(createUserModel()));
        var pageable = createPageable();
        when(userRepository.findAll(pageable)).thenReturn(expectedUserModelPageableList);

        var actualUserModelPageableList = userService.findAll(pageable);

        verify(userRepository).findAll(pageable);
        assertThat(actualUserModelPageableList).usingRecursiveComparison()
                .isEqualTo(expectedUserModelPageableList);
    }

    @Test
    void shouldSave() {
        var userModel = createUserModel();
        var expectedUserModel = createUserModel();
        when(userRepository.save(userModel)).thenReturn(expectedUserModel);

        var actualUserModel = userService.save(userModel);

        verify(userRepository).save(userModel);
        assertThat(actualUserModel).usingRecursiveComparison().isEqualTo(expectedUserModel);
    }

    @Test
    void shouldDeleteUser() {
        var id = RANDOM_UUID;
        var userModel = createUserModel();
        when(userRepository.findById(id)).thenReturn(Optional.of(userModel));
        doNothing().when(userRepository).delete(userModel);

        userService.delete(id);

        verify(userRepository).delete(userModel);
    }

    @Test
    void shouldUpdateUser() {
        var id = RANDOM_UUID;
        var userModel = createUserModel();
        var updatedUserModel = updatedUserModel();
        when(userRepository.findById(id)).thenReturn(Optional.of(userModel));
        when(userRepository.save(userModel)).thenReturn(userModel);

        var result = userService.update(updatedUserModel);

        verify(userRepository).findById(userModel.getId());
        verify(userRepository).save(userModel);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userModel.getId());
        assertThat(result.getName()).isEqualTo(updatedUserModel.getName());
    }

    @Test
    public void shouldBuildUserModel() throws EmailException, PasswordException {

        UserRequestDto userRequestDto = createUserRequestDto();

        UserModel result = userService.buildUserModel(userRequestDto, null);

        assertNull(result.getCreatedAt());
        assertNotNull(result.getPassword());

        assertThat(result.getEmail()).isNotNull().isNotEmpty();
        assertThat(result.getName()).isNotNull().isNotEmpty();

        assertThat(result.getRoles().stream().map(Role::getId).collect(Collectors.toSet()))
                .isEqualTo(userRequestDto.getRoles().stream().map(RoleRequestDto::getId).collect(Collectors.toSet()))
                .isNotEmpty()
                .isNotEqualTo("");

        assertThat(result)
                .isNotNull()
                .isNotEqualTo("")
                .extracting(UserModel::getName, UserModel::getEmail)
                .contains(userRequestDto.getName(), userRequestDto.getEmail());

        verify(userRepository).findByEmail(userRequestDto.getEmail());
    }

    @Test
    public void shouldBuildUserModelThrowsEmailException() throws EmailException {
        UUID id = UUID.randomUUID();
        UserModel existingUserModel = UserModel.builder().email(JOHN_EMAIL).build();
        UserRequestDto userRequestDto = createUserRequestDto();

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(existingUserModel);

        assertThatThrownBy(() -> userService.validateUser(userRequestDto.getEmail(), id))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Já possui um usuario cadastrado com esse email");

        verify(userRepository).findByEmail(userRequestDto.getEmail());
    }

    @Test
    public void shouldValidateUser() {
        String existingEmail = JOHN_EMAIL;
        UUID id = UUID.randomUUID();
        UserModel existingUser = UserModel.builder().id(id).email(existingEmail).build();

        when(userRepository.findByEmail(existingEmail)).thenReturn(existingUser);

        assertDoesNotThrow(() -> userService.validateUser(existingEmail, id));
    }

    @Test
    public void shouldThrowEmailExceptionWhenUserWithSameEmailAlreadyExistsButDifferentId() {
        String existingEmail = JOHN_EMAIL;
        UUID id = UUID.randomUUID();
        UserModel existingUserButDifferentId = UserModel.builder().id(UUID.randomUUID()).email(existingEmail).build();

        when(userRepository.findByEmail(existingEmail)).thenReturn(existingUserButDifferentId);

        assertThatThrownBy(() -> userService.validateUser(existingEmail, id))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Já possui um usuario cadastrado com esse email");
    }

    @Test
    public void shouldNotThrowEmailExceptionWhenUserWithSameEmailDoesNotExist() {

        String nonExistingEmail = JOHN_EMAIL;
        UUID id = UUID.randomUUID();

        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(null);

        assertDoesNotThrow(() -> userService.validateUser(nonExistingEmail, id));
    }

    @Test
    public void shouldThrowEmailExceptionWhenUserWithSameEmaiExistAndIdIsNull() {

        String email = JOHN_EMAIL;
        UUID id = null;
        UserModel existingUser = UserModel.builder().id(UUID.randomUUID()).email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        assertThatThrownBy(() -> userService.validateUser(email, id))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Já possui um usuario cadastrado com esse email");
    }

    @Test
    public void shouldNotThrowEmailExceptionWhenIdIsNull() {
        String nonExistingEmail = JOHN_EMAIL;

        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(null);

        assertDoesNotThrow(() -> userService.validateUser(nonExistingEmail, null));
    }

    // mudar nome
    @Test
    void buildUserModel_ShouldThrowPasswordException_WhenIdIsNullAndPasswordIsEmpty() {

        UserRequestDto dto = UserRequestDto.builder().email(JOHN_EMAIL).password("").build();
        UUID id = null;

        assertThatThrownBy(() -> userService.buildUserModel(dto, id))
                .isInstanceOf(PasswordException.class)
                .hasMessageContaining("A senha não pode ser vazia");
    }

    // mudar nome
    @Test
    void buildUserModel_ShouldChangePasswordToNullWhenIdIsNotNullAndPasswordIsEmpty()
            throws EmailException, PasswordException {

        UserRequestDto dto = UserRequestDto.builder().email(JOHN_EMAIL).password("").build();
        UUID id = RANDOM_UUID;

        var result = userService.buildUserModel(dto, id);
        assertNull(result.getPassword());

    }

    @Test
    void buildUserModel_ShouldThrowPasswordException_WhenIdIsNotNullAndPasswordHave5Character() {
        UserRequestDto dto = UserRequestDto.builder().email(JOHN_EMAIL).password("12345").build();
        UUID id = null;

        Assertions.assertTrue(dto.getPassword().length() <= 5);
        assertThatThrownBy(() -> userService.buildUserModel(dto, id))
                .isInstanceOf(PasswordException.class)
                .hasMessageContaining("A senha deve ter pelo menos 6 caracteres");
    }

    @Test
    public void testBuildPasswordWithShortInput() {
        UserRequestDto dto = UserRequestDto.builder().email(JOHN_EMAIL).password("12345").build();
        UUID id = null;

        Assertions.assertTrue(dto.getPassword().length() <= 5);
        assertThatThrownBy(() -> userService.buildUserModel(dto, id))
                .isInstanceOf(PasswordException.class)
                .hasMessageContaining("A senha deve ter pelo menos 6 caracteres");
        assertThrows(PasswordException.class, () -> userService.buildUserModel(dto, id));
    }

    private UserModel createUserModel() {
        return UserModel.builder()
                .id(RANDOM_UUID)
                .name(JOHN_WICK)
                .email(JOHN_EMAIL)
                .password(StringUtils.encoder(PASSWORD))
                .createdAt(new Date())
                .roles(new HashSet<Role>(Arrays.asList(createRole())))
                .build();
    }

    private UserModel updatedUserModel() {
        return UserModel.builder()
                .id(RANDOM_UUID)
                .name(MORPHEUS)
                .email(MORPHEUS_Email)
                .password(StringUtils.encoder(PASSWORD))
                .roles(new HashSet<Role>(Arrays.asList(createRole())))
                .build();
    }

    private RoleRequestDto createRoleRequestDto() {
        return RoleRequestDto.builder().id(1l).build();
    }

    private Role createRole() {
        return Role.builder().id(1l).roleName("ROLE_ADMIN").build();
    }

    private UserRequestDto createUserRequestDto() {
        return UserRequestDto.builder()
                .name(MORPHEUS)
                .email(MORPHEUS_Email)
                .password(PASSWORD)
                .roles(Arrays.asList(createRoleRequestDto()))
                .build();
    }

    private static Pageable createPageable() {
        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        return PageRequest.of(page, size, sort);
    }

}
