package as.set;

sealed interface TS<A extends Comparable<A>> permits Empty<A>, Branch<A> {
    boolean isEmpty();
    TS<A> insert(A a);
    boolean contains(A a);
}

record Empty<A extends Comparable<A>>() implements TS<A> {
    public boolean isEmpty() {
        return true;
    }
    
    public TS<A> insert(A a) {
        return new Branch<>(this, a, this);
    }

    public boolean contains(A a) {
        return false;
    }
}

record Branch<A extends Comparable<A>> (TS<A> left, A elem, TS<A> right) implements TS<A> {
    public boolean isEmpty() {
        return false;
    }

    public TS<A> insert(A a) {
        int cmp = elem.compareTo(a);
        if (cmp == 0) {
            return this;
        } else if(cmp < 0) {
            return new Branch<>(left, elem, right.insert(a));
        } else {
            return new Branch<>(left.insert(a), elem, right);
        }
    }

    public boolean contains(A a) {
        int cmp = elem.compareTo(a);
        if (cmp == 0) {
            return true;
        } else if(cmp < 0) {
            return right.contains(a);
        } else {
            return left.contains(a);
        }
    }
}

public class TreeSet2 {
    public static void main(String[] args) {
        var nil = new Empty<Integer>();
        var s1 = nil.insert(12);
        var s2 = s1.insert(12);
        System.out.println(s2);
    }
}