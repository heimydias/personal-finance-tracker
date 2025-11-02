package dias.heimy.service;

import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.TransactionResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request, String authorizationHeader);

    TransactionResponse getTransactionById(UUID id, String authorizationHeader);

    Page<TransactionResponse> listTransactions(Pageable pageable, String authorizationHeader);

    TransactionResponse updateTransaction(UUID id, TransactionRequest request, String authorizationHeader);

    void deleteTransaction(UUID id, String authorizationHeader);
}
