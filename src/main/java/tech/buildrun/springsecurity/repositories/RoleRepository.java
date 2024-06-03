package tech.buildrun.springsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.User;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
