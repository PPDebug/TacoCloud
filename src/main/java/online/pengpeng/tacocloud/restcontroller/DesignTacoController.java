package online.pengpeng.tacocloud.restcontroller;

import online.pengpeng.tacocloud.RepresentationModel.IngredientResource;
import online.pengpeng.tacocloud.RepresentationModel.IngredientResourceAssembler;
import online.pengpeng.tacocloud.RepresentationModel.TacoResource;
import online.pengpeng.tacocloud.RepresentationModel.TacoResourceAssembler;
import online.pengpeng.tacocloud.entity.Ingredient;
import online.pengpeng.tacocloud.entity.Taco;
import online.pengpeng.tacocloud.repository.IngredientRepository;
import online.pengpeng.tacocloud.repository.TacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


/**
 * @author pengpeng
 * @date 2022/4/24
 */
@RestController("restDesignTacoController") // 由于之前的MVC架构的代码依然保留着，所以需要自定义bean的名字（类可以重名是因为放在了不同的包下）
@RequestMapping( path = "/rest/design", produces = "application/json")
@CrossOrigin(origins = "*") // 允许跨域请求
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private final TacoRepository tacoRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
    }

    @GetMapping("/ingredients")
    public ResponseEntity<Iterable<Ingredient>> allIngredient() {
        return ResponseEntity.ok(ingredientRepo.findAll());
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientResource> getIngredientById(@PathVariable String id) {
        Optional<Ingredient> opt = ingredientRepo.findById(id);
        if (opt.isPresent()) {
            IngredientResource ingredientResource = new IngredientResource(opt.get());
            ingredientResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).getIngredientById(id)).withSelfRel());
            return ResponseEntity.ok(ingredientResource);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/recent")
    public Iterable<Taco> recentTaco() {
        Pageable page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending()
        );
        return tacoRepo.findAll(page).orElse(null);
    }


    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody @Valid Taco taco) {
        return tacoRepo.save(taco);
    }

    @GetMapping("/{id}")
    public Taco tacoById(@PathVariable("id") Long id) {
        Optional<Taco> optTaco = tacoRepo.findById(id);
        return optTaco.orElse(null);
    }

    // Hypermedia as the engine of application status
    @GetMapping("/hateoas/{id}")
    public HttpEntity<TacoResource> tacoByIdHateoas(@PathVariable("id") Long id) {
        Optional<Taco> optTaco = tacoRepo.findById(id);
        if (optTaco.isPresent()){
            TacoResource tacoResource = new TacoResource(optTaco.get());
            tacoResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).tacoByIdHateoas(id)).withSelfRel());
            return ResponseEntity.ok(tacoResource);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Autowired
    private TacoResourceAssembler tacoResourceAssembler;

    @GetMapping("/recent/hateoas")
    public ResponseEntity<CollectionModel<TacoResource>> recentTacoResources(){
        Pageable page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending()
        );
        Optional<List<Taco>> all = tacoRepo.findAll(page);
        if (all.isPresent()){
            CollectionModel<TacoResource> tacoResources = tacoResourceAssembler.toCollectionModel(all.get());
            tacoResources.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).recentTacoResources()).withRel("recentTaco"));
            return ResponseEntity.ok(tacoResources);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


}
