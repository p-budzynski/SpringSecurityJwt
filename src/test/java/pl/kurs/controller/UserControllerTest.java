package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.User;
import pl.kurs.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnUserById() throws Exception {
        //given
        User savedUser = userRepository.save(new User("userTest", "user@test.com", "Password"));

        //when
        MvcResult mvcResult = mockMvc.perform(get("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        UserDto userDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        assertThat(userDto.getId()).isEqualTo(savedUser.getId());
        assertThat(userDto.getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(userDto.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    void shouldCreateUser() throws Exception {
        //given
        CreateUserDto createUserDto = new CreateUserDto("userTest", "user@test.com", "Password1!");

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        UserDto userDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        assertThat(userDto.getId()).isNotNull();
        assertThat(userRepository.findById(userDto.getId())).isPresent();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void shouldUpdateUserSuccessfully() throws Exception {
        //given
        User savedUser = userRepository.save(new User("userTest", "user@test.com", "Password1!"));
        CreateUserDto createUserDto = new CreateUserDto("newUser", "new@test.com", "Password1!");

        //when
        MvcResult mvcResult = mockMvc.perform(put("/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        UserDto response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        assertThat(response.getId()).isEqualTo(savedUser.getId());
        assertThat(response.getUsername()).isEqualTo(createUserDto.getUsername());
        assertThat(response.getEmail()).isEqualTo(createUserDto.getEmail());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        //given
        User savedUser = userRepository.save(new User("userTest", "user@test.com", "Password1!"));

        //when
        mockMvc.perform(delete("/users/{id}", savedUser.getId()))
                .andExpect(status().isOk());

        //then
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

}