package online.pengpeng.tacocloud.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Data
public class Taco {
    private String name;
    private List<String> ingredients;
}
