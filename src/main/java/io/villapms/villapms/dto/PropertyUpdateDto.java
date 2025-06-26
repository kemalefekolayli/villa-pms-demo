
package io.villapms.villapms.dto;

import lombok.Data;

@Data
public class PropertyUpdateDto {
    private Long locationId;
    private String name;
    private String description;
    private Integer size;
    private Integer bedNum;
    private Integer personSize;
    private String propertyAddress;
    private Boolean animalsAllowed;
    private Integer nightlyRate;
}