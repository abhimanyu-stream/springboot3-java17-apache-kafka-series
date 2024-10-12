package com.stream.error.handle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.stream.error.handle.dto.UserRequest;
import com.stream.error.handle.util.CsvReaderUtils;
import com.stream.error.handle.model.User;
import com.stream.error.handle.producer.KafkaMessageProducer;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producer")
public class EventController {


    private KafkaMessageProducer producer;

    @Autowired
    public EventController(KafkaMessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish/user")
    public ResponseEntity<?> publishUserEvent(@RequestBody UserRequest userRequest) {


        try {
            User user = User.builder().id(userRequest.getId()).email(userRequest.getEmail()).gender(userRequest.getGender()).firstName(userRequest.getFirstName()).lastName(userRequest.getLastName()).ipAddress(userRequest.getIpAddress()).build();
            producer.sendUserEvent(user);
            return ResponseEntity.ok("Message published successfully");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/publish/csv")
    public ResponseEntity<?> publishEventFromCsvUser() {
        try {

            producer.publishCsvUserEvent();
            return ResponseEntity.ok("Message published successfully");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
