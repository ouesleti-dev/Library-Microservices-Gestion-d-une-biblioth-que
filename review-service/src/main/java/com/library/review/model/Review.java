
package com.library.review.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"bookId", "memberId"}
        )
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private Long memberId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    private String comment;

    public Review() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long v) {
        bookId = v;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long v) {
        memberId = v;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int v) {
        rating = v;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String v) {
        comment = v;
    }
}
