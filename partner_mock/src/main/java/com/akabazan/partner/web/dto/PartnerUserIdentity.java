package com.akabazan.partner.web.dto;

import java.io.Serializable;

public record PartnerUserIdentity(String externalUserId, String email, String username) implements Serializable {}

