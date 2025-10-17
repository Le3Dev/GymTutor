package com.gymtutor.gymtutor.commonusers.handler;


public abstract class AbstractPlanCopyHandler implements PlanCopyHandler {
    protected PlanCopyHandler next;

    @Override
    public void setNext(PlanCopyHandler next) {
        this.next = next;
    }

    protected void callNext(CopyContext ctx) {
        if (next != null) next.handle(ctx);
    }

    public abstract void handle(CopyContext ctx);
}