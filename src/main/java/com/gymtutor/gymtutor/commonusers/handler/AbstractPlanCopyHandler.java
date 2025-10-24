package com.gymtutor.gymtutor.commonusers.handler;

/**
 * Uma classe base abstrata que implementa a lógica comum de "encadeamento".
 * Isso evita que cada handler concreto precise reimplementar a lógica de
 * "chamar o próximo".
 */
public abstract class AbstractPlanCopyHandler implements PlanCopyHandler {

    /**
     * Referência para o próximo handler na cadeia.
     */
    protected PlanCopyHandler next;

    /**
     * Armazena a referência ao próximo handler.
     * Isso é usado para construir a cadeia (veja PlanCopyHandlersConfig).
     */
    @Override
    public void setNext(PlanCopyHandler next) {
        this.next = next;
    }

    /**
     * Um método utilitário para chamar o próximo handler, se ele existir.
     * Se 'next' for nulo, a cadeia termina aqui.
     * @param ctx O contexto de cópia.
     */
    protected void callNext(CopyContext ctx) {
        if (next != null) {
            next.handle(ctx);
        }
    }

    /**
     * Este é o método que cada handler concreto DEVE implementar
     * com sua própria lógica de negócio (ex: clonar o plano, clonar workouts, etc.).
     */
    public abstract void handle(CopyContext ctx);
}