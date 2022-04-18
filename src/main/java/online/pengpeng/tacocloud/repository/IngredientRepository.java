package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Ingredient;
import org.springframework.data.repository.CrudRepository;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface IngredientRepository
        extends CrudRepository<Ingredient, String> {
}
