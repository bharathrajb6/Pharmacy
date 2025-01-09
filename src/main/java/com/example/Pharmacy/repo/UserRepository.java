package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByContact(String contact);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?1 where u.username = ?2")
    void updatePassword(String password, String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = ?1, u.lastName = ?2, u.email = ?3, u.contact = ?4 where u.username = ?5")
    void updateUserDetails(String firstName, String lastName, String email, String contact, String username);
}
