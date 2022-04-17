package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Ingredient;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface IngredientRepository {

    Iterable<Ingredient> findAll();

    Ingredient findOne(String id);

    Ingredient save(Ingredient ingredient);
}
