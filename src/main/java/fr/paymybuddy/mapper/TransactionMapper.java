package fr.paymybuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.paymybuddy.dto.TransactionDTO;
import fr.paymybuddy.model.Transaction;

@Mapper
public interface TransactionMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "senderEmail", source = "sender.email")
    @Mapping(target = "senderBalance", source = "sender.balance")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    @Mapping(target = "receiverEmail", source = "receiver.email")
    public TransactionDTO toTransactionDTO(Transaction transaction);

    @Mapping(target = "sender.id", source = "senderId")
    @Mapping(target = "receiver.id", source = "receiverId")
    public Transaction toTransaction(TransactionDTO transactionDTO);

}
