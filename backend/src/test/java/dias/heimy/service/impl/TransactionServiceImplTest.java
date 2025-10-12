package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.DomainException;
import dias.heimy.dto.mapper.TransactionMapper;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserRepository;
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
    private TransactionMapper transactionMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("Should create transaction successfully when valid data provided")
    void shouldCreateTransaction_WhenValidData() {

        var request = new TransactionRequest(
                TransactionType.INCOME, "Salário", new BigDecimal("3000.00"), LocalDate.now(), "Salário mensal");
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.INCOME);
        var savedTransaction = createTestTransaction(user, TransactionType.INCOME);
        var expectedResponse = createTransactionResponse(TransactionType.INCOME);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
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

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

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
    @DisplayName("Should calculate monthly balance successfully")
    void shouldCalculateMonthlyBalance_WhenValidYearAndMonth() {

        var year = 2025;
        var month = 10;
        var user = createTestUser();
        var startDate = LocalDate.of(year, month, 1);
        var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(user, TransactionType.INCOME, startDate, endDate))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(user, TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(new BigDecimal("2000.00"));

        var result = transactionService.getMonthlyBalance(year, month, "Bearer token");

        assertThat(result.year()).isEqualTo(year);
        assertThat(result.month()).isEqualTo(month);
        assertThat(result.totalIncome()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(result.totalExpense()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(result.balance()).isEqualTo(new BigDecimal("3000.00"));
    }

    @Test
    @DisplayName("Should create expense transaction successfully")
    void shouldCreateExpenseTransaction() {

        var request = new TransactionRequest(
                TransactionType.EXPENSE, "Transporte", new BigDecimal("250.00"), LocalDate.now(), "Combustível");
        var user = createTestUser();
        var transaction = createTestTransaction(user, TransactionType.EXPENSE);
        var savedTransaction = createTestTransaction(user, TransactionType.EXPENSE);
        var expectedResponse = createTransactionResponse(TransactionType.EXPENSE);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        var result = transactionService.createTransaction(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.type()).isEqualTo(TransactionType.EXPENSE);
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
}
