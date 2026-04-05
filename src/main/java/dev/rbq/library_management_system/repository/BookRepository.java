package dev.rbq.library_management_system.repository;

import dev.rbq.library_management_system.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 图书信息数据访问接口
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * 根据ISBN查找图书
     * @param isbn ISBN
     * @return 图书（如果存在）
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * 检查ISBN是否存在
     * @param isbn ISBN
     * @return true 如果ISBN已存在，否则 false
     */
    boolean existsByIsbn(String isbn);

    /**
     * 根据书名模糊查询图书（分页）
     * @param title 书名
     * @param pageable 分页参数
     * @return 图书分页列表
     */
    Page<Book> findByTitleContaining(String title, Pageable pageable);

    /**
     * 根据作者模糊查询图书（分页）
     * @param author 作者
     * @param pageable 分页参数
     * @return 图书分页列表
     */
    Page<Book> findByAuthorContaining(String author, Pageable pageable);

    /**
     * 根据分类查询图书（分页）
     * @param category 分类
     * @param pageable 分页参数
     * @return 图书分页列表
     */
    Page<Book> findByCategory(Integer category, Pageable pageable);

    /**
     * 多条件查询图书（分页）
     * @param title 书名（模糊查询）
     * @param author 作者（模糊查询）
     * @param category 分类（精确查询）
     * @param pageable 分页参数
     * @return 图书分页列表
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:author IS NULL OR b.author LIKE %:author%) AND " +
            "(:category IS NULL OR b.category = :category)")
    Page<Book> findByMultipleConditions(
            @Param("title") String title,
            @Param("author") String author,
            @Param("category") Integer category,
            Pageable pageable
    );
}
