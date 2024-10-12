package com.stream.error.handle.controller;

import com.stream.error.handle.util.CsvReaderUtils;
import com.stream.error.handle.model.User;
import com.stream.error.handle.producer.KafkaMessageProducer;
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
    public EventController(KafkaMessageProducer publisher) {
        this.producer = publisher;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishEvent() {
        try {
            List<User> users = CsvReaderUtils.readDataFromCsv();
            users.forEach(usr -> producer.sendEvents(usr));
            return ResponseEntity.ok("Message published successfully");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
