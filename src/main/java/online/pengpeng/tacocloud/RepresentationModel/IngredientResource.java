package online.pengpeng.tacocloud.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import online.pengpeng.tacocloud.entity.Ingredient;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@Data
@Relation(value = "ingredient", collectionRelation = "ingredients")
public class IngredientResource extends RepresentationModel<IngredientResource> {
    private String name;
    private String type;

    public IngredientResource(Ingredient ingredient){
        this.name = ingredient.getName();
        this.type = ingredient.getType().toString();
    }
}
