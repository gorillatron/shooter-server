(ns shooter-server.game.router
  (:require [ring.util.response :as response]
            [shooter-server.game.data.game :as game]
            [org.httpkit.server :refer :all])
  (:use [compojure.core :only [routes GET POST DELETE ANY context]]
        [clojure.data.json :only [write-str read-str]]))


(def socket "s")


(defn handle-socket-command [data socket]
  (println data socket))


(defn router []
  (routes

    (GET "/state" [] (game/state))

    (GET "/join" []
         (fn [req]
           (with-channel
             req socket
             (let [{{player-name :player-name} :params} req
                   player (game/->Player player-name socket)]
               (game/player-join-game player)
               (on-close socket
                 (fn [status]
                   (game/player-leave-game player)))
               (on-receive socket
                 (fn [json-data]
                   (let [data (read-str json-data :key-fn clojure.core/keyword)]
                     (handle-socket-command data socket))))))))))