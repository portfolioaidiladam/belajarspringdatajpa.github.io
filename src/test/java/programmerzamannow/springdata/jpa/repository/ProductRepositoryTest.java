package programmerzamannow.springdata.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;
import programmerzamannow.springdata.jpa.entity.Category;
import programmerzamannow.springdata.jpa.entity.Product;
import programmerzamannow.springdata.jpa.model.ProductPrice;
import programmerzamannow.springdata.jpa.model.SimpleProduct;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void createProducts() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        {
            Product product = new Product();
            product.setName("Apple iPhone 14 Pro Max");
            product.setPrice(25_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }

        {
            Product product = new Product();
            product.setName("Apple iPhone 13 Pro Max");
            product.setPrice(18_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }
    }

    @Test
    void findByCategoryName() {
        List<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH");
        assertEquals(2, products.size());
        assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());
        assertEquals("Apple iPhone 13 Pro Max", products.get(1).getName());
    }

    @Test
    void sort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        List<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH", sort);
        assertEquals(2, products.size());
        assertEquals("Apple iPhone 13 Pro Max", products.get(0).getName());
        assertEquals("Apple iPhone 14 Pro Max", products.get(1).getName());
    }

    @Test
    void pageable() {
        // page 0
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("GADGET MURAH", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Apple iPhone 13 Pro Max", products.getContent().get(0).getName());

        // page 1
        pageable = PageRequest.of(1, 1, Sort.by(Sort.Order.desc("id")));
        products = productRepository.findAllByCategory_Name("GADGET MURAH", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(1, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Apple iPhone 14 Pro Max", products.getContent().get(0).getName());
    }

    @Test
    void count() {
        Long count = productRepository.count();
        assertEquals(2L, count);

        count = productRepository.countByCategory_Name("GADGET MURAH");
        assertEquals(2L, count);

        count = productRepository.countByCategory_Name("GAK ADA");
        assertEquals(0L, count);
    }

    @Test
    void exists() {
        boolean exists = productRepository.existsByName("Apple iPhone 14 Pro Max");
        assertTrue(exists);

        exists = productRepository.existsByName("Apple iPhone 14 Pro Max SALAH");
        assertFalse(exists);
    }

    @Test
    void deleteOld() {
        transactionOperations.executeWithoutResult(transactionStatus -> { // trasaksi 1
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Product product = new Product();
            product.setName("Samsung Galaxy S9");
            product.setPrice(10_000_000L);
            product.setCategory(category);
            productRepository.save(product); // trasaksi 1

            int delete = productRepository.deleteByName("Samsung Galaxy S9"); // trasaksi 1
            assertEquals(1, delete);

            delete = productRepository.deleteByName("Samsung Galaxy S9"); // trasaksi 1
            assertEquals(0, delete);
        });
    }

    @Test
    void deleteNew() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        Product product = new Product();
        product.setName("Samsung Galaxy S9");
        product.setPrice(10_000_000L);
        product.setCategory(category);
        productRepository.save(product); // trasaksi 1

        int delete = productRepository.deleteByName("Samsung Galaxy S9"); // transaksi 2
        assertEquals(1, delete);

        delete = productRepository.deleteByName("Samsung Galaxy S9"); // transaksi 3
        assertEquals(0, delete);
    }
    // belajar named query, di Repository tidak mendukung Sort
    @Test
    void namedQuery() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Product> products = productRepository.searchProductUsingName("Apple iPhone 14 Pro Max", pageable);
        assertEquals(1, products.size());
        assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());
    }
    // belajar Query Anotation, direpository mendukung sort dan paging
    @Test
    void searchProducts() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.searchProduct("%iPhone%", pageable);
        assertEquals(1, products.getContent().size());

        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());

        products = productRepository.searchProduct("%GADGET%", pageable);
        assertEquals(1, products.getContent().size());

        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());
    }
     // belajar modifying
    @Test
    // karena tidak dikasih @Transactional di javanya maka ini jadi read only, makanya kita buat transactionOperations
    void modifying() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            int total = productRepository.deleteProductUsingName("Wrong");
            assertEquals(0, total);

            total = productRepository.updateProductPriceToZero(1L);
            assertEquals(1, total);

            Product product = productRepository.findById(1L).orElse(null);
            assertNotNull(product);
            assertEquals(0L, product.getPrice());
        });
    }
    // Belajar Stream , dimana operasinya itu lazy
    @Test
    // karena tidak dikasih @Transactional di servicenya maka stream jadi diluar transactional, makanya kita buat transactionOperations
    void stream() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Stream<Product> stream = productRepository.streamAllByCategory(category);
            stream.forEach(product -> System.out.println(product.getId() + " : " + product.getName()));
        });
    }
    // belajar Slice, harus ada pageablenya
    @Test
    void slice() {
        Pageable firstPage = PageRequest.of(0, 1);

        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        Slice<Product> slice = productRepository.findAllByCategory(category, firstPage);
        // tampilkan konten product
        while (slice.hasNext()) {
            slice = productRepository.findAllByCategory(category, slice.nextPageable());
            // tampilkan konten product
        }
    }
    // Belajar Lock, harus jalan transactional mau find 30 juta lalu disleep 20 detik
    @Test
    void lock1() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            try {
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);
                product.setPrice(30_000_000L);

                Thread.sleep(20_000L);
                //save ke database
                productRepository.save(product);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
    // Belajar Lock, harus jalan transactional mau find 10 juta
    @Test
    void lock2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
            assertNotNull(product);
            product.setPrice(10_000_000L);
            productRepository.save(product);
        });
    }
    // belajar specification
    @Test
    void specification() {
        Specification<Product> specification= (root, criteriaQuery, criteriaBuilder) -> {
            // kita bisa mengirim kriteria API kedalam data repository
            return criteriaQuery.where(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 14 Pro Max"),
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 13 Pro Max")
                    )
            ).getRestriction();
        };

        List<Product> products = productRepository.findAll(specification);
        assertEquals(2, products.size());
    }
    // belajar fitur projection dan belajar dynamic projection bikin pake generic
    @Test
    void projection() {
        List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%Apple%", SimpleProduct.class);
        assertEquals(2, simpleProducts.size());

        List<ProductPrice> productPrices = productRepository.findAllByNameLike("%Apple%", ProductPrice.class);
        assertEquals(2, productPrices.size());
    }
}
