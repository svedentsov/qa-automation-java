package db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(fluent = true)
@Entity
@Table(name = "my_entity")
@NamedQueries({@NamedQuery(
        name = "MyEntity.findByStatus",
        query = "FROM MyEntity WHERE status = :status")
})
public class MyEntity {
    @Id
    private String id;
    private String name;
    private String status;
    private String type;
    private String role;
    private String description;
    private String email;
    private String middleName;
    private Integer age;
    private BigDecimal score;
    @ElementCollection
    private List<String> roles;
    private LocalDateTime creationDate;
    @Transient
    private Optional<String> middleNameOptional;
    @Embedded
    private Address address;
    @ElementCollection
    private List<Role> roleEntities;
}

@Data
@Embeddable
class Address {
    private String city;
    private String street;
}

@Data
class Role {
    private String name;
    private String description;
}
