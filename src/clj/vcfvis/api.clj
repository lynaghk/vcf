(ns vcfvis.api
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [bcbio.variation.api.file :as bc-file]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(defn gs-creds
  "Helper fn to extract GenomeSpace client from session and return creds map expected by bcbio api fns."
  [req]
  (when-let [ident (get-in req [:session :cemerick.friend/identity :current])]
    (when-let [gs-client (get-in req [:session :cemerick.friend/identity :authentications ident :client])]
      {:client gs-client})))

(defn clj-response [x]
  {:status 202
   :headers {"Content-Type" "application/clojure; charset=utf-8"}
   :body (pr-str x)})

(defroutes api-routes
  (GET "/files" req
       (if-let [creds (gs-creds req)]
         (clj-response (bc-file/get-files :vcf creds))))
  (route/not-found "API ERROR =("))
