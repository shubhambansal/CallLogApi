package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class ServicesResponse {

    private Date start;
    private List<Services> services;

}


