(ns shooter-server.game.router
  (:require [ring.util.response :as response]
            [shooter-server.game.data.game :as game]
            [org.httpkit.server :refer [with-channel on-close on-receive close send!]])
  (:use [compojure.core :only [routes GET POST DELETE ANY context]]
        [clojure.data.json :only [write-str read-str]]))


(defn handle-socket-command [data socket]
  (do
    (case (:update data)
      "player-change" (game/handle-player-change (:player data))
      "bullet-fired" (game/handle-bullet-fired (:bullet data))
      (println "unhandeled" data))))


(defn router []
  (routes

    (GET "/state" [] (game/state))

    (GET "/join" []
         (fn [req]
           (with-channel
             req socket
             (let [{{player-name :player-name} :params} req
                   player (game/->Player player-name socket)
                   validation-error (.validate player)]
               (if validation-error
                 (do
                   (println validation-error)
                   (close socket))
                 (do
                   (game/player-join-game player)
                   (on-close socket
                             (fn [status]
                               (game/player-leave-game player)))
                   (on-receive socket
                               (fn [json-data]
                                 (let [data (read-str json-data :key-fn clojure.core/keyword)]
                                   (handle-socket-command data socket))))))))))))