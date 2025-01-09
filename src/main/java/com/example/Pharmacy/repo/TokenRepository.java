package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
            select t from Token t inner join User u on t.user.username=u.username where t.user.username=:username and t.isLoggedOut=false
            """)
    List<Token> findAllTokens(String username);

    Optional<Token> findByToken(String token);

}
