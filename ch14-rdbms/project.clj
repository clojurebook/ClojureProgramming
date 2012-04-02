(defproject com.clojurebook/rdbms "1.0.0"
  :description "Examples for working with relational databases using Clojure.
                SQLite is assumed here, but everything should translate to your
                preferred database with a minimal of hassle."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [c3p0/c3p0 "0.9.1.2"]

                 ; SQLite JDBC driver
                 [org.xerial/sqlite-jdbc "3.7.2"]

                 ; only needed for korma
                 [korma "0.3.0-alpha11"]

                 ; only needed for hibernate
                 [org.hibernate/hibernate-core "4.0.0.Final"]]

  ; only needed for hibernate
  :java-source-path "java"
  :resources-path "rsrc")