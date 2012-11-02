(ns vcfvis.xprize
  "Provide X Prize specific scoring and comparisons."
  (:use [compojure.core])
  (:require [cemerick.friend :as friend]
            [vcfvis.pages :as pages]))

(defroutes xprize-routes
  (GET "/" req (pages/add-anti-forgery "public/xprize.html"))
  (GET "/analyses" [:as {session :session}])
  (POST "/score" req))