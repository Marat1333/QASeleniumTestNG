package com.leroy.core.api;

import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.configuration.Log;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ThreadApiClient<RD, CL extends BaseClient> extends Thread {

    private CL apiClient;
    private SendRequest<RD, CL> usingFunction;
    private RD data;
    private UserSessionData userSessionData;

    public ThreadApiClient(CL apiClient) {
        this.apiClient = apiClient;
        this.userSessionData = ContextProvider.getContext().getUserSessionData();
    }

    @Override
    public void run() {
        Log.debug(getName() + " have started");
        if (apiClient instanceof BaseMashupClient)
            ((BaseMashupClient) apiClient).setUserSessionData(userSessionData);
        Response<RD> response = usingFunction.execute(apiClient);
        data = response.asJson();
        Log.debug(getName() + " has ended");
    }

    public void sendRequest(SendRequest<RD, CL> fun) {
        this.usingFunction = fun;
        this.start();
    }

    public RD getData() throws InterruptedException {
        join();
        return data;
    }
}
