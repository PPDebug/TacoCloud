package online.pengpeng.tacocloud.entity;

import lombok.Data;
import lombok.Generated;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pengpeng
 * @date 2022/4/16
 */
@Data
@Entity
public class Taco {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Date createdAt;

    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    @ManyToMany(targetEntity = Ingredient.class)
    @Size(min=1, message="You must choose at least 1 ingredients")
    private List<Ingredient>  ingredients;

    @PrePersist
    void createdAt(){
        this.createdAt = new Date();
    }
}
