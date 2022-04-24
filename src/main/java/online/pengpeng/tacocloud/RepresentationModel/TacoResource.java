package online.pengpeng.tacocloud.RepresentationModel;

import lombok.Data;
import online.pengpeng.tacocloud.entity.Ingredient;
import online.pengpeng.tacocloud.entity.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@Data
@Relation(value = "taco", collectionRelation = "tacos")
public class TacoResource extends RepresentationModel<TacoResource> {

    private static final IngredientResourceAssembler
            ingredientAssembler = new IngredientResourceAssembler();
    private Date createdAt;
    private String name;
    private CollectionModel<IngredientResource> ingredients;

    public TacoResource(Taco taco) {
        this.createdAt = taco.getCreatedAt();
        this.name = taco.getName();
        this.ingredients = ingredientAssembler.toCollectionModel(taco.getIngredients());
    }
}
