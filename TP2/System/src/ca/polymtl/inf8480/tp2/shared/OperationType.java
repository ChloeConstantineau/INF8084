package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

public enum OperationType implements Serializable{
    Pell {
        public String toString() {
            return "pell";
        }
    },
    Prime {
        public String toString() {
            return "prime";
        }
    }
}
