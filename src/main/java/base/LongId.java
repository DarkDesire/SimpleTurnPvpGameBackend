package base;

public class LongId<T> {
    private Long id;

    public LongId(long id) {
        this.id = id;
    }

    public long get() {
        return id;
    }

    public boolean equals(Object obj) {
        if(obj instanceof LongId) {
            return ((LongId) obj).get() == id;
        }
        return false;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return id.toString();
    }
}