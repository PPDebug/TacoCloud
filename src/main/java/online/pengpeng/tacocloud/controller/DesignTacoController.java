package online.pengpeng.tacocloud.controller;

import lombok.extern.slf4j.Slf4j;
import online.pengpeng.tacocloud.entity.Order;
import online.pengpeng.tacocloud.entity.Taco;
import online.pengpeng.tacocloud.repository.IngredientRepository;
import online.pengpeng.tacocloud.repository.TacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import online.pengpeng.tacocloud.entity.Ingredient;
import online.pengpeng.tacocloud.entity.Ingredient.Type;

import javax.validation.Valid;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    @ModelAttribute(name = "order")
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco(){
        return new Taco();
    }

    private final IngredientRepository ingredientRepository;
    private final TacoRepository designRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository designRepository){
        this.ingredientRepository = ingredientRepository;
        this.designRepository = designRepository;
    }

    @GetMapping
    public  String showDesignFrom(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);
        Type[] types = Ingredient.Type.values();
        for (Type type:types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("design", new Taco());
        return "design";
    }

    @PostMapping
    public String processDesign(
            @Valid
            Taco design, Errors errors,
            @ModelAttribute Order order,
            Model model) {
        if (errors.hasErrors()){
            log.error(errors.toString());
            return "design";
        }
        Taco saved = designRepository.save(design);
        order.addDesign(saved);
        return "redirect:/orders/orderForm";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type){
        return ingredients
                .stream()
                .filter(x->x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
