package online.pengpeng.tacocloud.controller;

import lombok.extern.slf4j.Slf4j;
import online.pengpeng.tacocloud.entity.Order;
import online.pengpeng.tacocloud.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    private OrderRepository orderRepo;

    @Autowired
    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/orderForm")
    public String orderForm(@ModelAttribute Order order){
        return "current";
    }

    @PostMapping
    public String processOrder(
            @Valid
            @ModelAttribute Order order,
            Errors errors,
            SessionStatus sessionStatus) {
        if (errors.hasErrors()){
            return "current";
        }
        orderRepo.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }
}
