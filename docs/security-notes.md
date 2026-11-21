# Security notes — AutoLoc

Authentication uses HTTP Basic with two in-memory users (classroom only):

| User | Password | Role |
|---|---|---|
| student | student123 | USER |
| admin | admin123 | ADMIN |

## Route rules

| Request | Anonymous | USER | ADMIN |
|---|---|---|---|
| `GET /api/public/ping` | 200 | 200 | 200 |
| `GET /api/hello` | 401 | 200 | 200 |
| `GET /api/admin/report` | 401 | 403 | 200 |

Rules are evaluated **in declaration order** and the first match wins. `/api/admin/**` must therefore be
declared before `/api/**`; otherwise `/api/**` would match admin URLs first and let any USER in.

## 401 vs 403

- **401 Unauthorized**: the user is not authenticated (no credentials or wrong credentials).
- **403 Forbidden**: the user is authenticated but their role does not grant access to the resource.

## Password encoding

Passwords are hashed with **BCrypt** (salted, deliberately slow, one-way). If the user store leaks,
the original passwords cannot be read back, and brute-forcing them is expensive. Plain-text storage
would expose every password immediately.

## Limits of in-memory users

In-memory users are hard-coded, lost and recreated at every restart, cannot be managed at runtime
(no sign-up, password change or role change) and put credentials in the source code. They are
fine for a local exercise but not for production.

## Loading users from MySQL

1. A `Utilisateur` entity (username, BCrypt password hash, roles) and its repository.
2. A custom `UserDetailsService` that loads the user by username from the repository and maps roles
   to `GrantedAuthority`.
3. Remove the `InMemoryUserDetailsManager` bean; keep the `PasswordEncoder` bean so stored hashes are checked with BCrypt.
