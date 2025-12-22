package com.eventos.pf.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Eventos PF.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Compra compra = new Compra();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Compra getCompra() {
        return compra;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class Compra {

        /**
         * TTL por inactividad de la sesión activa de compra (consigna: 30 minutos, parametrizable).
         *
         * Ejemplo YAML: application.compra.sesion-ttl: PT30M
         */
        private Duration sesionTtl = Duration.ofMinutes(30);

        public Duration getSesionTtl() {
            return sesionTtl;
        }

        public void setSesionTtl(Duration sesionTtl) {
            this.sesionTtl = sesionTtl;
        }
    }
    // jhipster-needle-application-properties-property-class
}
