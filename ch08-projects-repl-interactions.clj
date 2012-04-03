;-----
*ns*
;= #<Namespace user>
(defn a [] 42)
;= #'user/a


;-----
(in-ns 'physics.constants)
;= #<Namespace physics.constants>
(def ^:const planck 6.62606957e-34)
;= #'physics.constants/planck


;-----
(+ 1 1)
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: + in this context, compiling:(NO_SOURCE_PATH:1)>


;-----
(clojure.core/range -20 20 4)
;= (-20 -16 -12 -8 -4 0 4 8 12 16)


;-----
user/a
;= #<user$a user$a@6080669d>
(clojure.core/refer 'user)
;= nil
(a)
;= 42


;-----
(clojure.core/refer 'clojure.core
  :exclude '(range)
  :rename '{+ add
            - sub
            / div
            * mul})
;= nil
(-> 5 (add 18) (mul 2) (sub 6))
;= 40
(range -20 20 4)
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: range in this context, compiling:(NO_SOURCE_PATH:1)>


;-----
(clojure.set/union #{1 2 3} #{4 5 6})
;= #<ClassNotFoundException java.lang.ClassNotFoundException: clojure.set>


;-----
(require 'clojure.set)
;= nil
(clojure.set/union #{1 2 3} #{4 5 6})
;= #{1 2 3 4 5 6}


;-----
(require '[clojure.set :as set])
;= nil
(set/union #{1 2 3} #{4 5 6})
;= #{1 2 3 4 5 6}


;-----
(require '(clojure string [set :as set]))


;-----
(require 'clojure.xml)
(refer 'clojure.xml)


;-----
(use '(clojure [string :only (join) :as str]
               [set :exclude (join)]))
;= nil
join
;= #<string$join clojure.string$join@2259a735>
intersection
;= #<set$intersection clojure.set$intersection@2f7fc44f>
str/trim
;= #<string$trim clojure.string$trim@283aa791>


;-----
(require '(clojure [string :as str]
                   [set :as set]))


;-----
(use '[clojure.set :as set :only (intersection)])


;-----
(Date.)
;= #<CompilerException java.lang.IllegalArgumentException:
;=   Unable to resolve classname: Date, compiling:(NO_SOURCE_PATH:1)>
(java.util.Date.)
;= #<Date Mon Jul 18 12:31:38 EDT 2011>
(import 'java.util.Date 'java.text.SimpleDateFormat)
;= java.text.SimpleDateFormat
(.format (SimpleDateFormat. "MM/dd/yyyy") (Date.))
;= "07/18/2011"


;-----
(import '(java.util Arrays Collections))
;= java.util.Collections
(->> (iterate inc 0)
  (take 5)
  into-array
  Arrays/asList
  Collections/max)
;= 4


;-----
(import 'java.awt.List 'java.util.List)
;= #<IllegalStateException java.lang.IllegalStateException:
;=   List already refers to: class java.awt.List in namespace: user>


;-----
(in-ns 'examples.ns)
(clojure.core/refer 'clojure.core :exclude '[next replace remove])
(require '(clojure [string :as string]
                   [set :as set])
         '[clojure.java.shell :as sh])
(use '(clojure zip xml))
(import 'java.util.Date
        'java.text.SimpleDateFormat
        '(java.util.concurrent Executors
                               LinkedBlockingQueue))


;-----
(ns examples.ns
  (:refer-clojure :exclude [next replace remove])        
  (:require (clojure [string :as string]
                     [set :as set])
            [clojure.java.shell :as sh])
  (:use (clojure zip xml))
  (:import java.util.Date
           java.text.SimpleDateFormat
           (java.util.concurrent Executors
                                 LinkedBlockingQueue)))


;-----
#<Exception java.lang.Exception:
  Cyclic load dependency:
  [ /some/namespace/X ]->/some/namespace/Y->[ /some/namespace/X ]>


;-----
(defn a [x] (+ constant (b x)))
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: constant in this context, compiling:(NO_SOURCE_PATH:1)>


;-----
(declare constant b)
;= #'user/b
(defn a [x] (+ constant (b x)))
;= #'user/a
(def constant 42)
;= #'user/constant
(defn b [y] (max y constant))
;= #'user/b
(a 100)
;= 142


;-----
java -cp '.:src:clojure.jar:lib/*' clojure.main


;-----
'.;src;clojure.jar;lib\*'


;-----
$ java -cp clojure.jar clojure.main
Clojure 1.3.0
(System/getProperty "java.class.path")
;= "clojure.jar"


;-----
[org.clojure/clojure "1.3.0"]


;-----
<dependency>
  <groupId>org.clojure</groupId>
  <artifactId>clojure</artifactId>
  <version>1.3.0</version>
</dependency>

;-----
(defproject com.clojurebook/lein-mixed-source "1.0.0"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :aot :all)

(require '(leiningen compile javac))

(add-hook #'leiningen.compile/compile
  (fn [compile project & args]
    (apply compile project args)
    (leiningen.javac/javac (assoc project :java-source-path "srcj"))))


