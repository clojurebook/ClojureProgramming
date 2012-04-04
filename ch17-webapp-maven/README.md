## _Clojure Programming_, Chapter 17

### Developing Clojure web applications with Maven and Jetty

This project contains a simple Clojure web application that can be
started and reloaded using Maven and Jetty via the maven-jetty-plugin. 

#### Running

In a terminal:

```
$ mvn jetty:run
```

(…or, if you're using Eclipse or another IDE with "built
in" Maven support, you can run the `jetty:run` goal in it…)

This will start the Jetty server on port 8080, running the Clojure
webapp. 

You can restart the Jetty server by hitting return in the terminal.  It
is possible to use this mechanism to reload the Clojure application
after making changes, etc., but a far more flexible and efficient
approach to interactive development would be to either:

* include a REPL server (either
  [nREPL](http://github.com/clojure/tools.nrepl) or
[swank](https://github.com/technomancy/swank-clojure)) in your webapp,
and connect to it from your development environment (e.g.
[Counterclockwise / Eclipse](http://code.google.com/p/counterclockwise/)
or
[Emacs](http://dev.clojure.org/display/doc/Getting+Started+with+Emacs)
or [vim](http://dev.clojure.org/display/doc/Getting+Started+with+Vim) or
your other favorite editor/IDE that provides quality Clojure REPL
support).  From there, you can load new code into the running webapp
with abandon.
* start the Jetty server from a REPL using the ring-jetty-adapter, to
  which you can provide the top-level var of your webapp.  Of course,
since you're using a REPL, and new code you load will be utilized
immediately. 
