package com.akabazan.admin.controller;

import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeResult;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin-ui")
public class AdminViewController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final DisputeService disputeService;

    private final com.akabazan.admin.service.CurrentAdminService currentAdminService;

    public AdminViewController(UserRepository userRepository,
                               OrderRepository orderRepository,
                               TradeRepository tradeRepository,
                               DisputeService disputeService,
                               com.akabazan.admin.service.CurrentAdminService currentAdminService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.disputeService = disputeService;
        this.currentAdminService = currentAdminService;
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
    public String users(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/users/{userId}/orders")
    public String ordersOfUser(@PathVariable UUID userId, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("orders", orderRepository.findOrdersByUserAndOptionalFilters(userId, null, null));
        model.addAttribute("selectedUserId", userId);
        return "admin/orders";
    }

    @GetMapping("/trades")
    public String trades(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("trades", tradeRepository.findAll());
        return "admin/trades";
    }

    @GetMapping("/users/{userId}/trades")
    public String tradesOfUser(@PathVariable UUID userId, Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        model.addAttribute("trades", tradeRepository.findByUser(userId));
        model.addAttribute("selectedUserId", userId);
        return "admin/trades";
    }

    @GetMapping("/disputes")
    public String disputes(Model model) {
        currentAdminService.getCurrentAdmin().ifPresent(a -> model.addAttribute("currentAdmin", a));
        java.util.List<DisputeResult> disputes = disputeService.getDisputes(null, false);
        model.addAttribute("disputes", disputes);
        return "admin/disputes";
    }
}


