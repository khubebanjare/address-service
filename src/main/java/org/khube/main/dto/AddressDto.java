package org.khube.main.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressDto {

    private Long addressId;
    private String street;
    private String landmark;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;
}
