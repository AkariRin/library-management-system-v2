package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.book.BookItemRequest;
import dev.rbq.library_management_system.dto.book.BookItemResponse;
import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.service.BookItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 图书副本控制器
 */
@RestController
@RequestMapping("/api/book-items")
public class BookItemController {

    @Autowired
    private BookItemService bookItemService;

    /**
     * 获取图书副本详情
     * 所有已登录用户都可以访问
     * @param itemId 副本ID
     * @return 图书副本详情
     */
    @GetMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookItemResponse>> getBookItemDetail(@PathVariable Integer itemId) {
        try {
            BookItemResponse response = bookItemService.getBookItemDetail(itemId);
            return ResponseEntity.ok(ApiResponse.success("\u6210\u529f\u83b7\u53d6\u4e66\u7c4d\u526f\u672c\u8be6\u60c5", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u83b7\u53d6\u4e66\u7c4d\u526f\u672c\u8be6\u60c5\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 根据图书ID分页查询副本列表
     * 所有已登录用户都可以访问
     * @param bookId 图书ID
     * @param page 页码（从0开始，默认为0）
     * @param size 每页数量（默认为10）
     * @param status 副本状态（可选）
     * @param sortBy 排序字段（默认为itemId）
     * @param sortDirection 排序方向（asc/desc，默认为desc）
     * @return 图书副本分页列表
     */
    @GetMapping("/book/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BookItemResponse>>> getBookItemsByBookId(
            @PathVariable Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "itemId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            PageResponse<BookItemResponse> response = bookItemService.getBookItemsByBookId(
                    bookId, page, size, status, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("\u4e66\u7c4d\u526f\u672c\u5217\u8868\u83b7\u53d6\u6210\u529f", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u83b7\u53d6\u4e66\u7c4d\u526f\u672c\u5217\u8868\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 添加图书副本
     * 仅管理员可以访问
     * @param request 图书副本请求
     * @return 添加后的图书副本详情
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookItemResponse>> addBookItem(@Valid @RequestBody BookItemRequest request) {
        try {
            BookItemResponse response = bookItemService.addBookItem(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("\u4e66\u7c4d\u526f\u672c\u6dfb\u52a0\u6210\u529f", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u4e66\u7c4d\u526f\u672c\u6dfb\u52a0\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 更新图书副本
     * 仅管理员可以访问
     * @param itemId 副本ID
     * @param request 图书副本请求
     * @return 更新后的图书副本详情
     */
    @PostMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookItemResponse>> updateBookItem(
            @PathVariable Integer itemId,
            @Valid @RequestBody BookItemRequest request) {
        try {
            BookItemResponse response = bookItemService.updateBookItem(itemId, request);
            return ResponseEntity.ok(ApiResponse.success("\u4e66\u7c4d\u526f\u672c\u66f4\u65b0\u6210\u529f", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u4e66\u7c4d\u526f\u672c\u66f4\u65b0\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 删除图书副本
     * 仅管理员可以访问
     * @param itemId 副本ID
     * @return 删除结果
     */
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBookItem(@PathVariable Integer itemId) {
        try {
            bookItemService.deleteBookItem(itemId);
            return ResponseEntity.ok(ApiResponse.success("\u4e66\u7c4d\u526f\u672c\u5220\u9664\u6210\u529f", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u4e66\u7c4d\u526f\u672c\u5220\u9664\u5931\u8d25:" + e.getMessage()));
        }
    }
}
