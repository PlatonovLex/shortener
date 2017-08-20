package ru.platonov.shortener.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Collection;
import java.util.Set;

/**
 * Account.
 * <p>
 *      Domain entity that represents users accounts
 *      All necessary information about users
 * </p>
 *
 * @author Platonov Alexey
 * @since 14.08.2017
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ACCOUNTS")
public class Account implements UserDetails{

    private static final long serialVersionUID = 3229401993955277416L;

    @Id
    @NotBlank
    @Column(name = "ID", nullable = false, unique = true, updatable = false)
    private String id;

    @Column(name = "PASSWORD")
    private String password;

    @Version
    @Column(name = "VERSION")
    @Setter(AccessLevel.PROTECTED)
    private Long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "account")
    @BatchSize(size = 100)
    private Set<Link> links;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("USER");
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
