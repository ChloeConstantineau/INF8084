package ca.polymtl.inf8480.tp1.shared;

public enum ConsoleOutput {

    REGISTRY_NOT_FOUND {
        @Override
        public String toString() {
            return "The given name was not found in the registry.";
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

    INVALID_FILE_NAME {
        @Override
        public String toString() {
            return "File name invalid.";
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
            return "Push denied: The file has to be locked prior to pushing";
        }
    },

    PUSHED_ACCEPTED{
        @Override
        public String toString() {
            return "Push successful";
        }
    },

    FILE_404{
        @Override
        public String toString() {
            return "File not found on server.";
        }
    }
}
