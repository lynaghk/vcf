(ns vcfvis.server
  (:use [vcfvis.api :only [api-routes]]
        [bcbio.variation.api.file :only [get-gs-client]]

        compojure.core
        [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.util.response :only [redirect response]])

  (:require [compojure.handler :as handler]
            [compojure.route :as route]

            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(defroutes main-routes
  (GET "/login" req (slurp "public/login.html"))
  (friend/logout (ANY "/logout" req (redirect "/")))

  (GET "/" req
       (friend/authorize #{::user} (slurp "public/index.html")))

  (context "/api" req
           (friend/wrap-authorize api-routes #{::user}))

  (route/files "/" {:root "public" :allow-symlinks? true})
  (route/not-found "Not found"))

(defn gs-credential-fn
  "Given map with GenomeSpace username and password keys, returns GS client (if valid) or nil."
  [{:keys [username password]}]
  (when-let [client (get-gs-client {:username username :password password})]
    {:client client
     :identity username
     :roles #{::user}}))

(def app
  (-> main-routes
      (friend/authenticate {:credential-fn gs-credential-fn
                            :unauthorized-redirect-uri "/login"
                            :workflows [(workflows/interactive-form :login-uri "/login")]})
      (wrap-file-info)
      (handler/site {:session {:cookie-attrs {:max-age 3600}}})))

(defn start!
  ([] (start! 8080))
  ([port]
     (defonce server
       (run-jetty #'app {:port port :join? false}))))
