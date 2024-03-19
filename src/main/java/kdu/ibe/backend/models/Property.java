package kdu.ibe.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    Long property_id;

    @Column(name = "tenant_id", nullable = false)
    String tenant_id;

    @Column(name = "property_name", nullable = false)
    String property_name;
}
