package com.swnur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponseDTO {

    private Long id;

    private String code;

    private String name;

    private String sign;

}
