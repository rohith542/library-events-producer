package com.vw.libraryeventsproducer.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vw.libraryeventsproducer.domain.LibraryEvent;
import com.vw.libraryeventsproducer.domain.LibraryEventType;
import com.vw.libraryeventsproducer.producer.LibraryEventsProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class LibraryEventsController {

    private LibraryEventsProducer eventsProducer;

    @PostMapping("/v1/libraryEvent")
    public ResponseEntity<?> postLibraryEvent(
            @RequestBody LibraryEvent libraryEvent
    ) throws JsonProcessingException {


        //eventsProducer.sendLibraryEvent(libraryEvent);



        eventsProducer.sendLibraryEvent_approach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }


    @PutMapping("/v1/libraryEvent")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        ResponseEntity<String> BAD_REQUEST = getStringResponseEntity(libraryEvent);

        if (BAD_REQUEST != null) return BAD_REQUEST;

        eventsProducer.sendLibraryEvent_approach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }

    private static ResponseEntity<String> getStringResponseEntity(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the library Event id");
        }

        if(!libraryEvent.getLibraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only Event type Update is required.");
        }
        return null;
    }


}
