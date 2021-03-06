package shop.tryit.web.payment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.tryit.domain.payment.PaymentFacade;
import shop.tryit.domain.payment.dto.PaymentRequestDto;
import shop.tryit.domain.payment.dto.PaymentRequestListDto;
import shop.tryit.domain.payment.dto.PaymentSaveDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @GetMapping
    public String newPaymentForm(@ModelAttribute PaymentRequestListDto paymentRequestListDto,
                                 @AuthenticationPrincipal User user,
                                 Model model) {
        log.info("======== 결제 폼 컨트롤러 시작 ========");

        List<PaymentRequestDto> payments = paymentRequestListDto.getPayments();
        log.info(payments.toString());
        model.addAttribute("");

        return "/payment/payment-form";
    }

    @PostMapping("/kakao")
    public ResponseEntity newPaymentForm(@RequestBody @ModelAttribute PaymentSaveDto paymentSaveDto) {
        log.info("결제 정보 {}", paymentSaveDto);
        paymentFacade.register(paymentSaveDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
