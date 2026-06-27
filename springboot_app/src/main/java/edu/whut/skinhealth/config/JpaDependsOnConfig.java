package edu.whut.skinhealth.config;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class JpaDependsOnConfig extends EntityManagerFactoryDependsOnPostProcessor {
    public JpaDependsOnConfig() {
        super("dataIntegrityInitializer");
    }
}
