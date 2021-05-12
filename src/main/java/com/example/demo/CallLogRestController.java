package com.example.demo;

import com.example.demo.dto.request.CallLogRequest;
import com.example.demo.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Slf4j
public class CallLogRestController {

    private CallLogRequest currentOngoingCall;
    private final List<CallLogResponse> callLogList = new ArrayList<>();
    private int statusQueryCounter = 0;

    @GetMapping
    public ResponseEntity<ServicesResponse> getEndPoints() {

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            System.out.println("Host Name:- " + inetAddress.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        Services status = new Services("status", "http://" + inetAddress.getHostAddress() + ":8080/status");
        Services log = new Services("log", "http://" + inetAddress.getHostAddress() + ":8080/log");

        return ResponseEntity.ok(ServicesResponse.builder().start(new Date()).services(List.of(status, log)).build());

    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallStatusResponse> getCallStatus() {

        CallStatusResponse dto = null;
        if (currentOngoingCall != null) {
            dto = new CallStatusResponse(true, currentOngoingCall.getNumber(), currentOngoingCall.getName());

            //We also have to updated the number of times it's get queried while phone call was in progress
            statusQueryCounter += 1;
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CallLogResponse>> getCallLog() {
        return ResponseEntity.ok(callLogList);
    }

    @PostMapping(value = "/callStart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> setCallStart(@RequestBody CallLogRequest request) {

        if (currentOngoingCall != null) {

            log.info("Rejecting Call start request from {}", request.getNumber());
            return ResponseEntity.ok(new ApiResponse(true, HttpStatus.IM_USED.value(), "Another call is in progress!"));
        }

        currentOngoingCall = request;
        return ResponseEntity.ok(new ApiResponse(true, HttpStatus.OK.value(), null));
    }

    @PutMapping(value = "/callEnd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> setCallEnd(@RequestBody CallLogRequest request) {

        //If there is a ongoing request then only it make sense to remove the entry and add to a log
        if (currentOngoingCall != null && request.getNumber().equals(currentOngoingCall.getNumber())) {


            long duration = getTotalDuration(currentOngoingCall.getBeginning(), request.getEnd());
            CallLogResponse logResponse = CallLogResponse.builder().beginning(request.getBeginning()).number(request.getNumber()).name(request.getName()).duration(duration).timesQueried(statusQueryCounter).build();
            statusQueryCounter = 0; //Resetting the counter back so we can count again from fresh
            callLogList.add(logResponse);

            //Resetting it back
            currentOngoingCall = null;

            return ResponseEntity.ok(new ApiResponse(true, HttpStatus.OK.value(), null));
        } else {
            log.info("Rejecting Call end request from {}", request.getNumber());
            return ResponseEntity.ok(new ApiResponse(true, HttpStatus.CONFLICT.value(), "Cannot end a call for now!"));
        }
    }

    /**
     * Fn to get difference between two given dates
     *
     * @param startDateTime
     * @param endDateTime
     * @return value in minutes
     */
    private long getTotalDuration(long startDateTime, long endDateTime) {

        long durationInMillis = endDateTime - startDateTime;

        return durationInMillis / 1000;
    }
}
