package edu.whut.skinhealth.dao;

import edu.whut.skinhealth.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    List<Record> findAllByOrderByTimeDesc();

    List<Record> findByUserUsernameOrderByTimeDesc(String username);

    List<Record> findByLesionProfileIdOrderByTimeDesc(Long lesionProfileId);

    List<Record> findByLesionProfileIdAndUserUsernameOrderByTimeDesc(Long lesionProfileId, String username);

    List<Record> findTop10ByOrderByTimeDesc();

    @Query(value = "SELECT r.disease, COUNT(r.disease) FROM record as r GROUP BY r.disease", nativeQuery = true)
    List<Object> findRecords();

    @Query(value = "SELECT COUNT(*) FROM record WHERE DATE(time) = CURRENT_DATE()", nativeQuery = true)
    Long countTodayRecords();

    @Query(value = "SELECT COUNT(*) FROM record WHERE time >= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
    Long countLast7DaysRecords();

    @Query(value = "SELECT u.gender, COUNT(*) FROM record r JOIN `user` u ON r.user_id = u.id GROUP BY u.gender", nativeQuery = true)
    List<Object> countRecordsByGender();

    @Query(value = "SELECT u.district, COUNT(*) FROM record r JOIN `user` u ON r.user_id = u.id GROUP BY u.district ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<Object> countRecordsByDistrict();

    @Modifying
    @Transactional
    @Query(value = "DELETE r FROM `record` r LEFT JOIN `user` u ON r.user_id = u.id WHERE u.id IS NULL", nativeQuery = true)
    int deleteRecordsWithMissingUser();

    @Query(value="SELECT\n" +
            "    CASE\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 2 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 2 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 3 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 3 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 4 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 4 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 5 MONTH)) THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 5 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH)) THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 7 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 7 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 8 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 8 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 9 MONTH)) THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 9 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 10 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 10 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 11 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 11 MONTH), '%Y-%m')\n" +
            "        WHEN MONTH(time) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH))  THEN DATE_FORMAT(DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH), '%Y-%m')\n" +
            "    END AS month,\n" +
            "    record.disease as disease, COUNT(record.disease) as count \n" +
            "FROM\n" +
            "    record \n" +
            "WHERE\n" +
            "    time >= DATE_SUB(CURRENT_DATE(), INTERVAL 11 MONTH)\n" +
            "GROUP BY\n" +
            "    month, record.disease\n" +
            "ORDER BY\n" +
            "\t\tmonth DESC", nativeQuery = true)
    List<Object> findRecordsByMonth();
}
