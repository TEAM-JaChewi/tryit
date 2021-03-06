package shop.tryit.domain.cart.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.tryit.domain.cart.dto.CartItemDto;
import shop.tryit.domain.cart.dto.CartListDto;
import shop.tryit.domain.cart.entity.Cart;
import shop.tryit.domain.cart.entity.CartItem;
import shop.tryit.domain.item.entity.Image;
import shop.tryit.domain.item.entity.Item;
import shop.tryit.domain.item.service.ImageService;
import shop.tryit.domain.item.service.ItemService;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartFacade {

    private final CartItemService cartItemService;
    private final CartService cartService;
    private final ItemService itemService;
    private final ImageService imageService;

    /**
     * 장바구니 상품 추가
     */
    @Transactional
    public Long addCartItem(CartItemDto cartItemDto, User user) {
        Cart cart = cartService.findCart(user.getUsername());
        Item item = itemService.findOne(cartItemDto.getItemId());

        CartItem cartItem = toEntity(cartItemDto, item, cart);

        return cartItemService.addCartItem(cartItem);
    }

    /**
     * 장바구니 상품 조회
     */
    public List<CartListDto> findCartItems(User user) {
        Cart cart = cartService.findCart(user.getUsername());
        List<CartItem> cartItems = cartItemService.findCartItemList(cart);

        List<Image> mainImages = cartItems.stream()
                .map(CartItem::getItemId) // CartItem -> Long
                .map(imageService::getMainImage)// Long -> Image
                .collect(toList());

        return toDto(cartItems, mainImages);
    }

    /**
     * 장바구니 상품 수량과 상품 재고 비교
     */
    public Boolean checkItemStock(CartItemDto cartItemDto) {
        Item item = itemService.findOne(cartItemDto.getItemId());
        return item.checkStock(cartItemDto.getQuantity());
    }

    /**
     * 장바구니에 담을 상품 재고 조회
     */
    public int getItemStock(CartItemDto cartItemDto) {
        Item item = itemService.findOne(cartItemDto.getItemId());
        return item.getStockQuantity();
    }

    private CartItem toEntity(CartItemDto cartItemDto, Item item, Cart cart) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .quantity(cartItemDto.getQuantity())
                .build();
    }

    private CartListDto toDto(CartItem cartItem, Image mainImage) {
        return CartListDto.builder()
                .itemId(cartItem.getItemId())
                .itemName(cartItem.getItemName())
                .itemPrice(cartItem.getItemPrice())
                .quantity(cartItem.getQuantity())
                .mainImage(mainImage)
                .build();
    }

    private List<CartListDto> toDto(List<CartItem> cartItems, List<Image> mainImages) {
        List<CartListDto> cartListDtos = new ArrayList<>();

        for (int i = 0; i < cartItems.size(); i++) {
            cartListDtos.add(toDto(cartItems.get(i), mainImages.get(i)));
        }

        return cartListDtos;
    }

}
