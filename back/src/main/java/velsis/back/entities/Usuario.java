package velsis.back.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "usuario")
@Table(name = "usuario", uniqueConstraints = {@UniqueConstraint(columnNames = {"document"})})
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private LocalDate birth_date;
    private String document;
    private String address_line;
    private Integer address_number;
    private String city;
    private String state;
    private String zip;
    private LocalDate created_at;
    private LocalDate updated_at;
}
