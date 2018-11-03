package ca.polymtl.inf8480.tp2.shared;

public enum ConsoleOutput {

    NOT_ENOUGH_ARGS {
        @Override
        public String toString() {
            return "Missing an argument. Please provide an argument m between 0 and 1";
        }
    },
    WRONG_ARGS {
        @Override
        public String toString() {
            return "Value of argument m must be between 0 and 1";
        }
    },
}
