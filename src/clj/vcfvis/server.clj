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

(defmulti bio-credential-fn
  "Handle login with multiple credentials to useful biological services."
  (fn [params]
    (::friend/workflow (meta params))))

(defmethod bio-credential-fn :interactive-form
  ^{:doc "Given map with GenomeSpace username and password keys, returns GS client (if valid) or nil."}
  [{:keys [username password]}]
  (when (seq username)
    (when-let [client (get-gs-client {:username username :password password})]
      {:client client
       :service :gs
       :identity username
       :roles #{::user}})))

(defmethod bio-credential-fn :galaxy
  ^{:doc "Retrieve Galaxy client connection using API key."}
  [{:keys [galaxy-server galaxy-apikey]}]
  (when (seq galaxy-apikey)
    (println galaxy-server galaxy-apikey)))

(defmethod bio-credential-fn :default [_] nil)

(defn galaxy-api-workflow
  "Provide retrieval of parameters for Galaxy API"
  [& {:keys [login-uri credential-fn redirect-on-auth?] :as form-config}]
  (fn [{:keys [uri request-method params] :as request}]
    (when (and (= uri (get-in request [::friend/auth-config :login-uri]))
               (= :post request-method))
      (let [credential-fn (or credential-fn (get-in request [::friend/auth-config :credential-fn]))]
        (if-let [user-rec (credential-fn (with-meta params {::friend/workflow :galaxy}))]
          (workflows/make-auth user-rec {::friend/workflow :galaxy
                                        ::friend/redirect-on-auth? redirect-on-auth?})
          ((get-in request [::friend/auth-config :login-failure-handler]
                   #'workflows/interactive-login-redirect)
           (update-in request [::friend/auth-config] merge form-config)))))))

(def app
  (-> main-routes
      (friend/authenticate {:credential-fn bio-credential-fn
                            :workflows [(workflows/interactive-form)
                                        (galaxy-api-workflow)]})
      (wrap-file-info)
      (handler/site {:session {:cookie-attrs {:max-age 3600}}})))

(defn start!
  ([] (start! 8080))
  ([port]
     (defonce server
       (run-jetty #'app {:port port :join? false}))))
