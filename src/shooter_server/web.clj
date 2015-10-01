(ns shooter-server.web

  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [com.stuartsierra.component :as component]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]])

  (:use [compojure.route :only [files not-found]]
        [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        [shooter-server.game.router :as game-router]
        org.httpkit.server))


(defn- start-server [handler port]
  (let [server (if
                 (or (= (env :ENV) "PROD")
                     (= (env :ENV) "PRODUCTION")) (run-server (site handler) {:port port})
                                                  (run-server (reload/wrap-reload (site handler)) {:port port}))]
    (println (str "Started server on port:" port))
    server))


(defn- stop-server [server]
  (when server
    (server))) ;; run-server returns a fn that stops the server


(defroutes all-routes
   (GET "/" [] "show-landing-page yooos")
   (context "/game" []
     (game-router/router))
   (route/not-found "<p>Page not found.</p>")) ;; all other, return 404


(defrecord Server []
  component/Lifecycle
  (start [this]
    (assoc this :server (start-server #'all-routes (Integer. (or (env :port) 8080)))))
  (stop [this]
    (stop-server (:server this))
    (dissoc this :server)))


(defn create-system []
  (Server.))


(defn -main [& args]
  (.start (create-system)))

