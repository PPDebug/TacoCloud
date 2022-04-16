package online.pengpeng.tacocloud.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Data
@RequiredArgsConstructor
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
