package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Order;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface OrderRepository {
    Order save(Order order);
}
