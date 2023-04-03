package push.commands.interpreter;

import java.util.HashMap;
import java.util.Map;

import push.commands.interpreter.Token.Factory;
import push.commands.interpreter.Token.GenericToken;

/**
 * Represents a 'token type'.
 * <p>
 * There are 2 types of token identifiers :
 * <ul>
 * <li>Generic token identifiers, which identify tokens which always have the
 * same symbol. Used mostly by operators.</li>
 * <li>Special token identifiers, which identify tokens which can have different
 * symbols. Used by words, names and numbers.</li>
 * </ul>
 * </p>
 */
public enum TokenIdentifier {
    // 'Variable' tokens, i.e. their symbol is not known in advance and can be
    // different
    WORD(Token.SpecialToken::new),
    ASSIGN_WORD,
    NAME,
    IO_NUMBER,

    // Operators
    PIPE("|"), AND("&"), SEMI(";"), GREAT(">", true, true), LESS("<", true, true), NEWLINE("\n"), EQUAL("="),

    AND_IF("&&"),
    OR_IF("||"),
    DSEMI(";;"),

    DLESS("<<", true, true), DGREAT(">>", true, true), LESSAND("<&", true, true), GREATAND(">&", true, true), LESSGREAT("<>", true, true), DLESSDASH("<<-", true, true),
    CLOBBER(">|", true, true),

    // Reserved words
    IF, THEN, ELSE, ELIF, FI, DO, DONE,
    CASE, ESAC, WHILE, UNTIL, FOR, IN,

    LBRACE("{", false, false), RBRACE("}", false, false), BANG("!", false, false),
    ;

    private final String symbol;
    private final Factory factory;
    private final Token prebuilt;
    private final boolean isOperator;
    private final boolean isRedirect;

    private static Map<String, TokenIdentifier> operators;
    private static Map<String, TokenIdentifier> reserved;

    /**
     * Constructs an operator token identifier.
     * 
     * @param symbol The symbol of the operator.
     */
    TokenIdentifier(String symbol) {
        this(symbol, true, false);
    }

    /**
     * Constructs either an operator token identifier, or a reserved word token
     * identifier, depending on {@code operator}.
     * 
     * @param symbol   The symbol of the token. If {@code null}, the symbol is the
     *                 lower-case name of the enum constant.
     * @param operator Indicates which type of token identifier to create. If
     *                 {@code true}, construct an operator, otherwise contruct a
     *                 reserved word.
     */
    TokenIdentifier(String symbol, boolean isOperator, boolean isRedirect) {
        if (symbol == null)
            this.symbol = name().toLowerCase();
        else
            this.symbol = symbol;
        this.prebuilt = new GenericToken(this);
        this.isOperator = isOperator;
        this.isRedirect = isRedirect;
        this.factory = (id, str) -> prebuilt;
        if (isOperator)
            addAsOperator();
        else
            addAsReserved();
    }

    /**
     * Constructs a special token identifier. Special tokens don't have a
     * predetermined symbol.
     * 
     * @param factory The function to run to create a new token. If {@code factory}
     *                is {@link Factory.NULL}, construct a marker instead.
     */
    TokenIdentifier(Factory factory) {
        this.symbol = name().toLowerCase();
        this.prebuilt = null;
        this.isOperator = false;
        this.isRedirect = false;
        this.factory = factory;
    }

    /**
         * Contructs a reserved word.
         */
        TokenIdentifier() {
            this(null, false, false);
        }

    @Override
    public String toString() {
        return symbol;
    }

    private void addAsOperator() {
        if (operators == null)
            operators = new HashMap<>();
        operators.put(symbol, this);
    }

    private void addAsReserved() {
        if (reserved == null)
            reserved = new HashMap<>();
        reserved.put(symbol, this);
    }

    public Token create(String symbol) {
        return factory.create(this, symbol);
    }

    public boolean isOperator() {
        return isOperator;
    }

    public boolean isReserved() {
        return !isOperator && prebuilt != null;
    }

    public boolean isRedirect() {
        return isOperator && isRedirect;
    }

    public static TokenIdentifier getOperator(String symbol) {
        return operators.get(symbol);
    }

    public static TokenIdentifier getReserved(String symbol) {
        return reserved.get(symbol);
    }

    public static boolean isOperatorPart(char c, int index) {
        for (String symbol : operators.keySet()) {
            if (symbol.length() <= index)
                continue;
            if (symbol.charAt(index) == c)
                return true;
        }
        return false;
    }

    private static Map<String, TokenIdentifier> getOperators() {
        return operators;
    }

    private static Map<String, TokenIdentifier> getReserved() {
        return reserved;
    }

}
