package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Taco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
public interface TacoRepository
        extends CrudRepository<Taco, Long> {
    Optional<List<Taco>> findAll(Pageable page);
}
