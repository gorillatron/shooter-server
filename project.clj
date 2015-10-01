(defproject shooter-server "1.0.0-SNAPSHOT"

  :description "Shooter game server"

  :url ""

  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [http-kit "2.1.18"]
                 [compojure "1.4.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [reloaded.repl "0.2.0"]
                 [com.rpl/specter "0.7.1"]
                 [org.clojure/data.json "0.2.6"]
                 [environ "1.0.0"]]

  :main ^:skip-aot shooter-server.web

  :min-lein-version "2.0.0"

  :plugins [[environ/environ.lein "0.3.1"]]

  :hooks [environ.leiningen.hooks]

  :uberjar-name "shooter-server-standalone.jar"

  :profiles {:production {:env {:production true}}
             :dev {:main ^:skip-aot user
                   :dependencies [[reloaded.repl "0.2.0"]]
                   :source-paths ["dev"]}})
