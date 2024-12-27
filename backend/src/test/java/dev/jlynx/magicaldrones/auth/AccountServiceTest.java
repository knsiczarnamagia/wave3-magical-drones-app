package dev.jlynx.magicaldrones.auth;

import dev.jlynx.magicaldrones.dto.AccountRegistration;
import dev.jlynx.magicaldrones.dto.AccountView;
import dev.jlynx.magicaldrones.dto.DeleteMyAccountRequest;
import dev.jlynx.magicaldrones.exception.AccessForbiddenException;
import dev.jlynx.magicaldrones.exception.ResourceNotFoundException;
import dev.jlynx.magicaldrones.exception.UsernameExistsException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private AccountRepository accountRepositoryMock;

    @Mock
    private AuthorityRepository authorityRepositoryMock;

    @Captor
    ArgumentCaptor<Account> accountCaptor;

    @InjectMocks
    private AccountService underTest;

    @Test
    void registerAccount_ShouldThrow_WhenUsernameAlreadyExists() {
        // given
        String username = "someuser";
        AccountRegistration registration = new AccountRegistration(username, "password");
        given(accountRepositoryMock.existsByUsername(anyString())).willReturn(true);

        // when
        Exception thrown = null;
        try {
            underTest.registerAccount(registration);
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        then(accountRepositoryMock).should().existsByUsername(username);
        then(accountRepositoryMock).should(never()).save(any());
        assertThat(thrown)
                .isNotNull()
                .isInstanceOf(UsernameExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("validRegistrationProvider")
    void registerAccount_ShouldCreateUserAccount_WhenDataValid(AccountRegistration registration) {
        // given
        String encodedPwd = "encoded";
        Authority expectedAuthority = new Authority(Role.USER);
        Account expectedAcc = new Account(registration.username(), encodedPwd);
        expectedAcc.setId(34L);
        expectedAcc.setAuthorities(List.of(expectedAuthority));
        given(passwordEncoderMock.encode(anyString())).willReturn(encodedPwd);
        given(authorityRepositoryMock.findByName(anyString())).willReturn(expectedAuthority);
        given(accountRepositoryMock.save(any())).willReturn(expectedAcc);
        given(accountRepositoryMock.existsByUsername(anyString())).willReturn(false);

        // when
        AccountView returned = underTest.registerAccount(registration);

        // then
        then(authorityRepositoryMock).should().findByName("ROLE_USER");
        then(passwordEncoderMock).should(times(1)).encode(registration.password());
        then(accountRepositoryMock).should(times(1)).save(accountCaptor.capture());
        assertThat(returned)
                .hasFieldOrPropertyWithValue("username", registration.username())
                .hasFieldOrPropertyWithValue("authorities", "ROLE_USER")
                .hasFieldOrPropertyWithValue("enabled", true)
                .extracting("enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired")
                .containsOnly(true);
        assertThat(returned.id()).isPositive();
        assertThat(accountCaptor.getValue())
                .hasFieldOrPropertyWithValue("username", registration.username())
                .hasFieldOrPropertyWithValue("password", encodedPwd);
    }

    private static Stream<AccountRegistration> validRegistrationProvider() {
        return Stream.of(
                new AccountRegistration("ivy", "P@ssw0rd"),
                new AccountRegistration("john_doe5", "abcDEF123$%^")
        );
    }

    @Test
    void deleteAccount_ShouldThrow_WhenAccountDoesNotExist() {
        // given
        long id = 17L;
        DeleteMyAccountRequest request = new DeleteMyAccountRequest("P@ssw0rd");
        given(accountRepositoryMock.findById(anyLong())).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> underTest.deleteAccount(id, request));

        // then
        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteAccount_ShouldThrow_WhenPasswordDoesNotMatch() {
        // given
        long id = 17L;
        Account toDelete = new Account("test", "P@ssw0rd");
        DeleteMyAccountRequest request = new DeleteMyAccountRequest("P@ssw0rd");
        given(accountRepositoryMock.findById(anyLong())).willReturn(Optional.of(toDelete));
        given(passwordEncoderMock.matches(anyString(), anyString())).willReturn(false);

        // when
        Throwable thrown = catchThrowable(() -> underTest.deleteAccount(id, request));

        // then
        then(accountRepositoryMock).should(never()).deleteById(id);
        assertThat(thrown).isInstanceOf(AccessForbiddenException.class);
    }

    @Test
    void deleteAccount_ShouldDelete_WhenPasswordMatches() {
        // given
        long id = 17L;
        Account toDelete = new Account("test", "P@ssw0rd");
        DeleteMyAccountRequest request = new DeleteMyAccountRequest("P@ssw0rd");
        given(accountRepositoryMock.findById(anyLong())).willReturn(Optional.of(toDelete));
        given(passwordEncoderMock.matches(anyString(), anyString())).willReturn(true);

        // when
        underTest.deleteAccount(id, request);

        // then
        then(accountRepositoryMock).should(times(1)).deleteById(id);
    }
}