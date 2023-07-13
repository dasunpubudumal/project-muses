package org.realtix.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import org.realtix.ObjectMapperSingleton;
import org.realtix.s3.ConversionBound;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRow extends ConversionBound {

    private String isbn;

    public static List<BookRow> ofList(String listOfBooks) throws JsonProcessingException {
        return ObjectMapperSingleton.INSTANCE.mapper().readValue(
                listOfBooks,
                new TypeReference<List<BookRow>>() {
                }
        );
    }
}
