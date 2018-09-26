package ca.polymtl.inf8480.tp1.shared;

public enum ConsoleOutput {

    REGISTRY_NOT_FOUND {
        @Override
        public String toString(){
            return "The given name was not found in the registry.";
        }
    },

    INVALID_FUNCTION_CALL {
        @Override
        public String toString(){
            return "The given function name or parameter is not supported";
        }
    },

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
