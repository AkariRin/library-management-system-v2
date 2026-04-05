package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书副本实体类（存储具体物理书籍信息）
 */
@Entity
@Table(name = "book_items")
public class BookItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull(message = "\u4e66\u7c4dID\u4e0d\u80fd\u4e3a\u7a7a")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_book_items_book_id"))
    private Book book;

    @NotBlank(message = "\u6761\u5f62\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max = 50, message = "\u6761\u5f62\u7801\u957f\u5ea6\u4e0d\u80fd\u8d85\u548e50\u4e2a\u5b57\u7b26")
    @Column(name = "barcode", length = 50, nullable = false, unique = true)
    private String barcode;

    @Size(max = 50, message = "The length of the collection location cannot exceed 50 characters")
    @Column(name = "location", length = 50)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BookItemStatus status = BookItemStatus.Available;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "acquisition_price", precision = 10, scale = 2)
    private BigDecimal acquisitionPrice;

    @Size(max = 500, message = "The length of the remarks section must not exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    // Constructors
    public BookItem() {
    }

    public BookItem(Book book, String barcode, String location, BookItemStatus status,
                    LocalDate acquisitionDate, BigDecimal acquisitionPrice, String notes) {
        this.book = book;
        this.barcode = barcode;
        this.location = location;
        this.status = status;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionPrice = acquisitionPrice;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BookItemStatus getStatus() {
        return status;
    }

    public void setStatus(BookItemStatus status) {
        this.status = status;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BigDecimal getAcquisitionPrice() {
        return acquisitionPrice;
    }

    public void setAcquisitionPrice(BigDecimal acquisitionPrice) {
        this.acquisitionPrice = acquisitionPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "itemId=" + itemId +
                ", barcode='" + barcode + '\'' +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", acquisitionDate=" + acquisitionDate +
                ", acquisitionPrice=" + acquisitionPrice +
                '}';
    }

    /**
     * 图书副本状态枚举
     */
    public enum BookItemStatus {
        Available("Available", "Available"),
        Checked_Out("Checked Out", "Checked Out"),
        Lost("Lost", "Lost"),
        Damaged("Damaged", "Damaged"),
        Withdrawn("Withdrawn", "Withdrawn");

        private final String value;
        private final String description;

        BookItemStatus(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}
