package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.FunUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FunUserRepository extends JpaRepository<FunUser, Long> {
//    List<FunUser> findByPhoneLike(String phone);
//
//    List<FunUser> findByFullNameLike(String name);


    @Query(nativeQuery = true, value = "SELECT * FROM fun_user WHERE phone LIKE CONCAT('%', :text, '%') OR full_name LIKE CONCAT('%', :text, '%')")
    List<FunUser> searchUser(String text);

    List<FunUser> findByPhone(String phone);

    List<FunUser> findByFullName(String name);
}
