package com.vw.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vw.libraryeventsproducer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    private String topic;


    private final KafkaTemplate<Integer,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(ObjectMapper objectMapper, KafkaTemplate<Integer, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();

        String value= objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<SendResult<Integer, String>> send = kafkaTemplate.send(topic, key, value);

        send.whenComplete((sendResult, throwable) -> {
                    if(throwable != null){
                        handleError(key,value,throwable);
                    }else {
                        handleSuccess(key,value,sendResult);
                    }
                });

    }


    //Blocking event to send the message to the Kafka topic before sending the HTTP status as success

    public void sendLibraryEvent_apprach2(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        Integer key = libraryEvent.getLibraryEventId();

        String value= objectMapper.writeValueAsString(libraryEvent);

        SendResult<Integer, String> send = kafkaTemplate.send(topic, key, value).get(5, TimeUnit.SECONDS);

        handleSuccess(key,value,send);

    }


    public void sendLibraryEvent_approach3(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();

        String value= objectMapper.writeValueAsString(libraryEvent);


        ProducerRecord<Integer,String> producerRecord=buildProducerRecord(key,value);

        CompletableFuture<SendResult<Integer, String>> send = kafkaTemplate.send(producerRecord);

        send.whenComplete((sendResult, throwable) -> {

            if(throwable!=null){
                handleError(key,value,throwable);
            }
            else {
                handleSuccess(key,value,sendResult);
            }

        });


    }

    private ProducerRecord<Integer,String> buildProducerRecord(Integer key, String value) {


        List<Header> recordHeaders = List.of(new RecordHeader("event-source","scanner".getBytes()));

        return  new ProducerRecord<>(topic,null,key,value,recordHeaders);


    }

    private void handleSuccess(int key, String value, SendResult<Integer, String> sendResult) {

        log.info("Message has been successfully sent with the details key {} , message {}, partition {}",key,value,sendResult.getRecordMetadata().partition());


    }

    private void handleError(int key, String value, Throwable throwable) {

        log.error("Error sending the message and the exception is {}", throwable.getMessage());
    }


}
