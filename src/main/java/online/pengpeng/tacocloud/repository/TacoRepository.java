package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Taco;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface TacoRepository
        extends CrudRepository<Taco, Long> {
}
