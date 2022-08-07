import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueHigh;

import java.util.ArrayList;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

public class InMemoryUserDetailsManagerChangePasswordFuzzer {
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "secret";
    private final static String USER_ROLE = "ADMIN";

    // Values chosen without heuristics or logic
    private final static int LENGTH_USERNAME = 100;
    private final static int LENGTH_PASSWORD = 500;

    public static void fuzzerInitialize() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // generating needed objects
        final String username = data.consumeString(LENGTH_USERNAME);
        final String initialPassword = data.consumeString(LENGTH_PASSWORD);
        final String newPassword = data.consumeRemainingAsString();

        // check if the fuzzer generated useful data
        if (username.equals(USERNAME)
            && (initialPassword.equals(PASSWORD) || newPassword.equals(PASSWORD))) {
            return;
        }

        // create all the objects needed for fuzzing the InMemoryUserDetailsManager
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(USER_ROLE);
        final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        final User user = new User(USERNAME, PASSWORD, authorities);

        final InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager(user);

        try {
            userDetailsManager.changePassword(initialPassword, newPassword);

            if (newPassword.equals(
                    ((User) userDetailsManager.loadUserByUsername(USERNAME)).getPassword())) {
                throw new FuzzerSecurityIssueHigh(
                    "Password was changed from '" + initialPassword + "' to '" + newPassword + "'");
            }
        } catch (UsernameNotFoundException err) {
            throw new FuzzerSecurityIssueHigh(
                "user disappeared from the InMemoryUserDetailsManager");
        } catch (AccessDeniedException err) { // TODO: make it so the exception stops being thrown
            // System.err.println(SecurityContextHolder.getContext().toString());
            // err.printStackTrace();
            return;
        }
    }

    public static void fuzzerTearDown() {
        SecurityContextHolder.clearContext();
    }
}
