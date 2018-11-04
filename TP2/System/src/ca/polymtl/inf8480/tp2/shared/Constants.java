package ca.polymtl.inf8480.tp2.shared;

public final class Constants {
    public static final String OPERATION_DATA_PATH = "./operationFiles/";
    public static final int RMI_REGISTRY_PORT = 5005;
    public static final int SERVER_MIN_PORT = 5001;
    public static final int SERVER_MAX_PORT = 5050;
    public static final String DEFAULT_DISPATCHER_CONFIGS = "./configs/dispatcher_configs.json";
    public static final String DEFAULT_OPERATIONSERVER_CONFIGS = "./configs/operationServer/";
    public static final String USER_MENU_OPERATION_SERVER =
            "Choose operation server options : " + System.lineSeparator() +
                    "[1] C = 4, m = 0" + System.lineSeparator() +
                    "[2] C = 5, m = 0" + System.lineSeparator() +
                    "[3] C = 5, m = 0.5" + System.lineSeparator() +
                    "[4] C = 5, m = 0.75" + System.lineSeparator();
}


