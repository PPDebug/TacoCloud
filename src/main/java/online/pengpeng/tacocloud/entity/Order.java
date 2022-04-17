package online.pengpeng.tacocloud.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Data
public class Order {
    private Long id;
    private Date placeAt;
    private List<Taco> tacos;

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "street is required")
    private String street;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "zip is required")
    private String zip;

    @CreditCardNumber(message = "Not a valid credit card number")
    private String ccNumber;

    @Pattern(regexp = "!(0[1-9]|1[0-2])([\\/])([1-9][0-9])$", message = "Must be formatted MM/YY")
    private String ccExpiration;

    @Digits(integer = 3, fraction = 0, message = "Invalid CVV")
    private String ccCVV;

    public void addDesign(Taco taco){
        if (tacos == null){
            tacos = new ArrayList<>();
        }
        tacos.add(taco);
    }
}
