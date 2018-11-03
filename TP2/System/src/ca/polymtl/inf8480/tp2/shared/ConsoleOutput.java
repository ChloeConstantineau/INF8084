package ca.polymtl.inf8480.tp2.shared;

public enum ConsoleOutput {

    NOT_ENOUGH_ARGS {
        @Override
        public String toString() {
            return "Missing an argument. Please provide m value and capacity";
        }
    },
    WRONG_ARGS {
        @Override
        public String toString() {
            return "Value of argument m must be between 0 and 1";
        }
    },
    NAN {
        @Override
        public String toString() {
            return "Arguments need to be numbers";
        }
    },
    POSITIVE_NUMBER_ONLY {
        @Override
        public String toString() {
            return "Please provide a positive capacity number";
        }
    },

}
