package com.akabazan.repository;

import com.akabazan.repository.constant.PaymentMethodType;
import com.akabazan.repository.entity.PaymentMethod;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findAllByActiveTrueOrderByDisplayOrderAscNameAsc();

    List<PaymentMethod> findAllByTypeAndActiveTrueOrderByDisplayOrderAscNameAsc(PaymentMethodType type);
}
