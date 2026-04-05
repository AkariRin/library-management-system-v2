package dev.rbq.library_management_system.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建/更新图书请求 DTO
 */
public class BookRequest {

    @Size(max = 20, message = "The ISBN length must not exceed 20 characters")
    private String isbn;

    @NotBlank(message = "The book title cannot be empty")
    @Size(max = 50, message = "The title length must not exceed 50 characters")
    private String title;

    @NotBlank(message = "The author cannot be empty")
    @Size(max = 50, message = "The author's name must not exceed 50 characters")
    private String author;

    @Size(max = 50, message = "The publisher's name must not exceed 50 characters")
    private String publisher;

    private LocalDate publishDate;

    private Integer category;

    private String summary;

    private BigDecimal price;

    // Constructors
    public BookRequest() {
    }

    public BookRequest(String isbn, String title, String author, String publisher,
                       LocalDate publishDate, Integer category, String summary, BigDecimal price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.category = category;
        this.summary = summary;
        this.price = price;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BookRequest{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishDate=" + publishDate +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}
