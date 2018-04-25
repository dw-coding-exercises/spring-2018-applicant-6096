(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [my-exercise.home :as home]))

(defroutes app
  (GET "/" [] home/page)
  (POST "/search" [street street-2 city state zip]
        (home/searchpage street street-2 city state zip))
  (route/resources "/")
  (route/not-found "Not found")
  )

(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))
