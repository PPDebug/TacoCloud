package online.pengpeng.tacocloud.repository;

import online.pengpeng.tacocloud.entity.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pengpeng
 * @date 2022/4/17
 */
@Repository
public class JdbcTacoRepository implements TacoRepository{

    private JdbcTemplate jdbc;

    private SimpleJdbcInsert tacoInserter;

    @Autowired
    public JdbcTacoRepository (JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.tacoInserter = new SimpleJdbcInsert(jdbc)
                .withTableName("Taco")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Taco save(Taco taco) {
//        long tacoId = saveTacoInfo(taco); // 这种方法一直不成功，我也不知道原因
        long tacoId = saveTacoInfoSimple(taco);
        taco.setId(tacoId);
        for (String ingredient : taco.getIngredients()) {
            saveIngredientToTaco(ingredient, tacoId);
        }
        return taco;
    }

    private long saveTacoInfo(Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
                "insert into Taco (name, createdAt) values (?, ?)",
                Types.VARCHAR,
                Types.TIMESTAMP)
                .newPreparedStatementCreator(
                    Arrays.asList(
                        taco.getName(),
                        new Timestamp(taco.getCreatedAt().getTime())
                )
                );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(psc, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private long saveTacoInfoSimple(Taco taco){
        taco.setCreatedAt(new Date());
        Map<String, Object> values = new HashMap<>();
        values.put("name", taco.getName());
        values.put("createdAt", new Timestamp(taco.getCreatedAt().getTime()));
        int id = tacoInserter.execute(values);
        return id;
    }

    private void saveIngredientToTaco(String ingredient, long tacoId){
        jdbc.update(
                "insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
                tacoId,
                ingredient
        );
    }
}
