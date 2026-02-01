package com.financeiro.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller de Health Check e exemplos de métricas
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;

    @Autowired
    public HealthController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Cria um contador customizado para requisições deste controller
        this.requestCounter = Counter.builder("financeiro.api.requests")
                .description("Total de requisições na API")
                .tag("endpoint", "health")
                .register(meterRegistry);
    }

    /**
     * Endpoint de status da aplicação
     * Cada chamada incrementa o contador de métricas
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        // Incrementa o contador de requisições
        requestCounter.increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Sistema de Controle Financeiro");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para simular processamento com métrica de tempo
     */
    @GetMapping("/processo")
    public ResponseEntity<Map<String, Object>> processar() {
        // Usa Timer para medir o tempo de execução
        return meterRegistry.timer("financeiro.processo.tempo", "tipo", "exemplo")
                .record(() -> {
                    try {
                        // Simula processamento
                        Thread.sleep(100);
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("status", "processado");
                        response.put("timestamp", LocalDateTime.now());
                        
                        return ResponseEntity.ok(response);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return ResponseEntity.internalServerError().build();
                    }
                });
    }

    /**
     * Endpoint de exemplo com gauge (métrica de valor)
     */
    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> metricas() {
        Map<String, Object> response = new HashMap<>();
        
        // Registra um gauge para memória usada
        Runtime runtime = Runtime.getRuntime();
        long memoriaUsada = runtime.totalMemory() - runtime.freeMemory();
        
        meterRegistry.gauge("financeiro.memoria.usada", memoriaUsada);
        
        response.put("memoria_usada_mb", memoriaUsada / (1024 * 1024));
        response.put("memoria_total_mb", runtime.totalMemory() / (1024 * 1024));
        response.put("processadores", runtime.availableProcessors());
        
        return ResponseEntity.ok(response);
    }
}
