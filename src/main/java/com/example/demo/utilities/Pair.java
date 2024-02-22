package com.example.demo.utilities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Pair <A extends Comparable, B extends Comparable> implements Comparable<Pair<A,B>>, Serializable {
    private A first;
    private B second;
    public Pair(A first, B second){
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second)) || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
    }

    @Override
    public int hashCode() {
        if(first == null && second == null)
            return Objects.hash(null, null);
        else if(first == null)
            return Objects.hash(second, null);
        else if(second == null)
            return Objects.hash(first, null);
        else if(first.compareTo(second) <= 0)
            return Objects.hash(first, second);
        else
            return Objects.hash(second, first);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    @Override
    public int compareTo(Pair<A, B> o) {
        int result = first.compareTo(o.getFirst());

        return result == 0 ? second.compareTo(o.getSecond()) : result;
    }
}
