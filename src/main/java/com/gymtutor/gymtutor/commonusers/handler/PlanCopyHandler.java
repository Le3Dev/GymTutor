package com.gymtutor.gymtutor.commonusers.handler;

/**
 * Define o contrato para qualquer "passo" (handler) na cadeia de responsabilidade
 * de cópia de planos.
 *
 * Qualquer classe que queira participar do processo de cópia deve implementar esta interface.
 */
public interface PlanCopyHandler {

    /**
     * Define o próximo handler (passo) na cadeia.
     * @param next O próximo PlanCopyHandler a ser executado.
     */
    void setNext(PlanCopyHandler next);

    /**
     * Executa a lógica de negócio deste handler específico.
     * @param ctx O "contexto" de cópia, um objeto que carrega os dados
     * através de todos os passos da cadeia.
     */
    void handle(CopyContext ctx);
}