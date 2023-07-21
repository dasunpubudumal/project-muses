package org.realtix.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class BookRow extends ConversionBound {
    private String id;
    private String title;
    private List<Author> authors;
    @JsonProperty(value = "author_name")
    private String authorName;
    @JsonProperty(value = "author_id")
    private String authorId;
    private String isbn;
    private String asin;
    private String language;
    @JsonProperty(value = "average_rating")
    private Double averageRating;
    @JsonProperty(value = "rating_dist")
    private String ratingDist;
    @JsonProperty(value = "ratings_count")
    private Long ratingsCount;
    @JsonProperty(value = "publication_date")
    private String publicationDate;
    @JsonProperty(value = "original_publication_date")
    private String originalPublicationDate;
    private String format;
    @JsonProperty(value = "edition_information")
    private String editionInformation;
    @JsonProperty(value = "image_url")
    private String imageUrl;
    private String publisher;
    @JsonProperty(value = "num_pages")
    private Integer numPages;
    @JsonProperty(value = "series_id")
    private String seriesId;
    @JsonProperty(value = "series_name")
    private String seriesName;
    @JsonProperty(value = "series_position")
    private String seriesPosition;
    private String description;

    public static List<BookRow> ofList(String listOfBooks) throws JsonProcessingException {
        return ObjectMapperSingleton.INSTANCE.mapper().readValue(
                listOfBooks,
                new TypeReference<List<BookRow>>() {
                }
        );
    }
}
