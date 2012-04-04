## _Clojure Programming_, Chapter 11

### Visualizing the Mandelbrot Set in Clojure

This project contains a Mandelbrot Set implementation in Clojure that
demonstrates the usage and impact of primitive type declarations on the
runtime of numerically-intensive algorithms.

#### Running

A canonical rendering of the Mandelbrot Set can be obtained by running
the `-main` entry point in the
[`com.clojurebook.mandelbrot`](src/com/clojurebook/mandelbrot.clj)
namespace using Leiningen:

```
$ lein run mandelbrot.png -2.25 0.75 -1.5 1.5 :width 800 :height 800 
```

After running this, you'll see this in `mandelbrot.png`:

![](https://github.com/clojurebook/ClojureProgramming/raw/master/ch11-mandelbrot/mandelbrot.png)

The run arguments correspond exactly to those required by
`com.clojurebook.mandelbrot/mandelbrot`.

You can change the view you get by modifying the coordinates provided to
that function:

```
$ lein run mandelbrot-zoomed.png -1.5 -1.3 -0.1 0.1 :width 800 :height 800
```

![](https://github.com/clojurebook/ClojureProgramming/raw/master/ch11-mandelbrot/mandelbrot-zoomed.png)

Of course, if you're going to do a bunch of exploration of the
Mandelbrot Set using this implementation, you'll be _way_ better off
working from the REPL rather than paying the JVM and Leiningen startup
cost repeatedly.  Refer to the book or the sources here for
REPL-oriented examples.

#### Optimization via primitive type declarations
A nearly order-of-magnitude improvement in the running time of
`com.clojurebook.mandelbrot/mandelbrot` can be had by replacing its
helper `escape` function with this implementation:

```clojure
(defn- fast-escape
  [^double a0 ^double b0 depth]
  (loop [a a0
         b b0
         iteration 0]
    (cond
      (< 4 (+ (* a a) (* b b))) iteration
      (>= iteration depth) -1
      :else (recur (+ a0 (- (* a a) (* b b)))
                   (+ b0 (* 2 (* a b)))
                   (inc iteration)))))
```

Aside from the `^double` type declarations for the `a0` and `b0`
arguments, this implemenation is otherwise unchanged compared to the
default (boxing) `escape` function.

An alternative `lein run` alias — called `:fast` — is set up to use `fast-escape`:

```
$ lein run :fast mandelbrot.png -2.25 0.75 -1.5 1.5 :width 800 :height 800 
```

The above will run far faster than the first `lein run` invocation
above.

