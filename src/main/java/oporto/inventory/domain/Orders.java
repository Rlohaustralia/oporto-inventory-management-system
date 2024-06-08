package oporto.inventory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@RequiredArgsConstructor
public class Orders {
    private String id;
    private String branchId;
    private String menuId;
    private int orderQuantity;
    private Timestamp orderDate;

    public Orders(String branchId, String menuId, int orderQuantity, Timestamp orderDate) {
        this.branchId = branchId;
        this.menuId = menuId;
        this.orderQuantity = orderQuantity;
        this.orderDate = orderDate;
    }
}




