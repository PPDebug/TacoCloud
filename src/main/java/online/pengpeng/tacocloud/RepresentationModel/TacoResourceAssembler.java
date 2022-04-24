package online.pengpeng.tacocloud.RepresentationModel;

import online.pengpeng.tacocloud.entity.Taco;
import online.pengpeng.tacocloud.restcontroller.DesignTacoController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@Component
public class TacoResourceAssembler implements RepresentationModelAssembler<Taco, TacoResource> {
    @Override
    public TacoResource toModel(Taco taco) {
        TacoResource tacoResource = new TacoResource(taco);
        tacoResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).tacoByIdHateoas(taco.getId())).withSelfRel());
        return tacoResource;
    }
}
