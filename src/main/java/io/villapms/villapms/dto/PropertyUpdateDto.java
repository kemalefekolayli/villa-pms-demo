package io.villapms.villapms.dto;

import lombok.Data;

@Data
public class PropertyUpdateDto {
    private Long locationId;
    private String name;
    private String description;

    // Villa management
    private Integer totalVillas;
    private String villaType;
    private Boolean isMultiVilla;

    // Villa specifications (per villa)
    private Integer size;
    private Integer bedNum;
    private Integer personSize;
    private String propertyAddress;
    private Boolean animalsAllowed;

    // Pricing (per villa)
    private Integer nightlyRate;
    private Integer cleaningFee;
    private Integer securityDeposit;
}
