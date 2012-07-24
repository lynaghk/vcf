(ns vcfvis.server
  (:use compojure.core
        [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.util.response :only [redirect]])

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(defroutes main-routes
  (GET "/login" req (slurp "public/login.html"))
  
  (friend/logout (ANY "/logout" req (redirect "/")))

  (GET "/" req
       (friend/authorize #{::user}
                         (slurp "public/index.html")))

  (route/files "/" {:root "public" :allow-symlinks? true})
  (route/not-found "Not found"))

;; a dummy in-memory user "database"
(def users {"vcf" {:username "vcf"
                   :password (creds/hash-bcrypt "vcf")
                   :roles #{::user}}})

(def app
  (-> main-routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn users)
                            :unauthorized-redirect-uri "/login"
                            :workflows [(workflows/interactive-form :login-uri "/login")]})
      (wrap-file-info)
      (handler/site)))

(defn start!
  ([] (start! 8080))
  ([port]
     (defonce server
       (run-jetty #'app {:port port :join? false}))))
