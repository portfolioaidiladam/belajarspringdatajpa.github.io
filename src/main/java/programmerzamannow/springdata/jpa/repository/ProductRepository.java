package programmerzamannow.springdata.jpa.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import programmerzamannow.springdata.jpa.entity.Category;
import programmerzamannow.springdata.jpa.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    <T> List<T> findAllByNameLike(String name, Class<T> tClass);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findFirstByIdEquals(Long id);
    // belajar Slice, harus ada pageablenya
    Slice<Product> findAllByCategory(Category category, Pageable pageable);
    // Belajar Stream , dimana operasinya itu lazy
    Stream<Product> streamAllByCategory(Category category);
    // belajar modifying
    @Modifying
    @Query("delete from Product p where p.name = :name")
    int deleteProductUsingName(@Param("name") String name);
    // belajar modifying
    @Modifying
    @Query("update Product p set p.price = 0 where p.id = :id")
    int updateProductPriceToZero(@Param("id") Long id);
     // belajar Query Anotation, direpository mendukung sort dan paging
    @Query(
            value = "select p from Product p where p.name like :name or p.category.name like :name",
            countQuery = "select count(p) from Product p where p.name like :name or p.category.name like :name"
    )
    Page<Product> searchProduct(@Param("name") String name, Pageable pageable);


    // belajar named query, di Repository tidak mendukung Sort
    List<Product> searchProductUsingName(@Param("name") String name, Pageable pageable);

    @Transactional
    int deleteByName(String name);

    boolean existsByName(String name);

    Long countByCategory_Name(String name);

    List<Product> findAllByCategory_Name(String name);

    List<Product> findAllByCategory_Name(String name, Sort sort);

    Page<Product> findAllByCategory_Name(String name, Pageable pageable);

}
