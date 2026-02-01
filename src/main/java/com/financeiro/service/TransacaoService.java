package com.financeiro.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de exemplo para demonstrar uso de métricas em serviços
 */
@Service
public class TransacaoService {

    private final MeterRegistry meterRegistry;
    private final Counter transacoesSucessoCounter;
    private final Counter transacoesErroCounter;
    private final Timer transacaoTimer;

    @Autowired
    public TransacaoService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Contador de transações bem-sucedidas
        this.transacoesSucessoCounter = Counter.builder("financeiro.transacoes.sucesso")
                .description("Total de transações bem-sucedidas")
                .tag("tipo", "credito")
                .register(meterRegistry);
        
        // Contador de transações com erro
        this.transacoesErroCounter = Counter.builder("financeiro.transacoes.erro")
                .description("Total de transações com erro")
                .tag("tipo", "credito")
                .register(meterRegistry);
        
        // Timer para medir duração das transações
        this.transacaoTimer = Timer.builder("financeiro.transacoes.duracao")
                .description("Tempo de processamento de transações")
                .tag("tipo", "credito")
                .register(meterRegistry);
    }

    /**
     * Processa uma transação e registra métricas
     */
    public void processarTransacao(double valor) {
        transacaoTimer.record(() -> {
            try {
                // Simula processamento
                Thread.sleep(50);
                
                if (valor > 0) {
                    transacoesSucessoCounter.increment();
                } else {
                    transacoesErroCounter.increment();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                transacoesErroCounter.increment();
            }
        });
    }

    /**
     * Registra métrica customizada para valores específicos
     */
    public void registrarValorTransacao(double valor) {
        // Usa tags diferentes para categorizar
        Counter.builder("financeiro.transacoes.valor")
                .description("Valor das transações")
                .tag("categoria", valor > 1000 ? "alto" : "baixo")
                .register(meterRegistry)
                .increment(valor);
    }
}
