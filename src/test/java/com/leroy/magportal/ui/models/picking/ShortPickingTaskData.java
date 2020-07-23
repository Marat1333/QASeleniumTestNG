package com.leroy.magportal.ui.models.picking;

import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.salesdoc.IDataWithNumberAndStatus;
import lombok.Data;

import java.util.List;

@Data
public class ShortPickingTaskData implements IDataWithNumberAndStatus {

    private String number;
    //private String orderLinkNumber;
    private PickingConst.AssemblyType assemblyType;
    private List<Integer> departments;
    private String status;
    private String client;
    private String collector;
    private String creationDate;
    private Double weight;
    private Double maxSize;
}
