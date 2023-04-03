package push.commands.interpreter;

import java.util.HashMap;
import java.util.Map;

public interface Token {


    TokenIdentifier getIdentifier();
    
    class GenericToken implements Token {

        private final TokenIdentifier identifier;

        public GenericToken(TokenIdentifier identifier) {
            this.identifier = identifier;
        }

        @Override
        public TokenIdentifier getIdentifier() {
            return identifier;
        }

        @Override
        public String toString() {
            return identifier.toString();
        }

    }

    class SpecialToken implements Token {

        private TokenIdentifier identifier;
        private final String text;
        
        SpecialToken(TokenIdentifier type, String text) {
            this.identifier = type;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public TokenIdentifier getIdentifier() {
            return identifier;
        }

        public void setIdentifier(TokenIdentifier identifier) {
            this.identifier = identifier;
        }

    }

    public interface Factory {

        public static final Factory NULL = (id, str) -> { throw new UnsupportedOperationException(); };

        Token create(TokenIdentifier identifier, String symbol);
        
    }

}
