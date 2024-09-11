package com.hello.jdbc.service.order;

import com.hello.jdbc.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {

        // given
        Order order = new Order();
        order.setUsername("정상");

        // when
        orderService.order(order);

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtimeException() {
        // given
        Order order = new Order();
        order.setUsername("예외");

        // when
        // 시스템에 문제가 생긴 경우엔 모두 롤백
        Assertions.assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);

        // then
        Optional<Order> findOrder = orderRepository.findById(order.getId());
        assertThat(findOrder.isEmpty()).isTrue();

    }

    @Test
    void bizException() {
        // given
        Order order = new Order();
        order.setUsername("잔고부족");

        // when
        // 체크예외 발생 시 작업을 롤백하지 않음 -> 시스템에 문제가 발생한 것이 아니라, 비즈니스 문제 상황을 예외를 통해 알려준다.
        // 런타임 예외는 항상 롤백된다. 체크 예외의 경우 'rollbackFor' 옵션을 사용해서 비즈니스 상황에 따라서 커밋과 롤백을 선택하면 된다.
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");

    }


}