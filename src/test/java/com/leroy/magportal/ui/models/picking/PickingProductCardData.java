package com.leroy.magportal.ui.models.picking;

import lombok.Data;

@Data
public class PickingProductCardData {
   private String lmCode;
   private String barCode;
   private String title;
   private Integer department;
   //private Dimension3D dimension;
   private String dimension;
   private Double price;
   private Double weight;

    private Integer stockQuantity;
    private Integer orderedQuantity;
    private Integer collectedQuantity;


   private static class Dimension3D {
       private Double length;
       private Double width;
       private Double height;
   }

}
