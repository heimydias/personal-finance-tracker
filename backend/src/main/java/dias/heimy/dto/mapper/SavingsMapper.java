package dias.heimy.dto.mapper;

import dias.heimy.domain.entity.Savings;
import dias.heimy.domain.entity.User;
import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.SavingsResponse;
import org.springframework.stereotype.Component;

@Component
public class SavingsMapper {

    public Savings toEntity(SavingsRequest request, User user) {
        Savings savings = new Savings();
        savings.setName(request.name());
        savings.setType(request.type());
        savings.setAmount(request.amount());
        savings.setInterestRate(request.interestRate());
        savings.setDescription(request.description());
        savings.setTransactionDate(request.transactionDate());
        savings.setUser(user);
        return savings;
    }

    public void updateEntity(Savings savings, SavingsRequest request) {
        savings.setName(request.name());
        savings.setType(request.type());
        savings.setInterestRate(request.interestRate());
        savings.setDescription(request.description());
        savings.setTransactionDate(request.transactionDate());
        // Amount não é atualizado aqui - deve ser feito via transações
    }

    public SavingsResponse toResponse(Savings savings) {
        return new SavingsResponse(
                savings.getId(),
                savings.getName(),
                savings.getType(),
                savings.getAmount(),
                savings.getInterestRate(),
                savings.getDescription(),
                savings.getTransactionDate(),
                savings.getUser().getId(),
                savings.getDepositTransaction() != null
                        ? savings.getDepositTransaction().getId()
                        : null,
                savings.getCreatedAt(),
                savings.getUpdatedAt());
    }
}
