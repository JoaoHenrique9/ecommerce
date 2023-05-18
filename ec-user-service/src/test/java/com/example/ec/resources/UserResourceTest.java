package com.example.ec.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.ec.dtos.role.RoleRequestDto;
import com.example.ec.dtos.user.UserRequestDto;
import com.example.ec.dtos.user.UserResponseDto;
import com.example.ec.exception.EmailException;
import com.example.ec.exception.PasswordException;
import com.example.ec.models.UserModel;
import com.example.ec.servicers.UserService;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String JOHN_WICK = "John Wick";
    private static final String PRODUCT_RESOURCE_PATH = "/users/";
    private static final String LOCALHOST = "http://localhost";
    private static final String JOHN_EMAIL = "john@gmail.com";
    private static final String PASSWORD = "S3nh@";

    @InjectMocks
    private UserResource userResources;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userResources = new UserResource(userService);
    }

    @Test
    void shouldFindById() {
        var id = RANDOM_UUID;
        var userModel = createUserModel();
        var expectedUserResponseDto = createUserResponseDto();

        when(userService.findById(id)).thenReturn(userModel);
        when(userService.buildUserResponseDto(userModel)).thenCallRealMethod();

        var response = userResources.findById(id);

        verify(userService).buildUserResponseDto(userModel);
        verify(userService).findById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedUserResponseDto);
    }

    @Test
    void shouldFindAll() {
        var userModel = createUserModel();
        var userModelPageableList = new PageImpl<>(Arrays.asList(userModel));
        var pageable = createPageable();
        var expectedUserResponseDtoList = new PageImpl<>(Arrays.asList(createUserResponseDto()));

        when(userService.findAll(pageable)).thenReturn(userModelPageableList);
        when(userService.buildUserResponseDto(userModel)).thenCallRealMethod();

        var response = userResources.findAll(pageable);

        verify(userService).buildUserResponseDto(userModel);
        verify(userService).findAll(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedUserResponseDtoList);
    }

    @Test
    void shouldSave() throws EmailException, PasswordException {
        var userModel = createUserModel();
        var userRequestDto = createUserRequestDto();
        var request = new MockHttpServletRequest("POST", "/users/");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var expectedUri = URI.create(LOCALHOST + PRODUCT_RESOURCE_PATH + RANDOM_UUID);

        when(userService.buildUserModel(userRequestDto, null)).thenReturn(userModel);
        when(userService.save(userModel)).thenReturn(userModel);

        var response = userResources.save(userRequestDto);

        verify(userService).buildUserModel(userRequestDto, null);
        verify(userService).save(userModel);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
    }

    @Test
    void shouldUpdate() throws EmailException, PasswordException {
        var id = RANDOM_UUID;
        var model = createUserModelWithoutId();
        var RequestDto = createUserRequestDto();

        when(userService.buildUserModel(RequestDto, id)).thenReturn(model);
        when(userService.update(model)).thenReturn(model);

        var response = userResources.update(id, RequestDto);

        verify(userService).buildUserModel(RequestDto, id);
        verify(userService)
                .update(Mockito.assertArg((modelToUpdate) -> assertThat(modelToUpdate.getId()).isEqualTo(id)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    void shouldDelete() {
        var id = RANDOM_UUID;

        doNothing().when(userService).delete(id);

        var response = userResources.delete(id);

        verify(userService).delete(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    private UserModel createUserModel() {
        return UserModel.builder()
                .id(RANDOM_UUID)
                .name(JOHN_WICK)
                .email(JOHN_EMAIL)
                .build();
    }

    private UserModel createUserModelWithoutId() {
        return UserModel.builder()
                .name(JOHN_WICK)
                .email(JOHN_EMAIL)
                .build();
    }

    private UserRequestDto createUserRequestDto() {
        return UserRequestDto.builder()
                .name(JOHN_WICK)
                .email(JOHN_EMAIL)
                .password(PASSWORD)
                .roles(Arrays.asList(
                        createRoleRequestDto()))
                .build();
    }

    private RoleRequestDto createRoleRequestDto() {
        return RoleRequestDto.builder().id(1L).build();
    }

    private UserResponseDto createUserResponseDto() {
        return UserResponseDto.builder()
                .id(RANDOM_UUID)
                .name(JOHN_WICK)
                .email(JOHN_EMAIL)
                .build();
    }

    private static Pageable createPageable() {
        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        return PageRequest.of(page, size, sort);
    }
}
