package com.example.demo.dto.response;

import com.example.demo.dto.request.CallLogRequest;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CallLogResponse {

    long beginning;
    long duration;
    String number;
    String name;
    long timesQueried;
}
