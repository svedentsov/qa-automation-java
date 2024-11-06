package db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@Entity
@Table(name = "my_entity")
@NamedQueries({@NamedQuery(
        name = "MyEntity.findByStatus",
        query = "FROM MyEntity WHERE status = :status")})
public class MyEntity {
    @Id
    String id;
    String name;
    String status;
}
