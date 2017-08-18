package ru.platonov.shortener.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "LINKS", indexes = @Index(name = "IDX_SHORT_URL", columnList = "SHORT_URL"))
public class Link implements Serializable {

    private static final long serialVersionUID = 4988781416070775637L;

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_LINK_ID_GENERATOR")
    @SequenceGenerator(name = "SQ_LINK_ID_GENERATOR", sequenceName = "SQ_LINK_ID", allocationSize = 0)
    private long id;

    @Column(name = "URL", columnDefinition = "TEXT", nullable = false)
    private String url;

    @NotBlank
    @Column(name = "SHORT_URL", nullable = false, unique = true)
    private String shortUrl;

    @Min(301)
    @Max(302)
    @Column(name = "REDIRECT_TYPE", nullable = false)
    private Integer redirectType = HttpStatus.FOUND.value();

    @Column(name = "REDIRECTS_AMOUNT", nullable = false)
    @Builder.Default
    private Long redirectsAmount = 0L;

    @Version
    @Column(name = "VERSION")
    @Setter(AccessLevel.PROTECTED)
    private Long version;

    @ManyToOne
    private Account account;

}
