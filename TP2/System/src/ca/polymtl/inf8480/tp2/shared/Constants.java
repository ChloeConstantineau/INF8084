package ca.polymtl.inf8480.tp2.shared;

public final class Constants {
    public static final String OPERATION_DATA_PATH = "./operationFiles/";
    public static final int RMI_REGISTRY_PORT = 5000;
    public static final int SERVER_MIN_PORT = 5001;
    public static final int SERVER_MAX_PORT = 5050;
    public static final int MAX_OPERATIONS_PER_TASK = 100;
    public static final int MAX_OPERATION_RETRY = 3;
    public static final String DEFAULT_DISPATCHER_CONFIGS = "./configs/dispatcher";
    public static final String DEFAULT_CONFIG_PATH = "./configs/";
    public static final String USER_MENU_OPARATIONS_SERVER =
            "Choose operation server options :" +
                    "[1] C = 4, m = 0" +
                    "[2] C = 5, m = 0 " +
                    "[3] C = 5, m = 0.5 " +
                    "[4] C = 5, m = 0.75 ";
}


