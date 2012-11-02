;; Interactive functionality for display of older analyses.

(ns vcfvis.xprize.analyses
  (:require [c2.dom :as dom]
            [c2.event :as event]
            [shoreleave.remotes.http-rpc :as rpc])
  (:require-macros [shoreleave.remotes.macros :as sl]))

(defn- display-selected-analysis
  "Update page with details from a selected analysis."
  [analysis-id]
  (sl/rpc (get-summary analysis-id) [sum-html]
          (-> (dom/select "#user-analyses")
              (dom/replace! sum-html))))

(defn ^:export display-analyses
  "Correctly set the top level navigation toolbar."
  []
  (event/on (-> (dom/select "#user-analyses")
                (dom/select "ul")
                dom/children)
            :click (fn [data node evt]
                     (display-selected-analysis (dom/attr node :id)))
            :capture true))
