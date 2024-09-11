package org.example.globaledugroup.repository;

import org.example.globaledugroup.projections.UserFullNameProjection;
import org.example.globaledugroup.entity.User;
import org.example.globaledugroup.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //    @Query("SELECT u.firstName as firstName, u.lastName as lastName FROM User u")
    List<UserFullNameProjection> findAllByRole(Role role);

    List<UserFullNameProjection> findAllByRoleAndActiveTrue(Role role);


    Optional<User> findByFirstNameAndLastNameAndActiveTrue(String firstName, String lastName);

    Optional<User> findByTelegramUserId(Long chatId);

    //active false bolganla
    List<User> findAllByCreatedByIdAndActiveFalse(Long id);

    Optional<User> findByPhone(String phone);

    List<User> findAllByActiveFalse();

    boolean existsByPhone(String phone);


}
