package shop.tryit.domain.order.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.tryit.domain.item.entity.Item;
import shop.tryit.domain.item.service.ItemService;
import shop.tryit.domain.member.Member;
import shop.tryit.domain.member.MemberService;
import shop.tryit.domain.order.Order;
import shop.tryit.domain.order.OrderDetail;
import shop.tryit.domain.order.OrderStatus;
import shop.tryit.domain.order.dto.OrderDetailDto;
import shop.tryit.domain.order.dto.OrderDto;
import shop.tryit.domain.order.dto.OrderFormDto;
import shop.tryit.domain.order.dto.OrderSearchDto;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final MemberService memberService;
    private final ItemService itemService;

    public Page<OrderSearchDto> searchOrders(int page, User user) {
        String email = user.getUsername();
        return orderService.searchOrders(page, email);
    }

    public void register(User user, List<OrderDetailDto> orderDetailDtos) {
        String email = user.getUsername();
        Member member = memberService.findMember(email);

        Long savedOrderId = orderService.register(Order.of(member, OrderStatus.ORDER));
        Order findOrder = orderService.findOne(savedOrderId);

        orderDetailDtos.stream()
                .map(orderDetailDto -> toEntity(orderDetailDto, findOrder))
                .forEach(orderDetailService::save);
    }

    public OrderDetail toEntity(OrderFormDto orderFormDto, Item item, Order order) {
        return OrderDetail.of(item, order, orderFormDto.getOrderQuantity());
    }

    public OrderFormDto toDto(OrderDetail orderDetail) {
        return OrderFormDto.builder()
                .itemId(orderDetail.getItem().getId())
                .itemName(orderDetail.getItem().getName())
                .orderQuantity(orderDetail.getQuantity())
                .build();
    }

    public List<OrderDto> toListDto(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(this::toOrderDto)
                .collect(toList());
    }

    private OrderDto toOrderDto(OrderDetail orderDetail) {
        return OrderDto.builder()
                .detailId(orderDetail.getId())
                .number(orderDetail.orderNumber())
                .itemName(orderDetail.itemName())
                .date(orderDetail.orderCreateDate())
                .status(orderDetail.orderStatus())
                .build();
    }

    private OrderDetail toEntity(OrderDetailDto orderDetailDto, Order order) {
        Long itemId = orderDetailDto.getItemId();
        Item item = itemService.findOne(itemId);

        return OrderDetail.of(item, order, orderDetailDto.getOrderQuantity());
    }

}
