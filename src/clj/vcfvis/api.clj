(ns vcfvis.api
  "Expose variant data retrieval and processing to client calls."
  (:use compojure.core
        [cemerick.friend :only [current-authentication]]
        [cemerick.shoreleave.rpc :only [defremote]]
        [bcbio.variation.api.run :only [do-analysis]]
        [cheshire.core :only [generate-string]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [bcbio.variation.api.file :as bc-file]
            [bcbio.variation.api.metrics :as bc-metrics]))

(defremote ^{:remote-name :variant/context} variant-context
  "Retrieve user-level details about available files and global metrics."
  [& {:keys [request]}]
  (when-let [rclient (:client (current-authentication))]
    {:files (bc-file/list-files-w-cache rclient :vcf)
     :metrics (bc-metrics/available-metrics nil)
     :username (:identity (current-authentication))}))

(defremote ^{:remote-name :variant/raw} variant-raw
  "Retrieve raw variant data for a given input file"
  [file-url & {:keys [request]}]
  (when-let [rclient (:client (current-authentication))]
    (let [raw (bc-metrics/get-raw-metrics file-url
                                          :rclient rclient :use-subsample? true)]
      {:available-metrics (-> raw first (dissoc :id) keys set)
       :raw (generate-string raw)})))

(defn- get-host-info
  "Retrieve local host information for generating callbacks to data."
  [req]
  {:server (:server-name req)
   :port (:server-port req)
   :ds-path "dataset"})

(defremote ^{:remote-name :run/filter} run-filter
  "Run filtering analysis, pushing results to remote server.
   TODO: make asynchronous and provide callbacks to check status"
  [file-url metrics & {:keys [request]}]
  (when-let [rclient (:client (current-authentication))]
    (-> (do-analysis :filter {:filename file-url :metrics metrics
                              :host-info (get-host-info request)}
                     rclient)
        :runner
        deref)))

(defroutes api-routes
  (GET "/vcf" req
       (when-let [rclient (:client (current-authentication))]
         (let [{{file-url :file-url} :params} req]
           (let [raw (bc-metrics/get-raw-metrics file-url
                                                 :rclient rclient :use-subsample? true)]
             (generate-string
              {:clj (pr-str {:file-url file-url
                             :available-metrics (-> raw first (dissoc :id) keys set)})
               :raw raw})))))

  (route/not-found "API ERROR =("))
