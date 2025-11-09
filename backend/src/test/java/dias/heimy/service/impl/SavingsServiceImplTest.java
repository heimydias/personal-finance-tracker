package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Savings;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.SavingsType;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.InsufficientBalanceException;
import dias.heimy.domain.exception.SavingsNotFoundException;
import dias.heimy.domain.exception.UnauthorizedAccessException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.mapper.SavingsMapper;
import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.SavingsResponse;
import dias.heimy.repository.SavingsRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for SavingsServiceImpl")
class SavingsServiceImplTest {

    @Mock
    private SavingsRepository savingsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SavingsMapper savingsMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserBalanceService userBalanceService;

    @InjectMocks
    private SavingsServiceImpl savingsService;

    @Test
    @DisplayName("Should create savings successfully when valid data provided")
    void shouldCreateSavings_WhenValidData() {

        var request = new SavingsRequest(
                "Reserva de Emergência",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Reserva para emergências",
                LocalDate.now());
        var user = createTestUser();
        var savings = createTestSavings(user);
        var transaction = createTestTransaction(user);
        var expectedResponse = createSavingsResponse();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getAccountBalanceUntilDate(user, request.transactionDate()))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(savingsMapper.toEntity(request, user)).thenReturn(savings);
        when(savingsRepository.save(any(Savings.class))).thenReturn(savings);
        when(savingsMapper.toResponse(savings)).thenReturn(expectedResponse);

        var result = savingsService.createSavings(request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userBalanceService).moveToSavings(user.getId(), savings.getAmount());
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void shouldThrowException_WhenInsufficientBalance() {

        var request = new SavingsRequest(
                "Reserva de Emergência",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Reserva para emergências",
                LocalDate.now());
        var user = createTestUser();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceService.getAccountBalanceUntilDate(user, request.transactionDate()))
                .thenReturn(new BigDecimal("500.00"));

