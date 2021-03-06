package shop.tryit.repository.item;

import static java.util.stream.Collectors.toMap;
import static shop.tryit.domain.item.entity.QImage.image;
import static shop.tryit.domain.item.entity.QItem.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.tryit.domain.item.Category;
import shop.tryit.domain.item.ImageType;
import shop.tryit.domain.item.ItemSearchCondition;
import shop.tryit.domain.item.dto.ItemSearchDto;
import shop.tryit.domain.item.dto.QItemSearchDto;
import shop.tryit.domain.item.entity.Image;

@Repository
@RequiredArgsConstructor
public class ItemCustomImpl implements ItemCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemSearchDto> searchItems(ItemSearchCondition condition, Pageable pageable) {
        List<ItemSearchDto> itemSearchDtoList = searchContent(condition, pageable);
        injectMainImage(itemSearchDtoList);
        return PageableExecutionUtils.getPage(itemSearchDtoList, pageable, totalCount(condition)::fetchOne);
    }

    @Override
    @SuppressWarnings("all")
    public ItemSearchDto findItemSearchDtoByOrderDetailId(Long orderDetailItemId) {
        QItemSearchDto qItemListDto = new QItemSearchDto(item.id, item.name, item.price);
        ItemSearchDto itemSearchDto = queryFactory
                .select(qItemListDto)
                .from(item)
                .where(item.id.eq(orderDetailItemId))
                .fetchOne();

        Image itemMainImage = findMainImageByOrderDetailItemId(orderDetailItemId);
        itemSearchDto.injectMainImage(itemMainImage);

        return itemSearchDto;
    }

    private Image findMainImageByOrderDetailItemId(Long orderDetailItemId) {
        return queryFactory.selectFrom(image)
                .where(
                        image.type.eq(ImageType.MAIN),
                        item.id.in(orderDetailItemId)
                )
                .join(image.item, item)
                .fetchOne();
    }

    /**
     * ?????? ????????? ??????
     */
    private void injectMainImage(List<ItemSearchDto> itemSearchDtoList) {
        Map<Long, ItemSearchDto> map = itemSearchDtoList.stream()
                .collect(toMap(ItemSearchDto::getId, Function.identity()));

        searchMainImage(map.keySet())
                .forEach(image -> map.get(image.getItemId()).injectMainImage(image));
    }

    /**
     * ??? ?????? ?????? ??????
     */
    private JPAQuery<Long> totalCount(ItemSearchCondition condition) {
        return queryFactory
                .select(item.count())
                .from(item)
                .where(
                        isContainName(condition.getName()),
                        isContainCategory(condition.getCategory())
                );
    }

    /**
     * ?????? ?????? ?????? (?????? ????????? ??????)
     */
    private List<ItemSearchDto> searchContent(ItemSearchCondition condition, Pageable pageable) {
        QItemSearchDto qItemListDto = new QItemSearchDto(item.id, item.name, item.price);
        return queryFactory
                .select(qItemListDto)
                .from(item)
                .where(
                        isContainName(condition.getName()),
                        isContainCategory(condition.getCategory())
                )
                .orderBy(item.createDate.desc()) // ?????? ?????? ????????? ??? ????????? ????????? ??????
                .limit(pageable.getPageSize())   // ??? ???????????? ????????? ??????
                .offset(pageable.getOffset())    // ??????????????? ????????? ?????????
                .fetch();
    }

    /**
     * ?????? ?????? ?????? (?????? ?????????)
     */
    private List<Image> searchMainImage(Collection<Long> itemIds) {
        return queryFactory.selectFrom(image)
                .where(
                        image.type.eq(ImageType.MAIN),
                        item.id.in(itemIds)
                )
                .join(image.item, item)
                .fetch();
    }

    /**
     * ?????? ?????? ?????? ??????
     */
    private BooleanExpression isContainName(String name) {
        // ?????? ???????????? ?????? ??? ?????? ???????????? ????????? null ??????
        return StringUtils.hasText(name) ? item.name.contains(name):null;
    }

    /**
     * ?????? ???????????? ?????? ??????
     */
    private BooleanExpression isContainCategory(Category category) {
        // ??????????????? ?????? ??? ?????? ??????????????? ???????????? ????????? null ??????
        return Optional.ofNullable(category).isPresent() ? item.category.eq(category):null;
    }

}