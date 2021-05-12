package com.example.demo.dto.request;

import com.example.demo.dto.response.CallLogResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.Date;

@Value
public class CallLogRequest {

    long beginning;
    long end;
    long duration;
    String number;
    String name;
    long timesQueried;
}
