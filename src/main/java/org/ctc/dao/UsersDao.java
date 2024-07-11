package org.ctc.dao;

import org.ctc.dto.UserProfileDTO;
import org.ctc.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDao extends JpaRepository<Users, Integer> {
    public boolean existsUsersByMail(String email);

    public Users findByMailAndSecret(String email, String secret);

    public Users findByMail(String email);

}
