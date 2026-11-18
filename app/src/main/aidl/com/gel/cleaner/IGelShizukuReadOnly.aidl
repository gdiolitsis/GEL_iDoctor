package com.gel.cleaner;

/** Fixed, read-only operations. No command strings supplied by callers. */
interface IGelShizukuReadOnly {
    String probe(int operationId);
    void destroy() = 16777114;
}
