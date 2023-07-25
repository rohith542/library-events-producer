package com.vw.libraryeventsproducer.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class LibraryEvent {

    Integer libraryEventId;

    LibraryEventType libraryEventType;

    Book book;
}