        assertThatThrownBy(() -> savingsService.createSavings(request, "Bearer token"))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should get savings by ID successfully")
    void shouldGetSavingsById_WhenValidId() {

        var savingsId = UUID.randomUUID();
        var user = createTestUser();
        var savings = createTestSavings(user);
        savings.setId(savingsId);
        var expectedResponse = createSavingsResponse();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
        when(savingsMapper.toResponse(savings)).thenReturn(expectedResponse);

        var result = savingsService.getSavingsById(savingsId, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw exception when savings not found")
    void shouldThrowException_WhenSavingsNotFound() {

        var savingsId = UUID.randomUUID();
        var user = createTestUser();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsService.getSavingsById(savingsId, "Bearer token"))
                .isInstanceOf(SavingsNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when user tries to access other user savings")
    void shouldThrowException_WhenUnauthorizedAccess() {

        var savingsId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(UUID.randomUUID());
        var otherUser = createTestUser();
        otherUser.setId(UUID.randomUUID());
        var savings = createTestSavings(otherUser);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));

        assertThatThrownBy(() -> savingsService.getSavingsById(savingsId, "Bearer token"))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should list savings successfully")
    void shouldListSavings_WhenValidUser() {

        var pageable = PageRequest.of(0, 10);
        var user = createTestUser();
        var savings = List.of(createTestSavings(user));
        var savingsPage = new PageImpl<>(savings, pageable, 1);
        var expectedResponse = createSavingsResponse();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findByUserId(user.getId(), pageable)).thenReturn(savingsPage);
        when(savingsMapper.toResponse(any(Savings.class))).thenReturn(expectedResponse);

        var result = savingsService.listSavings(pageable, "Bearer token");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update savings successfully")
    void shouldUpdateSavings_WhenValidData() {

        var savingsId = UUID.randomUUID();
        var request = new SavingsRequest(
                "Reserva Atualizada",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1500.00"),
                new BigDecimal("2.0"),
                "Reserva atualizada",
                LocalDate.now());
        var user = createTestUser();
        var savings = createTestSavings(user);
        savings.setId(savingsId);
        var expectedResponse = createSavingsResponse();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
        when(savingsRepository.save(savings)).thenReturn(savings);
        when(savingsMapper.toResponse(savings)).thenReturn(expectedResponse);

        var result = savingsService.updateSavings(savingsId, request, "Bearer token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(savingsMapper).updateEntity(savings, request);
    }

    @Test
    @DisplayName("Should delete savings successfully")
    void shouldDeleteSavings_WhenValidId() {

        var savingsId = UUID.randomUUID();
        var user = createTestUser();
        var savings = createTestSavings(user);
        savings.setId(savingsId);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(createTestTransaction(user));

        savingsService.deleteSavings(savingsId, "Bearer token");

        verify(savingsRepository).delete(savings);
        verify(userBalanceService).moveFromSavings(user.getId(), savings.getAmount());
    }

    @Test
    @DisplayName("Should calculate monthly yield correctly")
    void shouldCalculateMonthlyYield() {

        var savingsId = UUID.randomUUID();
        var user = createTestUser();
        var savings = createTestSavings(user);
        savings.setId(savingsId);
        savings.setAmount(new BigDecimal("1000.00"));
        savings.setInterestRate(new BigDecimal("1.5"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));

        var result = savingsService.calculateMonthlyYield(savingsId, "Bearer token");

        assertThat(result.currentAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.interestRate()).isEqualByComparingTo(new BigDecimal("1.5"));
        assertThat(result.monthlyYield()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(result.totalAfterYield()).isEqualByComparingTo(new BigDecimal("1015.00"));
    }

    @Test
    @DisplayName("Should calculate consolidated yield correctly")
    void shouldCalculateConsolidatedYield() {

        var user = createTestUser();
        var savings1 = createTestSavings(user);
        savings1.setAmount(new BigDecimal("1000.00"));
        savings1.setInterestRate(new BigDecimal("1.5"));

        var savings2 = createTestSavings(user);
        savings2.setAmount(new BigDecimal("2000.00"));
        savings2.setInterestRate(new BigDecimal("2.0"));

        var savingsList = List.of(savings1, savings2);
        var savingsPage = new PageImpl<>(savingsList);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findByUserId(eq(user.getId()), any())).thenReturn(savingsPage);

        var result = savingsService.calculateConsolidatedYield("Bearer token");

        assertThat(result.totalSavings()).isEqualTo(2);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(result.totalMonthlyYield()).isEqualByComparingTo(new BigDecimal("55.00"));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowException_WhenUserNotFound() {

        var request = new SavingsRequest(
                "Reserva",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Descrição",
                LocalDate.now());

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsService.createSavings(request, "Bearer token"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should return empty consolidated yield when no savings")
    void shouldReturnEmptyConsolidatedYield_WhenNoSavings() {

        var user = createTestUser();
        var savingsPage = new PageImpl<Savings>(List.of());

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findByUserId(eq(user.getId()), any())).thenReturn(savingsPage);

        var result = savingsService.calculateConsolidatedYield("Bearer token");

        assertThat(result.totalSavings()).isZero();
        assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalMonthlyYield()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageInterestRate()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate average interest rate correctly when total amount is zero")
    void shouldCalculateAverageInterestRate_WhenTotalAmountIsZero() {

        var user = createTestUser();
        var savings1 = createTestSavings(user);
        savings1.setAmount(BigDecimal.ZERO);
        savings1.setInterestRate(new BigDecimal("1.5"));

        var savingsList = List.of(savings1);
        var savingsPage = new PageImpl<>(savingsList);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findByUserId(eq(user.getId()), any())).thenReturn(savingsPage);

        var result = savingsService.calculateConsolidatedYield("Bearer token");

        assertThat(result.totalSavings()).isEqualTo(1);
        assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageInterestRate()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalMonthlyYield()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate weighted average interest rate correctly")
    void shouldCalculateWeightedAverageInterestRate_Correctly() {

        var user = createTestUser();
        var savings1 = createTestSavings(user);
        savings1.setAmount(new BigDecimal("1000.00"));
        savings1.setInterestRate(new BigDecimal("1.0"));

        var savings2 = createTestSavings(user);
        savings2.setAmount(new BigDecimal("2000.00"));
        savings2.setInterestRate(new BigDecimal("2.0"));

        var savings3 = createTestSavings(user);
        savings3.setAmount(new BigDecimal("3000.00"));
        savings3.setInterestRate(new BigDecimal("3.0"));

        var savingsList = List.of(savings1, savings2, savings3);
        var savingsPage = new PageImpl<>(savingsList);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(savingsRepository.findByUserId(eq(user.getId()), any())).thenReturn(savingsPage);

        var result = savingsService.calculateConsolidatedYield("Bearer token");

        assertThat(result.totalSavings()).isEqualTo(3);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("6000.00"));
        // Taxa média ponderada: (1000*1.0 + 2000*2.0 + 3000*3.0) / 6000 = 14000 / 6000 = 2.33
        assertThat(result.averageInterestRate()).isEqualByComparingTo(new BigDecimal("2.33"));
        // Rendimento total: (1000*1.0/100) + (2000*2.0/100) + (3000*3.0/100) = 10 + 40 + 90 = 140
        assertThat(result.totalMonthlyYield()).isEqualByComparingTo(new BigDecimal("140.00"));
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

    private Savings createTestSavings(User user) {
        Savings savings = new Savings();
        savings.setId(UUID.randomUUID());
        savings.setName("Reserva de Emergência");
        savings.setType(SavingsType.EMERGENCY_FUND);
        savings.setAmount(new BigDecimal("1000.00"));
        savings.setInterestRate(new BigDecimal("1.5"));
        savings.setDescription("Descrição teste");
        savings.setTransactionDate(LocalDate.now());
        savings.setUser(user);
        return savings;
    }

    private Transaction createTestTransaction(User user) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setUser(user);
        return transaction;
    }

    private SavingsResponse createSavingsResponse() {
        return new SavingsResponse(
                UUID.randomUUID(),
                "Reserva de Emergência",
                SavingsType.EMERGENCY_FUND,
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                "Descrição teste",
                LocalDate.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
