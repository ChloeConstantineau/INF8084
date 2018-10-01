package ca.polymtl.inf8480.tp1.shared;

public enum ConsoleOutput {

    REGISTRY_NOT_FOUND {
        @Override
        public String toString() {
            return "The given registry name was not found.";
        }
    },

    INVALID_FUNCTION_CALL {
        @Override
        public String toString() {
            return "The given function name or parameter is not supported";
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
    },

    REGISTRATION_DENIED {
        @Override
        public String toString() {
            return "The server was unable to register the client";
        }
    },

    REGISTRATION_APPROVED {
        @Override
        public String toString() {
            return "he client was successfully registered.";
        }
    },

    INVALID_FILE_NAME {
        @Override
        public String toString() {
            return "File name invalid.";
        }
    },

    INVALID_CREDENTIALS {
        @Override
        public String toString() {
            return "Invalid credentials.";
        }
    },

    NEW_FILE_CREATED {
        @Override
        public String toString() {
            return "New file created : ";
        }
    },

    CONTENT_UPDATED {
        @Override
        public String toString() {
            return "Content file has been updated";
        }
    },

    CONTENT_IS_ALREADY_UP_TO_DATE {
        @Override
        public String toString() {
            return "Content file is already up to date";
        }
    },

    PUSHED_DENIED {
        @Override
        public String toString() {
            return "Push denied: The file has to be locked by user prior to pushing";
        }
    },

    PUSHED_ACCEPTED {
        @Override
        public String toString() {
            return "Push successful";
        }
    },

    LOCK_ACCEPTED {
        @Override
        public String toString() {
            return "File successfully locked";
        }
    },

    FILE_404 {
        @Override
        public String toString() {
            return "File not found on server.";
        }
    },
    FILE_ALREADY_LOCKED {
        @Override
        public String toString() {
            return "File already locked by user ";
        }
    },
}
