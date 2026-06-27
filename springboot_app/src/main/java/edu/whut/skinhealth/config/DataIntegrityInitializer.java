package edu.whut.skinhealth.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("dataIntegrityInitializer")
public class DataIntegrityInitializer implements InitializingBean {
    private final JdbcTemplate jdbcTemplate;

    public DataIntegrityInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            jdbcTemplate.update("DELETE r FROM `record` r LEFT JOIN `user` u ON r.user_id = u.id WHERE u.id IS NULL");
        } catch (DataAccessException ignored) {
            // The schema may not exist yet on a brand-new database; Hibernate can create it afterwards.
        }
    }
}
