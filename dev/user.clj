(ns user
  (:require [clojure.string :refer [join]]
            [reloaded.repl :refer [system reset stop]]
            [shooter-server.web]))

(reloaded.repl/set-init! #'shooter-server.web/create-system)