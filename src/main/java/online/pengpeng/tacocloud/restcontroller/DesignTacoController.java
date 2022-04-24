package online.pengpeng.tacocloud.restcontroller;

import online.pengpeng.tacocloud.entity.Ingredient;
import online.pengpeng.tacocloud.entity.Taco;
import online.pengpeng.tacocloud.repository.IngredientRepository;
import online.pengpeng.tacocloud.repository.TacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@RestController("restDesignTacoController")
@RequestMapping( path = "/rest/design", produces = "application/json")
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

    @GetMapping("/recent")
    public Iterable<Taco> recentTaco() {
        Pageable page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending()
        );
        return tacoRepo.findAll(page).orElse(null);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco poseTaco(@RequestBody @Valid Taco taco) {
        return tacoRepo.save(taco);
    }
}
