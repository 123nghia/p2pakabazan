package com.akabazan.service;

import com.akabazan.service.dto.IntegrationSyncCommand;
import com.akabazan.service.dto.IntegrationSyncResult;

public interface ExternalUserIntegrationService {

    IntegrationSyncResult syncUserAndWallet(IntegrationSyncCommand command);
}
