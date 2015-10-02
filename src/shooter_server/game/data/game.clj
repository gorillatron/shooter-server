(ns shooter-server.game.data.game
  (:require [ring.util.response :as response]
            [com.rpl.specter :refer [select transform]]
            [org.httpkit.server :refer [with-channel on-close on-receive close send!]])
  (:use [clojure.data.json :only [write-str read-str]]))


(defprotocol Validatable
  (validate [this] "validate the object"))

(defrecord Game [players])

(defrecord Player [name socket]
  Validatable
  (validate [this]
    (if (< (count (:name this)) 3)
      {:errors [{:message "name must be more than 3 characters long"}]}
      nil)))

(extend-protocol compojure.response/Renderable
  Game
  (render [game _]
    (let [body (write-str game :value-fn (fn [n v] (if (not= n :socket) v nil)))]
      (-> (response/response body)
          (response/content-type "text/html; charset=utf-8")))))

(def game (atom (->Game {})))

(defn state [] @game)

(defn player-join-game [player]
  (let [player-name (:name player)]
    (swap! game assoc-in [:players player-name] player)))

(defn player-leave-game [player]
  (let [player-name (:name player)]
    (swap! game update :players dissoc player-name)))

(defn broadcast-except [except-player event-name data]
  (doseq [player (vals (:players (state)))]
    (println (:name except-player) (:name player))
    (if (not= (:name except-player) (:name player))
      (send! (:socket player) (write-str {:name event-name
                                          :data data})))))

(defn handle-player-change [player]
  (let [player-name (:name player)]
    (do
      (swap! game assoc-in [:players player-name] player)
      (broadcast-except player "remote-player-change" {:player player}))))

(defn handle-bullet-fired [bullet]
  (let [player (:fired-by bullet)]
    (broadcast-except player "remote-bullet-fired" {:bullet bullet})))