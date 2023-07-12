package org.realtix.transfer;

import lombok.*;
import org.realtix.s3.ConversionBound;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRow extends ConversionBound {
    private String isbn;
}
