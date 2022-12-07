package com.sivalabs.bookstore.catalog.clients.promotions;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    private String isbn;
    private BigDecimal discount;
}