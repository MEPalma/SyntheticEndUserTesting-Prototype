package backend.utils;

public class Pair<A, B> {
    public A fst;
    public B snd;

    public Pair() {
        this.fst = null;
        this.snd = null;
    }

    public Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
