package org.example.globaledugroup.repository;

import jakarta.persistence.QueryHint;
import kotlin.jvm.Volatile;
import org.example.globaledugroup.bot.botDto.OperatorProjection;
import org.example.globaledugroup.entity.RatingOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingOperatorRepository extends JpaRepository<RatingOperator, Long> {
    Optional<RatingOperator> findByTelegramUserId(Long id);

    void deleteByTelegramUserId(Long id);

    @Query("SELECT AVG(ro.rating) FROM RatingOperator ro WHERE ro.operator.id = :operatorId")
    Float findSumOfRatingsByOperatorId(@Param("operatorId") Long operatorId);

    @Query(nativeQuery = true, value = """
    select count(ro.id) as countTelegramUsers, 
           avg(ro.rating) as averageRating, 
           u.first_name as firstName, 
           u.last_name as lastName, 
           u.phone as phone
    from users u
    join operator op on u.id = op.user_id
    left join rating_operator ro on op.id = ro.operator_id
    where u.active = true and u.role='OPERATOR'
    group by op.id, u.id 
    order by u.updated_at
    """)
    List<OperatorProjection> findAllOperatorDto();

}