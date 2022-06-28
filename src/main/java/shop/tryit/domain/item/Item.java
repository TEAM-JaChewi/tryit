package shop.tryit.domain.item;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    @OneToOne(mappedBy = "item", fetch = LAZY, cascade = {PERSIST, REMOVE})
    private ItemFile mainImage; // 상품 메인이미지

    @OneToMany(mappedBy = "item", fetch = LAZY, cascade = {PERSIST, REMOVE})
    private List<ItemFile> detailImage = new ArrayList<>(); // 상품 상세이미지

    @Builder
    private Item(String name, int price, int stockQuantity, Category category, ItemFile mainImage, List<ItemFile> detailImage) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.mainImage = mainImage;
        this.detailImage = detailImage;
    }

}
