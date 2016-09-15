import java.lang.*;

public class QueueElement implements Comparable<QueueElement> {

        String name;
		private int clockValue;

        public QueueElement(String name, int clock) {
            this.name = name;
            this.clockValue = clock;
        }

        public String getName() {
            return name;
        }

        public int getClock() {
            return clockValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final QueueElement other = (QueueElement) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (this.clockValue != other.clockValue) {
                return false;
            }
            return true;
        }

        public int compareTo(QueueElement i) {
            if (this.clockValue == i.clockValue) {
                return this.name.compareTo(i.name);
            }

            return this.clockValue - i.clockValue;
        }

        @Override
        public String toString() {
            return String.format("%s: $%d", name, clockValue);
        }      
      
    }