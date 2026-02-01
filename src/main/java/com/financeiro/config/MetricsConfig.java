package com.financeiro.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de métricas customizadas para Prometheus
 */
@Configuration
public class MetricsConfig {

    /**
     * Contador de transações realizadas
     */
    @Bean
    public Counter transacaoCounter(MeterRegistry registry) {
        return Counter.builder("financeiro.transacoes.total")
                .description("Total de transações realizadas")
                .tag("tipo", "geral")
                .register(registry);
    }

    /**
     * Contador de erros da aplicação
     */
    @Bean
    public Counter erroCounter(MeterRegistry registry) {
        return Counter.builder("financeiro.erros.total")
                .description("Total de erros na aplicação")
                .tag("tipo", "geral")
                .register(registry);
    }

    /**
     * Timer para medir duração de operações
     */
    @Bean
    public Timer operacaoTimer(MeterRegistry registry) {
        return Timer.builder("financeiro.operacao.duracao")
                .description("Duração das operações financeiras")
                .tag("tipo", "operacao")
                .register(registry);
    }
}
