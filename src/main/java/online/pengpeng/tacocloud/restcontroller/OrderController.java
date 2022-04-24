package online.pengpeng.tacocloud.restcontroller;

import online.pengpeng.tacocloud.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author pengpeng
 * @date 2022/4/24
 */
@RestController("restOrderController")
@RequestMapping(path = "/rest/orders", produces = "application/json")
@CrossOrigin(origins = "*")
public class OrderController {

//    @PostMapping()
//    public ResponseEntity<Order> addNewOrder() {
//
//    }
}
