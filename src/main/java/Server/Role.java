package Server;

import lombok.ToString;

@ToString
public enum Role {
    USER{
        @Override
        public String toString() {
            return "USER";
        }
    },
    STUFF{
        @Override
        public String toString() {
            return "STUFF";
        }
    },
    ADMIN{
        @Override
        public String toString() {
            return "ADMIN";
        }
    }
}
