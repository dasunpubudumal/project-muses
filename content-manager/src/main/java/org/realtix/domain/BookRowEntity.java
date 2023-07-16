package org.realtix.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.realtix.s3.ConversionBound;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Builder
public class BookRowEntity extends ConversionBound {

    private String id;
    private String title;
    private List<AuthorEntity> authors;
    private String authorName;
    private String authorId;
    private String isbn;
    private String asin;
    private String language;
    private Double averageRating;
    private String ratingDist;
    private Long ratingsCount;
    private String publicationDate;
    private String originalPublicationDate;
    private String format;
    private String editionInformation;
    private String imageUrl;
    private String publisher;
    private Integer numPages;
    private String seriesId;
    private String seriesName;
    private String seriesPosition;
    private String description;

    @DynamoDbAttribute(value = "id")
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbAttribute(value = "title")
    @DynamoDbSecondaryPartitionKey(indexNames = {"title-index"})
    @DynamoDbSecondarySortKey(indexNames = {"author_id-title-index"})
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute(value = "authors")
    public List<AuthorEntity> getAuthors() {
        return authors;
    }

    @DynamoDbAttribute(value = "author_name")
    public String getAuthorName() {
        return authorName;
    }

    @DynamoDbAttribute(value = "author_id")
    @DynamoDbSecondaryPartitionKey(indexNames = {"author_id-title-index"})
    public String getAuthorId() {
        return authorId;
    }

    @DynamoDbAttribute(value = "isbn")
    public String getIsbn() {
        return isbn;
    }

    @DynamoDbAttribute(value = "asin")
    public String getAsin() {
        return asin;
    }

    @DynamoDbAttribute(value = "language")
    public String getLanguage() {
        return language;
    }

    @DynamoDbAttribute(value = "average_rating")
    public Double getAverageRating() {
        return averageRating;
    }

    @DynamoDbAttribute(value = "rating_dist")
    public String getRatingDist() {
        return ratingDist;
    }

    @DynamoDbAttribute(value = "ratings_count")
    public Long getRatingsCount() {
        return ratingsCount;
    }

    @DynamoDbAttribute(value = "publication_date")
    public String getPublicationDate() {
        return publicationDate;
    }

    @DynamoDbAttribute(value = "original_publication_date")
    public String getOriginalPublicationDate() {
        return originalPublicationDate;
    }

    @DynamoDbAttribute(value = "format")
    public String getFormat() {
        return format;
    }

    @DynamoDbAttribute(value = "edition_information")
    public String getEditionInformation() {
        return editionInformation;
    }

    @DynamoDbAttribute(value = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @DynamoDbAttribute(value = "publisher")
    public String getPublisher() {
        return publisher;
    }

    @DynamoDbAttribute(value = "num_pages")
    public Integer getNumPages() {
        return numPages;
    }

    @DynamoDbAttribute(value = "series_id")
    public String getSeriesId() {
        return seriesId;
    }

    @DynamoDbAttribute(value = "series_name")
    public String getSeriesName() {
        return seriesName;
    }

    @DynamoDbAttribute(value = "series_position")
    public String getSeriesPosition() {
        return seriesPosition;
    }

    @DynamoDbAttribute(value = "description")
    public String getDescription() {
        return description;
    }

}
