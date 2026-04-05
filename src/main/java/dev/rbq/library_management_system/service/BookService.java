package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.book.BookRequest;
import dev.rbq.library_management_system.dto.book.BookResponse;
import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.entity.Book;
import dev.rbq.library_management_system.entity.BookItem;
import dev.rbq.library_management_system.repository.BookItemRepository;
import dev.rbq.library_management_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookItemRepository bookItemRepository;

    /**
     * 获取图书详情
     * @param bookId 图书ID
     * @return 图书详情
     */
    public BookResponse getBookDetail(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("The book does not exist"));
        return convertToResponse(book);
    }

    /**
     * 分页查询图书列表
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @param title 书名（可选，模糊查询）
     * @param author 作者（可选，模糊查询）
     * @param category 分类（可选，精确查询）
     * @param sortBy 排序字段（默认为bookId）
     * @param sortDirection 排序方向（asc/desc，默认为desc）
     * @return 图书分页列表
     */
    public PageResponse<BookResponse> getBookList(
            int page, int size, String title, String author, Integer category,
            String sortBy, String sortDirection) {

        // 创建排序对象
        Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy != null ? sortBy : "bookId").ascending()
                : Sort.by(sortBy != null ? sortBy : "bookId").descending();

        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size, sort);

        // 执行查询
        Page<Book> bookPage;
        if (title != null || author != null || category != null) {
            bookPage = bookRepository.findByMultipleConditions(title, author, category, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        // 转换为响应对象
        List<BookResponse> content = bookPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast(),
                bookPage.isEmpty()
        );
    }

    /**
     * 添加图书
     * @param request 图书请求
     * @return 添加后的图书详情
     */
    @Transactional
    public BookResponse addBook(BookRequest request) {
        // 检查ISBN是否已存在
        if (request.getIsbn() != null && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("ISBN already exists");
        }

        // 创建图书实体
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategory(request.getCategory());
        book.setSummary(request.getSummary());
        book.setPrice(request.getPrice());

        // 保存图书
        Book savedBook = bookRepository.save(book);

        return convertToResponse(savedBook);
    }

    /**
     * 更新图书
     * @param bookId 图书ID
     * @param request 图书请求
     * @return 更新后的图书详情
     */
    @Transactional
    public BookResponse updateBook(Integer bookId, BookRequest request) {
        // 查找图书
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("The book does not exist"));

        // 检查ISBN是否已被其他图书使用
        if (request.getIsbn() != null && !request.getIsbn().equals(book.getIsbn())) {
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new IllegalArgumentException("This ISBN is already in use by another book");
            }
        }

        // 更新图书信息
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategory(request.getCategory());
        book.setSummary(request.getSummary());
        book.setPrice(request.getPrice());

        // 保存图书
        Book savedBook = bookRepository.save(book);

        return convertToResponse(savedBook);
    }

    /**
     * 删除图书
     * @param bookId 图书ID
     */
    @Transactional
    public void deleteBook(Integer bookId) {
        // 检查图书是否存在
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("The book does not exist.");
        }

        // 删除图书（会级联删除所有副本）
        bookRepository.deleteById(bookId);
    }

    /**
     * 将图书实体转换为响应对象
     * @param book 图书实体
     * @return 图书响应对象
     */
    private BookResponse convertToResponse(Book book) {
        BookResponse response = new BookResponse(
                book.getBookId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getCategory(),
                book.getSummary(),
                book.getPrice()
        );

        // 统计副本数量
        long totalCopies = bookItemRepository.countByBookId(book.getBookId());
        long availableCopies = bookItemRepository.countByBookIdAndStatus(
                book.getBookId(), BookItem.BookItemStatus.Available);

        response.setTotalCopies(totalCopies);
        response.setAvailableCopies(availableCopies);

        return response;
    }
}

