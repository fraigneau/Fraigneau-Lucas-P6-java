package fr.paymybuddy.dto;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 25)
    private String description;

    @DecimalMin(value = "0.1", inclusive = true)
    @Digits(integer = 10, fraction = 1)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double amount;

}
