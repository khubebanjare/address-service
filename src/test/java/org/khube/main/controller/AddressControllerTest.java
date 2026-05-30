package org.khube.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;
import org.khube.main.service.AddressService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {
    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(addressController).build();
    }

    private AddressCreateDto validCreateDto() {
        return new AddressCreateDto("Street", "Landmark", "City", "State", "Country", 123456);
    }

    private AddressDto validDto() {
        return new AddressDto(1L, "Street", "Landmark", "City", "State", "Country", 123456);
    }

    @Nested
    @DisplayName("POST /api/addresses/create")
    class CreateAddress {
        @Test
        void createsAddressSuccessfully() throws Exception {
            AddressCreateDto createDto = validCreateDto();
            AddressDto dto = validDto();
            when(addressService.createAddress(any(AddressCreateDto.class))).thenReturn(dto);

            mockMvc().perform(post("/api/addresses/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.addressId", is(dto.getAddressId().intValue())))
                    .andExpect(jsonPath("$.street", is(dto.getStreet())));

            verify(addressService).createAddress(any(AddressCreateDto.class));
        }

        @Test
        void throwsOnNullRequestBody() throws Exception {
            mockMvc().perform(post("/api/addresses/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("null"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/addresses/fetch/{addressId}")
    class FetchAddressById {
        @Test
        void fetchesAddressSuccessfully() throws Exception {
            AddressDto dto = validDto();
            when(addressService.fetchAddressById(1L)).thenReturn(dto);

            mockMvc().perform(get("/api/addresses/fetch/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.addressId", is(dto.getAddressId().intValue())));

            verify(addressService).fetchAddressById(1L);
        }

        @Test
        void throwsOnNullId(){
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    addressController.fetchAddressById(null));
            assertEquals("Address ID must not be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("GET /api/addresses/fetch/all")
    class FetchAllAddresses {
        @Test
        void fetchesAllAddressesSuccessfully() throws Exception {
            List<AddressDto> list = Arrays.asList(validDto(), new AddressDto(2L, "S2", "L2", "C2", "ST2", "CO2", 654321));
            when(addressService.fetchAllAddresses()).thenReturn(list);

            mockMvc().perform(get("/api/addresses/fetch/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].addressId", is(list.get(0).getAddressId().intValue())))
                    .andExpect(jsonPath("$[1].addressId", is(list.get(1).getAddressId().intValue())));

            verify(addressService).fetchAllAddresses();
        }
    }
}

