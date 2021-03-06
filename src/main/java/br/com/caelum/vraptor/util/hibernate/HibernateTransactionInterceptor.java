/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.util.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An interceptor that manages Hibernate Transaction. All requests are intercepted
 * and a transaction is created before execution. If the request has no erros, the transaction
 * will commited, or a rollback occurs otherwise.
 * 
 * @author Lucas Cavalcanti
 */
@Intercepts
public class HibernateTransactionInterceptor implements Interceptor {

    private final Session session;
    private final Validator validator;

    public HibernateTransactionInterceptor(Session session, Validator validator) {
        this.session = session;
        this.validator = validator;
    }

    //TODO I think that transaction null check is unnecessary, since we never get null transation (garcia-jj)
    public void intercept(InterceptorStack stack, ResourceMethod method, Object instance) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            stack.next(method, instance);
            if (!validator.hasErrors() && transaction != null) {
                transaction.commit();
            }
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public boolean accepts(ResourceMethod method) {
        return true; // Will intercept all requests
    }
}