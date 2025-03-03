package fr.paymybuddy.dto;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRequestDTO {

    @DecimalMin(value = "0.1", inclusive = true)
    @Digits(integer = 10, fraction = 1)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double amount;
}
