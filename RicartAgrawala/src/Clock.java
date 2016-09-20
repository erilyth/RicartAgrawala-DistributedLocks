import java.util.HashMap;


public class Clock implements Comparable<Clock> {

	private HashMap<String,Integer> clockValue;

    public Clock(HashMap<String,Integer> clock) {
        this.clockValue = clock;
    }
    
    public void UpdateClock(String key, int value) {
    	this.clockValue.put(key, value);
    }

    public HashMap<String,Integer> getClock() {
        return clockValue;
    }
    
    public void setClock(HashMap<String, Integer> clock) {
    	this.clockValue = clock;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Clock other = (Clock) obj;
        if (this.clockValue != other.clockValue) {
            return false;
        }
        return true;
    }

    public int compareTo(Clock i) {
        if (this.clockValue == i.clockValue) {
            return 0;
        }
        int greater = 1;
        for (String key : this.clockValue.keySet()) {
            if(this.clockValue.get(key) < i.clockValue.get(key)){
            	greater = 0;
            }
        }
        return greater;
    }
    
    public String toString() {
    	String res = "{";
    	for (String key: this.clockValue.keySet()) {
    		res += "(" + key + ", " + this.clockValue.get(key) + "), ";
    	}
    	res = res.substring(0, res.length() - 2);
    	res += "}";
    	return res;
    }
}
