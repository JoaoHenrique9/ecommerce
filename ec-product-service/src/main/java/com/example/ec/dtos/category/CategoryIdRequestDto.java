package com.example.ec.dtos.category;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryIdRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;

}
