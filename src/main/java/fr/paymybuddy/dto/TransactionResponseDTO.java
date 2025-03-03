package fr.paymybuddy.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private double amount;
    private LocalDateTime createdAt;

    private Long senderId;
    private String senderUsername;
    private String senderEmail;
    private String senderBalance;

    private Long receiverId;
    private String receiverUsername;
    private String receiverEmail;

}
