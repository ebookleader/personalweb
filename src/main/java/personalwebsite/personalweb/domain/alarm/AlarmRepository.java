package personalwebsite.personalweb.domain.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select a from Alarm a where a.checked=0 and a.user.id=:userId")
    List<Alarm> findUncheckedAlarmsByUserId(@Param("userId") Long userId);
}
