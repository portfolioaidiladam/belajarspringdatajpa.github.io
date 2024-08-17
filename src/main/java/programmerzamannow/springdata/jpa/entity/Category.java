package programmerzamannow.springdata.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
// perlu ditambahkan untuk belajar auditing karena ini bawaan dari Entity listener
@EntityListeners({AuditingEntityListener.class})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
    // akan diubah datanya setelah proses di insert (belajar auditing)
    // nah dia akan sekali doang tidak akan pernah diubah lagi
    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;
    // kalau ini setiap kali kita melakukan perubahan, dia akan diubah terus (belajar auditing)
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
}
