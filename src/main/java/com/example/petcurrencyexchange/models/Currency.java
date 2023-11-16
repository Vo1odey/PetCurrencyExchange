package com.example.petcurrencyexchange.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Currency {
    private int id;
    private String code;
    private String name;
    private String sign;
}
