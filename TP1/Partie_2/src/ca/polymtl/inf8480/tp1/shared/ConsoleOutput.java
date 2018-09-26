package ca.polymtl.inf8480.tp1.shared;

public enum ConsoleOutput {

    REGISTRATION_DENIED {
        @Override
        public String toString(){
            return "Client registration failed. Client already exists.";
        }
    },

    REGISTRATION_APPROVED {
        @Override
        public String toString() {
            return "Client has been registered with server.";
        }
    },

    AUTH_DENIED {
        @Override
        public String toString() {
            return "The server was unable to authenticate the client";
        }
    },

    AUTH_APPROVED {
        @Override
        public String toString() {
            return "The client was successfully authenticated.";
        }
    };
}
