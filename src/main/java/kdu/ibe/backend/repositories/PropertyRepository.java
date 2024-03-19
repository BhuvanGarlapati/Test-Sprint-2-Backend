package kdu.ibe.backend.repositories;

import kdu.ibe.backend.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {}