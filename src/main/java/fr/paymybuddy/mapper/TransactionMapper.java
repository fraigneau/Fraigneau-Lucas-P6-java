package fr.paymybuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fr.paymybuddy.dto.TransactionResponseDTO;
import fr.paymybuddy.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "senderEmail", source = "sender.email")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    @Mapping(target = "receiverEmail", source = "receiver.email")
    @Mapping(target = "senderBalance", source = "sender.balance")
    TransactionResponseDTO toTransactionResponseDTO(Transaction transaction);

    @Mapping(target = "sender.id", source = "senderId")
    @Mapping(target = "receiver.id", source = "receiverId")
    Transaction toTransaction(TransactionResponseDTO transactionDTO);
}
