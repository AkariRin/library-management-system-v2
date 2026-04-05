package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.book.BookRequest;
import dev.rbq.library_management_system.dto.book.BookResponse;
import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取图书详情
     * 所有已登录用户都可以访问
     * @param bookId 图书ID
     * @return 图书详情
     */
    @GetMapping("/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookResponse>> getBookDetail(@PathVariable Integer bookId) {
        try {
            BookResponse response = bookService.getBookDetail(bookId);
            return ResponseEntity.ok(ApiResponse.success("书籍详情获取成功", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取书籍详情失败:" + e.getMessage()));
        }
    }

    /**
     * 分页查询图书列表
     * 所有已登录用户都可以访问
     * @param page 页码（从0开始，默认为0）
     * @param size 每页数量（默认为10）
     * @param title 书名（可选，模糊查询）
     * @param author 作者（可选，模糊查询）
     * @param category 分类（可选，精确查询）
     * @param sortBy 排序字段（默认为bookId）
     * @param sortDirection 排序方向（asc/desc，默认为desc）
     * @return 图书分页列表
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getBookList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "bookId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            PageResponse<BookResponse> response = bookService.getBookList(
                    page, size, title, author, category, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("书籍列表获取成功", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取书籍列表失败:" + e.getMessage()));
        }
    }

    /**
     * 添加图书
     * 仅管理员可以访问
     * @param request 图书请求
     * @return 添加后的图书详情
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@Valid @RequestBody BookRequest request) {
        try {
            BookResponse response = bookService.addBook(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("书籍添加成功", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("书籍添加失败:" + e.getMessage()));
        }
    }

    /**
     * 更新图书
     * 仅管理员可以访问
     * @param bookId 图书ID
     * @param request 图书请求
     * @return 更新后的图书详情
     */
    @PostMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Integer bookId,
            @Valid @RequestBody BookRequest request) {
        try {
            BookResponse response = bookService.updateBook(bookId, request);
            return ResponseEntity.ok(ApiResponse.success("书籍更新成功", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("书籍更新失败:" + e.getMessage()));
        }
    }

    /**
     * 删除图书
     * 仅管理员可以访问
     * @param bookId 图书ID
     * @return 删除结果
     */
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Integer bookId) {
        try {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok(ApiResponse.success("书籍删除成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("书籍删除失败:" + e.getMessage()));
        }
    }
}
