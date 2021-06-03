package Server;

import lombok.ToString;

public enum Policy {
    AccessLevel1{
        @Override
        public String toString() {
            return "AccessLevel1";
        }
    },
    AccessLevel2{
        @Override
        public String toString() {
            return "AccessLevel2";
        }
    },
    AccessLevel3{
        @Override
        public String toString() {
            return "AccessLevel3";
        }
    },
    AccessLevel4{
        @Override
        public String toString() {
            return "AccessLevel4";
        }
    },
    AccessLevel5{
        @Override
        public String toString() {
            return "AccessLevel5";
        }
    };
}
