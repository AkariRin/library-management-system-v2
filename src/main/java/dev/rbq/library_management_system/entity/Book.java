package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 图书信息实体类（存储图书品种信息）
 */
@Entity
@Table(name = "books")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Size(max = 20, message = "The ISBN length must not exceed 20 characters")
    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @NotBlank(message = "The book title cannot be left blank")
    @Size(max = 50, message = "The title length must not exceed 50 characters")
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @NotBlank(message = "The author cannot be empty")
    @Size(max = 50, message = "The author's name must not exceed 50 characters")
    @Column(name = "author", length = 50, nullable = false)
    private String author;

    @Size(max = 50, message = "The publisher's name must not exceed 50 characters")
    @Column(name = "publisher", length = 50)
    private String publisher;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "category")
    private Integer category;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookItem> bookItems;

    // Constructors
    public Book() {
    }

    public Book(String isbn, String title, String author, String publisher, LocalDate publishDate,
                Integer category, String summary, BigDecimal price) {
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
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

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

    public List<BookItem> getBookItems() {
        return bookItems;
    }

    public void setBookItems(List<BookItem> bookItems) {
        this.bookItems = bookItems;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishDate=" + publishDate +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}
