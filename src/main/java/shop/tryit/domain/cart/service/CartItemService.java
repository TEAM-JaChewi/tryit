package shop.tryit.domain.cart.service;

import static java.util.Objects.nonNull;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.tryit.domain.cart.entity.Cart;
import shop.tryit.domain.cart.entity.CartItem;
import shop.tryit.domain.cart.repository.CartItemRepository;
import shop.tryit.domain.item.entity.Item;
import shop.tryit.domain.item.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    /**
     * 장바구니에 상품 추가
     */
    public Long addCartItem(CartItem cartItem) {
        // 장바구니에 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItem.getItemId())
                .orElseThrow(() -> new IllegalStateException("해당 상품을 찾을 수 없습니다."));

        // 장바구니에 해당 상품이 있는지 확인
        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cartItem.getCart(), item);

        // 상품이 이미 있을 경우, 개수 증가
        if (nonNull(savedCartItem)) {
            savedCartItem.addQuantity(cartItem.getQuantity());
            return savedCartItem.getId();
        }

        // 상품이 없을 경우, 장바구니에 상품 추가
        savedCartItem = CartItem.builder()
                .cart(cartItem.getCart())
                .item(cartItem.getItem())
                .quantity(cartItem.getQuantity())
                .build();
        cartItemRepository.save(savedCartItem);

        return savedCartItem.getId();
    }

    /**
     * 장바구니에 담긴 상품 조회
     */
    public List<CartItem> findCartItemList(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    /**
     * 장바구니에 담긴 상품 수량 변경
     */
    public void updateCartItemCount(Long cartItemId, int count) {
        // 변경할 장바구니 상품 엔티티 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalStateException("해당 장바구니 상품을 찾을 수 없습니다."));

        // 장바구니 상품의 수량 변경
        cartItem.updateQuantity(count);
    }

    /**
     * 장바구니에 담긴 상품 삭제
     */
    public void deleteCartItem(Long cartItemId) {
        // 삭제할 장바구니 상품 엔티티 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalStateException("해당 장바구니 상품을 찾을 수 없습니다."));

        cartItemRepository.delete(cartItem);
    }

}
