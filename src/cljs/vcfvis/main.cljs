(ns vcfvis.main
  (:use-macros [c2.util :only [pp p timeout]]
               [reflex.macros :only [constrain!]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            ;;[vcfvis.stub :as data]
            [vcfvis.controls :as controls]))

(add-watch controls/file-selector :load-metrics
           (fn [files]
             (if (seq files)
               (doseq [f files]
                 (data/load-vcf f (partial swap! core/!vcfs conj)))
               (reset! core/!vcfs []))))

;;Request file list from server
(data/load-context (partial reset! core/!context))
