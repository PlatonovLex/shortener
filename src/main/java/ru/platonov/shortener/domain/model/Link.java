package ru.platonov.shortener.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Link.
 * <p>
 *     Link entity implementation.
 *     Store real link data and its generated short version
 * </p>
 *
 * @author Platonov Alexey
 * @since 15.08.2017
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "shortUrlPart")
@Entity
@Table(name = "LINKS", indexes = @Index(name = "IDX_URL", columnList = "URL"))
public class Link implements Serializable {

    private static final long serialVersionUID = 4988781416070775637L;

    @Id
    @GenericGenerator(
            name = "assigned-sequence",
            strategy = "ru.platonov.shortener.jpa.StringSequenceIdentifier",
            parameters = {
                    @Parameter(name = "sequence_name", value = "SQ_LINK_ID"),
                    @Parameter(name = "initial_value", value = "33556431"),
                    @Parameter(name = "increment_size", value = "1")
            }

    )
    @GeneratedValue(generator = "assigned-sequence", strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "SHORT_URL_PART")
    private String shortUrlPart;

    @Column(name = "URL", columnDefinition = "TEXT", nullable = false)
    private String url;

    @Min(301)
    @Max(302)
    @Column(name = "REDIRECT_TYPE", nullable = false)
    private Integer redirectType = HttpStatus.FOUND.value();

    @Column(name = "REDIRECTS_AMOUNT", nullable = false)
    private Long redirectsAmount = 0L;

    @Version
    @Column(name = "VERSION")
    @Setter(AccessLevel.PROTECTED)
    private Long version;

    @ManyToOne
    private Account account;

    @Builder
    private Link(String url, Integer redirectType, Long redirectsAmount, Long version, Account account) {
        this.url = url;
        if(redirectType != null) {
            this.redirectType = redirectType;
        }
        if(redirectsAmount != null) {
            this.redirectsAmount = redirectsAmount;
        }
        this.version = version;
        this.account = account;
    }

}
