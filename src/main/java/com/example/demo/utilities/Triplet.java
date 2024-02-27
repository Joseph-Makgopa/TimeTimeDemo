package com.example.demo.utilities;

import java.io.Serializable;
import java.util.Objects;

public class Triplet<A, B, C> implements Serializable, Comparable<Triplet<A, B, C>> {
    private A first;
    private B second;
    private C third;
    public Triplet(A first, B second, C third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public Triplet clone(){
        return new Triplet(first, second, third);
    }
    public A getFirst() {
        return first;
    }
    public B getSecond() {
        return second;
    }
    public C getThird() {
        return third;
    }
    public void setFirst(A first) {
        this.first = first;
    }
    public void setSecond(B second) {
        this.second = second;
    }
    public void setThird(C third) {
        this.third = third;
    }
    @Override
    public String toString() {
        return "("  + first + ", "+ second + ", "+ third +")";
    }
    public boolean equals(A first, B second, C third){
        return Objects.equals(this.first, first ) && Objects.equals(this.second, second) && Objects.equals(this.third, third);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(first, triplet.first) && Objects.equals(second, triplet.second) && Objects.equals(third, triplet.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public int compareTo(Triplet<A, B, C> o) {
        return 0;
    }
}
