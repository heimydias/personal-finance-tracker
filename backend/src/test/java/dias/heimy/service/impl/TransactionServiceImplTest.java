package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.CannotDeleteIncomeException;
import dias.heimy.domain.exception.DomainException;
import dias.heimy.domain.exception.InsufficientBalanceException;
import dias.heimy.dto.mapper.TransactionMapper;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.UserBalanceService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for TransactionServiceImpl")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private dias.heimy.repository.UserBalanceRepository userBalanceRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserBalanceService userBalanceService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("Should create transaction successfully when valid data provided")
    void shouldCreateTransaction_WhenValidData() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Salário", new BigDecimal("3000.00"), LocalDate.now(), "Salário mensal");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        var savedTransaction = createTestTransaction(user, TransactionType.INCOME);
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(jwtTokenProvider).extractEmailFromToken("token");
        verify(userRepository).findByEmail("test@example.com");
        verify(transactionMapper).toEntity(request);
        verify(transactionRepository).save(transaction);
        verify(transactionMapper).toResponse(savedTransaction);
    }

    @Test
    @DisplayName("Should throw DomainException when user not found")
    void shouldThrowException_WhenUserNotFound() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Salário", new BigDecimal("3000.00"), LocalDate.now(), "Salário mensal");

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request, "Bearer token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(jwtTokenProvider).extractEmailFromToken("token");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void shouldGetTransactionById_WhenValidId() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        transaction.setId(transactionId);
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(expectedResponse);

        var result = transactionService.getTransactionById(transactionId, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(transactionRepository).findById(transactionId);
        verify(transactionMapper).toResponse(transaction);
    }

    @Test
    @DisplayName("Should throw DomainException when transaction not found")
    void shouldThrowException_WhenTransactionNotFound() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(transactionId, "Bearer token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Transação não encontrada");

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @DisplayName("Should throw DomainException when user tries to access transaction of another user")
    void shouldThrowException_WhenUserTriesToAccessOtherUserTransaction() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(UUID.randomUUID());
        var otherUser = createTestUser();
        otherUser.setId(UUID.randomUUID());
        var transaction = createTestTransaction(otherUser, TransactionType.INCOME);
        transaction.setId(transactionId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.getTransactionById(transactionId, "Bearer token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Você não tem permissão para acessar esta transação");

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @DisplayName("Should list transactions successfully")
    void shouldListTransactions_WhenValidUser() {

        var pageable = PageRequest.of(0, 10);
        var user = createTestUser();
        var transactions = List.of(createTestTransaction(user, TransactionType.INCOME));
        var transactionsPage = new PageImpl<>(transactions, pageable, 1);
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByUser(user, pageable)).thenReturn(transactionsPage);
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(expectedResponse);

        var result = transactionService.listTransactions(pageable, "Bearer token");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(transactionRepository).findByUser(user, pageable);
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void shouldUpdateTransaction_WhenValidData() {

        var transactionId = UUID.randomUUID();
        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Alimentação", new BigDecimal("150.00"), LocalDate.now(), "Supermercado");
        var user = createTestUser();
        var existingTransaction = createTestTransaction(user, TransactionType.INCOME);
        existingTransaction.setId(transactionId);
        var updatedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(existingTransaction)).thenReturn(updatedTransaction);
        when(transactionMapper.toResponse(updatedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.updateTransaction(transactionId, request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(transactionMapper).updateEntityFromRequest(request, existingTransaction);
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    @DisplayName("Should throw DomainException when trying to update transaction of another user")
    void shouldThrowException_WhenUpdatingOtherUserTransaction() {

        var transactionId = UUID.randomUUID();
        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Alimentação", new BigDecimal("150.00"), LocalDate.now(), "Supermercado");
        var user = createTestUser();
        user.setId(UUID.randomUUID());
        var otherUser = createTestUser();
        otherUser.setId(UUID.randomUUID());
        var transaction = createTestTransaction(otherUser, TransactionType.INCOME);
        transaction.setId(transactionId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.updateTransaction(transactionId, request, "Bearer token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Você não tem permissão para acessar esta transação");

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransaction_WhenValidId() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal("100.00"));

        // Mock do UserBalance para permitir a deleção (saldo suficiente)
        var userBalance = new dias.heimy.domain.entity.UserBalance();
        userBalance.setTotalIncome(new BigDecimal("500.00"));
        userBalance.setTotalExpense(new BigDecimal("100.00"));
        userBalance.setSavingsBalance(new BigDecimal("0.00"));
        userBalance.setAccountBalance(new BigDecimal("400.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);

        transactionService.deleteTransaction(transactionId, "Bearer token");

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).delete(transaction);
    }

    @Test
    @DisplayName("Should throw DomainException when trying to delete transaction of another user")
    void shouldThrowException_WhenDeletingOtherUserTransaction() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(UUID.randomUUID());
        var otherUser = createTestUser();
        otherUser.setId(UUID.randomUUID());
        var transaction = createTestTransaction(otherUser, TransactionType.INCOME);
        transaction.setId(transactionId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.deleteTransaction(transactionId, "Bearer token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Você não tem permissão para acessar esta transação");

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @DisplayName("Should create expense transaction successfully")
    void shouldCreateExpenseTransaction() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Transporte", new BigDecimal("250.00"), LocalDate.now(), "Combustível");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));
        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        var savedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.type()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    @DisplayName("Should throw exception when creating expense with insufficient balance")
    void shouldThrowException_WhenInsufficientBalanceForExpense() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Transporte", new BigDecimal("1500.00"), LocalDate.now(), "Combustível");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);

        assertThatThrownBy(() -> transactionService.createTransaction(request, "Bearer token"))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should throw exception when trying to update TRANSFER transaction")
    void shouldThrowException_WhenUpdatingTransferTransaction() {

        var transactionId = UUID.randomUUID();
        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Alimentação", new BigDecimal("150.00"), LocalDate.now(), "Supermercado");
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.TRANSFER);
        transaction.setId(transactionId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.updateTransaction(transactionId, request, "Bearer token"))
                .isInstanceOf(dias.heimy.domain.exception.TransferTransactionNotModifiableException.class);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete TRANSFER transaction")
    void shouldThrowException_WhenDeletingTransferTransaction() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.TRANSFER);
        transaction.setId(transactionId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.deleteTransaction(transactionId, "Bearer token"))
                .isInstanceOf(dias.heimy.domain.exception.TransferTransactionNotModifiableException.class);
    }

    @Test
    @DisplayName("Should throw exception when deleting income would result in negative balance")
    void shouldThrowException_WhenDeletingIncomeWouldResultInNegativeBalance() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal("1000.00"));

        var userBalance = createTestUserBalance(user, new BigDecimal("100.00"));
        userBalance.setTotalIncome(new BigDecimal("1000.00"));
        userBalance.setTotalExpense(new BigDecimal("900.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);

        assertThatThrownBy(() -> transactionService.deleteTransaction(transactionId, "Bearer token"))
                .isInstanceOf(CannotDeleteIncomeException.class);
    }

    @Test
    @DisplayName("Should delete expense transaction successfully")
    void shouldDeleteExpenseTransaction() {

        var transactionId = UUID.randomUUID();
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal("100.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(transactionId, "Bearer token");

        verify(transactionRepository).delete(transaction);
    }

    @Test
    @DisplayName("Should create income transaction and update balance")
    void shouldCreateIncomeTransaction_AndUpdateBalance() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Freelance", new BigDecimal("500.00"), LocalDate.now(), "Projeto freelance");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        var savedTransaction = createTestTransaction(user, TransactionType.INCOME);
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userBalanceRepository).save(userBalance);
    }

    @Test
    @DisplayName("Should create expense transaction and update balance correctly")
    void shouldCreateExpenseTransaction_AndUpdateBalance() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Alimentação", new BigDecimal("200.00"), LocalDate.now(), "Supermercado");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("1000.00"));
        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("200.00"));
        var savedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        savedTransaction.setAmount(new BigDecimal("200.00"));
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("1200.00"));
    }

    @Test
    @DisplayName("Should validate expense and update total expense correctly")
    void shouldValidateExpense_AndUpdateTotalExpenseCorrectly() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Compras", new BigDecimal("500.00"), LocalDate.now(), "Shopping");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("2000.00"));
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("500.00"));
        var savedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        // Verificar que passou pela validação de saldo (linha 51-60)
        // e pela atualização do totalExpense (linha 68-70)
        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("3500.00"));
        assertThat(userBalance.getAccountBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Should execute EXPENSE branch and increment totalExpense")
    void shouldExecuteExpenseBranch_AndIncrementTotalExpense() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Transporte", new BigDecimal("150.00"), LocalDate.now(), "Uber");
        var user = createTestUser();
        var userBalance = createTestUserBalance(user, new BigDecimal("1000.00"));
        userBalance.setTotalIncome(new BigDecimal("2000.00"));
        userBalance.setTotalExpense(new BigDecimal("500.00"));
        userBalance.setSavingsBalance(new BigDecimal("500.00"));

        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("150.00"));
        var savedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        savedTransaction.setAmount(new BigDecimal("150.00"));
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId()))
                .thenReturn(userBalance);
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("650.00"));
        assertThat(userBalance.getAccountBalance()).isEqualByComparingTo(new BigDecimal("850.00"));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        user.setLastModifiedBy("SYSTEM");
        return user;
    }

    private Transaction createTestTransaction(User user, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setType(type);
        transaction.setCategory("Categoria Teste");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription("Descrição teste");
        transaction.setUser(user);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setCreatedBy("SYSTEM");
        transaction.setLastModifiedBy("SYSTEM");
        return transaction;
    }

    private TransactionResponse createTransactionResponse(TransactionType type) {
        return new TransactionResponse(
                UUID.randomUUID().toString(),
                type,
                "Categoria Teste",
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Descrição teste",
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    private UserBalance createTestUserBalance(User user, BigDecimal accountBalance) {
        UserBalance userBalance = new UserBalance();
        userBalance.setId(UUID.randomUUID());
        userBalance.setUser(user);
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(BigDecimal.ZERO);
        userBalance.setAccountBalance(accountBalance);
        userBalance.setSavingsBalance(BigDecimal.ZERO);
        userBalance.setTotalBalance(accountBalance);
        return userBalance;
    }
}
