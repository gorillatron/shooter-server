(ns shooter-server.game.router
  (:require [ring.util.response :as response])
  (:use [compojure.core :only [routes GET POST DELETE ANY context]]))



(defrecord Gamelist [games])

(extend-protocol compojure.response/Renderable
  Gamelist
  (render [gamelist _]
    (let [body (str "[" (clojure.string/join ", " (:games gamelist)) "]")]
      (-> (response/response body)
          (response/content-type "text/html; charset=utf-8")))))

(def gamelist (atom (->Gamelist ['game1 'game2])))

(defn router []
  (routes
    (GET "/list" [] @gamelist)))