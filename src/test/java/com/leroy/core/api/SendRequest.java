package com.leroy.core.api;

import ru.leroymerlin.qa.core.clients.base.Response;

@FunctionalInterface
public interface SendRequest<D, CL> {

    Response<D> execute(CL client);

}
