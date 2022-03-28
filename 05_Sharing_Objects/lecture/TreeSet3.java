package as.set;

/*
File: org.eclipse.jdt.core.prefs

eclipse.preferences.version=1
org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode=enabled
org.eclipse.jdt.core.compiler.codegen.methodParameters=do not generate
org.eclipse.jdt.core.compiler.codegen.targetPlatform=17
org.eclipse.jdt.core.compiler.codegen.unusedLocal=preserve
org.eclipse.jdt.core.compiler.compliance=17
org.eclipse.jdt.core.compiler.debug.lineNumber=generate
org.eclipse.jdt.core.compiler.debug.localVariable=generate
org.eclipse.jdt.core.compiler.debug.sourceFile=generate
org.eclipse.jdt.core.compiler.problem.assertIdentifier=error
org.eclipse.jdt.core.compiler.problem.enablePreviewFeatures=enabled
org.eclipse.jdt.core.compiler.problem.enumIdentifier=error
org.eclipse.jdt.core.compiler.problem.reportPreviewFeatures=ignore
org.eclipse.jdt.core.compiler.release=enabled
org.eclipse.jdt.core.compiler.source=17



*/


record Nil<A>() implements TSet<A>{};
record Branch<A>(TSet<A> left, A elem, TSet<A> right) implements TSet<A>{}

sealed interface TSet<A> permits Nil<A>, Branch<A> {

    static <A> boolean isEmpty(TSet<A> ts) {
        return switch (ts) {
            case Nil<A> n -> true;
            case Branch<A> b -> false;
        };
    }

    static <A extends Comparable<A>> boolean contains(TSet<A> ts, A a) {
        return switch (ts) {
            case Nil<A> n -> false;
            case Branch<A> b -> {
                var cmp = b.elem().compareTo(a);
                if (cmp == 0) {
                    yield true;
                } else if (cmp < 0) {
                    yield contains(b.right(), a);
                } else {
                    yield contains(b.left(), a);
                }
            }
        };
    }

    static <A extends Comparable<A>> TSet<A> insert(TSet<A> ts, A a) {
        return switch (ts) {
            case Nil<A> n -> new Branch<>(n, a, n);
            case Branch<A> b -> {
                var cmp = b.elem().compareTo(a);
                if (cmp == 0) {
                    yield b;
                } else if (cmp < 0) {
                    yield new Branch<>(b.left(), b.elem(), insert(b.right(), a));
                } else {
                    yield new Branch<>(insert(b.left(), a), b.elem(), b.right());
                }
            }
        };
    }
}

public class TreeSet3 {
    public static void main(String[] args) {
        var nil = new Nil<Integer>();
        var s1 = TSet.insert(nil, 12);
        var s2 = TSet.insert(s1, 12);
        System.out.println(s2);
    }
}



