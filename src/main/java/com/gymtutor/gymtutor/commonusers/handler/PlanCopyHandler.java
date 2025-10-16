package com.gymtutor.gymtutor.commonusers.handler;

public interface PlanCopyHandler {
    void setNext(PlanCopyHandler next);
    void handle(CopyContext ctx);
}
