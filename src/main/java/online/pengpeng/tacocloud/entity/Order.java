package online.pengpeng.tacocloud.entity;

import lombok.Data;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Data
public class Order {
    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
}
