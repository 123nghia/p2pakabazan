package com.akabazan.service;

import com.akabazan.service.dto.PartnerUserProvisioningCommand;
import java.util.UUID;

public interface PartnerUserProvisioningService {

    UUID provisionPartnerUser(PartnerUserProvisioningCommand command);
}

