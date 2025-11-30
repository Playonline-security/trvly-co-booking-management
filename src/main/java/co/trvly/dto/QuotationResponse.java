package co.trvly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la respuesta de cotización de una reserva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponse {
    private TravelPackageDto travelPackage;
    private Integer passengerCount;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private String message;
}

