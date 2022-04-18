package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface OrderRepository
        extends CrudRepository<Order, Long> {
    List<Order> findAllByZip(String zip);
    List<Order> readOrdersByZipAndPlaceAtBetween(
            String zip, Date startDate, Date endDate
    );

    @Query("select o from Order o where o.city='Seattle'")
    List<Order> readOrdersInSeattle();
}
