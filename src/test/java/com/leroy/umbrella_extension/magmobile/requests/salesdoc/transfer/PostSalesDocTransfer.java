package com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/salesdoc/transfer")
public class PostSalesDocTransfer extends RequestBuilder<PostSalesDocTransfer> {

    public PostSalesDocTransfer setLdap(String val) {
        return header("ldap", val);
    }

}
