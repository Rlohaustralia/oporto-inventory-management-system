package oporto.inventory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@RequiredArgsConstructor
public class Orders {
    private int id;
    private String branchId;
    private String menuId;
    private String menuName;
    private int hqQuantity;
    private int orderQuantity;
    private Timestamp orderDate;


    public Orders(String branchId, String menuId, String menuName, int hqQuantity, int orderQuantity) {
        // Order details
        this.branchId = branchId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.hqQuantity = hqQuantity;
        this.orderQuantity = orderQuantity;
        this.orderDate = new Timestamp(System.currentTimeMillis());
    }
}




