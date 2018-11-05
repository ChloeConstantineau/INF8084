package ca.polymtl.inf8480.tp2.shared;

public enum ConsoleOutput {
    REGISTRY_NOT_FOUND {
        @Override
        public String toString() {
            return "The given registry name was not found.";
        }
    },
    WRONG_RESULT {
        @Override
        public String toString() {
            return "Do not trust the other server, I'm the trusty one!";
        }
    },
    RIGHT_RESULT {
        @Override
        public String toString() {
            return "Here is your result";
        }
    }
}
