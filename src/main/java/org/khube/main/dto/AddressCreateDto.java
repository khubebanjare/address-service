package org.khube.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressCreateDto {

    @NotBlank(message = "Street cannot be blank")
    @Size(min = 1, max = 50, message = "Street must be between 1 and 50 characters")
    private String street;

    @NotBlank(message = "Landmark cannot be blank")
    @Size(min = 1, max = 50, message = "Landmark must be between 1 and 50 characters")
    private String landmark;

    @NotBlank(message = "City cannot be blank")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    @NotBlank(message = "Country cannot be blank")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;

    @NotNull(message = "Pin code cannot be null")
    private Integer pinCode;
}
