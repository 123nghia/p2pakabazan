package com.akabazan.admin.controller;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.DisputeReasonRepository;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin-ui")
public class AdminViewController {

    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final DisputeService disputeService;
    private final OrderRepository orderRepository;
    private final DisputeReasonRepository disputeReasonRepository;
    private final com.akabazan.repository.CurrencyRepository currencyRepository;
    private final com.akabazan.repository.PaymentMethodRepository paymentMethodRepository;

    private final com.akabazan.admin.service.CurrentAdminService currentAdminService;

    public AdminViewController(UserRepository userRepository,
            OrderRepository orderRepository,
            TradeRepository tradeRepository,
            DisputeReasonRepository disputeReasonRepository,
            DisputeService disputeService,
            com.akabazan.repository.CurrencyRepository currencyRepository,
            com.akabazan.repository.PaymentMethodRepository paymentMethodRepository,
            com.akabazan.admin.service.CurrentAdminService currentAdminService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.disputeService = disputeService;
        this.currencyRepository = currencyRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.disputeReasonRepository = disputeReasonRepository;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping("/currencies")
    public String currencies(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("currencies", currencyRepository.findAll());
        return "admin/currencies";
    }

    @GetMapping("/currencies/new")
    public String createCurrency(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("currency", new com.akabazan.repository.entity.Currency());
        model.addAttribute("types", com.akabazan.repository.constant.CurrencyType.values());
        return "admin/currency-form";
    }

    @GetMapping("/currencies/{id}")
    public String editCurrency(@PathVariable UUID id, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var currency = currencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency Id:" + id));
        model.addAttribute("currency", currency);
        model.addAttribute("types", com.akabazan.repository.constant.CurrencyType.values());
        return "admin/currency-form";
    }

    @GetMapping("/payment-methods")
    public String paymentMethods(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("paymentMethods", paymentMethodRepository.findAll());
        return "admin/payment-methods";
    }

    @GetMapping("/payment-methods/new")
    public String createPaymentMethod(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("paymentMethod", new com.akabazan.repository.entity.PaymentMethod());
        model.addAttribute("types", com.akabazan.repository.constant.PaymentMethodType.values());
        return "admin/payment-method-form";
    }

    @GetMapping("/payment-methods/{id}")
    public String editPaymentMethod(@PathVariable UUID id, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment method Id:" + id));
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("types", com.akabazan.repository.constant.PaymentMethodType.values());
        return "admin/payment-method-form";
    }

    @GetMapping
    public String dashboard(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("tradeCount", tradeRepository.count());
        model.addAttribute("disputeCount", disputeService.getDisputes(null, false).size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var users = userRepository.findAll().stream()
                .filter(u -> search == null || search.isBlank()
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(search.toLowerCase())))
                .toList();
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @GetMapping("/orders")
    public String orders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var orders = orderRepository.findAll().stream()
                .filter(o -> status == null
                        || (o.getStatus() != null && o.getStatus().equalsIgnoreCase(status)))
                .filter(o -> search == null || search.isBlank()
                        || (o.getUser() != null && o.getUser().getEmail() != null
                                && o.getUser().getEmail().toLowerCase().contains(search.toLowerCase()))
                        || (o.getId() != null && o.getId().toString().contains(search)))
                .toList();
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("search", search);
        return "admin/orders";
    }

    @GetMapping("/users/{userId}/orders")
    public String ordersOfUser(
            @PathVariable UUID userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var orders = orderRepository.findOrdersByUserAndOptionalFilters(userId, status, null).stream()
                .filter(o -> search == null || search.isBlank()
                        || (o.getId() != null && o.getId().toString().contains(search)))
                .toList();
        model.addAttribute("orders", orders);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("currentStatus", status);
        model.addAttribute("search", search);
        return "admin/orders";
    }

    @GetMapping("/trades")
    public String trades(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var trades = tradeRepository.findAll().stream()
                .filter(t -> status == null
                        || (t.getStatus() != null && t.getStatus().name().equalsIgnoreCase(status)))
                .filter(t -> search == null || search.isBlank()
                        || (t.getBuyer() != null && t.getBuyer().getEmail() != null
                                && t.getBuyer().getEmail().toLowerCase().contains(search.toLowerCase()))
                        || (t.getSeller() != null && t.getSeller().getEmail() != null
                                && t.getSeller().getEmail().toLowerCase().contains(search.toLowerCase()))
                        || (t.getTradeCode() != null && t.getTradeCode().toLowerCase().contains(search.toLowerCase()))
                        || (t.getId() != null && t.getId().toString().contains(search)))
                .toList();
        model.addAttribute("trades", trades);
        model.addAttribute("currentStatus", status);
        model.addAttribute("search", search);
        return "admin/trades";
    }

    @GetMapping("/users/{userId}/trades")
    public String tradesOfUser(
            @PathVariable UUID userId,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var trades = tradeRepository.findByUser(userId).stream()
                .filter(t -> status == null
                        || (t.getStatus() != null && t.getStatus().name().equalsIgnoreCase(status)))
                .toList();
        model.addAttribute("trades", trades);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("currentStatus", status);
        return "admin/trades";
    }

    @GetMapping("/disputes")
    public String disputes(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        java.util.List<DisputeResult> disputes = disputeService.getDisputes(null, false);
        model.addAttribute("disputes", disputes);
        return "admin/disputes";
    }

    @GetMapping("/disputes/{disputeId}")
    public String disputeDetail(@PathVariable UUID disputeId, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        DisputeResult dispute = disputeService.getDisputeById(disputeId);
        model.addAttribute("dispute", dispute);
        return "admin/dispute-detail";
    }

    @GetMapping("/dispute-reasons")
    public String disputeReasons(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("disputeReasons", disputeReasonRepository.findAll());
        return "admin/dispute-reasons";
    }

    @GetMapping("/dispute-reasons/new")
    public String createDisputeReason(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("disputeReason", new com.akabazan.repository.entity.DisputeReason());
        model.addAttribute("priorities", com.akabazan.repository.constant.DisputePriority.values());
        return "admin/dispute-reason-form";
    }

    @GetMapping("/dispute-reasons/{id}")
    public String editDisputeReason(@PathVariable UUID id, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        var disputeReason = disputeReasonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid dispute reason Id:" + id));
        model.addAttribute("disputeReason", disputeReason);
        model.addAttribute("priorities", com.akabazan.repository.constant.DisputePriority.values());
        return "admin/dispute-reason-form";
    }
}
