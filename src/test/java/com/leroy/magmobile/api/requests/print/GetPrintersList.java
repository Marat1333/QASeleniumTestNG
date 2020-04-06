package com.leroy.magmobile.api.requests.print;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/printing/printers")
public class GetPrintersList extends CommonSearchRequestBuilder<GetPrintersList> {
}
