package dev.rbq.library_management_system.dto.borrow;

import jakarta.validation.constraints.NotNull;

/**
 * 借阅请求 DTO
 */
public class BorrowRequest {

    @NotNull(message = "The book copy ID cannot be empty")
    private Integer itemId;

    // Constructors
    public BorrowRequest() {
    }

    public BorrowRequest(Integer itemId) {
        this.itemId = itemId;
    }

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
