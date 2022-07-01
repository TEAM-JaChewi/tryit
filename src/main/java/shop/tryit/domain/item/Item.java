package shop.tryit.domain.item;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.tryit.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_id")
    private Long id; // 상품 식별자

    private String name; // 상품 이름
    private int price; // 상품 가격
    private int stockQuantity; // 상품 재고

    @Enumerated(STRING)
    private Category category; // 상품 카테고리 [DOG, CAT]

    @OneToMany(mappedBy = "item", fetch = LAZY, cascade = {PERSIST, REMOVE})
    private List<Image> images; // 상품 이미지

    @Builder
    private Item(String name, int price, int stockQuantity, Category category, List<Image> images) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.images = images;
    }

    public void update(String name, int price, int stockQuantity, Category category) {
        changeName(name);
        changePrice(price);
        changeStockQuantity(stockQuantity);
        changeCategory(category);
    }

    private void changeName(String name) {
        this.name = name;
    }

    private void changePrice(int price) {
        this.price = price;
    }

    private void changeStockQuantity(int stockQuantity) {
        this.stockQuantity = price;
    }

    private void changeCategory(Category category) {
        this.category = category;
    }

    public void addImage(Image image) {
        image.setItem(this);
        images.add(image);
    }

}
