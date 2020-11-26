package com.leroy.core.util.mountebank;

import org.mbtest.javabank.http.predicates.Predicate;
import org.mbtest.javabank.http.predicates.PredicateType;

import java.util.List;

public class PredicateExtend extends Predicate {

    public PredicateExtend(PredicateType type) {
        super(type);
    }

    public PredicateExtend withPredicates(List<Predicate> predicates) {
        this.put(getType(), predicates);
        return this;
    }
}
