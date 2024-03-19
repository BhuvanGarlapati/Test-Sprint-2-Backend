package kdu.ibe.backend.repositories;

import kdu.ibe.backend.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
}