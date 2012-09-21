(ns vcfvis.main
  (:use-macros [c2.util :only [pp p timeout]]
               [reflex.macros :only [constrain!]]
               [dubstep.macros :only [publish!]])
  (:use [c2.util :only [clj->js]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            ;;[vcfvis.stub :as data]
            [vcfvis.controls :as controls]))

(add-watch controls/file-selector :load-metrics
           (fn [files]
             (reset! core/!vcfs [])
             (when (seq files)
               (doseq [f files]
                 (data/load-vcf f (fn [vcf] (publish! {:vcf vcf})))))))

;;Request file list from server
(.modal (js/jQuery "#waiting-modal")
        (clj->js {:backdrop "static" :keyboard false :show true}))
(data/load-context (partial reset! core/!context))
