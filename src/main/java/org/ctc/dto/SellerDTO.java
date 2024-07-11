package org.ctc.dto;

import lombok.Data;
import org.ctc.entity.Users;

import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link Users} entity
 */
@Data
public class SellerDTO implements Serializable {
    private final Integer userId;
    private final String userName;
    private final Date rDate;
    private final String householdCounty;
    private final String householdArea;
    private final String householdAddress;
    private final String sellerName;
    private final String phone;
    private final String sellerImage;
    private final String sellerDisc;
}