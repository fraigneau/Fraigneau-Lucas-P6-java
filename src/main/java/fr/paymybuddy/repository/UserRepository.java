package fr.paymybuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.paymybuddy.model.User;
import org.springframework.lang.NonNull;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    Optional<User> findById(@NonNull Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
