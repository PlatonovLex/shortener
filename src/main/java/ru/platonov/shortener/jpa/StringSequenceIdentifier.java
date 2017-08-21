package ru.platonov.shortener.jpa;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * StringSequenceIdentifier.
 * <p>
 *     Creates an alphabetic sequence in the form of a key
 *     The algorithm works much faster than the previous version,
 *     besides this approach excludes the possibility of collisions,
 *     because The keys are issued sequentially.
 *
 *     The key generation algorithm is based on the standard translation between multi-digit systems.
 *     A typical example of a translation from a hexadecimal system to a decimal one.
 *     Here also use a system of 32 symbols coded for 5 bits.
 * </p>
 *
 * @author Platonov Alexey
 * @since 21.08.2017
 */
public class StringSequenceIdentifier extends SequenceStyleGenerator {

    private static final int CHAR_BITS_COUNT = 5;

    private static final int ONE_LETTER_MASK = 0b11111;

    private String sequenceCallSyntax;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(type, params, serviceRegistry);
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        Dialect dialect = jdbcEnvironment.getDialect();

        String sequencePerEntitySuffix = ConfigurationHelper.getString(CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, params, DEF_SEQUENCE_SUFFIX);

        String defaultSequenceName = ConfigurationHelper.getBoolean(CONFIG_PREFER_SEQUENCE_PER_ENTITY, params, false)
                ? params.getProperty(JPA_ENTITY_NAME) + sequencePerEntitySuffix
                : DEF_SEQUENCE_NAME;

        sequenceCallSyntax = dialect.getSequenceNextValString(ConfigurationHelper.getString(SEQUENCE_PARAM, params, defaultSequenceName));
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) {
        long seqValue = ((Number) Session.class.cast(session)
                .createSQLQuery(sequenceCallSyntax)
                .uniqueResult()).longValue();

        return getCharsKey(seqValue);
    }

    /**
     * Letters associated with codes
     */
    private enum Letter {
        a(0b0000),
        b(0b0001),
        c(0b0010),
        d(0b0011),
        e(0b0100),
        f(0b0101),
        g(0b0110),
        h(0b0111),
        i(0b1000),
        j(0b1001),
        k(0b1010),
        l(0b1011),
        m(0b1100),
        n(0b1101),
        o(0b1110),
        p(0b1111),
        A(0b10000),
        B(0b10001),
        C(0b10010),
        D(0b10011),
        E(0b10100),
        F(0b10101),
        G(0b10110),
        H(0b10111),
        I(0b11000),
        J(0b11001),
        K(0b11010),
        L(0b11011),
        M(0b11100),
        N(0b11101),
        O(0b11110),
        P(0b11111);

        private static final Map<Integer, Letter> LETTER_MAP = new HashMap<>();

        static {
            for (Letter letter : Letter.values()) {
                LETTER_MAP.put(letter.code, letter);
            }
        }

        final int code;

        Letter(int code) {
            this.code = code;
        }

        /**
         * Get character byte code
         *
         * @return character byte code
         */
        public int getCode() {
            return code;
        }

        /**
         * Get letter by its byte code
         * @param code byte code
         * @return character
         */
        public static Letter getByCode(int code) {
            return LETTER_MAP.get(code);
        }
    }

    /**
     * Get chars representation of the number
     *
     * @param inputNumber number to convert
     * @return character representation
     */
    private static String getCharsKey(long inputNumber) {
        long number = inputNumber;
        StringBuilder key = new StringBuilder();
        do {
            long letterCode = number & ONE_LETTER_MASK;
            key.append(Letter.getByCode((int)letterCode));

            number >>= CHAR_BITS_COUNT;
        } while (number != 0L);

        return key.toString();
    }

}