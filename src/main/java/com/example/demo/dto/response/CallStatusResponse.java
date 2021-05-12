package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CallStatusResponse {

    private final boolean ongoing;
    private final String number;
    private final String name;
}
