package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author pengpeng
 * @date 2022/4/19
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
