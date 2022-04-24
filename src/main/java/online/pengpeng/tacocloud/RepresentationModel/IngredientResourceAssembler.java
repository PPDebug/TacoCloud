package online.pengpeng.tacocloud.RepresentationModel;

import online.pengpeng.tacocloud.entity.Ingredient;
import online.pengpeng.tacocloud.restcontroller.DesignTacoController;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@Component
public class IngredientResourceAssembler implements RepresentationModelAssembler<Ingredient, IngredientResource> {
    @Override
    public IngredientResource toModel(Ingredient entity) {
        IngredientResource ingredientResource = new IngredientResource(entity);
        return ingredientResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).getIngredientById(entity.getId())).withSelfRel());
    }
}
