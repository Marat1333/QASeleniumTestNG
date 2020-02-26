package com.leroy.umbrella_extension;

import ru.leroymerlin.qa.core.clients.base.Response;

@FunctionalInterface
public interface SendRequest<D, CL> {

    Response<D> execute(CL client);

}
