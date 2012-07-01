(ns vcfvis.core
  (:use-macros [c2.util :only [pp p interval]]
               [reflex.macros :only [computed-observable]])
  (:use [clojure.set :only [intersection]])
  (:require [reflex.core :as reflex]))

;;;;;;;;;;;;;;;;;;;;
;;From server

(def !available-filenames
  "User's files available on the server.
   May want to refactor this design if users have more than a few dozen files."
  (atom []))

(def !vcfs
  "VCFs currently under analysis."
  (atom []))


;;;;;;;;;;;;;;;;;;;;
;;Derived

(def !shared-metrics
  "Metrics"
  (computed-observable
   ;;TODO, order metrics by relevance/usefulness to biologists.
   (let [sets (map (fn [vcf]
                     (set (map (fn [metric]
                                 (select-keys metric [:id :desc]))
                               (vcf :metrics))))
                   @!vcfs)]
     
     (when (seq sets)
       (apply intersection sets)))))







;;;;;;;;;;;;;;;;;;;;
;;From UI

(def !metric
  "The metric to be displayed on the histogram."
  (atom {}))

(defn select-metric! [metric]
  (reset! !metric metric))



;;;;;;;;;;;;;;;;;;;;
;;Querying

(defn vcf-metric [vcf metric-id]
  (first (filter #(= metric-id (% :id))
                 (vcf :metrics))))
