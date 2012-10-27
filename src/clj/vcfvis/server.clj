(ns vcfvis.server
  (:use [vcfvis.api :only [api-routes]]
        [vcfvis.login :only [bio-remote-workflow bio-credential-fn]]
        compojure.core
        [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.util.response :only [redirect response]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]))

(defroutes main-routes
  (GET "/login" req (slurp "public/login.html"))
  (friend/logout (ANY "/logout" req (redirect "/")))

  (GET "/" req
       (friend/authorize #{:user} (slurp "public/index.html")))

  (context "/api" req
           (friend/wrap-authorize api-routes #{:user}))

  (route/files "/" {:root "public" :allow-symlinks? true})
  (route/not-found "Not found"))

(def app
  (-> main-routes
      (friend/authenticate {:credential-fn bio-credential-fn
                            :workflows [(bio-remote-workflow)]})
      (wrap-file-info)
      (handler/site {:session {:cookie-attrs {:max-age 3600}}})))

(defn start!
  ([] (start! 8080))
  ([port]
     (defonce server
       (run-jetty #'app {:port port :join? false}))))
