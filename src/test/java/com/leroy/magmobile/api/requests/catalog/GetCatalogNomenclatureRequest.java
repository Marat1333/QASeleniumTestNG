package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/nomenclature")
public class GetCatalogNomenclatureRequest extends CommonLegoRequest<GetCatalogNomenclatureRequest> {
}
